package com.github.Snuslyk.slib.factory;

import javafx.scene.Cursor;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

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

    public static RadioButton createOptionButtons(ToggleGroup group, String text, boolean isSelected) {
        RadioButton button = new RadioButton(text);
        button.getStyleClass().add("radio-button-things");
        button.getStyleClass().add("option-button");
        button.setCursor(Cursor.HAND);
        button.setToggleGroup(group);

        if (isSelected) {
            button.setSelected(true);
        }
        return button;
    }
}
