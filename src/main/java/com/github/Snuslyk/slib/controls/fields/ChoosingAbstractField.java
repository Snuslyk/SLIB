package com.github.Snuslyk.slib.controls.fields;

import com.dlsc.gemsfx.SearchField;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.github.Snuslyk.slib.factory.ButtonFactory.*;

public class ChoosingAbstractField extends AbstractTextField implements AllowPopup {
    protected static final int descFontSize = 20;
    private static final int mainFontSize = 20;
    private static final int Hmargin = 20;
    private static final int Vmargin = 5;
    private static final int height = 40;
    private static final int popUpHeight = 99;

    private final Supplier<ObservableList<String>> items;
    private Pane outOfBounds;

    public ChoosingSearchField searchField;

    public ChoosingAbstractField(String key, String text, String descriptionText, Supplier<ObservableList<String>> items, @Nullable String textFieldText) {
        super(key, text, descriptionText, textFieldText);
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

        VBox vBox = null;
        if (container instanceof VBox){
            vBox = (VBox) container;
        }

        System.out.println(items.get());

        searchField = new ChoosingAbstractField.ChoosingSearchField(outOfBounds);
        searchField.setMinHeight(height);
        searchField.setMaxHeight(height);

        searchField.addEventHandler(SearchField.SearchEvent.SEARCH_FINISHED, (evt) -> {
            if (!isAllowPopup()) {
                if (searchField.getPopup().isShowing()) {
                    searchField.getPopup().hide();
                }
            }
        });

        Label descriptionLabel = new Label(descriptionText);
        descriptionTextFieldOptions(descriptionLabel, descFontSize, Hmargin);

        textFieldOptions(promptText, mainFontSize, Hmargin, height, textFieldText, searchField.getEditor());
        if (vBox == null)
            searchField.prefWidthProperty().bind(container.widthProperty());
        else
            searchField.prefWidthProperty().bind(vBox.widthProperty());

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

        field.getChildren().addAll(descriptionLabel, buttonContainer);
        if (vBox == null)
            container.getChildren().add(field);
        else
            vBox.getChildren().add(field);

    }

    @Override
    public String getTextFieldText() {
        return searchField.getText();
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

    public class ChoosingSearchField extends SearchField<String> {

        public ChoosingSearchField(Pane outOfBounds) {
            setSuggestionProvider(request -> items.get().stream()
                    .filter(item -> item.toLowerCase().contains(request.getUserText().toLowerCase()))
                    .collect(Collectors.toList()));

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

    private boolean allowPopup = false;

    public void setAllowPopup(boolean allowPopup) {
        this.allowPopup = allowPopup;
    }

    public boolean isAllowPopup() {
        return allowPopup;
    }
}
