package com.github.Snuslyk.slib.factory;

import javafx.scene.layout.Pane;

public abstract class AbstractField {

    protected static final int descFontSize = 0;
    protected static final int mainFontSize = 0;
    protected static final int Hmargin = 0;
    protected static final int Vmargin = 0;
    protected static final int height = 0;

    abstract public String getTextFieldText();
    abstract public void setTextFieldText(String text);
    abstract public void setError(String message);
    abstract public void clearError();
    abstract public boolean getError();
    abstract public void register(Pane container);
    abstract public String getKey();
}
