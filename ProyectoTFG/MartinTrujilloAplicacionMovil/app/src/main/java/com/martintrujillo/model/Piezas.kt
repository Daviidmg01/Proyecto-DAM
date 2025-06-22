package com.martintrujillo.model

data class Piezas (
    val idPieza: Int,
    val nombrePieza: String,
    val cantidadDisponible: Int,
    val precioCompra: Float,
    val precioVenta: Float,
    val stockMinimo: Int,
    val idProveedor: Int
)