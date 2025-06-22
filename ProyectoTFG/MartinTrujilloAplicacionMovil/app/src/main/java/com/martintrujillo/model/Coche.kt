package com.martintrujillo.model

data class Coche (
    val idCoche: Int?,
    val matricula: String,
    val marca: String,
    val modelo: String,
    val idCliente: Int,
    val imagen: String,
    val enGaraje: Boolean
)