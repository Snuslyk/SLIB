package com.github.Snuslyk.slib.electives;

public class NewObject implements ManageableElectives {
    private String name;

    public NewObject(String name) {
        this.name = name;
    }

    @Override
    public String getDisplayName() {
        return name;
    }

    @Override
    public void setDisplayName(String displayName) {
        name = displayName;
    }
}
