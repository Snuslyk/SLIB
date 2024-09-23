package com.github.Snuslyk.slib;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private ComboBox<Section> sections;

    @FXML
    private VBox objectContainer;

    private static Section pickedSection;

    public static final List<Section> SECTIONS = List.of(
            new Section("Развлечения", "Мероприятия", User.class, "Виды мироприятий", User.class)

    );

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ToggleGroup group = new ToggleGroup();

        RadioButton clonedObject = newObject(group);
        RadioButton clonedObject1 = newObject(group);
        RadioButton clonedObject2 = newObject(group);
        clonedObject.setSelected(true);
        clonedObject.setText("Мероприятия");
        clonedObject1.setText("Виды мероприятий");
        clonedObject2.setText("Виды заявок");

        sections.setValue(SECTIONS.getFirst());
        sections.getItems().addAll(SECTIONS);

        pickedSection = SECTIONS.getFirst();

        updateSection();

        sections.setOnAction(actionEvent -> {
            pickedSection = sections.getValue();

            updateSection();
        });
    }

    private void updateSection(){

    }

    private RadioButton newObject(ToggleGroup group) {
        RadioButton clone = new RadioButton();

        clone.setCursor(Cursor.HAND);
        clone.getStyleClass().add("object-button");
        clone.setToggleGroup(group);
        clone.setPrefSize(320, 40);
        objectContainer.getChildren().add(clone);

        return clone;
    }

    public static Section getPickedSection() {
        return pickedSection;
    }
}