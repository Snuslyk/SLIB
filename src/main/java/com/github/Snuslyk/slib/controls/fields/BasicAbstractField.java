package com.github.Snuslyk.slib.controls.fields;

import com.github.Snuslyk.slib.factory.AbstractField;
import com.sun.istack.Nullable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import static com.github.Snuslyk.slib.factory.ButtonFactory.*;

public class BasicAbstractField extends AbstractField {

    // Параметры дизайна с значениями по умолчанию
    private static final int descFontSize = 20;
    private static final int mainFontSize = 20;
    private static final int Hmargin = 20;
    private static final int Vmargin = 5;
    private static final int height = 40;

    private VBox field;
    private TextField textField;
    private final Label errorLabel = new Label();

    private final Boolean isError = false;
    private final String text;
    private final String descText;
    private String textFieldText;
    private final String key;

    public final String errorSample;

    public BasicAbstractField(String key, String text, String descText, String errorSample, @Nullable String textFieldText) {
        this.key = key;
        this.errorSample = errorSample;
        this.text = text;
        this.descText = descText;
        this.textFieldText = textFieldText;
    }

    public String getKey() {
        return key;
    }

    // Методы для работы с ошибками
    public boolean getError() {
        return isError;
    }

    @Override
    public void register(Pane container) {
        field = new VBox();
        field.setSpacing(Vmargin);

        // Label с описанием
        Label descriptionLabel = new Label(descText);
        descriptionTextFieldOptions(descriptionLabel, descFontSize, Hmargin);

        // TextField
        textField = new TextField();
        textFieldOptions(text, mainFontSize, Hmargin - 2, height, textFieldText, textField);
        textField.setMinHeight(height);
        textField.setMaxHeight(height);

        // Добавление элементов в контейнер
        field.getChildren().addAll(descriptionLabel, textField);
        container.getChildren().add(field);
    }

    public void setError(String error) {
        errorSetter(error, isError, errorLabel, field, descFontSize, Hmargin);
    }

    public void clearError() {
        setError(null);
    }

    // Метод для получения текста из TextField
    public String getTextFieldText() {
        return textField.getText();
    }

    public void setTextFieldText(String text) {
        textFieldText = text;
    }
}