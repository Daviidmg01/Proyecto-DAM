package com.mycompany.proyectofinal.controller.data;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *Clase con el modelo de pieza para el tableModel de compra
 */
public class PiezaTableModel {

    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty nombre = new SimpleStringProperty();
    private final IntegerProperty stock = new SimpleIntegerProperty();
    private final IntegerProperty minimo = new SimpleIntegerProperty();
    private final FloatProperty precio = new SimpleFloatProperty();
    private final IntegerProperty cantidad = new SimpleIntegerProperty(0); // Inicialmente 0
    private final BooleanProperty seleccionado = new SimpleBooleanProperty(false); // Inicialmente no seleccionado

    public PiezaTableModel(int id, String nombre, int stock, int minimo, float precio) {
        this.id.set(id);
        this.nombre.set(nombre);
        this.stock.set(stock);
        this.minimo.set(minimo);
        this.precio.set(precio);
    }

    // Getters y Setters básicos
    public int getId() {
        return id.get();
    }

    public String getNombre() {
        return nombre.get();
    }

    public int getStock() {
        return stock.get();
    }

    public int getMinimo() {
        return minimo.get();
    }

    public float getPrecio() {
        return precio.get();
    }

    public int getCantidad() {
        return cantidad.get();
    }

    public boolean isSeleccionado() {
        return seleccionado.get();
    }

    // Properties para enlazar con la TableView
    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty nombreProperty() {
        return nombre;
    }

    public IntegerProperty stockProperty() {
        return stock;
    }

    public IntegerProperty minimoProperty() {
        return minimo;
    }

    public IntegerProperty cantidadProperty() {
        return cantidad;
    }

    public BooleanProperty seleccionadoProperty() {
        return seleccionado;
    }
    
    public FloatProperty precioProperty() {
        return precio;
    }
}
