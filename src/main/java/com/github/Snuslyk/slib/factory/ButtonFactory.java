package com.github.Snuslyk.slib.factory;

import com.dlsc.gemsfx.SearchField;
import com.dlsc.gemsfx.TagsField;
import com.github.Snuslyk.slib.util.StylesUtil;
import com.github.Snuslyk.slib.сontrols.fields.BasicTextField;
import com.github.Snuslyk.slib.сontrols.fields.ChoosingTextField;
import com.github.Snuslyk.slib.сontrols.fields.DatePickerField;
import com.sun.istack.Nullable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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
                validateTextField(textFieldWrapper, ((ChoosingTextField) textFieldWrapper).errorSample, null, ((ChoosingTextField) textFieldWrapper).getItems());
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
        if (input != null)
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
        void setTextFieldText(String text);
        void setError(String message);
        void clearError();
        boolean getError();
        void register(Pane container);
        String getKey();
    }

    public interface AllowPopup {
        boolean isAllowPopup();
        void setAllowPopup(boolean allowPopup);
    }

    public static void textFieldOptions(String text, int mainFontSize, int Hmargin, int height, @Nullable String textFieldText, TextField textField) {
        StylesUtil.add(textField, "textfield-form");
        textField.setPromptText(text);
        textField.setStyle("-fx-font-size: " + mainFontSize + ";");
        if (textFieldText != null) {
            textField.setText(textFieldText);
        }
        textField.setStyle("-fx-padding: 0 0 0 " + Hmargin + ";");
        textField.setPrefHeight(height);
    }

    public static void descriptionTextFieldOptions(Label descriptionText, int descFontSize, int Hmargin) {
        StylesUtil.add(descriptionText, "textfield-desc");
        descriptionText.setStyle("-fx-font-size: " + descFontSize + ";");
        descriptionText.setStyle("-fx-padding: 0 0 0 " + Hmargin + ";");
    }
}
