package com.github.Snuslyk.slib;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private ComboBox<Section> sections;

    private static Section pickedSection;

    public static final List<Section> SECTIONS = List.of(
            new Section("Развлечения", "Мероприятия", User.class, "Виды мироприятий", User.class)

    );

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sections.setValue(SECTIONS.get(0));

        sections.getItems().addAll(SECTIONS);

        sections.setOnAction(actionEvent -> {
            pickedSection = sections.getValue();

            get

        });
    }

    public static Section getPickedSection() {
        return pickedSection;
    }
}