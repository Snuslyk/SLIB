package com.github.Snuslyk.slib.factory;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public abstract class AbstractField {

    protected static final int descFontSize = 0;
    protected static final int mainFontSize = 0;
    protected static final int Hmargin = 0;
    protected static final int Vmargin = 0;
    protected static final int height = 0;

    protected boolean isError;
    protected final Label errorLabel = new Label();
    protected VBox field;


    abstract public String getTextFieldText();
    abstract public void setTextFieldText(String text);
    abstract public void setError(String message);
    abstract public void clearError();
    abstract public boolean getError();
    abstract public void register(Pane container);
    abstract public String getKey();

    public boolean isError() {
        return isError;
    }

    protected void addToContainer(Pane container) {
        container.getChildren().add(field);
    }

    protected void initializeField() {
        field = new VBox();
        field.setSpacing(Vmargin);
    }


}
