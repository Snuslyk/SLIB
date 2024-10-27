package com.github.Snuslyk.slib;

import com.github.Snuslyk.slib.electives.Button;
import com.github.Snuslyk.slib.electives.ButtonElective;
import com.github.Snuslyk.slib.electives.ManageableElectives;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

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

    private Section selectedSection;

    private RadioButton previousSelectedOption;

    private final ToggleGroup sectionToggleGroup = new ToggleGroup();
    private final ToggleGroup objectToggleGroup = new ToggleGroup();

    private final ToggleGroup optionToggleGroup = new ToggleGroup();

    private List<List<ButtonElective>> externalObjects;
    private List<ManageableElectives> externalSections;

    private List<ManageableElectives> externalOptions;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupSections();  // СЕКЦИИ
        setupObjects(0);  // ОБЪЕКТЫ
        setupOptions();   // ОПЦИИ
        update();
    }

    // ПЕРЕДАТЬ СПИСОК ОБЪЕКТОВ И СЕКЦИЙ
    // Эти методы используются для объявления кастомных объектов и секций
    public void setSectionList(List<ManageableElectives> sections) {
        this.externalSections = sections;
    }

    public void setObjectsList(List<List<ButtonElective>> objects) {
        this.externalObjects = objects;
    }

    public void setOptionsList(List<ManageableElectives> options) {
        this.externalOptions = options;
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
            List<ButtonElective> sectionObjects = externalObjects.get(sectionIndex);
            boolean isFirst = true;
            for (ButtonElective object : sectionObjects) {
                addObjectButton(object, isFirst);
                isFirst = false;
            }
        }
    }

    private void setupOptions() {
        boolean isFirst = true;

        if (externalOptions == null) return;
        for (ManageableElectives option : externalOptions) {
            addOptionButton(option.getDisplayName(), isFirst);
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

        objectButton.setOnMouseClicked(event -> object.pressed());
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
