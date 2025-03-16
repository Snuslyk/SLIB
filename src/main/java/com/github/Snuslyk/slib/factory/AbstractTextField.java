package com.github.Snuslyk.slib.factory;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import static com.github.Snuslyk.slib.factory.ButtonFactory.descriptionTextFieldOptions;

public abstract class AbstractTextField extends AbstractField {

    protected String promptText;
    protected String descriptionText;
    protected String key;
    protected String errorSample;
    protected String textFieldText;

    public AbstractTextField(String promptText, String descriptionText, String key, String errorSample, String textFieldText) {
        this.promptText = promptText;
        this.descriptionText = descriptionText;
        this.key = key;
        this.errorSample = errorSample;
        this.textFieldText = textFieldText;
    }

    protected Label createDescriptionLabel() {
        Label descriptionLabel = new Label(descriptionText);
        descriptionTextFieldOptions(descriptionLabel, descFontSize, Hmargin);
        return descriptionLabel;
    }

    protected void assembleComponents(Label descriptionLabel, HBox buttonContainer) {
        field.getChildren().addAll(descriptionLabel, buttonContainer);
    }

    public String getTextFieldText() {
        return textFieldText;
    }

    public void setTextFieldText(String text) {
        textFieldText = text;
    }

    public String getKey() {
        return key;
    }
}
