package com.github.Snuslyk.slib.controls.fields;

import com.github.Snuslyk.slib.factory.AbstractTextField;
import com.sun.istack.Nullable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import static com.github.Snuslyk.slib.factory.ButtonFactory.*;

public class BasicAbstractField extends AbstractTextField {

    // Параметры дизайна с значениями по умолчанию
    private static final int descFontSize = 20;
    private static final int mainFontSize = 20;
    private static final int Hmargin = 20;
    private static final int Vmargin = 5;
    private static final int height = 40;

    private TextField textField;

    public BasicAbstractField(String key, String text, String descriptionText,  @Nullable String textFieldText) {
        super(key, text, descriptionText, textFieldText);
    }

    @Override
    public void register(Pane container) {
        initializeField(Vmargin);
        Label descriptionLabel = createDescriptionLabel(descFontSize, Hmargin);
        createTextField();
        applyTextFieldStyling();
        assembleComponents(descriptionLabel);
        addToContainer(container);
    }

    protected void createTextField() {
        textField = new TextField();
    }

    protected void applyTextFieldStyling() {
        textFieldOptions(promptText, mainFontSize, Hmargin - 2, height, textFieldText, textField);
        textField.setMinHeight(height);
        textField.setMaxHeight(height);
    }

    protected void assembleComponents(Label descriptionLabel) {
        field.getChildren().addAll(descriptionLabel, textField);
    }

    @Override
    public void setError(String error) {
        errorSetter(error, isError, errorLabel, field, descFontSize, Hmargin);
    }

    @Override
    public void clearError() {
        setError(null);
    }

    @Override
    public boolean getError() {
        return isError;
    }

    @Override
    public String getTextFieldText() {
        return textField.getText();
    }

    public TextField getTextField() {
        return textField;
    }
}