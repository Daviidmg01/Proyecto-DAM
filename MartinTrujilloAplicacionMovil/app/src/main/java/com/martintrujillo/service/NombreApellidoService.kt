package com.martintrujillo.service

import com.martintrujillo.constantes.Constantes
import com.martintrujillo.model.Usuario
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.SQLException

class NombreApellidoService {

    fun actualizarUsuario(usuario: Usuario): Int {
        var connection: Connection? = null
        var preparedStatement: PreparedStatement? = null
        var filasAfectadas = 0

        try {
            // 1. Establecer conexión
            Class.forName("com.mysql.jdbc.Driver")
            connection = DriverManager.getConnection(
                Constantes.URLCONNECTION,
                Constantes.USUARIO,
                Constantes.CONTRASENIA
            )

            // 2. Crear consulta SQL para actualizar
            val sql = """
                UPDATE usuarios 
                SET nombre = ?, email = ?, contraseña = ?, rol = ? 
                WHERE id_usuario = ?
            """.trimIndent()

            // 3. Preparar statement
            preparedStatement = connection.prepareStatement(sql)
            preparedStatement.setString(1, usuario.nombre)
            preparedStatement.setString(2, usuario.email)
            preparedStatement.setString(3, usuario.contrasenia)
            preparedStatement.setString(4, usuario.rol)
            preparedStatement.setInt(5, usuario.id)

            // 4. Ejecutar actualización
            filasAfectadas = preparedStatement.executeUpdate()

        } catch (e: SQLException) {
            e.printStackTrace()
            throw e
        } finally {
            // 5. Cerrar recursos
            preparedStatement?.close()
            connection?.close()
        }

        return filasAfectadas
    }
}