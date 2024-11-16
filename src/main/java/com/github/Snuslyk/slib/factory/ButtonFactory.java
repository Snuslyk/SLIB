package com.github.Snuslyk.slib.factory;

import com.sun.istack.Nullable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.stage.Popup;

public class ButtonFactory {

    // ОБЩАЯ ФОРМА КНОПОК
    public static RadioButton createLeftSideButtons(ToggleGroup group, boolean isObject, String text, boolean isSelected) {
        RadioButton button = new RadioButton(text);
        button.setCursor(Cursor.HAND);
        button.getStyleClass().add(isObject ? "object-button" : "sections-button");
        button.getStyleClass().add("radio-button-things");
        button.setToggleGroup(group);
        button.setPrefSize(320, 40);

        if (isSelected) {
            button.setSelected(true);
        }
        return button;
    }

    public static VBox createUnderlinedButtons(HBox container, String text, boolean isSelected, ToggleGroup optionToggleGroup, int fontSize, int substractHeight, int margin) {
        // Создаем новый VBox для каждой опции вместо использования копирования optionExample
        VBox optionCopy = new VBox();
        optionCopy.setSpacing(8);

        RadioButton radioButton = new RadioButton(text);
        radioButton.getStyleClass().add("radio-button-things");
        radioButton.getStyleClass().add("option-button");
        radioButton.setStyle("-fx-font-size: " + fontSize + ";");
        radioButton.setCursor(Cursor.HAND);
        radioButton.setToggleGroup(optionToggleGroup);

        if (isSelected) {
            radioButton.setSelected(true);
        }

        HBox substractBox = new HBox();
        substractBox.setPrefHeight(substractHeight);
        VBox.setMargin(substractBox, new Insets(0, 0, 0, margin));
        substractBox.getStyleClass().add("substract");

        optionCopy.getChildren().addAll(radioButton, substractBox);

        // Добавляем в контейнер опций
        container.getChildren().add(optionCopy);

        substractBox.visibleProperty().bind(radioButton.selectedProperty());

        return optionCopy;
    }

    public static VBox createBasicTextField(VBox container, String text, String descText, int descFontSize, int mainFontSize, int Hmargin, int Vmargin, int height, @Nullable String textFieldText) {
        VBox field = new VBox();
        field.setSpacing(Vmargin);

        Label descriptionText = new Label(descText);
        descriptionTextFieldOptions(descriptionText, descFontSize, Hmargin);

        TextField textField = new TextField();
        textFieldOptions(text, mainFontSize, Hmargin, height, textFieldText, textField);

        field.getChildren().addAll(descriptionText, textField);

        container.getChildren().add(field);

        return field;
    }

    public static VBox createChoosingTextField(VBox container, String text, String descText, int descFontSize, int mainFontSize, int Hmargin, int Vmargin, int height, int popUpHeight, int popUpWidth, Pane outOfBoundsContainer, @Nullable String textFieldText) {
        VBox field = new VBox();
        field.setSpacing(Vmargin);

        Label descriptionText = new Label(descText);
        descriptionTextFieldOptions(descriptionText, descFontSize, Hmargin);

        TextField textField = new TextField();
        textFieldOptions(text, mainFontSize, Hmargin, height, textFieldText, textField);

        textField.setMinWidth(popUpWidth + 1);

        ToggleButton button = new ToggleButton();
        SVGPath svgIcon = new SVGPath();
        svgIcon.setContent("M1, 7L7, 0.999999L13, 7");
        svgIcon.setFill(Color.TRANSPARENT);
        svgIcon.setStroke(Color.web("#3D3D3D"));
        svgIcon.setStrokeWidth(1.0);
        button.setGraphic(svgIcon);

        // Создаем контейнер HBox и добавляем в него TextField и кнопку
        HBox buttonContainer = new HBox(-41);
        buttonContainer.getChildren().addAll(textField, button);
        buttonContainer.setAlignment(Pos.CENTER_LEFT);

        // Устанавливаем стиль для корректного отображения
        buttonContainer.getStyleClass().add("text-field-with-button");


        ObservableList<String> items = FXCollections.observableArrayList(
                "Apple", "Banana", "Cherry", "Date", "Elderberry", "Fig", "Grape", "Grape", "Grape", "Grape", "Grape", "Grape", "Grape", "Grape", "Grape", "Grape", "Grape"
        );

        Popup suggestionsPopup = new Popup();
        suggestionsPopup.setAutoHide(true);

        ListView<String> listView = new ListView<>();
        listView.setMaxHeight(popUpHeight); // Ограничиваем высоту списка
        listView.setPrefWidth(popUpWidth); // Задаем ширину списка
        listView.setItems(items);

        suggestionsPopup.getContent().add(listView);

        button.setOnAction(event -> {
            if (button.isSelected()) {
                popupShow(height, textField, suggestionsPopup);
            } else {
                suggestionsPopup.hide();
            }
        });

        button.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            if (isNowSelected) {
                svgIcon.setContent("M1, 0L7, 6L13, 0");
            } else {
                svgIcon.setContent("M1, 7L7, 0.999999L13, 7");
            }
        });

        suggestionsPopup.showingProperty().addListener((obs, wasShowing, isNowShowing) -> {
            button.setSelected(isNowShowing);
        });

        outOfBoundsContainer.setOnMouseClicked(mouseEvent -> {
            button.setSelected(false);
            suggestionsPopup.hide();
        });

        textField.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
            String input = textField.getText();
            if (input.isEmpty()) {
                listView.setItems(items);
                return;
            }

            // Фильтрация вариантов
            ObservableList<String> filteredItems = items.filtered(item ->
                    item.toLowerCase().contains(input.toLowerCase())
            );

            if (filteredItems.isEmpty()) {
                suggestionsPopup.hide();
            } else {
                listView.setItems(filteredItems);

                popupShow(height, textField, suggestionsPopup);
            }
        });

        textField.setOnMouseClicked(event -> {
            if (textField.getText().isEmpty()) {
                listView.setItems(items);
            }

            popupShow(height, textField, suggestionsPopup);
        });

        listView.setOnMouseClicked(event -> {
            String selectedItem = listView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                textField.setText(selectedItem);
                suggestionsPopup.hide();
            }
        });

        field.getChildren().addAll(descriptionText, buttonContainer);
        container.getChildren().add(field);

        return field;
    }

    private static void popupShow(int height, TextField textField, Popup suggestionsPopup) {
        if (!suggestionsPopup.isShowing()) {
            // Вычисляем абсолютные координаты TextField
            Bounds boundsInScene = textField.localToScene(textField.getBoundsInLocal());
            double x = boundsInScene.getMinX() + textField.getScene().getWindow().getX() + 8;
            double y = boundsInScene.getMaxY() + textField.getScene().getWindow().getY() + height - 3;

            suggestionsPopup.show(textField, x, y);
        }
    }

    private static void textFieldOptions(String text, int mainFontSize, int Hmargin, int height, @Nullable String textFieldText, TextField textField) {
        textField.getStyleClass().add("textfield-form");
        textField.setPromptText(text);
        textField.setStyle("-fx-font-size: " + mainFontSize + ";");
        if (textFieldText != null) {
            textField.setText(textFieldText);
        }
        textField.setStyle("-fx-padding: 0 0 0 " + Hmargin + "px;");
        textField.setPrefHeight(height);
    }

    private static void descriptionTextFieldOptions(Label descriptionText, int descFontSize, int Hmargin) {
        descriptionText.getStyleClass().add("textfield-desc");
        descriptionText.setStyle("-fx-font-size: " + descFontSize + ";");
        descriptionText.setStyle("-fx-padding: 0 0 0 " + Hmargin + "px;");
    }

}
