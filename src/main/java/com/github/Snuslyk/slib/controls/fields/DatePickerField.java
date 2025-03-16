package com.github.Snuslyk.slib.controls.fields;

import com.github.Snuslyk.slib.factory.ButtonFactory;
import com.github.Snuslyk.slib.factory.AbstractField;
import com.github.Snuslyk.slib.util.TimeUtil;
import com.sun.istack.Nullable;
import javafx.geometry.Pos;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import static com.github.Snuslyk.slib.factory.ButtonFactory.descriptionTextFieldOptions;
import static com.github.Snuslyk.slib.factory.ButtonFactory.errorSetter;

public class DatePickerField extends AbstractField {

    private static final int descFontSize = 20;
    private static final int Hmargin = 20;
    private static final int mainFontSize = 20;
    private static final int Vmargin = 5;
    private static final int height = 40;

    private VBox field;
    private DatePicker datePicker;
    private final boolean isError = false;
    private final Label errorLabel = new Label();
    public final String errorSample;
    private final String descText;
    private String textFieldText;
    private final String key;


    public DatePickerField(String key, String descText, String errorSample, @Nullable String textFieldText) {
        this.key = key;
        this.errorSample = errorSample;
        this.descText = descText;
        this.textFieldText = textFieldText;
    }

    @Override
    public void register(Pane container) {
        field = new VBox();
        field.setSpacing(Vmargin);
        datePicker = new DatePicker();

        ButtonFactory.textFieldOptions(null, mainFontSize, Hmargin - 3, height, textFieldText, datePicker.getEditor());

        datePicker.prefWidthProperty().bind(container.widthProperty());

        datePicker.setMinHeight(height);
        datePicker.setMaxHeight(height);

        Label descriptionLabel = new Label(descText);
        descriptionTextFieldOptions(descriptionLabel, descFontSize, Hmargin);

        SVGPath svgIcon = new SVGPath();
        svgIcon.setContent("M11.8333 1V4.2M5.16667 1V4.2M1 7.4H16M2.66667 2.6H14.3333C15.2538 2.6 16 3.31634 16 4.2V15.4C16 16.2837 15.2538 17 14.3333 17H2.66667C1.74619 17 1 16.2837 1 15.4V4.2C1 3.31634 1.74619 2.6 2.66667 2.6Z");
        svgIcon.setFill(Color.TRANSPARENT);
        svgIcon.setStroke(Color.web("#3D3D3D"));
        svgIcon.setStrokeWidth(1.0);

        // Button container
        HBox buttonContainer = new HBox(-36);
        buttonContainer.getChildren().addAll(datePicker, svgIcon);
        buttonContainer.setAlignment(Pos.CENTER_LEFT);

        datePicker.getEditor().setOnMouseClicked(event -> {
            if (!datePicker.isShowing()) {
                datePicker.show();
            }
        });

        if (textFieldText != null && !textFieldText.isEmpty()) {
            try {
                LocalDate date = TimeUtil.parseDate(textFieldText);
                setDatePickerValue(date);
            } catch (DateTimeParseException e) {
                setError("Invalid date format");
            }
        }

        field.getChildren().addAll(descriptionLabel, buttonContainer);
        container.getChildren().add(field);
    }

    public String getKey() {
        return key;
    }

    public DatePicker getDatePicker() {
        return datePicker;
    }

    public String getTextFieldText() {
        return datePicker.getValue().toString();
    }

    public void setTextFieldText(String text) {
        textFieldText = text;
    }

    public void setDatePickerValue(LocalDate localDate) {
        datePicker.setValue(localDate);
    }

    public void setError(String error) {
        errorSetter(error, isError, errorLabel, field, descFontSize, Hmargin);
    }

    public void clearError() {
        setError(null);
    }

    public boolean getError() {
        return isError;
    }
}
