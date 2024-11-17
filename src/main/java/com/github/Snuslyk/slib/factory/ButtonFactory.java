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

import java.util.List;
import java.util.Objects;

import static com.github.Snuslyk.slib.factory.ButtonFactory.validateTextField;

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

    public static class BasicTextField implements TextFieldWrapper {

        private final VBox field;
        private final TextField textField;
        private String error = null;
        private final Label errorLabel = new Label();
        private Boolean isError = false;
        private final int descFontSize;
        private final int Hmargin;
        private final String errorSample;

        public BasicTextField(VBox container, String text, String descText, String errorSample, int descFontSize, int mainFontSize, int Hmargin, int Vmargin, int height, @Nullable String textFieldText) {
            this.errorSample = errorSample;
            this.descFontSize = descFontSize;
            this.Hmargin = Hmargin;
            field = new VBox();
            field.setSpacing(Vmargin);

            Label descriptionLabel = new Label(descText);
            descriptionTextFieldOptions(descriptionLabel, descFontSize, Hmargin);

            textField = new TextField();
            textFieldOptions(text, mainFontSize, Hmargin, height, textFieldText, textField);

            field.getChildren().addAll(descriptionLabel, textField);

            container.getChildren().add(field);
        }

        public Boolean getError() {
            return isError;
        }

        public String getErrorText() {
            return error;
        }

        public TextField getTextField() {
            return textField;
        }

        public String getTextFieldText() {
            return textField.getText();
        }

        public void setError(String error) {
            this.error = error;

            if (error != null && !error.isEmpty()) {
                isError = true;
                errorLabel.setText(error);
                errorLabel.setTextFill(Color.rgb(239, 48, 48));
                descriptionTextFieldOptions(errorLabel, descFontSize, Hmargin);
                if (!field.getChildren().contains(errorLabel)) {
                    field.getChildren().add(errorLabel);
                }
            } else {
                isError = false;
                field.getChildren().remove(errorLabel);
            }
        }

        public void clearError() {
            setError(null);
        }

        public VBox getContainer() {
            return field;
        }

        public String getTextFieldValue() {
            return textField.getText();
        }

        public void setTextFieldValue(String value) {
            textField.setText(value);
        }
    }

    public static class ChoosingTextField implements TextFieldWrapper {
        private final VBox field;
        private final TextField textField;
        private final ToggleButton button;
        private final Popup suggestionsPopup;
        private final ListView<String> listView;
        private final ObservableList<String> items;
        private String error = null;
        private final String errorSample;
        private final String errorSampleD;
        private final Label errorLabel = new Label();
        private boolean isError = false;
        private final int descFontSize;
        private final int Hmargin;

        public ChoosingTextField(VBox container, String text, String descText, String errorSample, String errorSampleD,
                                 int descFontSize, int mainFontSize, int Hmargin, int Vmargin, int height, int popUpHeight, int popUpWidth,
                                 Pane outOfBoundsContainer, ObservableList<String> items, @Nullable String textFieldText) {

            this.errorSample = errorSample;
            this.errorSampleD = errorSampleD;
            this.descFontSize = descFontSize;
            this.Hmargin = Hmargin;
            this.items = items;
            field = new VBox();
            field.setSpacing(Vmargin);

            // Description label
            Label descriptionText = new Label(descText);
            descriptionTextFieldOptions(descriptionText, descFontSize, Hmargin);

            // TextField
            textField = new TextField();
            textFieldOptions(text, mainFontSize, Hmargin, height, textFieldText, textField);
            textField.setMinWidth(popUpWidth + 1);

            // Button and SVG Icon
            button = new ToggleButton();
            SVGPath svgIcon = new SVGPath();
            svgIcon.setContent("M1, 7L7, 0.999999L13, 7");
            svgIcon.setFill(Color.TRANSPARENT);
            svgIcon.setStroke(Color.web("#3D3D3D"));
            svgIcon.setStrokeWidth(1.0);
            button.setGraphic(svgIcon);

            // Button container
            HBox buttonContainer = new HBox(-41);
            buttonContainer.getChildren().addAll(textField, button);
            buttonContainer.setAlignment(Pos.CENTER_LEFT);
            buttonContainer.getStyleClass().add("text-field-with-button");

            // Popup
            suggestionsPopup = new Popup();
            suggestionsPopup.setAutoHide(true);
            listView = new ListView<>();
            listView.setMaxHeight(popUpHeight);
            listView.setPrefWidth(popUpWidth);
            listView.setItems(items);
            suggestionsPopup.getContent().add(listView);

            // Button action listener
            button.setOnAction(event -> {
                if (button.isSelected()) {
                    popupShow(height, textField, suggestionsPopup);
                } else {
                    suggestionsPopup.hide();
                }
            });

            // Toggle button state change listener
            button.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                if (isNowSelected) {
                    svgIcon.setContent("M1, 0L7, 6L13, 0");
                } else {
                    svgIcon.setContent("M1, 7L7, 0.999999L13, 7");
                }
            });

            // Popup visibility listener
            suggestionsPopup.showingProperty().addListener((obs, wasShowing, isNowShowing) -> {
                button.setSelected(isNowShowing);
            });

            // Out of bounds click listener
            outOfBoundsContainer.setOnMouseClicked(mouseEvent -> {
                button.setSelected(false);
                suggestionsPopup.hide();
            });

            // Key released filter
            textField.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
                String input = textField.getText();
                if (input.isEmpty()) {
                    listView.setItems(items);
                    return;
                }

                ObservableList<String> filteredItems = items.filtered(item -> item.toLowerCase().contains(input.toLowerCase()));

                if (filteredItems.isEmpty()) {
                    suggestionsPopup.hide();
                } else {
                    listView.setItems(filteredItems);
                    popupShow(height, textField, suggestionsPopup);
                }
            });

            // TextField click listener
            textField.setOnMouseClicked(event -> {
                if (textField.getText().isEmpty()) {
                    listView.setItems(items);
                }
                popupShow(height, textField, suggestionsPopup);
            });

            // ListView click listener
            listView.setOnMouseClicked(event -> {
                String selectedItem = listView.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    textField.setText(selectedItem);
                    suggestionsPopup.hide();
                }
            });

            field.getChildren().addAll(descriptionText, buttonContainer);
            container.getChildren().add(field);
        }

        public Boolean getError() {
            return isError;
        }

        public String getErrorText() {
            return error;
        }

        public void setError(@Nullable String error) {
            this.error = error;

            if (error != null && !error.isEmpty()) {
                isError = true;
                errorLabel.setText(error);
                errorLabel.setTextFill(Color.rgb(239, 48, 48));
                descriptionTextFieldOptions(errorLabel, descFontSize, Hmargin);
                if (!field.getChildren().contains(errorLabel)) {
                    field.getChildren().add(errorLabel);
                }
            } else {
                isError = false;
                field.getChildren().remove(errorLabel);
            }
        }

        public void clearError() {
            setError(null);
        }

        public TextField getTextField() {
            return textField;
        }

        private void popupShow(int height, TextField textField, Popup suggestionsPopup) {
            if (!suggestionsPopup.isShowing()) {
                Bounds boundsInScene = textField.localToScene(textField.getBoundsInLocal());
                double x = boundsInScene.getMinX() + textField.getScene().getWindow().getX() + 8;
                double y = boundsInScene.getMaxY() + textField.getScene().getWindow().getY() + height - 3;
                suggestionsPopup.show(textField, x, y);
            }
        }

        // Метод для получения текста из TextField
        public String getTextFieldText() {
            return textField.getText();
        }

        // Метод для установки текста в TextField
        public void setTextFieldText(String text) {
            textField.setText(text);
        }

        // Метод для получения списка элементов
        public ObservableList<String> getItems() {
            return items;
        }

        // Метод для изменения элементов в списке
        public void setItems(ObservableList<String> newItems) {
            items.setAll(newItems);
            listView.setItems(items);
        }
    }

    public static Boolean validateChecker(TextFieldWrapper... textFieldWrappers) {
        boolean hasErrors = false;

        for (TextFieldWrapper textFieldWrapper : textFieldWrappers) {
            if (textFieldWrapper instanceof BasicTextField) {
                validateTextField(textFieldWrapper, ((BasicTextField) textFieldWrapper).errorSample, null, null);
            } else if (textFieldWrapper instanceof ChoosingTextField) {
                validateTextField(textFieldWrapper, ((ChoosingTextField) textFieldWrapper).errorSample, ((ChoosingTextField) textFieldWrapper).errorSampleD, ((ChoosingTextField) textFieldWrapper).getItems());
            }

            if (textFieldWrapper.getError()) {
                hasErrors = true;
            }
        }

        return hasErrors;
    }

    public static void validateChoosingTextField(TextFieldWrapper textFieldWrapper, @Nullable String errorMessage, List<String> validItems) {
        boolean isValid = false;

        for (String item : validItems) {
            if (Objects.equals(textFieldWrapper.getTextFieldText(), item)) {
                isValid = true;
                break;
            }
        }

        if (isValid) {
            textFieldWrapper.clearError();
        } else {
            textFieldWrapper.setError(errorMessage);
        }
    }

    // Универсальный метод для проверки текста
    public static void validateTextField(TextFieldWrapper textFieldWrapper, @Nullable String errorMessage, @Nullable String secondErrorMessage, @Nullable List<String> validItems) {
        String input = textFieldWrapper.getTextFieldText();

        // Если текст пустой, устанавливаем ошибку
        if (input.isEmpty()) {
            textFieldWrapper.setError(errorMessage);
            return;
        }

        // Если список валидных элементов не задан, очищаем ошибку
        if (validItems == null) {
            textFieldWrapper.clearError();
            return;
        }

        // Проверяем, соответствует ли введенный текст допустимым значениям
        if (!validItems.contains(input)) {
            textFieldWrapper.setError(secondErrorMessage);
        } else {
            textFieldWrapper.clearError();
        }
    }


    // Интерфейс-обёртка для текстовых полей
    public interface TextFieldWrapper {
        TextField getTextField();
        String getTextFieldText();
        void setError(String message);
        void clearError();
        String getErrorText();
        Boolean getError();
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
