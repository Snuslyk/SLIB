package com.github.Snuslyk.slib.сontrols.buttons;

import com.github.Snuslyk.slib.util.StylesUtil;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class UnderlinedButton {

    private static final int DEFAULT_SPACING = 8;
    private static final String RADIO_BUTTON_STYLE = "radio-button-things";
    private static final String OPTION_BUTTON_STYLE = "option-button";
    private static final String FONT_SIZE_TEMPLATE = "-fx-font-size: %d;";
    private static final String SUBSTRACT_STYLE = "substract";

    private final HBox container;
    private final ToggleGroup optionToggleGroup;
    private final int fontSize;
    private final int substractHeight;
    private final int margin;VBox optionCopy;

    public UnderlinedButton(HBox container, ToggleGroup optionToggleGroup, int fontSize, int substractHeight, int margin) {
        this.container = container;
        this.optionToggleGroup = optionToggleGroup;
        this.fontSize = fontSize;
        this.substractHeight = substractHeight;
        this.margin = margin;
    }

    public void createUnderlinedButton(String text, boolean isSelected) {
        optionCopy = createOptionContainer();
        RadioButton radioButton = createRadioButton(text, isSelected);
        HBox substractBox = createSubstractBox();

        optionCopy.getChildren().addAll(radioButton, substractBox);
        container.getChildren().add(optionCopy);

        bindVisibility(substractBox, radioButton);
    }

    public VBox getButton() {
        return optionCopy;
    }

    private VBox createOptionContainer() {
        VBox optionCopy = new VBox();
        optionCopy.setSpacing(DEFAULT_SPACING);
        return optionCopy;
    }

    private RadioButton createRadioButton(String text, boolean isSelected) {
        RadioButton radioButton = new RadioButton(text);
        StylesUtil.add(radioButton, RADIO_BUTTON_STYLE, OPTION_BUTTON_STYLE);
        radioButton.setStyle(String.format(FONT_SIZE_TEMPLATE, fontSize));
        radioButton.setCursor(Cursor.HAND);
        radioButton.setToggleGroup(optionToggleGroup);
        radioButton.setSelected(isSelected);
        return radioButton;
    }

    private HBox createSubstractBox() {
        HBox substractBox = new HBox();
        substractBox.setPrefHeight(substractHeight);
        VBox.setMargin(substractBox, new Insets(0, 0, 0, margin));
        StylesUtil.add(substractBox, SUBSTRACT_STYLE);
        return substractBox;
    }

    private static void bindVisibility(HBox substractBox, RadioButton radioButton) {
        substractBox.visibleProperty().bind(radioButton.selectedProperty());
    }
}

