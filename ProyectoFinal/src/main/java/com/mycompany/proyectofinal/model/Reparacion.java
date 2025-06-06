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
public class Reparacion {
    private int idReparacion;
    private int idCoche;
    private int idMecanico;
    private String motivo;
    private int horasTrabajo;
    private float costeManoObra;
    private float costeTotalPiezas;
    private float costeTotalReparacion;
    private Date fechaEntrada;
    private Date fechaSalida;
    private String estado;

    public Reparacion() {
    }

    public Reparacion(int idReparacion, int idCoche, int idMecanico, String motivo, int horasTrabajo, float costeManoObra, float costeTotalPiezas, float costeTotalReparacion, Date fechaEntrada, Date fechaSalida, String estado) {
        this.idReparacion = idReparacion;
        this.idCoche = idCoche;
        this.idMecanico = idMecanico;
        this.motivo = motivo;
        this.horasTrabajo = horasTrabajo;
        this.costeManoObra = costeManoObra;
        this.costeTotalPiezas = costeTotalPiezas;
        this.costeTotalReparacion = costeTotalReparacion;
        this.fechaEntrada = fechaEntrada;
        this.fechaSalida = fechaSalida;
        this.estado = estado;
    }

    public int getIdReparacion() {
        return idReparacion;
    }

    public void setIdReparacion(int idReparacion) {
        this.idReparacion = idReparacion;
    }

    public int getIdCoche() {
        return idCoche;
    }

    public void setIdCoche(int idCoche) {
        this.idCoche = idCoche;
    }

    public int getIdMecanico() {
        return idMecanico;
    }

    public void setIdMecanico(int idMecanico) {
        this.idMecanico = idMecanico;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public int getHorasTrabajo() {
        return horasTrabajo;
    }

    public void setHorasTrabajo(int horasTrabajo) {
        this.horasTrabajo = horasTrabajo;
    }

    public float getCosteManoObra() {
        return costeManoObra;
    }

    public void setCosteManoObra(float costeManoObra) {
        this.costeManoObra = costeManoObra;
    }

    public float getCosteTotalPiezas() {
        return costeTotalPiezas;
    }

    public void setCosteTotalPiezas(float costeTotalPiezas) {
        this.costeTotalPiezas = costeTotalPiezas;
    }

    public float getCosteTotalReparacion() {
        return costeTotalReparacion;
    }

    public void setCosteTotalReparacion(float costeTotalReparacion) {
        this.costeTotalReparacion = costeTotalReparacion;
    }

    public Date getFechaEntrada() {
        return fechaEntrada;
    }

    public void setFechaEntrada(Date fechaEntrada) {
        this.fechaEntrada = fechaEntrada;
    }

    public Date getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(Date fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    
}
