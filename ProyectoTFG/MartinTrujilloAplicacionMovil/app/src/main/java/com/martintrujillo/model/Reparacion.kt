package com.martintrujillo.model

import java.sql.Date

data class Reparacion (
    val idReparacion: Int,
    val idCoche: Int,
    val idMecanico: Int?,
    val motivo: String,
    val horasTrabajo: Int?,
    val costeManoObra: Float?,
    val costeTotalPiezas: Float?,
    val costeTotalReparacion: Float?,
    val fechaEntrada: Date?,
    val fechaSalida: Date?,
    val estado: String
)