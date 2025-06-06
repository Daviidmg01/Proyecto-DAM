// com.martintrujillo.service/UsuarioService.kt
package com.martintrujillo.service

import android.content.Context
import android.widget.Toast
import com.martintrujillo.constantes.Constantes
import com.martintrujillo.model.Usuario
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet

class UsuarioService(private val context: Context) {


    fun autenticarUsuario(usuario: String, contrasenia: String, callback: (Boolean, Usuario?, String?) -> Unit) {
        Thread {
            var connection: Connection? = null
            var statement: PreparedStatement? = null
            var resultSet: ResultSet? = null

            try {
                Class.forName("com.mysql.jdbc.Driver")
                connection = DriverManager.getConnection(Constantes.URLCONNECTION, Constantes.USUARIO, Constantes.CONTRASENIA)

                val query = """
                    SELECT * FROM usuarios 
                    WHERE (nombre = ? OR email = ?) 
                    AND contraseña = ?
                """.trimIndent()

                statement = connection.prepareStatement(query)
                statement.setString(1, usuario)
                statement.setString(2, usuario)
                statement.setString(3, contrasenia)

                resultSet = statement.executeQuery()

                if (resultSet.next()) {
                    val usuarioObj = Usuario(
                        id = resultSet.getInt("id_usuario"),
                        nombre = resultSet.getString("nombre"),
                        email = resultSet.getString("email"),
                        contrasenia = resultSet.getString("contraseña"),
                        rol = resultSet.getString("rol")
                    )
                    callback(true, usuarioObj, null)
                } else {
                    callback(false, null, "Usuario no encontrado")
                }

            } catch (e: Exception) {
                e.printStackTrace()
                callback(false, null, "Error de conexión: ${e.message}")
            } finally {
                resultSet?.close()
                statement?.close()
                connection?.close()
            }
        }.start()
    }
}