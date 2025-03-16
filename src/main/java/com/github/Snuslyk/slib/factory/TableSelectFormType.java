package com.github.Snuslyk.slib.factory;

import javafx.css.PseudoClass;
import javafx.event.Event;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;

import java.util.*;

public class TableSelectFormType extends TableFormType {

    private Set<TableRow<Map<String, Object>>> selectedRows = new HashSet<>();

    @Override
    protected void setupRowFactory(TableView<Map<String, Object>> tableView) {
        PseudoClass filled = PseudoClass.getPseudoClass("filled");

        tableView.setRowFactory(tv -> {
            TableRow<Map<String, Object>> row = new TableRow<>();

            row.hoverProperty().addListener((obs, wasHover, isHover) -> {
                if (row.getPseudoClassStates().contains(filled) && !selectedRows.contains(row)) {
                    row.setStyle(isHover ? "-fx-background-color: #1C1C1C" : "");
                }
            });

            row.setOnMouseClicked(event -> rowClick(row, filled));

            row.itemProperty().addListener((obs, oldItem, newItem) -> rowListener(row, filled, newItem));
            return row;
        });
    }

    private void rowClick(TableRow<Map<String, Object>> row, PseudoClass filled) {
        if (!row.getPseudoClassStates().contains(filled)) return;

        boolean isSelected = selectedRows.contains(row);
        if (isSelected) {
            selectedRows.remove(row);
            row.setStyle("");
        } else {
            selectedRows.add(row);
            row.setStyle("-fx-background-color: #1C1C1C");
        }
    }

    @Override
    protected void setupButtonColumn(TableView<Map<String, Object>> tableView) {
        TableColumn<Map<String, Object>, Void> buttonColumn = new TableColumn<>();
        buttonColumn.setResizable(false);
        buttonColumn.setReorderable(false);
        buttonColumn.setSortable(false);
        buttonColumn.setCellFactory(col -> {
            ButtonCell buttonCell = new ButtonCell();
            ToggleButton button = buttonCell.getButton();
            button.hoverProperty().addListener((observableValue, aBoolean, t1) -> {
                if (t1) {
                    button.setStyle("-fx-background-color: #121212");
                } else {
                    button.setStyle("-fx-background-color: transparent");
                }
            });
            return buttonCell;
        });

        tableView.getColumns().add(buttonColumn);
    }
}