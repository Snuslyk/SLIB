package com.github.Snuslyk.slib.electives;

public class Elective implements ManageableElectives {
    private String name;

    public Elective(String name) {
        this.name = name;
    }

    public Elective(String name, Elective section) {
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
