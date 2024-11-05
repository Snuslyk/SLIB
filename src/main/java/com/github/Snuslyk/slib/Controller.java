package com.github.Snuslyk.slib;

import com.github.Snuslyk.slib.electives.Button;
import com.github.Snuslyk.slib.electives.ButtonElective;
import com.github.Snuslyk.slib.electives.ManageableElectives;
import com.github.Snuslyk.slib.factory.Form;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.IntegerExpression;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.net.URL;
import java.util.*;

import static com.github.Snuslyk.slib.factory.ButtonFactory.createLeftSideButtons;
import static com.github.Snuslyk.slib.factory.ButtonFactory.createOptionButtons;

public class Controller implements Initializable {

    @FXML
    private VBox optionExample;

    @FXML
    private HBox optionsContainer;

    @FXML
    private VBox optionContainer;

    @FXML
    private HBox substract;

    @FXML
    private AnchorPane rootContainer;

    @FXML
    private AnchorPane popUp;

    @FXML
    private ToggleButton comboBox;

    @FXML
    private VBox objectContainer;

    @FXML
    private VBox sectionsContainer;

    @FXML
    private AnchorPane rightSideContainer;

    private TableView<Map<String, Object>> tableView = new TableView<>();

    private Section selectedSection;

    private RadioButton previousSelectedOption;

    private final ToggleGroup sectionToggleGroup = new ToggleGroup();
    private final ToggleGroup objectToggleGroup = new ToggleGroup();

    private final ToggleGroup optionToggleGroup = new ToggleGroup();

    private List<List<Button>> externalObjects;
    private List<ManageableElectives> externalSections;

    private int pickedSectionIndex = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupSections();  // СЕКЦИИ
        setupObjects(0);  // ОБЪЕКТЫ
        setupOptions(0,0);   // ОПЦИИ
        update();
    }

    // ПЕРЕДАТЬ СПИСОК ОБЪЕКТОВ И СЕКЦИЙ
    // Эти методы используются для объявления кастомных объектов и секций
    public void setSectionList(List<ManageableElectives> sections) {
        this.externalSections = sections;
    }

    public void setObjectsList(List<List<Button>> objects) {
        this.externalObjects = objects;
    }


    // УСТАНОВКА СПИСОКОВ В ПРОГРАММУ
    private void setupSections() {
        boolean isFirst = true;

        if (externalSections == null) return;
        for (ManageableElectives section : externalSections) {
            comboBox.setText(isFirst ? section.getDisplayName() : comboBox.getText()); // Текст combo box по умолчанию
            addSectionButton(section.getDisplayName(), isFirst);
            isFirst = false;
        }

    }

    private void setupObjects(int sectionIndex) {
        objectContainer.getChildren().clear();

        if (externalObjects != null && sectionIndex < externalObjects.size()) {
            List<Button> sectionObjects = externalObjects.get(sectionIndex);
            boolean isFirst = true;
            for (ButtonElective object : sectionObjects) {
                addObjectButton(object, isFirst);
                isFirst = false;
            }
        }
    }

    private void setupOptions(int sectionIndex, int objectIndex) {
        boolean isFirst = true;

        if (externalObjects == null) return;
        if (optionsContainer != null) {
            optionsContainer.getChildren().clear();
        }
        for (String option : externalObjects.get(sectionIndex).get(objectIndex).getForm().getOptions()) {
            addOptionButton(option, isFirst);
            isFirst = false;
        }
    }

    private <T> void setupTableColumns(int sectionIndex, int objectIndex, int optionIndex, TableView<Map<String, Object>> tableView, Class<T> dataClass) {
        if (externalObjects == null || tableView == null) return;

        tableView.getColumns().clear();

        List<List<Form.Column>> columnsList = externalObjects.get(sectionIndex)
                .get(objectIndex)
                .getForm()
                .getColumns();

        System.out.println(columnsList);

        Form form = externalObjects.get(sectionIndex)
                .get(objectIndex)
                .getForm();

        List<Form.Column> columns = form
                .getColumns()
                .get(optionIndex);

        for (Form.Column column : columns) {
            TableColumn<Map<String, Object>, String> tableColumn = new TableColumn<>(column.displayName());
            tableColumn.setResizable(false);
            tableColumn.setReorderable(false);

            // Используем лямбду для установки значения ячейки
            tableColumn.setCellValueFactory(cellData -> {
                Map<String, Object> rowData = cellData.getValue();
                Object cellValue = rowData.get(column.displayName()); // column.name() - ключ в Map
                return new SimpleStringProperty(cellValue != null ? cellValue.toString() : "");
            });

            tableView.widthProperty().addListener((obs, oldWidth, newWidth) -> {
                double totalWidth = newWidth.doubleValue();
                double columnWidth = totalWidth / tableView.getColumns().size(); // Распределяем ширину
                for (TableColumn<?, ?> columnA : tableView.getColumns()) {
                    columnA.setPrefWidth(columnWidth);
                }
            });

            tableView.getColumns().add(tableColumn);
        }

        // Пример добавления данных
        ObservableList<Map<String, Object>> data = FXCollections.observableArrayList();

        List<Map<String, Object>> rows = new ArrayList<>();

        List<?> list = HibernateUtil.getObjectWithFilter(form.getTableClass()[optionIndex], form.getFilter()[optionIndex]);

        try {
            for (Object object : list) {
                Map<String, Object> row = new HashMap<>();
                for (Form.Column column : columns)
                    row.put(column.displayName(), object.getClass().getField(column.key()).get(object));
                rows.add(row);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        // Добавляем строки данных в ObservableList
        data.addAll(rows);

        // Устанавливаем данные в таблицу
        tableView.setItems(data);
    }




    // МЕТОДЫ СОЗДАНИЯ КНОПОК СЕКЦИЙ И ОБЪЕКТОВ
    private void addSectionButton(String text, boolean isSelected) {
        RadioButton sectionButton = createLeftSideButtons(sectionToggleGroup, false, text, isSelected);
        sectionButton.setOnAction(this::handleSectionSelection);
        sectionsContainer.getChildren().add(sectionButton);
    }

    private void addObjectButton(ButtonElective object, boolean isSelected) {
        RadioButton objectButton = createLeftSideButtons(objectToggleGroup, true, object.getDisplayName(), isSelected);
        objectContainer.getChildren().add(objectButton);

        objectButton.setOnAction(this::handleObjectSelection);
        // Установка действия кнопки
    }

    // МЕТОД СОЗДАНИЯ КНОПКИ ОПЦИЙ
    private void addOptionButton(String text, boolean isSelected) {
        // Создаем новый VBox для каждой опции вместо использования копирования optionExample
        VBox optionCopy = new VBox();
        optionCopy.setSpacing(8);

        RadioButton radioButton = createOptionButtons(optionToggleGroup, text, isSelected);

        if (isSelected) {
            previousSelectedOption = radioButton;
        }

        HBox substractBox = new HBox();
        substractBox.setPrefHeight(4);
        VBox.setMargin(substractBox, new Insets(0, 0, 0, 9));
        substractBox.getStyleClass().add("substract");

        if (!isSelected) {
            substractBox.setVisible(false);
        }

        radioButton.setOnAction(this::handleOptionSelection);

        optionCopy.getChildren().addAll(radioButton, substractBox);

        // Добавляем в контейнер опций
        optionsContainer.getChildren().add(optionCopy);
    }

    // Устанавливает для combo-box название выбраной секции
    private void handleSectionSelection(ActionEvent event) {
        RadioButton selectedButton = (RadioButton) sectionToggleGroup.getSelectedToggle();

        if (selectedButton == null) return;

        comboBox.setText(selectedButton.getText());
        int sectionIndex = sectionsContainer.getChildren().indexOf(selectedButton);
        setupObjects(sectionIndex);

        comboBox.setSelected(false);
        pickedSectionIndex = sectionIndex;

        // Выбираем первый объект и обновляем его опции
        if (!objectContainer.getChildren().isEmpty()) {
            RadioButton firstObjectButton = (RadioButton) objectContainer.getChildren().get(0);
            firstObjectButton.setSelected(true);

            int objectIndex = objectContainer.getChildren().indexOf(firstObjectButton);
            setupOptions(pickedSectionIndex, objectIndex);
        }
    }

    // Устанавливает substract для выбранной опции
    private void handleOptionSelection(ActionEvent event) {
        RadioButton selectedButton = (RadioButton) optionToggleGroup.getSelectedToggle();

        if (selectedButton == null) return;

        // Скрываем substract у предыдущей выбранной кнопки, если она существует
        if (previousSelectedOption != null && previousSelectedOption != selectedButton) {
            VBox previousContainer = (VBox) previousSelectedOption.getParent();
            HBox previousSubstract = (HBox) previousContainer.getChildren().get(1);
            previousSubstract.setVisible(false);
        }

        // Делаем substract текущей кнопки видимым
        VBox container = (VBox) selectedButton.getParent();
        HBox substract = (HBox) container.getChildren().get(1);
        substract.setVisible(true);

        // Обновляем previousSelectedOption на текущую выбранную кнопку
        previousSelectedOption = selectedButton;

        int optionIndex = optionsContainer.getChildren().indexOf(selectedButton.getParent());
        int sectionIndex = sectionsContainer.getChildren().indexOf((RadioButton) sectionToggleGroup.getSelectedToggle());
        int objectIndex = objectContainer.getChildren().indexOf((RadioButton) objectToggleGroup.getSelectedToggle());

        System.out.println("optionIndex: " + optionIndex);
        System.out.println("sectionIndex: " + sectionIndex);
        System.out.println("objectIndex: " + objectIndex);

        tableView.setPrefWidth(200);
        tableView.setPrefHeight(297);
        AnchorPane.setTopAnchor(tableView, 173.0);
        AnchorPane.setBottomAnchor(tableView, 40.0);
        AnchorPane.setLeftAnchor(tableView, 0.0);
        AnchorPane.setRightAnchor(tableView, 0.0);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.getStyleClass().add("tableD");

        if (optionIndex == 0) {
            Form form = externalObjects.get(sectionIndex)
                    .get(objectIndex)
                    .getForm();
            setupTableColumns(sectionIndex, objectIndex, optionIndex, tableView, form.getClass());
            rightSideContainer.getChildren().add(tableView);
        } else {
            rightSideContainer.getChildren().remove(tableView);
        }

    }

    private void handleObjectSelection(ActionEvent event){
        RadioButton selectedButton = (RadioButton) objectToggleGroup.getSelectedToggle();

        if (selectedButton == null) return;

        int objectIndex = objectContainer.getChildren().indexOf(selectedButton);
        setupOptions(pickedSectionIndex, objectIndex);
    }

    private void update() {
        rootContainer.setOnMouseClicked(event -> {
            if (!comboBox.isHover() && !popUp.isHover()) {
                comboBox.setSelected(false);
            }
        });

        objectContainer.getChildren().forEach(node -> {
            node.setOnMouseClicked(event -> {
                System.out.println("aa");
            });
        });

        popUp.visibleProperty().bind(comboBox.selectedProperty());
    }

    public Section getSelectedSection() {
        return selectedSection;
    }
}
