package com.martintrujillo.controller.Dto

data class PiezaReparacionDetalle(
    val matricula: String,
    val nombrePieza: String,
    val cantidadUsada: Int,
    val precioUnidad: Float,
    val precioTotal: Float,
    val nombreMecanico: String
)
