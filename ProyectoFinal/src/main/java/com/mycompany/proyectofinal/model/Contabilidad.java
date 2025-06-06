/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectofinal.model;

import java.util.Date;

/**
 *
 * @author dav85
 */
public class Contabilidad {
    private int idTransaccion;
    private String tipo;
    private int cantidad;
    private Date fecha;
    private String concepto;
    private int idUsuario;

    public Contabilidad() {
    }

    public Contabilidad(int idTransaccion, String tipo, int cantidad, Date fecha, String concepto, int idUsuario) {
        this.idTransaccion = idTransaccion;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.fecha = fecha;
        this.concepto = concepto;
        this.idUsuario = idUsuario;
    }

    public int getIdTransaccion() {
        return idTransaccion;
    }

    public void setIdTransaccion(int idTransaccion) {
        this.idTransaccion = idTransaccion;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
    
    
    
}
