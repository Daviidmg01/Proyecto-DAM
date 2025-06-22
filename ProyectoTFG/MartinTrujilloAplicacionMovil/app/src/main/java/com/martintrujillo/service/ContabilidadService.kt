package com.martintrujillo.service

import com.martintrujillo.constantes.Constantes
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.SQLException

class ContabilidadService {

    fun insertarIngreso(cantidad: Double, concepto: String, idUsuario: Int?, callback: (Boolean) -> Unit) {
        Thread {
            var connection: Connection? = null
            var statement: PreparedStatement? = null
            try {
                connection = DriverManager.getConnection(
                    Constantes.URLCONNECTION,
                    Constantes.USUARIO,
                    Constantes.CONTRASENIA)
                val sql = "INSERT INTO contabilidad (tipo, cantidad, concepto, id_usuario) VALUES ('ingreso', ?, ?, ?)"
                statement = connection.prepareStatement(sql)

                statement.setDouble(1, cantidad)
                statement.setString(2, concepto)

                if (idUsuario != null) {
                    statement.setInt(3, idUsuario)
                } else {
                    statement.setNull(3, java.sql.Types.INTEGER)
                }

                val filasInsertadas = statement.executeUpdate()
                callback(filasInsertadas > 0)
            } catch (e: Exception) {
                e.printStackTrace()
                callback(false)
            } finally {
                try {
                    statement?.close()
                    connection?.close()
                } catch (e: SQLException) {
                    e.printStackTrace()
                }
            }
        }.start()
    }

}