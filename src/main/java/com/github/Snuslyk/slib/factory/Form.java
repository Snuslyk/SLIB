package com.github.Snuslyk.slib.factory;

import com.github.Snuslyk.slib.Filter;
import com.github.Snuslyk.slib.HibernateUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Form {

    private final List<String> options;
    private final Type[] type;
    private final Class<?>[] tableClass;
    private final Filter[] filter;
    private final List<List<Column>> columns;

    private Form(Type[] type, Class<?>[] tableClass, Filter[] filter, List<List<Column>> columns, List<String> options) {
        this.options = options;
        this.type = type;
        this.tableClass = tableClass;
        this.filter = filter;
        this.columns = columns;
    }

    public List<Object> get(int option){
        return Collections.singletonList(HibernateUtil.getObjectWithFilter(tableClass[option], filter[option]));
    }

    public Type[] getType() {
        return type;
    }
    public Class<?>[] getTableClass() {
        return tableClass;
    }
    public Filter[] getFilter() {
        return filter;
    }
    public List<List<Column>> getColumns() {
        return columns;
    }

    public List<String> getOptions() {
        return options;
    }

    public static class Builder {
        private int optionSize;
        private int optionId = -1;
        private final List<String> options = new ArrayList<>();
        private Type[] type;
        private Class<?>[] tableClass;
        private Filter[] filter;
        private final List<List<Column>> columns = new ArrayList<>();

        public Builder type(Type type){
            this.type[optionId] = type;
            return this;
        }
        public Builder tableClass(Class<?> clazz){
            for (int i = optionId; i < optionSize; i++){
                if (i < 0) i = 0;
                this.tableClass[i] = clazz;
            }
            return this;
        }
        public Builder filter(Filter filter){
            this.filter[optionId] = filter;
            return this;
        }
        public Builder column(String displayName, String key){
            this.columns.get(optionId).add(new Column(displayName, key));
            return this;
        }
        public Builder sizeOfOption(int size){
            optionSize = size;
            type = new Type[size];
            tableClass = new Class[size];
            filter = new Filter[size];
            return this;
        }
        public Builder option(String name){
            optionId+=1;
            options.add(name);
            return this;
        }
        public Form build(){
            return new Form(type, tableClass, filter, columns, options);
        }
    }

    public enum Type{
        TABLE,
        CREATE
    }

    public record Column(String displayName, String key){}

}
