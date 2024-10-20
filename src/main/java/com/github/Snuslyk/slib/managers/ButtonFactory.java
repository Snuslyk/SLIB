package com.github.Snuslyk.slib.managers;

import javafx.scene.Cursor;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

public class ButtonFactory {

    public static RadioButton createRadioButton(ToggleGroup group, String text, boolean isSelected) {
        RadioButton button = new RadioButton(text);
        button.setCursor(Cursor.HAND);
        button.getStyleClass().add(group == null ? "object-button" : "sections-button");
        button.getStyleClass().add("radio-button-things");
        button.setToggleGroup(group);
        button.setPrefSize(320, 40);

        if (isSelected) {
            button.setSelected(true);
        }
        return button;
    }
}
