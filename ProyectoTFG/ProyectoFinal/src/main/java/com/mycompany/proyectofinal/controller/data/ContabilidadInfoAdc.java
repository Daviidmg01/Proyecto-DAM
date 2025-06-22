/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectofinal.controller.data;

import com.mycompany.proyectofinal.model.Contabilidad;
import java.util.Date;

/**
 *
 * @author Usuario
 */
public class ContabilidadInfoAdc extends Contabilidad{
    private String nombreUsuario;

    public ContabilidadInfoAdc() {
    }

    public ContabilidadInfoAdc(String nombreUsuario, int idTransaccion, String tipo, int cantidad, Date fecha, String concepto, int idUsuario) {
        super(idTransaccion, tipo, cantidad, fecha, concepto, idUsuario);
        this.nombreUsuario = nombreUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }
}
