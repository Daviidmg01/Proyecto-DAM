package com.mycompany.proyectofinal.controller.data;

import com.mycompany.proyectofinal.model.Reparacion;
import java.util.Date;

/**
 *
 * @author Usuario
 */
public class ReparacionesInfoAdc extends Reparacion{
    private String matricula;
    private String nombreMecanico;

    public ReparacionesInfoAdc() {
    }

    public ReparacionesInfoAdc(String matricula, String nombreMecanico, int idReparacion, int idCoche, int idMecanico, String motivo, int horasTrabajo, float costeManoObra, float costeTotalPiezas, float costeTotalReparacion, Date fechaEntrada, Date fechaSalida, String estado) {
        super(idReparacion, idCoche, idMecanico, motivo, horasTrabajo, costeManoObra, costeTotalPiezas, costeTotalReparacion, fechaEntrada, fechaSalida, estado);
        this.matricula = matricula;
        this.nombreMecanico = nombreMecanico;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getNombreMecanico() {
        return nombreMecanico;
    }

    public void setNombreMecanico(String nombreMecanico) {
        this.nombreMecanico = nombreMecanico;
    }
}
