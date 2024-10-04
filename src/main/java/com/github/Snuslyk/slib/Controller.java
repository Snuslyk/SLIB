package com.github.Snuslyk.slib;

import com.github.Snuslyk.slib.electives.ManageableElectives;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private HBox rootContainer;

    @FXML
    private AnchorPane popUp;

    @FXML
    private ToggleButton comboBox;

    @FXML
    private VBox objectContainer;

    @FXML
    private VBox sectionsContainer;

    private Section selectedSection;

    private final ToggleGroup objectToggleGroup = new ToggleGroup();
    private final ToggleGroup sectionToggleGroup = new ToggleGroup();

    private List<ManageableElectives> externalObjects;
    private List<ManageableElectives> externalSections;

    private static final List<Section> SECTIONS = List.of(
            new Section("Развлечения", "Мероприятия", User.class, "Виды мероприятий", User.class)
    );

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupSections();  // СЕКЦИИ
        setupObjects();  // ОБЪЕКТЫ
        update();
    }

    // ПЕРЕДАТЬ СПИСОК ОБЪЕКТОВ И СЕКЦИЙ
    // Эти методы используются для объявления кастомных объектов и секций
    public void setSectionList(List<ManageableElectives> sections) {
        this.externalSections = sections;
    }

    public void setObjectsList(List<ManageableElectives> objects) {
        this.externalObjects = objects;
    }


    // УСТАНОВКА СПИСОКОВ В ПРОГРАММУ
    private void setupSections() {
        boolean isFirst = true;

        for (ManageableElectives section : externalSections) {
            comboBox.setText(isFirst ? section.getDisplayName() : comboBox.getText()); // Текст combo box по умолчанию
            addSectionButton(section.getDisplayName(), isFirst);
            isFirst = false;
        }
    }

    private void setupObjects() {
        boolean isFirst = true;

        for (ManageableElectives object : externalObjects) {
            addObjectButton(object.getDisplayName(), isFirst);
            isFirst = false;
        }
    }


    // МЕТОДЫ СОЗДАНИЯ КНОПОК СЕКЦИЙ И ОБЪЕКТОВ
    private void addSectionButton(String text, boolean isSelected) {
        RadioButton sectionButton = createRadioButton(sectionToggleGroup, text, isSelected);
        sectionButton.setOnAction(this::handleSectionSelection);
        sectionsContainer.getChildren().add(sectionButton);
    }

    private void addObjectButton(String text, boolean isSelected) {
        RadioButton objectButton = createRadioButton(objectToggleGroup, text, isSelected);
        objectContainer.getChildren().add(objectButton);
    }

    // Устанавливает для combo-box название выбраной секции
    private void handleSectionSelection(ActionEvent event) {
        RadioButton selectedButton = (RadioButton) sectionToggleGroup.getSelectedToggle();
        if (selectedButton != null) {
            comboBox.setText(selectedButton.getText());
        }
    }


    // ОБЩАЯ ФОРМА КНОПОК
    private RadioButton createRadioButton(ToggleGroup group, String text, boolean isSelected) {
        RadioButton button = new RadioButton(text);
        button.setCursor(Cursor.HAND);
        button.getStyleClass().add(group == objectToggleGroup ? "object-button" : "sections-button");
        button.setToggleGroup(group);
        button.setPrefSize(320, 40);
        if (isSelected) {
            button.setSelected(true);
        }
        return button;
    }

    private void update() {
        rootContainer.setOnMouseClicked(event -> {
            if (!comboBox.isHover() && !popUp.isHover()) {
                comboBox.setSelected(false);
            }
        });

        popUp.visibleProperty().bind(comboBox.selectedProperty());
    }

    public Section getSelectedSection() {
        return selectedSection;
    }
}
