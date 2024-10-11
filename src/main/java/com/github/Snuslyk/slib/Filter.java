package com.github.Snuslyk.slib;

public class Filter {

    public final String filteredValue;
    public final Object exceptedValue;
    public final Type type;

    public Filter(String filteredValue, Object exceptedValue, Type type){
        this.filteredValue = filteredValue;
        this.exceptedValue = exceptedValue;
        this.type = type;
    }

    public enum Type {
        ONLY,
        ADD
    }
}
