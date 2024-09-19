package com.github.Snuslyk.slib;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private ComboBox<String> sections;

    private final List<String> SECTIONS = List.of("Развлечения", "Просвещение", "Образование");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sections.setValue(SECTIONS.getFirst());
        sections.getItems().addAll(SECTIONS);
    }
}