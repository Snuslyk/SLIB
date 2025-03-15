package com.github.Snuslyk.slib.controls.buttons;

import com.github.Snuslyk.slib.util.StylesUtil;
import javafx.scene.Cursor;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

public class FilledButton {
    private final RadioButton button;

    public FilledButton(ToggleGroup group, boolean isObject, String text, boolean isSelected) {
        button = new RadioButton(text);
        button.setCursor(Cursor.HAND);
        applyStyles(isObject);
        button.setToggleGroup(group);
        button.setPrefSize(320, 40);
        button.setSelected(isSelected);
    }

    private void applyStyles(boolean isObject) {
        StylesUtil.add(button, isObject ? "object-button" : "sections-button", "radio-button-things");
    }

    public RadioButton getButton() {
        return button;
    }
}

