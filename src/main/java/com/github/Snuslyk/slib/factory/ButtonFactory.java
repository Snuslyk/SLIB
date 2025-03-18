package com.github.Snuslyk.slib.factory;

import com.github.Snuslyk.slib.controls.fields.BasicAbstractField;
import com.github.Snuslyk.slib.controls.fields.ChoosingAbstractField;
import com.github.Snuslyk.slib.controls.fields.DatePickerField;
import com.github.Snuslyk.slib.util.StylesUtil;
import com.sun.istack.Nullable;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Objects;

public class ButtonFactory {

    // ОБЩАЯ ФОРМА КНОПОК
    public static RadioButton createLeftSideButtons(ToggleGroup group, boolean isObject, String text, boolean isSelected) {
        RadioButton button = new RadioButton(text);
        button.setCursor(Cursor.HAND);
        StylesUtil.add(button, isObject ? "object-button" : "sections-button", "radio-button-things");
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
        StylesUtil.add(radioButton, "radio-button-things", "option-button");
        radioButton.setStyle("-fx-font-size: " + fontSize + ";");
        radioButton.setCursor(Cursor.HAND);
        radioButton.setToggleGroup(optionToggleGroup);

        if (isSelected) {
            radioButton.setSelected(true);
        }

        HBox substractBox = new HBox();
        substractBox.setPrefHeight(substractHeight);
        VBox.setMargin(substractBox, new Insets(0, 0, 0, margin));
        StylesUtil.add(substractBox, "substract");

        optionCopy.getChildren().addAll(radioButton, substractBox);

        // Добавляем в контейнер опций
        container.getChildren().add(optionCopy);

        substractBox.visibleProperty().bind(radioButton.selectedProperty());

        return optionCopy;
    }

    public static void errorSetter(String error, boolean isError, Label errorLabel, VBox field, int descFontSize, int Hmargin) {
        if (error != null && !error.isEmpty()) {
            isError = true;
            errorLabel.setText(error);
            descriptionTextFieldOptions(errorLabel, descFontSize, Hmargin);
            errorLabel.setPadding(new Insets(0, 0, 0, Hmargin));
            errorLabel.setStyle("-fx-text-fill: rgb(239, 48, 48);");
            if (!field.getChildren().contains(errorLabel)) {
                field.getChildren().add(-1, errorLabel);
            }
        } else {
            isError = false;
            field.getChildren().remove(errorLabel);
        }
    }

    // Собираюсь использовать в будущем для динамической проверки "ошибочности"
    public static void validateChoosingTextField(AbstractField abstractField, @Nullable String errorMessage, List<String> validItems) {
        boolean isValid = false;

        for (String item : validItems) {
            if (Objects.equals(abstractField.getTextFieldText(), item)) {
                isValid = true;
                break;
            }
        }

        if (isValid) {
            abstractField.clearError();
        } else {
            abstractField.setError(errorMessage);
        }
    }

    // Универсальный метод для проверки текста
    public static void validateTextField(AbstractField abstractField, @Nullable String errorMessage, @Nullable String secondErrorMessage, @Nullable List<String> validItems) {
        String input = abstractField.getTextFieldText();

        // Если текст пустой, устанавливаем ошибку
        if (input != null)
            if (input.isEmpty()) {
                abstractField.setError(errorMessage);
                return;
            }

        // Если список валидных элементов не задан, очищаем ошибку
        if (validItems == null) {
            abstractField.clearError();
            return;
        }

        // Проверяем, соответствует ли введенный текст допустимым значениям
        if (!validItems.contains(input)) {
            abstractField.setError(secondErrorMessage);
        } else {
            abstractField.clearError();
        }
    }


    public static void textFieldOptions(@Nullable String text, int mainFontSize, int Hmargin, int height, @Nullable String textFieldText, TextInputControl textField) {
        StylesUtil.add(textField, "textfield-form");
        if (text != null) {
            textField.setPromptText(text);
        }
        textField.setStyle("-fx-font-size: " + mainFontSize + ";");
        if (textFieldText != null) {
            textField.setText(textFieldText);
        }
        textField.setStyle("-fx-padding: 0 " + Hmargin + ";");
        textField.setPrefHeight(height);
    }

    public static void descriptionTextFieldOptions(Label descriptionText, int descFontSize, int Hmargin) {
        StylesUtil.add(descriptionText, "textfield-desc");
        descriptionText.setStyle("-fx-font-size: " + descFontSize + ";");
        descriptionText.setStyle("-fx-padding: 0 0 0 " + Hmargin + ";");
    }
}
