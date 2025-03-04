package com.github.Snuslyk.slib;

import com.github.Snuslyk.slib.controls.buttons.UnderlinedButton;
import com.github.Snuslyk.slib.controls.fields.ChoosingTextField;
import com.github.Snuslyk.slib.controls.fields.MultiChooseField;
import com.github.Snuslyk.slib.electives.Button;
import com.github.Snuslyk.slib.electives.ButtonElective;
import com.github.Snuslyk.slib.electives.ManageableElectives;
import com.github.Snuslyk.slib.factory.Form;
import com.github.Snuslyk.slib.factory.FormType;
import com.github.Snuslyk.slib.factory.SetupData;
import com.github.Snuslyk.slib.factory.TextFieldWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static com.github.Snuslyk.slib.factory.ButtonFactory.createLeftSideButtons;

public class Controller implements Initializable {

    private static final Controller instance = new Controller();

    public static Controller instance(){
        return instance;
    }

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
    public AnchorPane getRootContainer(){
        return rootContainer;
    }

    @FXML
    private AnchorPane popUp;

    @FXML
    private ToggleButton comboBox;

    @FXML
    private VBox objectContainer;

    @FXML
    private VBox sectionsContainer;

    @FXML
    public AnchorPane rightSideContainer;
    public AnchorPane getRightSideContainer(){
        return rightSideContainer;
    }
    @FXML
    private AnchorPane leftSideContainer;

    @FXML
    private Label objectRightSideText;

    public final VBox createRowContainer = new VBox();

    public VBox getCreateRowContainer() {
        return createRowContainer;
    }

    private Section selectedSection;

    private RadioButton previousSelectedOption;

    private final ToggleGroup sectionToggleGroup = new ToggleGroup();
    private final ToggleGroup objectToggleGroup = new ToggleGroup();

    private final ToggleGroup optionToggleGroup = new ToggleGroup();

    private List<List<Button>> externalObjects;
    private List<ManageableElectives> externalSections;

    private int pickedSectionIndex = 0;

    private String idColumnsDisplay;

    private final VBox tableWithFiltersContainer = new VBox();
    public VBox getTableWithFiltersContainer(){
        return tableWithFiltersContainer;
    }

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
        UnderlinedButton Box = new UnderlinedButton(optionsContainer, optionToggleGroup, 20, 4, 9);
        Box.createUnderlinedButton(text, isSelected);

        RadioButton radioButton = (RadioButton) Box.getButton().getChildren().get(0);

        radioButton.setOnAction(event -> {
            modelUpdate();
        });
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

        modelUpdate();
    }

    private void handleObjectSelection(ActionEvent event){
        RadioButton selectedButton = (RadioButton) objectToggleGroup.getSelectedToggle();

        if (selectedButton == null) return;

        int objectIndex = objectContainer.getChildren().indexOf(selectedButton);
        setupOptions(pickedSectionIndex, objectIndex);

        modelUpdate();
    }

    private void update() {
        rootContainer.setOnMouseClicked(event -> {
            if (!comboBox.isHover() && !popUp.isHover()) {
                comboBox.setSelected(false);
            }
        });

        popUp.visibleProperty().bind(comboBox.selectedProperty());

        modelUpdate(); // обнавляю модели при первом старте
    }

    public void modelUpdate() {
        clearRightSideContainer();

        if (getOptionToggleGroup().getSelectedToggle() == null) return;
        int sectionIndex = getSectionIndex();
        int objectIndex = getObjectIndex();
        int optionIndex = getOptionIndex();

        objectRightSideText.setText(((RadioButton) objectToggleGroup.getSelectedToggle()).getText());

        Form form = externalObjects.get(sectionIndex).get(objectIndex).getForm();
        FormType formType = form.getFormTypes().get(optionIndex);

        formType.setup(new SetupData(this, sectionIndex, objectIndex, optionIndex, form));

        System.out.println(rightSideContainer.getChildren());
        System.out.println(((Pane)rightSideContainer.getChildren().get(0)).getChildren());
    }

    private void clearRightSideContainer() {
        createRowContainer.getChildren().clear();
        rightSideContainer.getChildren().clear();
    }

    public final ScrollPane scrollPane = new ScrollPane();
    public final VBox addScrollPane = new VBox(scrollPane);

    public void registerFields(List<TextFieldWrapper> fields) {
        for (TextFieldWrapper field : fields) {
            if (field instanceof ChoosingTextField choosingTextField) {
                choosingTextField.register(createRowContainer, rootContainer);
            } else if (field instanceof MultiChooseField multiChooseField) {
                multiChooseField.register(createRowContainer, rootContainer);
            } else {
                field.register(createRowContainer);
            }
        }
    }


    public static void setAnchors(Node node, double top, double bottom, double left, double right) {
        AnchorPane.setTopAnchor(node, top);
        AnchorPane.setBottomAnchor(node, bottom);
        AnchorPane.setLeftAnchor(node, left);
        AnchorPane.setRightAnchor(node, right);
    }

    public static void alignHorizontally(Node node, double containerWidth, double nodeWidth) {
        if (containerWidth > nodeWidth) {
            double horizontalCenter = (containerWidth - nodeWidth) / 2;
            AnchorPane.setLeftAnchor(node, horizontalCenter);
            AnchorPane.setRightAnchor(node, horizontalCenter);
        } else {
            AnchorPane.setLeftAnchor(node, 0.0);
            AnchorPane.setRightAnchor(node, 0.0);
        }
    }

    public int getSectionIndex() {
        return sectionsContainer.getChildren().indexOf((RadioButton) sectionToggleGroup.getSelectedToggle());
    }
    public int getObjectIndex() {
        return objectContainer.getChildren().indexOf((RadioButton) objectToggleGroup.getSelectedToggle());
    }
    public int getOptionIndex() {
        int index = optionsContainer.getChildren().indexOf(((RadioButton) optionToggleGroup.getSelectedToggle()).getParent());
        return index == -1 ? 0 : index;
    }

    public ToggleGroup getOptionToggleGroup() {
        return optionToggleGroup;
    }

    public List<List<Button>> getExternalObjects() {
        return externalObjects;
    }

    public Section getSelectedSection() {
        return selectedSection;
    }
    public String getIdColumnsDisplay() {
        return idColumnsDisplay;
    }
}
