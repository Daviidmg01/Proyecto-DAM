package com.martintrujillo.model

data class Usuario(
    val id: Int,
    val nombre: String,
    val email: String,
    val contrasenia: String,
    val rol: String
)