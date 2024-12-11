package com.github.Snuslyk.slib.factory;

import com.dlsc.gemsfx.SearchField;
import com.dlsc.gemsfx.TagsField;
import com.sun.istack.Nullable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ButtonFactory {

    // ОБЩАЯ ФОРМА КНОПОК
    public static RadioButton createLeftSideButtons(ToggleGroup group, boolean isObject, String text, boolean isSelected) {
        RadioButton button = new RadioButton(text);
        button.setCursor(Cursor.HAND);
        button.getStyleClass().add(isObject ? "object-button" : "sections-button");
        button.getStyleClass().add("radio-button-things");
        button.setToggleGroup(group);
        button.setPrefSize(320, 40);

        if (isSelected) {
            button.setSelected(true);
        }
        return button;
    }

    public static VBox createUnderlinedButtons(HBox container, String text, boolean isSelected, ToggleGroup optionToggleGroup, int fontSize, int substractHeight, int margin) {
        // Создаем новый VBox для каждой опции вместо использования копирования optionExample
        VBox optionCopy = new VBox();
        optionCopy.setSpacing(8);

        RadioButton radioButton = new RadioButton(text);
        radioButton.getStyleClass().add("radio-button-things");
        radioButton.getStyleClass().add("option-button");
        radioButton.setStyle("-fx-font-size: " + fontSize + ";");
        radioButton.setCursor(Cursor.HAND);
        radioButton.setToggleGroup(optionToggleGroup);

        if (isSelected) {
            radioButton.setSelected(true);
        }

        HBox substractBox = new HBox();
        substractBox.setPrefHeight(substractHeight);
        VBox.setMargin(substractBox, new Insets(0, 0, 0, margin));
        substractBox.getStyleClass().add("substract");

        optionCopy.getChildren().addAll(radioButton, substractBox);

        // Добавляем в контейнер опций
        container.getChildren().add(optionCopy);

        substractBox.visibleProperty().bind(radioButton.selectedProperty());

        return optionCopy;
    }

    public static class BasicTextField implements TextFieldWrapper {

        // Параметры дизайна с значениями по умолчанию
        private static final int descFontSize = 20;
        private static final int mainFontSize = 20;
        private static final int Hmargin = 20;
        private static final int Vmargin = 5;
        private static final int height = 40;

        private VBox field;
        private TextField textField;
        private final Label errorLabel = new Label();
        private Boolean isError = false;
        private final String text;
        private final String descText;
        private String textFieldText;
        private final String key;

        private final String errorSample;

        public BasicTextField(String key, String text, String descText, String errorSample, @Nullable String textFieldText) {
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
        public void register(VBox container) {
            field = new VBox();
            field.setSpacing(Vmargin);

            // Label с описанием
            Label descriptionLabel = new Label(descText);
            descriptionTextFieldOptions(descriptionLabel, descFontSize, Hmargin);

            // TextField
            textField = new TextField();
            textFieldOptions(text, mainFontSize, Hmargin - 2, height, textFieldText, textField);

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

    public static class ChoosingTextField implements TextFieldWrapper {
        private static final int descFontSize = 20;
        private static final int mainFontSize = 20;
        private static final int Hmargin = 20;
        private static final int Vmargin = 5;
        private static final int height = 40;
        private static final int popUpHeight = 99;

        private final Supplier<ObservableList<String>> items;
        private final String key;
        private Pane outOfBounds;

        private VBox field;
        private final String errorSample;
        private final Label errorLabel = new Label();
        private final String text;
        private final String descText;
        private String textFieldText;
        private boolean isError;

        private ChoosingSearchField searchField;

        public ChoosingTextField(String key, String text, String descText, String errorSample, Supplier<ObservableList<String>> items, @Nullable String textFieldText) {
            this.key = key;
            this.items = items;
            this.errorSample = errorSample;
            this.text = text;
            this.descText = descText;
            this.textFieldText = textFieldText;
        }

        public void register(VBox container, Pane outOfBounds){
            this.outOfBounds = outOfBounds;
            register(container);
        }

        @Override
        public void register(VBox container) {
            field = new VBox();
            field.setSpacing(Vmargin);

            System.out.println(items.get());

            searchField = new ChoosingSearchField(outOfBounds);
            searchField.setText(textFieldText);

            Label descriptionText = new Label(descText);
            descriptionTextFieldOptions(descriptionText, descFontSize, Hmargin);

            textFieldOptions(text, mainFontSize, Hmargin, height, textFieldText, searchField.getEditor());
            searchField.prefWidthProperty().bind(container.widthProperty());

            SVGPath svgIcon = new SVGPath();
            svgIcon.setContent("M1, 0L7, 6L13, 0");
            svgIcon.setFill(Color.TRANSPARENT);
            svgIcon.setStroke(Color.web("#3D3D3D"));
            svgIcon.setStrokeWidth(1.0);
            HBox buttonContainer = new HBox(-33);
            buttonContainer.getChildren().addAll(searchField, svgIcon);
            buttonContainer.setAlignment(Pos.CENTER_LEFT);

            searchField.getPopup().showingProperty().addListener(((obs, wasSelected, isNowSelected) -> {
                if (!isNowSelected) {
                    svgIcon.setContent("M1, 0L7, 6L13, 0");
                } else {
                    svgIcon.setContent("M1, 7L7, 0.999999L13, 7");
                }
            }));

            field.getChildren().addAll(descriptionText, buttonContainer);
            container.getChildren().add(field);
        }

        @Override
        public String getTextFieldText() {
            return searchField.getText();
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

        public List<String> getItems() {
            return items.get();
        }

        public class ChoosingSearchField extends SearchField<String> {

            public ChoosingSearchField(Pane outOfBounds) {
                setSuggestionProvider(request -> items.get().stream()
                        .filter(item -> item.toLowerCase().contains(request.getUserText().toLowerCase()))
                        .collect(Collectors.toList()));

                getEditor().setOnMouseClicked(mouseEvent -> {
                    if (!getSuggestions().isEmpty() || getPlaceholder() != null) {
                        update(items.get());
                        getEditor().requestFocus();
                        getPopup().show(this);
                    }
                });

                getEditor().addEventFilter(KeyEvent.KEY_RELEASED, event -> {
                    String input = getEditor().getText();
                    if (input.isEmpty()) {
                        if (getSuggestions().isEmpty() || getPlaceholder() != null) {
                            update(items.get());
                            getEditor().requestFocus();
                            getPopup().show(this);
                        }
                    }
                });

                setShowSearchIcon(false);
                getStyleClass().add("search-field-d");

                outOfBounds.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
                    if (getPlaceholder() != null) {
                        getPopup().hide();
                    }
                });

                setMatcher((item, searchText) -> item.toLowerCase().startsWith(searchText.toLowerCase()));
                setComparator(String::compareToIgnoreCase);

                Region content = (Region) getPopup().getScene().getRoot();
                Rectangle clip = new Rectangle();
                clip.setArcWidth(26);
                clip.setArcHeight(26);
                clip.widthProperty().bind(content.widthProperty());
                clip.heightProperty().bind(content.heightProperty());
                clip.setLayoutY(8);
                content.setClip(clip);
            }
        }
    }

    public static class DatePickerField implements TextFieldWrapper {

        private static final int descFontSize = 20;
        private static final int Hmargin = 20;
        private static final int mainFontSize = 20;
        private static final int Vmargin = 5;
        private static final int height = 40;

        private VBox field;
        private DatePicker datePicker;
        private final boolean isError = false;
        private final Label errorLabel = new Label();
        private final String errorSample;
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
        public void register(VBox container) {
            field = new VBox();
            field.setSpacing(Vmargin);
            datePicker = new DatePicker();

            datePicker.setPrefHeight(height);
            datePicker.setStyle("-fx-font-size: " + mainFontSize + ";");
            datePicker.getEditor().setStyle("-fx-padding: 0 0 0 " + Hmargin + ";");
            datePicker.prefWidthProperty().bind(container.widthProperty());
            
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
                String[] parts = textFieldText.split("\\.");

                if (parts.length == 3) {
                    try {
                        int day = Integer.parseInt(parts[0]);
                        int month = Integer.parseInt(parts[1]);
                        int year = Integer.parseInt(parts[2]);

                        setTextFieldText(LocalDate.of(year, month, day));
                    } catch (NumberFormatException e) {
                        setError("Invalid date format");
                    }
                } else {
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

        public void setTextFieldText(LocalDate localDate) {
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

    public static class ChoiceBoxField implements TextFieldWrapper {
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
        public void register(VBox container) {
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
            comboBox.getStyleClass().add("combo-box-field");

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

    public static class MultiChooseField implements TextFieldWrapper {

        private static final int descFontSize = 20;
        private static final int mainFontSize = 20;
        private static final int Hmargin = 20;
        private static final int Vmargin = 5;
        private static final int height = 40;
        private static final int popUpHeight = 99;

        private Supplier<ObservableList<String>> items;
        private final String key;
        private Pane outOfBounds;

        private VBox field;
        private final String errorSample;
        private final Label errorLabel = new Label();
        private final String text;
        private final String descText;
        private String textFieldText;
        private boolean isError;

        private ChoosingTagsField searchField;

        public MultiChooseField(String key, String text, String descText, String errorSample, Supplier<ObservableList<String>> items, @Nullable String textFieldText) {
            this.key = key;
            this.items = items;
            this.errorSample = errorSample;
            this.text = text;
            this.descText = descText;
            this.textFieldText = textFieldText;
        }

        public void register(VBox container, Pane outOfBounds){
            this.outOfBounds = outOfBounds;
            register(container);
        }

        @Override
        public void register(VBox container) {
            field = new VBox();
            field.setSpacing(Vmargin);
            searchField = new ChoosingTagsField();
            List<String> itemsList = Arrays.asList(textFieldText.split(",\\s*"));
            for (String item : itemsList) {
                searchField.select(item);
            }

            // Создание SVGPath для стрелки
            SVGPath arrow = new SVGPath();
            arrow.setContent("M1, 0L7, 6L13, 0"); // Стрелка вниз
            arrow.setFill(Color.TRANSPARENT);
            arrow.setStroke(Color.web("#3D3D3D"));
            arrow.setStrokeWidth(1.0);

            // Label с описанием
            Label descriptionLabel = new Label(descText);
            descriptionTextFieldOptions(descriptionLabel, descFontSize, Hmargin);

            // Обновление стрелки в зависимости от состояния ChoiceBox (раскрыт/не раскрыт)
            searchField.getPopup().showingProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) {
                    // Когда список раскрыт, стрелка вверх
                    arrow.setContent("M1, 7L7, 0.999999L13, 7");
                } else {
                    // Когда список закрыт, стрелка вниз
                    arrow.setContent("M1, 0L7, 6L13, 0");
                }
            });

            StackPane stackPane = new StackPane();
            stackPane.getChildren().addAll(searchField, arrow);
            StackPane.setAlignment(arrow, Pos.CENTER_RIGHT);
            arrow.setTranslateX(-19);

            field.getChildren().addAll(descriptionLabel, stackPane);
            container.getChildren().add(field);
        }

        @Override
        public String getTextFieldText() {
            StringBuilder builder = new StringBuilder();
            searchField.getTags().forEach(tag -> {builder.append(tag).append(", ");});
            builder.deleteCharAt(builder.length()-1);
            builder.deleteCharAt(builder.length()-1);
            return builder.toString();
        }
        public ObservableList<String> getTags(){
            return searchField.getTags();
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

        public List<String> getItems() {
            return items.get();
        }

        public class ChoosingTagsField extends TagsField<String> {

            public ChoosingTagsField() {
                setSuggestionProvider(request -> items.get().stream().filter(item -> item.toLowerCase().contains(request.getUserText().toLowerCase())).collect(Collectors.toList()));

                getEditor().addEventFilter(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
                    ObservableList<String> mutableItems = FXCollections.observableArrayList(items.get());
                    update(mutableItems);
                    getEditor().requestFocus();

                    if (!getPopup().isShowing()) {
                        Bounds bounds = getBoundsInLocal();
                        Bounds screenBounds = localToScreen(bounds);

                        double popupX = screenBounds.getMinX();
                        double popupY = screenBounds.getMaxY(); // Позиция под полем

                        getPopup().show(this, popupX, popupY);
                    }
                });

                getEditor().addEventFilter(KeyEvent.KEY_RELEASED, event -> {
                    String input = getEditor().getText();
                    if (input.isEmpty()) {
                        if (getSuggestions().isEmpty() || getPlaceholder() != null) {
                            ObservableList<String> mutableItems = FXCollections.observableArrayList(items.get());
                            update(mutableItems);
                            getEditor().requestFocus();
                            getPopup().show(this);
                        }
                    }
                });

                setShowSearchIcon(false);
                getStyleClass().add("search-field-d");

                outOfBounds.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
                    if (getPlaceholder() != null) {
                        getPopup().hide();
                    }
                });

                setOnMouseClicked(mouseEvent -> {
                    if (getTagSelectionModel().getSelectedItem() != null) {
                        removeTags(getTagSelectionModel().getSelectedItem());
                    }
                });

                setMatcher((item, searchText) -> item.toLowerCase().startsWith(searchText.toLowerCase()));
                setComparator(String::compareToIgnoreCase);

                Region content = (Region) getPopup().getScene().getRoot();
                Rectangle clip = new Rectangle();
                clip.setArcWidth(26);
                clip.setArcHeight(26);
                clip.widthProperty().bind(content.widthProperty());
                clip.heightProperty().bind(content.heightProperty());
                clip.setLayoutY(8);
                content.setClip(clip);

                getEditor().setStyle("-fx-text-fill: white; -fx-prompt-text-fill: #3D3D3D;");
            }
        }
    }

    private static void errorSetter(String error, boolean isError, Label errorLabel, VBox field, int descFontSize, int Hmargin) {
        if (error != null && !error.isEmpty()) {
            isError = true;
            errorLabel.setText(error);
            descriptionTextFieldOptions(errorLabel, descFontSize, Hmargin);
            errorLabel.setPadding(new Insets(0, 0, 0, Hmargin));
            errorLabel.setStyle("-fx-text-fill: rgb(239, 48, 48);");
            if (!field.getChildren().contains(errorLabel)) {
                field.getChildren().add(errorLabel);
            }
        } else {
            isError = false;
            field.getChildren().remove(errorLabel);
        }
    }

    public static boolean validateChecker(TextFieldWrapper... textFieldWrappers) {
        boolean hasErrors = false;

        for (TextFieldWrapper textFieldWrapper : textFieldWrappers) {
            if (textFieldWrapper instanceof BasicTextField) {
                validateTextField(textFieldWrapper, ((BasicTextField) textFieldWrapper).errorSample, null, null);
            } else if (textFieldWrapper instanceof ChoosingTextField) {
                validateTextField(textFieldWrapper, ((ChoosingTextField) textFieldWrapper).errorSample, null, ((ChoosingTextField) textFieldWrapper).getItems());
            } else if (textFieldWrapper instanceof DatePickerField) {
                if (((DatePickerField) textFieldWrapper).getDatePicker().getValue() == null) {
                    textFieldWrapper.setError(((DatePickerField) textFieldWrapper).errorSample);
                } else {
                    textFieldWrapper.clearError();
                }
            }

            if (textFieldWrapper.getError()) {
                hasErrors = true;
            }
        }

        return hasErrors;
    }

    // Собираюсь использовать в будущем для динамической проверки "ошибочности"
    public static void validateChoosingTextField(TextFieldWrapper textFieldWrapper, @Nullable String errorMessage, List<String> validItems) {
        boolean isValid = false;

        for (String item : validItems) {
            if (Objects.equals(textFieldWrapper.getTextFieldText(), item)) {
                isValid = true;
                break;
            }
        }

        if (isValid) {
            textFieldWrapper.clearError();
        } else {
            textFieldWrapper.setError(errorMessage);
        }
    }

    // Универсальный метод для проверки текста
    public static void validateTextField(TextFieldWrapper textFieldWrapper, @Nullable String errorMessage, @Nullable String secondErrorMessage, @Nullable List<String> validItems) {
        String input = textFieldWrapper.getTextFieldText();

        // Если текст пустой, устанавливаем ошибку
        if (input.isEmpty()) {
            textFieldWrapper.setError(errorMessage);
            return;
        }

        // Если список валидных элементов не задан, очищаем ошибку
        if (validItems == null) {
            textFieldWrapper.clearError();
            return;
        }

        // Проверяем, соответствует ли введенный текст допустимым значениям
        if (!validItems.contains(input)) {
            textFieldWrapper.setError(secondErrorMessage);
        } else {
            textFieldWrapper.clearError();
        }
    }


    // Интерфейс-обёртка для текстовых полей
    public interface TextFieldWrapper {
        String getTextFieldText();
        void setTextFieldText(String text);
        void setError(String message);
        void clearError();
        boolean getError();
        void register(VBox container);
        String getKey();
    }

    private static void textFieldOptions(String text, int mainFontSize, int Hmargin, int height, @Nullable String textFieldText, TextField textField) {
        textField.getStyleClass().add("textfield-form");
        textField.setPromptText(text);
        textField.setStyle("-fx-font-size: " + mainFontSize + ";");
        if (textFieldText != null) {
            textField.setText(textFieldText);
        }
        textField.setStyle("-fx-padding: 0 0 0 " + Hmargin + ";");
        textField.setPrefHeight(height);
    }

    private static void descriptionTextFieldOptions(Label descriptionText, int descFontSize, int Hmargin) {
        descriptionText.getStyleClass().add("textfield-desc");
        descriptionText.setStyle("-fx-font-size: " + descFontSize + ";");
        descriptionText.setStyle("-fx-padding: 0 0 0 " + Hmargin + ";");
    }

}
