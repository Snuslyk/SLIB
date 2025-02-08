package com.github.Snuslyk.slib.factory;

import com.github.Snuslyk.slib.Controller;
import com.github.Snuslyk.slib.HibernateUtil;
import com.sun.istack.Nullable;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static com.github.Snuslyk.slib.factory.ButtonFactory.validateChecker;

public class CreateFormType<T> extends FormType implements FormWithType<CreateFormType<?>> {

    private final List<ButtonFactory.TextFieldWrapper> fields = new ArrayList<>();
    private Supplier<T> instanceSupplier;
    CreateSupplier<T> createSupplier;
    private Class<T> clazz;
    private CreateFields<T> createFields;
    private int createReturnOption = 0;

    @Override
    public CreateFormType<T> name(String name) {
        this.name = name;
        return this;
    }

    @Override
    public CreateFormType<T> type(Class<?> clazz) {
        this.clazz = (Class<T>) clazz;
        return this;
    }

    public CreateFormType<T> instanceSupplier(Supplier<T> instanceSupplier){
        this.instanceSupplier = instanceSupplier;
        return this;
    }

    public CreateFormType<T> createSupplier(CreateSupplier<T> createSupplier){
        this.createSupplier = createSupplier;
        return this;
    }

    public CreateFormType<T> returnToOption(int createReturnOption){
        this.createReturnOption = createReturnOption;
        return this;
    }

    public CreateFormType<T> textField(String key, String name, String description, String errorSample, @Nullable String textFieldText){
        fields.add(new ButtonFactory.BasicTextField(key, name, description, errorSample, textFieldText));
        return this;
    }

    public CreateFormType<T> chooseField(String key, String name, String description, String errorSample, Supplier<ObservableList<String>> items, @Nullable String textFieldText){
        fields.add(new ButtonFactory.ChoosingTextField(key, name, description, errorSample, items, textFieldText));
        return this;
    }
    public CreateFormType<T> datePickerField(String key, String description, String errorSample, @Nullable String textFieldText){
        fields.add(new ButtonFactory.DatePickerField(key, description, errorSample, textFieldText));
        return this;
    }
    public CreateFormType<T> choiceBox(String key, String description, String errorSample, Supplier<ObservableList<String>> items, @Nullable String textFieldText){
        fields.add(new ButtonFactory.ChoiceBoxField(key, description, errorSample, items, textFieldText));
        return this;
    }
    public CreateFormType<T> multiChooseField(String key, String name, String description, String errorSample, Supplier<ObservableList<String>> items, @Nullable String textFieldText){
        fields.add(new ButtonFactory.MultiChooseField(key, name, description, errorSample, items, textFieldText));
        return this;
    }

    public CreateFields<T> getCreateFields() {
        return createFields;
    }

    public record CreateFields<T>(Class<T> clazz, List<ButtonFactory.TextFieldWrapper> fields, Supplier<T> supplier, CreateSupplier<T> createSupplier) {}
    public interface CreateSupplier<T> {
        T get(Object object, List<ButtonFactory.TextFieldWrapper> fields);
    }

    private static Controller controller;
    private static ToggleButton lastSelectedButton = null;
    private static AnchorPane rootContainer;
    private static AnchorPane rightSideContainer;
    private ToggleGroup optionToggleGroup;
    private Form form;
    private VBox createRowContainer;
    private final ScrollPane scrollPane = new ScrollPane();
    private final VBox addScrollPane = new VBox(scrollPane);

    @Override
    public void setup(SetupData data) {
        controller = data.controller();
        form = data.form();
        int optionIndex = data.optionIndex();
        rootContainer = controller.getRootContainer();
        rightSideContainer = controller.getRightSideContainer();
        createRowContainer = controller.getCreateRowContainer();
        optionToggleGroup = controller.getOptionToggleGroup();

        // Добавляем отступы через VBox
        VBox wrapper = new VBox(createRowContainer);
        wrapper.setPadding(new Insets(0, 4, 24, 4));

        scrollPane.setContent(wrapper);

        scrollPane.getStyleClass().add("add-scroll-pane");
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        createRowContainer.setPrefSize(720, 297);
        createRowContainer.setAlignment(Pos.TOP_CENTER);
        createRowContainer.setSpacing(17);

        scrollPane.setContent(wrapper);

        scrollPane.setMaxWidth(730);

        addScrollPane.setAlignment(Pos.TOP_CENTER);
        scrollPane.prefHeightProperty().bind(addScrollPane.heightProperty());

        Controller.setAnchors(addScrollPane, 181.0, 149.0, 0.0, 0.0);

        createFields = new CreateFields<T>(clazz, fields, instanceSupplier, createSupplier);
        List<ButtonFactory.TextFieldWrapper> fields = createFields.fields();
        fields.forEach(field -> field.setTextFieldText(""));

        controller.registerFields(fields);
        addSaveButton(fields, createFields.createSupplier());

        if (!rightSideContainer.getChildren().contains(addScrollPane)) {
            rightSideContainer.getChildren().add(addScrollPane);
        }
    }

    private void addSaveButton(List<ButtonFactory.TextFieldWrapper> fields, CreateSupplier<?> supplier) {
        javafx.scene.control.Button create = new javafx.scene.control.Button("Сохранить");
        create.getStyleClass().add("save-button");
        create.setPrefSize(720, 39);
        create.setTranslateY(23);

        create.setOnAction(event -> handleSaveAction(fields, supplier));
        createRowContainer.getChildren().add(create);
    }

    @Transactional
    private void handleSaveAction(List<ButtonFactory.TextFieldWrapper> fields, CreateSupplier<?> supplier) {
        if (validateChecker(fields.toArray(new ButtonFactory.TextFieldWrapper[0]))) {
            System.out.println("ОШИБКА: Проверьте введенные данные.");
        } else {
            Object object = supplier.get(createFields.supplier().get(), fields);
            if (object != null) {
                HibernateUtil.merge(object);
                optionToggleGroup.selectToggle(optionToggleGroup.getToggles().get(createReturnOption));
                controller.modelUpdate();
            }
        }
    }
}
