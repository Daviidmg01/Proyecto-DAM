package com.martintrujillo.service

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.martintrujillo.constantes.Constantes
import com.martintrujillo.model.Cliente
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Types

class ClienteService {
    fun insertarOActualizarCliente(
        context: Context,
        nombre: String,
        dni: String,
        telefono: String,
        email: String?,
        callback: (Boolean, Int?, String?) -> Unit // Ahora devuelve también el ID del cliente
    ) {
        Thread {
            var connection: Connection? = null
            var statement: PreparedStatement? = null
            var resultSet: ResultSet? = null
            var idCliente: Int? = null

            try {
                Class.forName("com.mysql.jdbc.Driver")
                connection = DriverManager.getConnection(
                    Constantes.URLCONNECTION,
                    Constantes.USUARIO,
                    Constantes.CONTRASENIA
                )

                // 1. Verificar si el cliente ya existe
                val queryExistencia = "SELECT id_cliente FROM clientes WHERE dni = ?"
                statement = connection.prepareStatement(queryExistencia)
                statement.setString(1, dni)
                resultSet = statement.executeQuery()

                if (resultSet.next()) {
                    // Cliente existe - obtener su ID
                    idCliente = resultSet.getInt("id_cliente")

                    // Opcional: Actualizar datos si han cambiado
                    val updateQuery = """
                    UPDATE clientes 
                    SET nombre = ?, telefono = ?, email = ?
                    WHERE id_cliente = ?
                """.trimIndent()
                    statement.close()
                    statement = connection.prepareStatement(updateQuery)
                    statement.setString(1, nombre)
                    statement.setString(2, telefono)
                    if (email.isNullOrBlank()) {
                        statement.setNull(3, Types.VARCHAR)
                    } else {
                        statement.setString(3, email)
                    }
                    statement.setInt(4, idCliente)
                    statement.executeUpdate()
                } else {
                    // Cliente no existe - insertar nuevo
                    val queryInsert = """
                    INSERT INTO clientes (nombre, dni, telefono, email) 
                    VALUES (?, ?, ?, ?)
                """.trimIndent()
                    statement.close()
                    statement = connection.prepareStatement(queryInsert, PreparedStatement.RETURN_GENERATED_KEYS)
                    statement.setString(1, nombre)
                    statement.setString(2, dni)
                    statement.setString(3, telefono)
                    statement.setString(4, email ?: "")
                    statement.executeUpdate()

                    // Obtener el ID generado
                    val generatedKeys = statement.generatedKeys
                    if (generatedKeys.next()) {
                        idCliente = generatedKeys.getInt(1)
                    }
                }

                callback(true, idCliente, null)

            } catch (e: Exception) {
                e.printStackTrace()
                callback(false, null, "Error: ${e.message}")
            } finally {
                try {
                    resultSet?.close()
                    statement?.close()
                    connection?.close()
                } catch (e: SQLException) {
                    e.printStackTrace()
                }
            }
        }.start()
    }

    fun consultarIdClientePorDNI(DNI: String, callback: (Int?) -> Unit) {
        val query = "SELECT id_cliente FROM clientes WHERE dni = ?"

        Thread {
            var connection: Connection? = null
            var statement: PreparedStatement? = null
            var resultSet: ResultSet? = null

            try {
                Class.forName("com.mysql.jdbc.Driver")
                connection = DriverManager.getConnection(
                    Constantes.URLCONNECTION,
                    Constantes.USUARIO,
                    Constantes.CONTRASENIA
                )

                statement = connection.prepareStatement(query)
                statement.setString(1, DNI)
                resultSet = statement.executeQuery()

                if (resultSet.next()) {
                    val idCliente = resultSet.getInt("id_cliente")
                    callback(idCliente) // Devuelve el id_cliente a través del callback
                } else {
                    callback(null) // Si no se encuentra el cliente, devuelve null
                }

            } catch (e: Exception) {
                e.printStackTrace()
                callback(null) // En caso de error, también devuelve null
            } finally {
                try {
                    resultSet?.close()
                    statement?.close()
                    connection?.close()
                } catch (e: SQLException) {
                    e.printStackTrace()
                }
            }
        }.start()
    }

    fun consultarClientePorId(idCliente: Int, callback: (Cliente?) -> Unit) {
        val query = "SELECT * FROM clientes WHERE id_cliente = ?"

        Thread {
            var connection: Connection? = null
            var statement: PreparedStatement? = null
            var resultSet: ResultSet? = null

            try {
                Class.forName("com.mysql.jdbc.Driver")
                connection = DriverManager.getConnection(
                    Constantes.URLCONNECTION,
                    Constantes.USUARIO,
                    Constantes.CONTRASENIA
                )

                statement = connection.prepareStatement(query)
                statement.setInt(1, idCliente)
                resultSet = statement.executeQuery()

                if (resultSet.next()) {
                    val cliente = Cliente(
                        nombre = resultSet.getString("nombre"),
                        dni = resultSet.getString("dni"),
                        telefono = resultSet.getString("telefono"),
                        email = resultSet.getString("email")
                    )
                    callback(cliente)
                } else {
                    callback(null)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                callback(null)
            } finally {
                try {
                    resultSet?.close()
                    statement?.close()
                    connection?.close()
                } catch (e: SQLException) {
                    e.printStackTrace()
                }
            }
        }.start()
    }


}