package com.github.Snuslyk.slib;

public record Section(String name, Object... object) {

    @Override
    public String toString() {
        return name;
    }
}
