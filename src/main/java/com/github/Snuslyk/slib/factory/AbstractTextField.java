package com.github.Snuslyk.slib.factory;

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
