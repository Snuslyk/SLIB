package com.github.Snuslyk.slib.factory;

public abstract class FormType {

    public String name;

    abstract FormType name(String name);

    abstract public void setup(SetupData setupData);
}
