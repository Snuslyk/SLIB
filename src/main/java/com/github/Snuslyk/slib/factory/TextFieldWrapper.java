package com.github.Snuslyk.slib.factory;

import javafx.scene.layout.Pane;

public interface TextFieldWrapper {
    String getTextFieldText();
    void setTextFieldText(String text);
    void setError(String message);
    void clearError();
    boolean getError();
    void register(Pane container);
    String getKey();
}
