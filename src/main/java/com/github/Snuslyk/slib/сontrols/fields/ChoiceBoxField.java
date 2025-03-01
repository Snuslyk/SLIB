package com.github.Snuslyk.slib.сontrols.fields;

import com.github.Snuslyk.slib.factory.ButtonFactory;
import com.github.Snuslyk.slib.util.StylesUtil;
import com.sun.istack.Nullable;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

import java.util.function.Supplier;

import static com.github.Snuslyk.slib.factory.ButtonFactory.descriptionTextFieldOptions;
import static com.github.Snuslyk.slib.factory.ButtonFactory.errorSetter;

public class ChoiceBoxField implements ButtonFactory.TextFieldWrapper {
    private static final int descFontSize = 20;
    private static final int Hmargin = 20;
    private static final int mainFontSize = 20;
    private static final int Vmargin = 5;
    private static final int height = 40;

    private VBox field;
    private ComboBox<String> comboBox;
    private final boolean isError = false;
    private final Label errorLabel = new Label();
    private final Supplier<ObservableList<String>> items;

    private final String errorSample;
    private final String descText;
    private String textFieldText;
    private final String key;

    public ChoiceBoxField(String key, String descText, String errorSample, Supplier<ObservableList<String>> items, @Nullable String textFieldText) {
        this.items = items;
        this.key = key;
        this.errorSample = errorSample;
        this.descText = descText;
        this.textFieldText = textFieldText;
    }

    @Override
    public void register(Pane container) {
        field = new VBox();
        field.setSpacing(Vmargin);
        comboBox = new ComboBox<String>();
        comboBox.setValue(textFieldText);

        comboBox.setItems(items.get());

        // Создание SVGPath для стрелки
        SVGPath arrow = new SVGPath();
        arrow.setContent("M1, 0L7, 6L13, 0"); // Стрелка вниз
        arrow.setFill(Color.TRANSPARENT);
        arrow.setStroke(Color.web("#3D3D3D"));
        arrow.setStrokeWidth(1.0);

        comboBox.setMinHeight(height);
        comboBox.setMaxHeight(height);
        comboBox.setStyle("-fx-font-size: " + mainFontSize + ";");
        comboBox.setStyle("-fx-padding: 0 0 0 " + (Hmargin - 6) + ";");
        comboBox.prefWidthProperty().bind(container.widthProperty());

        StylesUtil.add(comboBox,"combo-box-field");

        comboBox.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setStyle(""); // Очищаем стиль для пустых ячеек
                } else {
                    setText(item);
                    setPrefWidth(comboBox.getPrefWidth() - 30);
                    if (getIndex() == 0) { // Первый элемент
                        setStyle("-fx-border-color: #3D3D3D; -fx-border-width: 1 1 0 1; -fx-border-radius: 12 12 0 0; -fx-background-radius: 12 12 0 0;");
                    } else if (getIndex() == comboBox.getItems().size() - 1) { // Последний элемент
                        setStyle("-fx-border-color: #3D3D3D; -fx-border-width: 0 1 1 1; -fx-border-radius: 0 0 12 12; -fx-background-radius: 0 0 12 12;");
                    } else {
                        setStyle("-fx-border-color: #3D3D3D; -fx-border-width: 0 1 0 1;");
                    }
                }
            }
        });

        comboBox.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(item);
                    setTranslateX(-14);
                    setTranslateY(-1);
                    setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
                }
            }
        });

        // Label с описанием
        Label descriptionLabel = new Label(descText);
        descriptionTextFieldOptions(descriptionLabel, descFontSize, Hmargin);

        // Контейнер для размещения ChoiceBox и стрелки
        HBox hContainer = new HBox(-33, comboBox, arrow); // Хранит ChoiceBox и стрелку
        hContainer.setAlignment(Pos.CENTER_LEFT);

        // Обновление стрелки в зависимости от состояния ChoiceBox (раскрыт/не раскрыт)
        comboBox.showingProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                // Когда список раскрыт, стрелка вверх
                arrow.setContent("M1, 7L7, 0.999999L13, 7");
            } else {
                // Когда список закрыт, стрелка вниз
                arrow.setContent("M1, 0L7, 6L13, 0");
            }
        });

        field.getChildren().addAll(descriptionLabel, hContainer);
        container.getChildren().add(field);
    }

    @Override
    public String getTextFieldText() {
        return comboBox.getValue();
    }

    public void setTextFieldText(String text) {
        textFieldText = text;
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
    public String getKey() {
        return key;
    }
}