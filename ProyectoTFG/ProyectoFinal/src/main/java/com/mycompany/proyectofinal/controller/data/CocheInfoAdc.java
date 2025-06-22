package com.mycompany.proyectofinal.controller.data;

import java.util.Date;

/**
 * Clase con los detalles adicionales de los coches
 */
public class CocheInfoAdc {
    
    private String motivo;
    private Date fechaEntrada;
    private String propietario;

    public CocheInfoAdc() {
    }

    public CocheInfoAdc(String motivo, Date fechaEntrada, String propietario) {
        this.motivo = motivo;
        this.fechaEntrada = fechaEntrada;
        this.propietario = propietario;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Date getFechaEntrada() {
        return fechaEntrada;
    }

    public void setFechaEntrada(Date fechaEntrada) {
        this.fechaEntrada = fechaEntrada;
    }

    public String getPropietario() {
        return propietario;
    }

    public void setPropietario(String propietario) {
        this.propietario = propietario;
    }
    
}
