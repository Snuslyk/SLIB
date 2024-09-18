package com.github.Snuslyk.slib;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
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

        sections.setOnShowing(event -> {
            sections.setStyle("-fx-background-color: #242424; " +
                    "-fx-border-color: linear-gradient(from 0% 0% to 100% 0%, rgba(255, 152, 88, 1), rgba(250, 113, 113, 1));");
        });

        sections.setOnHiding(event -> {
            sections.setStyle("");
        });

        sections.setValue(SECTIONS.getFirst());
        sections.getItems().addAll(SECTIONS);
    }
}