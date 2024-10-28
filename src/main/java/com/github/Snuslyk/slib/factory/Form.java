package com.github.Snuslyk.slib.factory;

import com.github.Snuslyk.slib.Filter;
import com.github.Snuslyk.slib.HibernateUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Form {

    private final Type type;
    private final Class<?> tableClass;
    private final Filter filter;
    private final List<String> columns;

    private Form(Type type, Class<?> tableClass, Filter filter, List<String> columns) {
        this.type = type;
        this.tableClass = tableClass;
        this.filter = filter;
        this.columns = columns;
    }

    public List<Object> get(){
        return Collections.singletonList(HibernateUtil.getObjectWithFilter(tableClass, filter));
    }

    public Type getType() {
        return type;
    }
    public Class<?> getTableClass() {
        return tableClass;
    }
    public Filter getFilter() {
        return filter;
    }
    public List<String> getColumns() {
        return columns;
    }

    public static class Builder {
        private Type type;
        private Class<?> tableClass;
        private Filter filter;
        private final List<String> columns = new ArrayList<>();

        public Builder type(Type type){
            this.type = type;
            return this;
        }
        public Builder tableClass(Class<?> clazz){
            this.tableClass = clazz;
            return this;
        }
        public Builder filter(Filter filter){
            this.filter = filter;
            return this;
        }
        public Builder column(String columnName){
            this.columns.add(columnName);
            return this;
        }
        public Form build(){
            return new Form(type, tableClass, filter, columns);
        }
    }

    public enum  Type{
        TABLE,
        CREATE
    }

}
