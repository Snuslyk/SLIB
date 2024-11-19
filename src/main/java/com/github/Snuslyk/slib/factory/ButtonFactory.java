package com.github.Snuslyk.slib.factory;

import com.sun.istack.Nullable;
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
import javafx.stage.Popup;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

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

        // Параметры дизайна с значениями по умолчанию
        private static final int descFontSize = 20;
        private static final int mainFontSize = 20;
        private static final int Hmargin = 20;
        private static final int Vmargin = 8;
        private static final int height = 40;

        private VBox field;
        private TextField textField;
        private final Label errorLabel = new Label();
        private Boolean isError = false;
        private final String text, descText, textFieldText, key;

        private final String errorSample;

        public BasicTextField(String key, String text, String descText, String errorSample, @Nullable String textFieldText) {
            this.key = key;
            this.errorSample = errorSample;
            this.text = text;
            this.descText = descText;
            this.textFieldText = textFieldText;
        }

        public String getKey() {
            return key;
        }

        // Методы для работы с ошибками
        public Boolean getError() {
            return isError;
        }

        @Override
        public void register(VBox container) {
            field = new VBox();
            field.setSpacing(Vmargin);

            // Label с описанием
            Label descriptionLabel = new Label(descText);
            descriptionTextFieldOptions(descriptionLabel, descFontSize, Hmargin);

            // TextField
            textField = new TextField();
            textFieldOptions(text, mainFontSize, Hmargin, height, textFieldText, textField);

            // Добавление элементов в контейнер
            field.getChildren().addAll(descriptionLabel, textField);
            container.getChildren().add(field);
        }

        public void setError(String error) {
            errorSetter(error, isError, errorLabel, field, descFontSize, Hmargin);
        }

        public void clearError() {
            setError(null);
        }

        // Метод для получения текста из TextField
        public String getTextFieldText() {
            return textField.getText();
        }
    }

    public static class ChoosingTextField implements TextFieldWrapper {
        // Параметры дизайна с значениями по умолчанию
        private static final int descFontSize = 20;
        private static final int mainFontSize = 20;
        private static final int Hmargin = 20;
        private static final int Vmargin = 8;
        private static final int height = 40;
        private static final int popUpHeight = 99;
        private static final int popUpWidth = 719;

        private VBox field;
        private TextField textField;
        private ToggleButton button;
        private Popup suggestionsPopup;
        private ListView<String> listView;
        private final ObservableList<String> items;
        private final String errorSample;
        private final String errorSampleD;
        private final Label errorLabel = new Label();
        private final String text, descText, textFieldText, key;
        private Pane outOfBoundsContainer;
        private boolean isError = false;

        public ChoosingTextField(String key, String text, String descText, String errorSample, String errorSampleD, ObservableList<String> items, @Nullable String textFieldText) {
            this.errorSample = errorSample;
            this.errorSampleD = errorSampleD;
            this.items = items;

            this.text = text;
            this.descText = descText;
            this.textFieldText = textFieldText;
            this.key = key;
        }
        public void register(VBox container, Pane outOfBoundsContainer){
            this.outOfBoundsContainer = outOfBoundsContainer;
            register(container);
        }
        @Override
        public void register(VBox container) {
            field = new VBox();
            field.setSpacing(Vmargin);

            // Description label
            Label descriptionText = new Label(descText);
            descriptionTextFieldOptions(descriptionText, descFontSize, Hmargin);

            // TextField
            textField = new TextField();
            textFieldOptions(text, mainFontSize, Hmargin, height, textFieldText, textField);
            textField.setPrefWidth(popUpWidth + 1);

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
            listView.setItems(items);
            suggestionsPopup.getContent().add(listView);

            textField.prefWidthProperty().bind(container.widthProperty());
            listView.prefWidthProperty().bind(container.widthProperty());

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

                ObservableList<String> filteredItems = items.filtered(item -> item.toLowerCase().contains(input.toLowerCase()));

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
        }

        public String getKey() {
            return key;
        }

        public Boolean getError() {
            return isError;
        }

        public void setError(@Nullable String error) {
            errorSetter(error, isError, errorLabel, field, descFontSize, Hmargin);
        }

        public void clearError() {
            setError(null);
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

        // Метод для получения списка элементов
        public ObservableList<String> getItems() {
            return items;
        }
    }

    public static class DatePickerField implements TextFieldWrapper {

        private static final int descFontSize = 20;
        private static final int Hmargin = 20;
        private static final int mainFontSize = 20;
        private static final int Vmargin = 8;
        private static final int height = 40;

        private VBox field;
        private DatePicker datePicker;
        private final boolean isError = false;
        private final Label errorLabel = new Label();
        private final String errorSample, descText, textFieldText, key;


        public DatePickerField(String key, String descText, String errorSample, @Nullable String textFieldText) {
            this.key = key;
            this.errorSample = errorSample;
            this.descText = descText;
            this.textFieldText = textFieldText;
        }

        @Override
        public void register(VBox container) {
            field = new VBox();
            field.setSpacing(Vmargin);
            datePicker = new DatePicker();

            datePicker.setPrefHeight(height);
            datePicker.setStyle("-fx-font-size: " + mainFontSize + "px;");
            datePicker.getEditor().setStyle("-fx-padding: 0 0 0 " + Hmargin + "px;");

            // Label с описанием
            Label descriptionLabel = new Label(descText);
            descriptionTextFieldOptions(descriptionLabel, descFontSize, Hmargin);

            if (textFieldText != null) {
                // Разделяем строку по точкам
                String[] parts = textFieldText.split("\\.");

                // Преобразуем части в числа
                int day = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]);
                int year = Integer.parseInt(parts[2]);

                setTextFieldText(LocalDate.of(year, month, day));
            }

            field.getChildren().addAll(descriptionLabel, datePicker);
            container.getChildren().add(field);
        }

        public String getKey() {
            return key;
        }

        public DatePicker getDatePicker() {
            return datePicker;
        }

        public String getTextFieldText() {
            return datePicker.getValue().toString();
        }

        public void setTextFieldText(LocalDate localDate) {
            datePicker.setValue(localDate);
        }

        public void setError(String error) {
            errorSetter(error, isError, errorLabel, field, descFontSize, Hmargin);
        }

        public void clearError() {
            setError(null);
        }

        public Boolean getError() {
            return isError;
        }
    }

    private static void errorSetter(String error, boolean isError, Label errorLabel, VBox field, int descFontSize, int Hmargin) {
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

    public static boolean validateChecker(TextFieldWrapper... textFieldWrappers) {
        boolean hasErrors = false;

        for (TextFieldWrapper textFieldWrapper : textFieldWrappers) {
            if (textFieldWrapper instanceof BasicTextField) {
                validateTextField(textFieldWrapper, ((BasicTextField) textFieldWrapper).errorSample, null, null);
            } else if (textFieldWrapper instanceof ChoosingTextField) {
                validateTextField(textFieldWrapper, ((ChoosingTextField) textFieldWrapper).errorSample, ((ChoosingTextField) textFieldWrapper).errorSampleD, ((ChoosingTextField) textFieldWrapper).getItems());
            } else if (textFieldWrapper instanceof DatePickerField) {
                if (((DatePickerField) textFieldWrapper).getDatePicker().getValue() == null) {
                    textFieldWrapper.setError(((DatePickerField) textFieldWrapper).errorSample);
                } else {
                    textFieldWrapper.clearError();
                }
            }

            if (textFieldWrapper.getError()) {
                hasErrors = true;
            }
        }

        return hasErrors;
    }

    // Собираюсь использовать в будущем для динамической проверки "ошибочности"
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
        String getTextFieldText();
        void setError(String message);
        void clearError();
        Boolean getError();
        void register(VBox container);
        String getKey();
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
