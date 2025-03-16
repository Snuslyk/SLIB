package com.github.Snuslyk.slib.factory;

import javafx.css.PseudoClass;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TableSelectFormType extends TableFormType {

    private Set<TableRow<Map<String, Object>>> selectedRows = new HashSet<>();

    @Override
    protected void setupRowFactory(TableView<Map<String, Object>> tableView) {
        PseudoClass filled = PseudoClass.getPseudoClass("filled");

        tableView.setRowFactory(tv -> {
            TableRow<Map<String, Object>> row = new TableRow<>();

            row.hoverProperty().addListener(((observableValue, aBoolean, t1) -> {
                if (!row.getPseudoClassStates().contains(filled) || selectedRows.contains(row)) return;
                if (t1) {
                    row.setStyle("-fx-background-color: #1C1C1C");
                } else {
                    row.setStyle("");
                }
            }));
            row.selectedProperty().addListener(((observableValue, aBoolean, t1) -> {
                if (!row.getPseudoClassStates().contains(filled)) return;
                if (aBoolean) {
                    if (!selectedRows.contains(row)) {
                        System.out.println("Sdadsa");
                        selectedRows.remove(row);
                        row.setStyle("");
                        return;
                    }
                    else {
                        selectedRows.add(row);
                        row.setStyle("-fx-text-fill: black");
                        row.setStyle("-fx-background-color: #FF9858");
                    }
                    return;
                }
                if (selectedRows.contains(row)) {
                    System.out.println("Sdadsa");
                    selectedRows.remove(row);
                    row.setStyle("");
                    return;
                }
                else {
                    selectedRows.add(row);
                    row.setStyle("-fx-text-fill: black");
                    row.setStyle("-fx-background-color: #FF9858");
                }
            }));

            row.itemProperty().addListener((obs, oldItem, newItem) -> rowListener(row, filled, newItem));
            return row;
        });
    }
}
