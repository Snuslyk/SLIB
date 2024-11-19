package com.github.Snuslyk.slib.electives;

import com.github.Snuslyk.slib.factory.Form;

public class Button extends Elective implements ButtonElective {

    private final Form form;

    public Button(String name, Form form) {
        super(name);
        this.form = form;
    }

    public Form getForm() {
        return form;

    }

    @Override
    public void pressed() {
        // Ваш код на случай, если кнопка нажата
    }
}
