package com.github.Snuslyk.slib.controls.fields;

import com.github.Snuslyk.slib.factory.AbstractField;
import com.sun.istack.Nullable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import static com.github.Snuslyk.slib.factory.ButtonFactory.*;

public class AbstractAreaField extends AbstractField {

    private static final int descFontSize = 20;
    private static final int mainFontSize = 20;
    private static final int Hmargin = 20;
    private static final int Vmargin = 5;
    private static final int height = 80;

    private VBox field;
    private final Label errorLabel = new Label();

    private final Boolean isError = false;
    private final String text;
    private final String descText;
    private String textFieldText;
    private final String key;

    public final String errorSample;

    private TextArea textArea;

    public AbstractAreaField(String text, String descText, String key, String errorSample, @Nullable String textFieldText) {
        this.text = text;
        this.descText = descText;
        this.key = key;
        this.errorSample = errorSample;
        this.textFieldText = textFieldText;
    }

    @Override
    public void register(Pane container) {
        field = new VBox();
        field.setSpacing(Vmargin);

        Label descriptionLabel = new Label(descText);
        descriptionTextFieldOptions(descriptionLabel, descFontSize, Hmargin);

        textArea = new TextArea();

        textFieldOptions(text, mainFontSize, Hmargin - 2, height, textFieldText, textArea);

        field.getChildren().addAll(descriptionLabel, textArea);
        container.getChildren().add(field);
    }

    @Override
    public String getTextFieldText() {
        return textArea.getText();
    }

    @Override
    public void setTextFieldText(String text) {
        textFieldText = text;
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

    @Override
    public String getKey() {
        return key;
    }
}
