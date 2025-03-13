package com.github.Snuslyk.slib.factory;

import com.github.Snuslyk.slib.Controller;
import com.github.Snuslyk.slib.FilterIO;
import com.github.Snuslyk.slib.HibernateUtil;
import com.github.Snuslyk.slib.RowData;
import com.github.Snuslyk.slib.controls.fields.ChoosingTextField;
import com.github.Snuslyk.slib.electives.Button;
import com.github.Snuslyk.slib.util.StylesUtil;
import com.sun.istack.Nullable;
import javafx.animation.PauseTransition;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static com.github.Snuslyk.slib.factory.ButtonFactory.validateChecker;

public class TableFormType extends FormType implements FormWithType<TableFormType> {

    private Class<?> clazz;
    private final List<Column> columns = new ArrayList<>();
    private final List<TableActionButton> tableActionButtons = new ArrayList<>();
    private ColorSupplier tableColumnColorSupplier;
    private String filterId;
    private final List<FilterButton> filterButtons = new ArrayList<>();

    @Override
    public TableFormType name(String name) {
        this.name = name;
        return this;
    }

    @Override
    public TableFormType type(Class<?> clazz) {
        this.clazz = clazz;
        return this;
    }

    public TableFormType column(String display, String key) {
        columns.add(new Column(display, key, null));
        return this;
    }

    public TableFormType column(String display, String key, ColumnInterface columnInterface) {
        columns.add(new Column(display, key, columnInterface));
        return this;
    }

    public TableFormType cellActionButton(TableActionButton button) {
        tableActionButtons.add(button);
        return this;
    }

    public TableFormType filter(String id) {
        filterId = id;
        return this;
    }

    public TableFormType filterButton(String displayName, Supplier<ObservableList<String>> items, String defaultItem, FilterGet filterGet) {
        filterButtons.add(new FilterButton(new ChoosingTextField("", displayName, displayName, "", items, defaultItem), filterGet, defaultItem));
        return this;
    }

    public record TableActionButton(
            String display,
            Color color,
            @Nullable String svg,
            @Nullable TableActionButtonIO io,
            int extraParam // Дополнительный параметр
    ) {
        // Конструктор для создания кнопки без параметра
        public TableActionButton(String display, Color color, @Nullable String svg, @Nullable TableActionButtonIO io) {
            this(display, color, svg, io, -1); // Значение по умолчанию для extraParam
        }

        // Метод для создания новой кнопки с измененным параметром
        public TableActionButton withExtraParam(int extraParam) {
            return new TableActionButton(display, color, svg, io, extraParam); // Создаем новый объект с переданным параметром
        }

        // Метод для вызова действия с учетом параметра
        public void execute(Controller controller, Map<String, Object> data) {
            if (io != null) {
                io.handle(controller, data, extraParam); // Передаем параметр в обработчик
            }
        }
    }

    @FunctionalInterface
    public interface TableActionButtonIO {
        void handle(Controller controller, Map<String, Object> data, int extraParam);
    }

    private record Column(String displayName, String key, ColumnInterface columnInterface) {
    }

    public interface ColumnInterface {
        Object get(Object object);
    }

    public TableFormType tableColumnColorSupplier(ColorSupplier colorSupplier) {
        tableColumnColorSupplier = colorSupplier;
        return this;
    }

    public interface ColorSupplier {
        Color get(Controller controller, Map<String, Object> rowData);
    }

    private final TableView<Map<String, Object>> tableView = new TableView<>();

    private static Controller controller;
    private static ToggleButton lastSelectedButton = null;
    private static AnchorPane rootContainer;
    private static AnchorPane rightSideContainer;
    private List<List<Button>> externalObjects;
    private Form form;

    @Override
    public void setup(SetupData data) {
        controller = data.controller();
        form = data.form();
        int optionIndex = data.optionIndex();
        rootContainer = controller.getRootContainer();
        rightSideContainer = controller.getRightSideContainer();
        externalObjects = controller.getExternalObjects();
        VBox tableWithFiltersContainer = controller.getTableWithFiltersContainer();

        StylesUtil.add(tableView, "tableD");
        tableView.setPrefSize(200, 297);

        HBox filters = new HBox();

        for (FilterButton filterButton : filterButtons) {
            filterButton.button().register(filters, rootContainer);
            filters.getChildren().add(filterButton.button().searchField);
            filterButton.button().searchField.setSelectedItem(filterButton.defaultItem());
            filterButton.button().searchField.setOnCommit(string -> {
                setupTableColumns(optionIndex, tableView, filterButton.filterGet().get(string));
                adjustTableColumnsWidth(rightSideContainer.getWidth());
            });
        }

        if (filters.getChildren().isEmpty()) {
            filters.setMinHeight(0);
            filters.setMaxHeight(0);
        } else {
            filters.setMinHeight(40);
            filters.setMaxHeight(40);
        }

        VBox.setVgrow(tableView, Priority.ALWAYS);
        tableWithFiltersContainer.getChildren().setAll(filters, tableView);
        tableWithFiltersContainer.setSpacing(50);
        StylesUtil.add(tableWithFiltersContainer, "tableWithFiltersContainer");
        Controller.setAnchors(tableWithFiltersContainer, 0.0, 40.0, -1.0, -1.0);

        setupTableColumns(optionIndex, tableView, new ArrayList<>());
        adjustTableColumnsWidth(rightSideContainer.getWidth());

        rightSideContainer.getChildren().remove(tableWithFiltersContainer);
        rightSideContainer.getChildren().add(tableWithFiltersContainer);
    }

    private void setupTableColumns(int optionIndex, TableView<Map<String, Object>> tableView, List<FilterIO> filters) {
        if (externalObjects == null || tableView == null) return;

        tableView.getColumns().clear();

        setupNumberColumn(tableView); // Добавляем нумерацию строк

        setupColumns(tableView, columns);
        setupButtonColumn(tableView);
        setupRowFactory(tableView);

        rightSideContainer.widthProperty().addListener((obs, oldWidth, newWidth) -> adjustTableColumnsWidth(newWidth.doubleValue()));

        ObservableList<Map<String, Object>> data = fetchData(optionIndex, columns, filters);
        tableView.setItems(data);
    }


    private void setupButtonColumn(TableView<Map<String, Object>> tableView) {
        TableColumn<Map<String, Object>, Void> buttonColumn = new TableColumn<>();
        buttonColumn.setResizable(false);
        buttonColumn.setReorderable(false);
        buttonColumn.setSortable(false);
        buttonColumn.setCellFactory(col -> new ButtonCell());
        tableView.getColumns().add(buttonColumn);
    }

    private void setupNumberColumn(TableView<Map<String, Object>> tableView) {
        TableColumn<Map<String, Object>, Number> numberColumn = new TableColumn<>("№");
        numberColumn.setResizable(false);
        numberColumn.setReorderable(false);
        numberColumn.setSortable(false);
        numberColumn.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(tableView.getItems().indexOf(cellData.getValue()) + 1));
        numberColumn.setSortable(false);
        numberColumn.setStyle("-fx-alignment: CENTER-LEFT;");
        numberColumn.setId("number-column");
        tableView.getColumns().add(numberColumn);
    }

    //

    private void setupRowFactory(TableView<Map<String, Object>> tableView) {
        PseudoClass filled = PseudoClass.getPseudoClass("filled");

        tableView.setRowFactory(tv -> {
            TableRow<Map<String, Object>> row = new TableRow<>();

            row.itemProperty().addListener((obs, oldItem, newItem) -> rowListener(row, filled, newItem));
            return row;
        });
    }

    private void rowListener(TableRow<Map<String, Object>> row, PseudoClass filled, Map<String, Object> newItem) {
        if (newItem != null) {
            row.pseudoClassStateChanged(filled, true);

            setupColor(row, newItem);
            return;
        }

        row.pseudoClassStateChanged(filled, false);
        row.setStyle(""); // Очистка стиля для пустых строк
    }

    //

    private void setupColor(TableRow<Map<String, Object>> row, Map<String, Object> newItem) {
        if (tableColumnColorSupplier != null) {
            Color color = tableColumnColorSupplier.get(controller, newItem);

            if (color == null) return;

            String colorStyle = "-fx-border-color: " + toWebColor(color) + ";";
            row.setStyle(colorStyle);

        }
    }

    private String toWebColor(Color color) {
        return color.toString().replace("0x", "#");
    }

    private ObservableList<Map<String, Object>> fetchData(int optionIndex, List<Column> columns, List<FilterIO> filters) {
        ObservableList<Map<String, Object>> data = FXCollections.observableArrayList();
        List<Map<String, Object>> rows = new ArrayList<>();

        form.getData(filterId, FilterIO.class).ifPresent(filters::add);

        FormType formType = form.getFormTypes().get(optionIndex);

        if (!(formType instanceof TableFormType tableFormType)) return data;

        List<?> list = HibernateUtil.getObjectWithFilter(tableFormType.clazz, filters.toArray(new FilterIO[0]));


        for (Object object : list) {
            Map<String, Object> row = new LinkedHashMap<>();

            // Извлекаем ID из строки объекта
            if (object instanceof RowData d) {
                row.put("id", d.getID());
                row.put("colorData", d.getColorData());
            }

            // Добавляем остальные колонки
            for (Column column : columns) {
                String key = column.key(); // внутренний ключ
                String displayName = column.displayName(); // отображаемое имя для UI (если нужно)

                try {
                    Object value = object.getClass().getField(key).get(object);

                    ColumnInterface columnInterface = column.columnInterface();

                    if (columnInterface == null) columnInterface = o -> o;
                    row.put(key, columnInterface.get(value));  // храним данные с оригинальным ключом

                } catch (Exception e) {
                    row.put(key, "ERROR OF LOADING! KEY: " + key + " IS MISSING IN: " + object.getClass().getSimpleName());
                    e.printStackTrace();
                }
                // Используйте displayName для отображения в таблице, если необходимо
            }

            rows.add(row);
        }

        data.addAll(rows);
        return data;
    }

    private void adjustTableColumnsWidth(double totalWidth) {
        if (tableView.getColumns().isEmpty()) return;
        double columnWidth = totalWidth / tableView.getColumns().size();
        for (TableColumn<?, ?> column : tableView.getColumns()) {
            column.setPrefWidth(columnWidth);
            column.setMinWidth(100);
        }
    }

    //

    private void setupColumns(TableView<Map<String, Object>> tableView, List<Column> columns) {
        for (Column column : columns) {
            TableColumn<Map<String, Object>, String> tableColumn = new TableColumn<>(column.displayName());
            tableColumn.setResizable(false);
            tableColumn.setReorderable(false);

            if (column.key().equals("id")) {
                // Кал какой-то idColumnsDisplay = column.displayName();
            }

            tableColumnSetCellValueFactory(tableColumn, column);

            tableColumnSetCellFactory(tableColumn);

            tableView.getColumns().add(tableColumn);
        }
    }

    private void tableColumnSetCellValueFactory(TableColumn<Map<String, Object>, String> tableColumn, Column column) {
        tableColumn.setCellValueFactory(cellData -> {
            Map<String, Object> rowData = cellData.getValue();
            Object cellValue = rowData.get(column.key());
            return new SimpleStringProperty(cellValue != null ? cellValue.toString() : "");
        });
    }


    private void tableColumnSetCellFactory(TableColumn<Map<String, Object>, String> tableColumn) {
        tableColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                    return;
                }

                Label label = new Label(item);
                label.setTextFill(Color.WHITE);

                Tooltip tooltip = new Tooltip(item);
                label.setTooltip(tooltip);

                // Обработка события нажатия мыши
                label.setOnMouseClicked(event -> {
                    if (event.getButton() != MouseButton.PRIMARY) return; // ЛКМ
                    // Показать Tooltip при нажатии
                    tooltip.show(label, event.getScreenX(), event.getScreenY());

                    // Скрыть Tooltip через 1 секунду (1000 мс)
                    PauseTransition pause = new PauseTransition(Duration.seconds(1));
                    pause.setOnFinished(e -> tooltip.hide());
                    pause.play();
                });

                setGraphic(label);
            }
        });
    }

    //


    public class ButtonCell extends TableCell<Map<String, Object>, Void> {
        private final VBox editPopUp = new VBox();
        private final ToggleButton button = new ToggleButton();

        ButtonCell() {
            SVGPath svgIcon = new SVGPath();
            svgIcon.setContent("M0,2 A2,2 0 1,0 4,2 A2,2 0 1,0 0,2 M6,2 A2,2 0 1,0 10,2 A2,2 0 1,0 6,2 M12,2 A2,2 0 1,0 16,2 A2,2 0 1,0 12,2");
            svgIcon.setFill(Color.WHITE);
            button.setGraphic(svgIcon);
            StylesUtil.add(this, "button-cell");

            button.setOnAction(event -> toggleButton());
        }

        private void toggleButton() {
            if (lastSelectedButton != null && lastSelectedButton != button) {
                lastSelectedButton.setSelected(false);
            }
            if (button.isSelected()) {
                lastSelectedButton = button;
                Map<String, Object> rowData = getTableView().getItems().get(getIndex());
                System.out.println("Button clicked for row: " + rowData);
                setupEditPopUp(button, rowData);
            } else {
                closeEditPopUp();
            }
        }

        private void setupEditPopUp(ToggleButton button, Map<String, Object> rowData) {
            editPopUp.setPrefWidth(140);
            editPopUp.setPrefHeight(28);
            StylesUtil.add(editPopUp, "editPopUp");
            rightSideContainer.getChildren().removeIf(node -> node.getStyleClass().contains("editPopUp"));
            rightSideContainer.getChildren().add(editPopUp);

            Point2D buttonPosition = button.localToScene(0.0, 0.0);
            Point2D containerCoordinates = rightSideContainer.sceneToLocal(buttonPosition);
            editPopUp.setLayoutX(containerCoordinates.getX() - 100);
            editPopUp.setLayoutY(containerCoordinates.getY());

            rootContainer.setOnMouseClicked(event -> closeEditPopUp());
            getTableView().setOnMouseClicked(event -> closeEditPopUp());

            if (controller.getOptionToggleGroup().getSelectedToggle() == null) return;
            if (!editPopUp.getChildren().isEmpty()) return;

            // Перебираем кнопки и добавляем их с соответствующими классами
            for (int i = 0; i < tableActionButtons.size(); i++) {
                TableActionButton actionButton = tableActionButtons.get(i);
                javafx.scene.control.Button editButton = addEditButton(actionButton, rowData);

                editButton.setOnMouseClicked(event -> closeEditPopUp());

                // Определение класса для первой и последней кнопки
                if (tableActionButtons.size() > 1) {
                    String buttonClass = (i == 0) ? "firstChild" : (i == tableActionButtons.size() - 1) ? "secondChild" : "";
                    if (!buttonClass.isEmpty()) {
                        StylesUtil.add(editButton, buttonClass);
                    }
                } else {
                    StylesUtil.add(editButton, "singleChild");
                }

                editPopUp.getChildren().add(editButton);
            }
        }

        private void closeEditPopUp() {
            button.setSelected(false);
            rightSideContainer.getChildren().remove(editPopUp);
            lastSelectedButton = null;
        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            setGraphic(empty ? null : button);
        }
    }

    private javafx.scene.control.Button addEditButton(TableActionButton tableActionButton, Map<String, Object> rowData) {
        HBox contentBox = new HBox();
        contentBox.setSpacing(7);
        Label label = new Label(tableActionButton.display()); // Используем display из TableActionButton

        // Проверяем, есть ли логотип (SVGPath) для создания и добавления в HBox
        if (tableActionButton.svg() != null && !tableActionButton.svg().isEmpty()) {
            SVGPath svgIcon = new SVGPath();
            svgIcon.setContent(tableActionButton.svg());
            svgIcon.setStroke(tableActionButton.color());
            svgIcon.setFill(null);
            svgIcon.setStrokeWidth(1);

            // Устанавливаем отступ для SVGPath
            HBox.setMargin(svgIcon, new Insets(0, 0, 0, 7));
            contentBox.getChildren().add(svgIcon);
        } else {
            // Устанавливаем отступ для текста, если нет SVG
            HBox.setMargin(label, new Insets(0, 0, 0, 27));
        }

        // Добавляем текст в любом случае
        label.setTextFill(tableActionButton.color());
        contentBox.getChildren().add(label);

        // Настраиваем размеры HBox
        contentBox.setPrefWidth(140);
        contentBox.setAlignment(Pos.CENTER_LEFT);

        // Создаем кнопку и добавляем HBox внутрь
        javafx.scene.control.Button button = new javafx.scene.control.Button();
        button.setGraphic(contentBox);
        StylesUtil.add(button, "editButton");
        button.setMinHeight(28);

        // Настраиваем обработчик событий кнопки
        if (tableActionButton.io() != null) {
            button.setOnAction(event -> tableActionButton.execute(controller, rowData));  // Вызываем execute с учетом extraParam
        }

        return button;
    }

    public static class TableActionButtons {
        public static final TableActionButton DELETE = new TableActionButton("Удалить", Color.rgb(239, 48, 48),
                "M1 3.4H2.22222M2.22222 3.4H12.9997M2.22222 3.4V11.8C2.22222 12.1183 2.35099 12.4235 2.5802 12.6485C2.80941 12.8736 3.12029 13 3.44444 13H10.5552C10.8794 13 11.1903 12.8736 11.4195 12.6485C11.6487 12.4235 11.7775 12.1183 11.7775 11.8V3.4M4.05556 3.4V2.2C4.05556 1.88174 4.18433 1.57652 4.41354 1.35147C4.64275 1.12643 4.95362 1 5.27778 1H8.7219C9.04605 1 9.35693 1.12643 9.58614 1.35147C9.81535 1.57652 9.94412 1.88174 9.94412 2.2V3.4M5.27778 6.4V10M8.7219 6.4V10",
                (controller, data, index) -> {
                    Form form = controller.getExternalObjects().get(controller.getSectionIndex()).get(controller.getObjectIndex()).getForm();

                    FormType formType = form.getFormTypes().get(controller.getOptionIndex());

                    if (!(formType instanceof TableFormType tableFormType)) return;

                    AtomicReference<FilterIO> filterIO = new AtomicReference<>();
                    form.getData(tableFormType.filterId, FilterIO.class).ifPresent(filterIO::set);

                    List<?> list = HibernateUtil.getObjectWithFilter(tableFormType.clazz, filterIO.get());

                    // Получаем id из rowData
                    int id = (int) data.get("id");

                    // Находим объект с совпадающим id в списке list
                    Object toDelete = list.stream()
                            .filter(obj -> {
                                try {
                                    return obj.getClass().getField("id").get(obj).equals(id);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    return false;
                                }
                            })
                            .findFirst()
                            .orElse(null);

                    // Удаляем объект, если он найден
                    if (toDelete != null) {
                        HibernateUtil.remove(toDelete);
                        controller.modelUpdate();
                    } else {
                        System.out.println("Object with id " + id + " not found in the list.");
                    }
                }
        );
        public static final TableActionButton EDIT = new TableActionButton("Редактировать", Color.WHITE, "M6.36744 2.26512H2.19276C1.87642 2.26512 1.57304 2.39078 1.34935 2.61447C1.12567 2.83816 1 3.14154 1 3.45788V11.8072C1 12.1236 1.12567 12.427 1.34935 12.6506C1.57304 12.8743 1.87642 13 2.19276 13H10.5421C10.8585 13 11.1618 12.8743 11.3855 12.6506C11.6092 12.427 11.7349 12.1236 11.7349 11.8072V7.63256M10.8403 1.37054C11.0776 1.13329 11.3994 1 11.7349 1C12.0704 1 12.3922 1.13329 12.6295 1.37054C12.8667 1.6078 13 1.92959 13 2.26512C13 2.60065 12.8667 2.92244 12.6295 3.15969L6.96382 8.82532L4.57829 9.42171L5.17468 7.03618L10.8403 1.37054Z", (controller, data, index) -> {
            if (controller.getOptionToggleGroup().getSelectedToggle() == null) return;

            controller.getOptionToggleGroup().getSelectedToggle().setSelected(false);
            controller.modelUpdate();

            // Получаем форму и текущую опцию
            Form form = controller.getExternalObjects().get(controller.getSectionIndex())
                    .get(controller.getObjectIndex()).getForm();

            if (index == -1) {
                for (int i = 0; i <= form.getOptions().size(); i++) {
                    if (form.getFormTypes().get(i) instanceof CreateFormType) {
                        index = i;
                        break;
                    }
                }
            }

            CreateFormType createFormType = (CreateFormType) form.getFormTypes().get(index);

            CreateFormType.CreateFields createFields = createFormType.getCreateFields();
            List<TextFieldWrapper> fields = createFields.fields();
            fields.forEach(field -> {
                field.setTextFieldText(data.get(field.getKey()).toString());
                if (field instanceof AllowPopup allowPopup && allowPopup.isAllowPopup()) {
                    allowPopup.setAllowPopup(false);
                }
            });

            int id = (int) data.get("id");

            if (!fields.isEmpty()) {
                // Добавляем отступы через VBox
                VBox wrapper = new VBox(controller.createRowContainer);
                wrapper.setPadding(new Insets(0, 4, 24, 4));

                controller.scrollPane.setContent(controller.createRowContainer);

                StylesUtil.add(controller.scrollPane, "add-scroll-pane");
                controller.scrollPane.setFitToWidth(true);
                controller.scrollPane.setFitToHeight(true);

                controller.createRowContainer.setPrefSize(720, 297);
                controller.createRowContainer.setAlignment(Pos.TOP_CENTER);
                controller.createRowContainer.setSpacing(17);

                controller.scrollPane.setMaxWidth(730);

                controller.addScrollPane.setAlignment(Pos.TOP_CENTER);
                controller.scrollPane.prefHeightProperty().bind(controller.addScrollPane.heightProperty());

                Controller.setAnchors(controller.addScrollPane, 181.0, 149.0, 0.0, 0.0);

                controller.registerFields(fields);

                javafx.scene.control.Button create = new javafx.scene.control.Button("Сохранить");
                StylesUtil.add(create, "save-button");
                create.setPrefSize(720, 39);
                create.setTranslateY(23);

                final int finalIndex = index == -1 ? 0 : index;

                create.setOnAction(event -> {
                    if (validateChecker(fields.toArray(new TextFieldWrapper[0]))) {
                        System.out.println("ОШИБКА: Проверьте введенные данные.");
                    } else {
                        AtomicBoolean updated = new AtomicBoolean(false);
                        HibernateUtil.update(createFields.clazz(), id, update -> {
                            update = createFields.createSupplier().get(update, fields);
                            if (update != null) updated.set(true);
                            return update;
                        });

                        if (updated.get()) {
                            // TODO
                            //controller.getOptionToggleGroup().selectToggle(controller.getOptionToggleGroup().getToggles().get(form.getCreateReturnOption().get(finalIndex)));
                            controller.getOptionToggleGroup().selectToggle(controller.getOptionToggleGroup().getToggles().get(0));
                            controller.modelUpdate();
                        }
                    }
                });
                controller.createRowContainer.getChildren().add(create);

                controller.rightSideContainer.getChildren().add(controller.addScrollPane);

            } else {
                System.out.println("Нет полей для редактирования.");
            }

        });
    }
}
