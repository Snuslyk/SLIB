package com.github.Snuslyk.slib.controls.fields;

import com.dlsc.gemsfx.SearchField;
import com.dlsc.gemsfx.TagsField;
import com.github.Snuslyk.slib.factory.AbstractTextField;
import com.github.Snuslyk.slib.factory.AllowPopup;
import com.github.Snuslyk.slib.factory.AbstractField;
import com.github.Snuslyk.slib.util.StylesUtil;
import com.sun.istack.Nullable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.github.Snuslyk.slib.factory.ButtonFactory.descriptionTextFieldOptions;
import static com.github.Snuslyk.slib.factory.ButtonFactory.errorSetter;

public class MultiChooseField extends AbstractTextField implements AllowPopup {

    private static final int descFontSize = 20;
    private static final int mainFontSize = 20;
    private static final int Hmargin = 20;
    private static final int Vmargin = 5;
    private static final int height = 40;
    private static final int popUpHeight = 99;

    private final Supplier<ObservableList<String>> items;
    private Pane outOfBounds;

    private String textFieldText;

    public ChoosingTagsField searchField;

    public MultiChooseField(String key, String text, String descriptionText, String errorSample, Supplier<ObservableList<String>> items, @Nullable String textFieldText) {
        super(text, descriptionText, key, errorSample, textFieldText);
        this.items = items;
    }

    public void register(Pane container, Pane outOfBounds){
        this.outOfBounds = outOfBounds;
        register(container);
    }

    @Override
    public void register(Pane container) {
        field = new VBox();
        field.setSpacing(Vmargin);
        searchField = new ChoosingTagsField();

        searchField.addEventHandler(SearchField.SearchEvent.SEARCH_FINISHED, (evt) -> {
            if (!isAllowPopup()) {
                if (searchField.getPopup().isShowing()) {
                    searchField.getPopup().hide();
                }
            }
        });

        if (textFieldText != null && !textFieldText.isEmpty()) {
            String[] itemsList = textFieldText.split(",\\s*");
            for (String item : itemsList) {
                searchField.select(item);
            }
        }

        System.out.println(textFieldText);

        // Создание SVGPath для стрелки
        SVGPath arrow = new SVGPath();
        arrow.setContent("M1, 0L7, 6L13, 0"); // Стрелка вниз
        arrow.setFill(Color.TRANSPARENT);
        arrow.setStroke(Color.web("#3D3D3D"));
        arrow.setStrokeWidth(1.0);

        // Label с описанием
        Label descriptionLabel = new Label(descriptionText);
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
        searchField.getTags().forEach(tag -> builder.append(tag).append(", "));
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

                if (!isAllowPopup()) setAllowPopup(true);

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

                if (!isAllowPopup()) setAllowPopup(true);

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
            StylesUtil.add(this, "search-field-d");

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

    private boolean allowPopup = false;

    public void setAllowPopup(boolean allowPopup) {
        this.allowPopup = allowPopup;
    }

    public boolean isAllowPopup() {
        return allowPopup;
    }
}