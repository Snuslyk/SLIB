package com.github.Snuslyk.slib.util;

import com.github.Snuslyk.slib.factory.ButtonFactory;

import java.util.function.Consumer;

public interface FieldWork {
    void run(ButtonFactory.TextFieldWrapper field);
}
