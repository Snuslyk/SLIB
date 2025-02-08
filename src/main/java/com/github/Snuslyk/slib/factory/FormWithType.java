package com.github.Snuslyk.slib.factory;

public interface FormWithType<T extends FormType> {
    T type(Class<?> clazz);
}
