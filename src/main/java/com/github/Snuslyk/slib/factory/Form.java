package com.github.Snuslyk.slib.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class Form {

    private List<FormType> formTypes;
    private HashMap<String, Object> data;

    private Form() {
    }

    private void setFormTypes(List<FormType> formTypes) {
        if (this.formTypes == null) {
            this.formTypes = formTypes;
        }
    }

    public List<FormType> getFormTypes() {
        return formTypes;
    }

    private List<String> options;

    public List<String> getOptions(){
        setupOptions();
        System.out.println(options);
        return options;
    }

    private void setupOptions(){
        if (options != null) {
            if (options.size() == formTypes.size()) return;
            setOptions();
        } else
            setOptions();
    }

    private void setOptions(){
        options = new ArrayList<>();
        for (FormType formType : formTypes){
            options.add(formType.name);
        }
    }

    private void setData(HashMap<String, Object> data){
        this.data = data;
    }

    public HashMap<String, Object> getData() {
        return data;
    }

    public Object getData(String s){
        return s;
    }
    public <T> Optional<T> getData(String s, Class<T> tClass){
        Object o = getData(s);
        if (!tClass.isInstance(o)) return Optional.empty();
        return Optional.of((T) o);
    }

    public static class Builder {

        private final List<FormType> formTypes = new ArrayList<>();
        private final HashMap<String, Object> data = new HashMap<>();

        public Builder add(FormType formType) {
            formTypes.add(formType);
            return this;
        }

        public Builder data(String string, Object o){
            data.put(string, o);
            return this;
        }

        public Form build() {
            Form form = new Form();
            form.setFormTypes(formTypes);
            form.setData(data);
            return form;
        }
    }
}
