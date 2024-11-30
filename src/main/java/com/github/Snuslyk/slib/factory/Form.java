package com.github.Snuslyk.slib.factory;

import com.github.Snuslyk.slib.Controller;
import com.github.Snuslyk.slib.FilterIO;
import com.github.Snuslyk.slib.HibernateUtil;
import com.sun.istack.Nullable;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static com.github.Snuslyk.slib.factory.ButtonFactory.validateChecker;

public class Form {

    private final List<String> options;
    private final Type[] type;
    private final Class<?>[] tableClass;
    private final FilterIO[] filter;
    private final List<List<Column>> columns;
    private final List<List<TableActionButton>> tableButtons;
    private final List<CreateFields> createFields;
    private final List<ColorSupplier> columnColorSupplier;

    private Form(Type[] type, Class<?>[] tableClass, FilterIO[] filter, List<List<Column>> columns, List<String> options, List<List<TableActionButton>> tableButtons,
                 List<CreateFields> createFields, List<ColorSupplier> columnColorSupplier) {
        this.options = options;
        this.type = type;
        this.tableClass = tableClass;
        this.filter = filter;
        this.columns = columns;
        this.tableButtons = tableButtons;
        this.createFields = createFields;
        this.columnColorSupplier = columnColorSupplier;
    }

    public List<Object> get(int option){
        return Collections.singletonList(HibernateUtil.getObjectWithFilter(tableClass[option], filter[option]));
    }

    public Type[] getType() {
        return type;
    }
    public Class<?>[] getTableClass() {
        return tableClass;
    }
    public FilterIO[] getFilter() {
        return filter;
    }
    public List<List<Column>> getColumns() {
        return columns;
    }

    public List<String> getOptions() {
        return options;
    }

    public List<List<TableActionButton>> getTableButtons() {
        return tableButtons;
    }

    public List<CreateFields> getCreateFields() {
        return createFields;
    }

    public List<ColorSupplier> getColumnColorSupplier() {
        return columnColorSupplier;
    }

    public static class Builder {
        private static final ColorSupplier defaultColorSupplier = (controller, rowData) -> Color.WHITE;
        private static final ColumnInterface defaultColumnInterface = a -> a;

        private int optionSize;
        private int optionId = -1;
        private final List<String> options = new ArrayList<>();
        private Type[] type;
        private Class<?>[] tableClass;
        private FilterIO[] filter;
        private final List<List<Column>> columns = new ArrayList<>();
        private final List<CreateFields> createFields = new ArrayList<>();
        private final List<ColorSupplier> columnColorSupplier = new ArrayList<>();

        private List<String> usedDisplays = new ArrayList<>();

        private final List<List<TableActionButton>> tableButtons = new ArrayList<>();

        public Builder type(Type type){
            this.type[optionId] = type;
            return this;
        }
        public Builder tableClass(Class<?> clazz){
            for (int i = optionId; i < optionSize; i++){
                if (i < 0) i = 0;
                this.tableClass[i] = clazz;
            }
            return this;
        }
        public Builder tableActionButton(String display, Color color, @Nullable String svg, @Nullable TableActionButtonIO io){
            tableButtons.get(optionId).add(new TableActionButton(display, color, svg, io));
            return this;
        }
        public Builder tableActionButton(String display, Color color, @Nullable String svg){
            tableButtons.get(optionId).add(new TableActionButton(display, color, svg, null));
            return this;
        }
        public Builder tableActionButton(TableActionButton tableActionButton){
            tableButtons.get(optionId).add(tableActionButton);
            return this;
        }
        public Builder tableColumnColorSupplier(ColorSupplier supplier){
            columnColorSupplier.remove(columnColorSupplier.size()-1);
            columnColorSupplier.add(supplier);
            return this;
        }

        public <T> Builder createClass(Class<?> clazz, Supplier<T> supplier1, CreateSupplier<T> supplier){
            createFields.remove(createFields.size()-1);
            createFields.add(new CreateFields(clazz, new ArrayList<>(), supplier1,  supplier));
            return this;
        }

        public Builder createTextField(String key, String name, String description, String errorSample, @Nullable String textFieldText){
            createFields.get(optionId).fields.add(new ButtonFactory.BasicTextField(key, name, description, errorSample, textFieldText));
            return this;
        }
        public Builder createChooseField(String key, String name, String description, String errorSample, String errorSampleD, Supplier<ObservableList<String>> items, @Nullable String textFieldText){
            createFields.get(optionId).fields.add(new ButtonFactory.ChoosingTextField(key, name, description, errorSample, errorSampleD, items, textFieldText));
            return this;
        }
        public Builder createDatePickerField(String key, String description, String errorSample, @Nullable String textFieldText){
            createFields.get(optionId).fields.add(new ButtonFactory.DatePickerField(key, description, errorSample, textFieldText));
            return this;
        }
        public Builder createChoiceBox(String key, String description, String errorSample, Supplier<ObservableList<String>> items, @Nullable String textFieldText){
            createFields.get(optionId).fields.add(new ButtonFactory.ChoiceBoxField(key, description, errorSample, items, textFieldText));
            return this;
        }

        public Builder filter(FilterIO filter){
            this.filter[optionId] = filter;
            return this;
        }

        public Builder column(String displayName, String key, ColumnInterface columnInterface){
            if (usedDisplays.contains(displayName)) {
                System.out.println("Уже есть колонна с таким названием!!!");
                return this;
            }
            if (columnInterface == null) columnInterface = defaultColumnInterface;
            this.columns.get(optionId).add(new Column(displayName, key, columnInterface));
            usedDisplays.add(displayName);
            return this;
        }
        public Builder column(String displayName, String key){
            return column(displayName, key, null);
        }

        public Builder sizeOfOption(int size){
            optionSize = size;
            type = new Type[size];
            tableClass = new Class[size];
            filter = new FilterIO[size];
            return this;
        }
        public Builder option(String name) {
            usedDisplays = new ArrayList<>();
            optionId += 1;
            options.add(name);
            columns.add(new ArrayList<>());
            tableButtons.add(new ArrayList<>());
            createFields.add(null);
            columnColorSupplier.add(defaultColorSupplier);
            return this;
        }

        public Form build(){
            return new Form(type, tableClass, filter, columns, options, tableButtons, createFields, columnColorSupplier);
        }
    }

    public enum Type{
        TABLE,
        CREATE
    }

    public record Column(String displayName, String key, ColumnInterface columnInterface){}

    public interface ColumnInterface{
        Object get(Object object);
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


    public record CreateFields<T>(Class<T> clazz, List<ButtonFactory.TextFieldWrapper> fields, Supplier<T> supplier, CreateSupplier<T> createSupplier) {}

    public interface CreateSupplier<T> {
        T get(Object object, List<ButtonFactory.TextFieldWrapper> fields);
    }

    public interface ColorSupplier {
        Color get(Controller controller, Map<String, Object> rowData);
    }

    @FunctionalInterface
    public interface TableActionButtonIO {
        void handle(Controller controller, Map<String, Object> data, int extraParam);
    }

    public static class TableActionButtons{
        public static final TableActionButton DELETE = new TableActionButton("Удалить", Color.rgb(239, 48, 48),
                "M1 3.4H2.22222M2.22222 3.4H12.9997M2.22222 3.4V11.8C2.22222 12.1183 2.35099 12.4235 2.5802 12.6485C2.80941 12.8736 3.12029 13 3.44444 13H10.5552C10.8794 13 11.1903 12.8736 11.4195 12.6485C11.6487 12.4235 11.7775 12.1183 11.7775 11.8V3.4M4.05556 3.4V2.2C4.05556 1.88174 4.18433 1.57652 4.41354 1.35147C4.64275 1.12643 4.95362 1 5.27778 1H8.7219C9.04605 1 9.35693 1.12643 9.58614 1.35147C9.81535 1.57652 9.94412 1.88174 9.94412 2.2V3.4M5.27778 6.4V10M8.7219 6.4V10",
                (controller, data, index) -> {
                    Form form = controller.getExternalObjects().get(controller.getSectionIndex()).get(controller.getObjectIndex()).getForm();
                    List<?> list = HibernateUtil.getObjectWithFilter(form.getTableClass()[controller.getOptionIndex()], form.getFilter()[controller.getOptionIndex()]);
                    System.out.println("asdasdasd" + list);

                    // Получаем id из rowData
                    System.out.println(data.toString());
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
                    if (form.getType()[i] == Type.CREATE) {
                        index = i;
                        break;
                    }
                }
            }

            CreateFields createFields = form.getCreateFields().get(index);
            List<ButtonFactory.TextFieldWrapper> fields = createFields.fields();
            fields.forEach(field -> field.setTextFieldText((String) data.get(field.getKey())));

            int id = (int) data.get("id");

            if (!fields.isEmpty()) {
                controller.createRowContainer.setPrefSize(720, 297);
                controller.createRowContainer.setAlignment(Pos.TOP_CENTER);
                controller.createRowContainer.setSpacing(20);
                controller.adjustCreateRowContainerAlignment();

                controller.registerFields(fields);

                Button create = new Button("Сохранить");
                create.getStyleClass().add("save-button");
                create.setPrefSize(720, 39);
                create.setTranslateY(20);

                create.setOnAction(event -> {
                    if (validateChecker(fields.toArray(new ButtonFactory.TextFieldWrapper[0]))) {
                        System.out.println("ОШИБКА: Проверьте введенные данные.");
                    } else {
                        //Object object = createFields.createSupplier.get(HibernateUtil.getObjectById(Object.class, id), fields);
                        HibernateUtil.update(createFields.clazz, id, update -> {
                            update = createFields.createSupplier.get(update, fields);
                            return update;
                        }); // ЛИОН! ВОТ ТУТ НАДО ВМЕСТО СОХРАНЕНИЯ ДЕЛАТЬ ОБНОВЛЕНИЕ ОБЪЕКТА
                    }
                });
                controller.createRowContainer.getChildren().add(create);

                controller.rightSideContainer.getChildren().add(controller.createRowContainer);

            } else {
                System.out.println("Нет полей для редактирования.");
            }

        });
    }

}
