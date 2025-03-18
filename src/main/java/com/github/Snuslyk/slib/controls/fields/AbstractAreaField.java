package com.github.Snuslyk.slib.controls.fields;

import com.dlsc.gemsfx.ResizableTextArea;
import com.github.Snuslyk.slib.factory.AbstractField;
import com.github.Snuslyk.slib.factory.AbstractTextField;
import com.sun.istack.Nullable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import static com.github.Snuslyk.slib.factory.ButtonFactory.*;

public class AbstractAreaField extends AbstractTextField {

    private static final int descFontSize = 20;
    private static final int mainFontSize = 20;
    private static final int Hmargin = 20;
    private static final int Vmargin = 5;
    private static final int height = 100;

    private TextArea textArea;

    public AbstractAreaField(String key, String text, String descText,  @Nullable String textFieldText) {
        super(key, text, descText, textFieldText);
        isError = false;
    }

    @Override
    public void register(Pane container) {
        initializeField(Vmargin);
        Label descriptionLabel = createDescriptionLabel(descFontSize, Hmargin);
        createTextArea();
        applyTextAreaStyling();
        assembleAreaComponents(descriptionLabel);
        addToContainer(container);
    }

    protected void createTextArea() {
        textArea = new TextArea();
    }

    protected void applyTextAreaStyling() {
        textFieldOptions(promptText, mainFontSize, Hmargin - 2, height, textFieldText, textArea);
        textArea.setWrapText(true);
        textArea.setMinHeight(height);
        textArea.setMaxHeight(height);
    }

    protected void assembleAreaComponents(Label descriptionLabel) {
        field.maxHeight(200);
        field.getChildren().addAll(descriptionLabel, textArea);
    }

    @Override
    public String getTextFieldText() {
        return textArea.getText();
    }

    @Override
    public void setError(String message) {
        errorSetter(message, isError, errorLabel, field, descFontSize, Hmargin);
    }

    @Override
    public void clearError() {
        setError(null);
    }

    @Override
    public boolean getError() {
        return isError;
    }

    public TextArea getTextArea() {
        return textArea;
    }
}