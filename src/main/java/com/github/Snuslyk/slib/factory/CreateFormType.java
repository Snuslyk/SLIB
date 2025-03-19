package com.github.Snuslyk.slib.factory;

import com.github.Snuslyk.slib.Controller;
import com.github.Snuslyk.slib.HibernateUtil;
import com.github.Snuslyk.slib.controls.fields.*;
import com.github.Snuslyk.slib.util.StylesUtil;
import com.sun.istack.Nullable;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class CreateFormType<T> extends FormType implements FormWithType<CreateFormType<?>> {

    protected final List<AbstractField> fields = new ArrayList<>();
    protected Supplier<T> instanceSupplier;
    protected CreateSupplier<T> createSupplier;
    protected Class<T> clazz;
    protected CreateFields<T> createFields;
    protected int createReturnOption = 0;

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

    public CreateFormType<T> textField(String key, String name, String description, @Nullable String textFieldText){
        fields.add(new BasicAbstractField(key, name, description, textFieldText));
        return this;
    }

    public CreateFormType<T> textArea(String key, String name, String description, @Nullable String textFieldText){
        fields.add(new AbstractAreaField(key, name, description, textFieldText));
        return this;
    }

    public CreateFormType<T> chooseField(String key, String name, String description, Supplier<ObservableList<String>> items, @Nullable String textFieldText){
        fields.add(new ChoosingAbstractField(key, name, description, items, textFieldText));
        return this;
    }
    public CreateFormType<T> datePickerField(String key, String description,  @Nullable String textFieldText){
        fields.add(new DatePickerField(key, description, textFieldText));
        return this;
    }
    public CreateFormType<T> choiceBox(String key, String description, Supplier<ObservableList<String>> items, @Nullable String textFieldText){
        fields.add(new ChoiceBoxField(key, description, items, textFieldText));
        return this;
    }
    public CreateFormType<T> multiChooseField(String key, String name, String description, Supplier<ObservableList<String>> items, @Nullable String textFieldText){
        fields.add(new MultiChooseField(key, name, description, items, textFieldText));
        return this;
    }
    public CreateFormType<T> multiDatePicker(String key, String description, boolean withRange, @Nullable String textFieldText){
        fields.add(new MultiDatePicker(key, description, withRange, textFieldText));
        return this;
    }

    public CreateFields<T> getCreateFields() {
        return createFields;
    }

    public CreateFormType<T> buildCreateFields(){
        // TODO
        createFields = new CreateFields<T>(clazz, fields, instanceSupplier, createSupplier);
        return this;
    }

    public record CreateFields<T>(Class<T> clazz, List<AbstractField> fields, Supplier<T> supplier, CreateSupplier<T> createSupplier) {}
    public interface CreateSupplier<T> {
        T get(Object object, List<AbstractField> fields);
    }

    protected static Controller controller;
    protected ToggleGroup optionToggleGroup;
    protected VBox createRowContainer;
    protected final ScrollPane scrollPane = new ScrollPane();
    protected final VBox addScrollPane = new VBox(scrollPane);

    @Override
    public void setup(SetupData data) {
        createFields = new CreateFields<T>(clazz, fields, instanceSupplier, createSupplier);
        controller = data.controller();
        AnchorPane rightSideContainer = controller.getRightSideContainer();
        createRowContainer = controller.getCreateRowContainer();
        optionToggleGroup = controller.getOptionToggleGroup();

        // Добавляем отступы через VBox
        VBox wrapper = new VBox(createRowContainer);
        wrapper.setPadding(new Insets(0, 4, 24, 4));

        scrollPane.setContent(wrapper);

        StylesUtil.add(scrollPane,"add-scroll-pane");
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        createRowContainer.setPrefSize(720, 297);
        createRowContainer.setAlignment(Pos.TOP_CENTER);
        createRowContainer.setSpacing(17);

        scrollPane.setContent(wrapper);

        scrollPane.setMaxWidth(730);

        addScrollPane.setAlignment(Pos.TOP_CENTER);
        scrollPane.prefHeightProperty().bind(addScrollPane.heightProperty());

        Controller.setAnchors(addScrollPane, 88.0, 92.0, 0.0, 0.0);

        List<AbstractField> fields = createFields.fields();
        //fields.forEach(field -> field.setTextFieldText("")); чо? зачем?

        controller.registerFields(fields);
        addSaveButton(fields, createFields.createSupplier());

        if (!rightSideContainer.getChildren().contains(addScrollPane)) {
            rightSideContainer.getChildren().add(addScrollPane);
        }
    }

    protected void addSaveButton(List<AbstractField> fields, CreateSupplier<?> supplier) {
        javafx.scene.control.Button create = new javafx.scene.control.Button("Сохранить");
        StylesUtil.add(create, "save-button");
        create.setPrefSize(720, 39);
        create.setTranslateY(23);

        create.setOnAction(event -> handleSaveAction(fields, supplier));
        createRowContainer.getChildren().add(create);
    }

    @Transactional
    protected void handleSaveAction(List<AbstractField> fields, CreateSupplier<?> supplier) {
        Object object = supplier.get(createFields.supplier().get(), fields);

        if (object == null) return;

        HibernateUtil.merge(object);
        optionToggleGroup.selectToggle(optionToggleGroup.getToggles().get(createReturnOption));
        controller.modelUpdate();
    }
}
