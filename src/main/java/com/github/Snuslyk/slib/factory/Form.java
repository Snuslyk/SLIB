package com.github.Snuslyk.slib.factory;

import com.github.Snuslyk.slib.Controller;
import com.github.Snuslyk.slib.FilterIO;
import com.github.Snuslyk.slib.HibernateUtil;
import com.sun.istack.Nullable;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Form {

    private final List<String> options;
    private final Type[] type;
    private final Class<?>[] tableClass;
    private final FilterIO[] filter;
    private final List<List<Column>> columns;
    private final List<List<TableActionButton>> tableButtons;

    private Form(Type[] type, Class<?>[] tableClass, FilterIO[] filter, List<List<Column>> columns, List<String> options, List<List<TableActionButton>> tableButtons) {
        this.options = options;
        this.type = type;
        this.tableClass = tableClass;
        this.filter = filter;
        this.columns = columns;
        this.tableButtons = tableButtons;
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
    public FilterIO[] getFilter() {
        return filter;
    }
    public List<List<Column>> getColumns() {
        return columns;
    }

    public List<String> getOptions() {
        return options;
    }

    public List<List<TableActionButton>> getTableButtons() {
        return tableButtons;
    }

    public static class Builder {
        private int optionSize;
        private int optionId = -1;
        private final List<String> options = new ArrayList<>();
        private Type[] type;
        private Class<?>[] tableClass;
        private FilterIO[] filter;
        private final List<List<Column>> columns = new ArrayList<>();

        private List<String> usedDisplays = new ArrayList<>();

        private final List<List<TableActionButton>> tableButtons = new ArrayList<>();

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
        public Builder tableActionButton(String display, Color color, @Nullable String svg, @Nullable TableActionButtonIO io){
            tableButtons.get(optionId).add(new TableActionButton(display, color, svg, io));
            return this;
        }
        public Builder tableActionButton(String display, Color color, @Nullable String svg){
            tableButtons.get(optionId).add(new TableActionButton(display, color, svg, null));
            return this;
        }
        public Builder tableActionButton(TableActionButton tableActionButton){
            tableButtons.get(optionId).add(tableActionButton);
            return this;
        }

        public Builder filter(FilterIO filter){
            this.filter[optionId] = filter;
            return this;
        }
        public Builder column(String displayName, String key){
            if (usedDisplays.contains(displayName)) {
                System.out.println("Уже есть колонна с таким названием!!!");
                return this;
            }
            this.columns.get(optionId).add(new Column(displayName, key));
            usedDisplays.add(displayName);
            return this;
        }
        public Builder sizeOfOption(int size){
            optionSize = size;
            type = new Type[size];
            tableClass = new Class[size];
            filter = new FilterIO[size];
            return this;
        }
        public Builder option(String name) {
            usedDisplays = new ArrayList<>();
            optionId += 1;
            options.add(name);
            columns.add(new ArrayList<>());
            tableButtons.add(new ArrayList<>());
            return this;
        }

        public Form build(){
            return new Form(type, tableClass, filter, columns, options, tableButtons);
        }
    }

    public enum Type{
        TABLE,
        CREATE
    }

    public record Column(String displayName, String key){}

    public record TableActionButton(String display, Color color, @Nullable String svg, @Nullable TableActionButtonIO io){}

    public interface TableActionButtonIO{
        void run(Controller controller, Map<String, Object> rowData);
    }

    public static class TableActionButtons{
        public static final TableActionButton DELETE = new TableActionButton("Удалить", Color.RED, null, (controller, data) -> {
            Form form = controller.getExternalObjects().get(controller.getSectionIndex()).get(controller.getObjectIndex()).getForm();
            List<?> list = HibernateUtil.getObjectWithFilter(form.getTableClass()[controller.getOptionIndex()], form.getFilter()[controller.getOptionIndex()]);
            Object o = list.get(0); // Вместо 0 id в таблицы
            HibernateUtil.remove(o);
            controller.modelUpdate();
        });
        public static final TableActionButton EDIT = new TableActionButton("Изменить", Color.WHITE, null, (controller, data) -> {

        });
    }

}
