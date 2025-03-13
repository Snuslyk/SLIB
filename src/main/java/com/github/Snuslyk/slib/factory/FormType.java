package com.github.Snuslyk.slib.factory;

import java.util.Optional;

public abstract class FormType {

    public String name;

    abstract FormType name(String name);

    abstract public void setup(SetupData setupData);

    public <T extends FormType> Optional<T> cast(Class<T> t){
        if (this.getClass().isInstance(t)) {
            return Optional.of((T) this);
        }
        return Optional.empty();
    }
}
