package com.github.Snuslyk.slib.controls.fields;

import com.github.Snuslyk.slib.factory.TextFieldWrapper;
import com.github.Snuslyk.slib.util.TimeUtil;
import com.sun.istack.Nullable;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import java.time.LocalDate;

import static com.github.Snuslyk.slib.factory.ButtonFactory.*;
import static java.time.temporal.ChronoUnit.DAYS;

import java.util.*;

import javafx.collections.ObservableSet;
import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class MultiDatePicker implements TextFieldWrapper {

    private final ObservableSet<LocalDate> selectedDates;
    private final DatePicker datePicker;

    private final boolean withRange;

    private static final int descFontSize = 20;
    private static final int mainFontSize = 20;
    private static final int Hmargin = 20;
    private static final int Vmargin = 5;
    private static final int height = 40;

    private VBox field;

    private final Label errorLabel = new Label();

    private final Boolean isError = false;
    private final String text;
    private final String descText;
    private final String textFieldText;
    private final String key;

    public final String errorSample;

    public MultiDatePicker(String key, String text, String descText, String errorSample, boolean withRange, @Nullable String textFieldText) {
        this.text = text;
        this.descText = descText;
        this.key = key;
        this.errorSample = errorSample;
        this.withRange = withRange;
        this.textFieldText = textFieldText;

        this.selectedDates = FXCollections.observableSet(new TreeSet<>());
        this.datePicker = new DatePicker();
    }

    @Override
    public void register(Pane container) {
        field = new VBox();
        field.setSpacing(Vmargin);

        // Label с описанием
        Label descriptionLabel = new Label(descText);
        descriptionTextFieldOptions(descriptionLabel, descFontSize, Hmargin);

        textFieldOptions(text, mainFontSize, Hmargin - 2, height, textFieldText, datePicker.getEditor());
        datePicker.getEditor().setMinHeight(height);
        datePicker.getEditor().setMaxHeight(height);

        setUpDatePicker();

        // Добавление элементов в контейнер
        if (withRange) field.getChildren().addAll(descriptionLabel, withRangeSelectionMode().getDatePicker());
        else field.getChildren().addAll(descriptionLabel, getDatePicker());

        datePicker.getEditor().setOnMouseClicked(event -> {
            if (!datePicker.isShowing()) {
                datePicker.show();
            }
        });

        container.getChildren().add(field);

    }

    public MultiDatePicker withRangeSelectionMode() {

        EventHandler<MouseEvent> mouseClickedEventHandler = (MouseEvent clickEvent) -> {
            if (clickEvent.getButton() == MouseButton.PRIMARY) {
                LocalDate selectedDate = datePicker.getValue();

                if (!selectedDates.contains(selectedDate)) {
                    selectedDates.add(selectedDate);
                    selectedDates.addAll(getRangeGaps(Collections.min(selectedDates), Collections.max(selectedDates)));
                } else {
                    selectedDates.removeAll(getTailEndDatesToRemove(selectedDates, selectedDate));
                }

                updateTextField();
            }
            datePicker.show();
            clickEvent.consume();
        };

        datePicker.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                if (!selectedDates.isEmpty()) {
                    return TimeUtil.formatDateRange(selectedDates);
                }
                return "";
            }

            @Override
            public LocalDate fromString(String string) {
                setDateRange(string);
                TreeSet<LocalDate> sortedDates = new TreeSet<>(selectedDates);
                return sortedDates.isEmpty() ? null : sortedDates.first();
            }
        });

        this.datePicker.setDayCellFactory((DatePicker param) -> new DateCell() {

            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);

                //...
                if (item != null && !empty) {
                    //...
                    addEventHandler(MouseEvent.MOUSE_CLICKED, mouseClickedEventHandler);
                } else {
                    //...
                    removeEventHandler(MouseEvent.MOUSE_CLICKED, mouseClickedEventHandler);
                }

                if (!selectedDates.isEmpty() && selectedDates.contains(item)) {
                    if (Objects.equals(item, selectedDates.toArray()[0]) || Objects.equals(item, selectedDates.toArray()[selectedDates.size() - 1])) {
                        setStyle("-fx-background-color: rgba(3, 169, 244, 0.7);");
                    } else {
                        setStyle("-fx-background-color: rgba(160,215,241,0.7);");
                    }
                } else {
                    setStyle(null);
                }

            }
        });
        return this;
    }

    private void updateTextField() {
        if (!selectedDates.isEmpty()) {
            datePicker.getEditor().setText(TimeUtil.formatDateRange(selectedDates));
        } else {
            datePicker.getEditor().clear();
        }
    }

    public ObservableSet<LocalDate> getSelectedDates() {
        return this.selectedDates;
    }

    public DatePicker getDatePicker() {
        return this.datePicker;
    }

    public void setDateRange(String dateRange) {
        selectedDates.clear();
        selectedDates.addAll(TimeUtil.parseDateRange(dateRange));
        updateTextField();
    }

    private void setUpDatePicker() {
        this.datePicker.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                return TimeUtil.formatDate(date);
            }

            @Override
            public LocalDate fromString(String string) {
                return TimeUtil.parseDate(string);
            }
        });

        EventHandler<MouseEvent> mouseClickedEventHandler = (MouseEvent clickEvent) -> {
            if (clickEvent.getButton() == MouseButton.PRIMARY) {
                if (!this.selectedDates.contains(this.datePicker.getValue())) {
                    this.selectedDates.add(datePicker.getValue());

                } else {
                    this.selectedDates.remove(this.datePicker.getValue());

                    this.datePicker.setValue(null);
                }

            }
            this.datePicker.show();
            clickEvent.consume();
        };

        this.datePicker.setDayCellFactory((DatePicker param) -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);

                //...
                if (item != null && !empty) {
                    //...
                    addEventHandler(MouseEvent.MOUSE_CLICKED, mouseClickedEventHandler);
                } else {
                    //...
                    removeEventHandler(MouseEvent.MOUSE_CLICKED, mouseClickedEventHandler);
                }

                if (selectedDates.contains(item)) {

                    setStyle("-fx-background-color: rgba(3, 169, 244, 0.7);");

                } else {
                    setStyle(null);

                }
            }
        });

    }

    private static Set<LocalDate> getTailEndDatesToRemove(Set<LocalDate> dates, LocalDate date) {
        TreeSet<LocalDate> tempTree = new TreeSet<>(dates);
        int lowerSize = tempTree.headSet(date).size();
        int higherSize = tempTree.tailSet(date, false).size();

        return (lowerSize <= higherSize) ? tempTree.headSet(date) : tempTree.tailSet(date, false);
    }

    private static LocalDate getClosestDateInTree(TreeSet<LocalDate> dates, LocalDate date) {
        if (dates.isEmpty()) return null;
        LocalDate lower = dates.floor(date);
        LocalDate higher = dates.ceiling(date);
        return (lower == null) ? higher : (higher == null || DAYS.between(date, lower) <= DAYS.between(date, higher)) ? lower : higher;
    }

    private static Set<LocalDate> getRangeGaps(LocalDate min, LocalDate max) {
        Set<LocalDate> rangeGaps = new LinkedHashSet<>();
        for (LocalDate date = min.plusDays(1); date.isBefore(max); date = date.plusDays(1)) {
            rangeGaps.add(date);
        }
        return rangeGaps;
    }

    @Override
    public String getTextFieldText() {
        if (!withRange) return selectedDates.toString();
        return datePicker.getEditor().getText();
    }

    @Override
    public void setTextFieldText(String text) {
        if (!withRange) return;
        if (text == null) return;
        if (Objects.equals(text, "")) return;
        setDateRange(text);
        datePicker.commitValue();
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