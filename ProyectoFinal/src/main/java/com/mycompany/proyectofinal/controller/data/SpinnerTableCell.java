/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectofinal.controller.data;

import javafx.scene.control.Spinner;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.IntegerStringConverter;

/**
 *
 * @author Usuario
 */
public class SpinnerTableCell extends TableCell<PiezaTableModel, Integer> {
    private final Spinner<Integer> spinner;

    public SpinnerTableCell() {
        spinner = new Spinner<>(0, 100, 0);
        spinner.setEditable(true);

        TextFormatter<Integer> formatter = new TextFormatter<>(
            new IntegerStringConverter(), 0,
            change -> change.getControlNewText().matches("\\d*") ? change : null
        );
        spinner.getEditor().setTextFormatter(formatter);
    }

    @Override
    protected void updateItem(Integer item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setGraphic(null);
        } else {
            spinner.getValueFactory().setValue(item);
            setGraphic(spinner);

            // Solo añadir el listener si la celda no está vacía
            spinner.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (!isEmpty()) {
                    PiezaTableModel pieza = (PiezaTableModel) getTableRow().getItem();
                    if (pieza != null) {
                        pieza.cantidadProperty().set(newVal);
                        getTableView().refresh();
                    }
                }
            });
        }
    }
}
