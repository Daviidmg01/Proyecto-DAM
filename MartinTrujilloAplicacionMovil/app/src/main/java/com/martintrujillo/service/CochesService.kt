package com.martintrujillo.service

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.martintrujillo.constantes.Constantes
import com.martintrujillo.model.Coche
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

class CochesService {

    fun obtenerCochesEnGaraje(callback: (List<Coche>?, String?) -> Unit) {
        Thread {
            var connection: Connection? = null
            var statement: PreparedStatement? = null
            var resultSet: ResultSet? = null
            val listaCoches = mutableListOf<Coche>()

            try {
                Class.forName("com.mysql.jdbc.Driver")
                connection = DriverManager.getConnection(
                    Constantes.URLCONNECTION,
                    Constantes.USUARIO,
                    Constantes.CONTRASENIA
                )

                val query = "SELECT * FROM coches WHERE en_garaje = 1"
                statement = connection.prepareStatement(query)
                resultSet = statement.executeQuery()

                while (resultSet.next()) {
                    val coche = Coche(
                        idCoche = resultSet.getInt("id_coche"),
                        matricula = resultSet.getString("matricula"),
                        marca = resultSet.getString("marca"),
                        modelo = resultSet.getString("modelo"),
                        idCliente = resultSet.getInt("id_cliente"),
                        imagen = resultSet.getString("imagen"),
                        enGaraje = resultSet.getBoolean("en_garaje")
                    )
                    listaCoches.add(coche)
                }

                callback(listaCoches, null)

            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
                callback(null, "Error: Driver JDBC no encontrado")
            } catch (e: SQLException) {
                e.printStackTrace()
                callback(null, "Error de base de datos: ${e.message}")
            } catch (e: Exception) {
                e.printStackTrace()
                callback(null, "Error inesperado: ${e.message}")
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
    fun actualizarCocheFueraDeGaraje(idCoche: Int, callback: (Boolean) -> Unit) {
        Thread {
            var conexion: Connection? = null
            var preparedStatement: PreparedStatement? = null
            var exito = false

            try {
                conexion = DriverManager.getConnection(
                    Constantes.URLCONNECTION,
                    Constantes.USUARIO,
                    Constantes.CONTRASENIA
                )
                val sql = "UPDATE coches SET en_garaje = false WHERE id_coche = ?"
                preparedStatement = conexion.prepareStatement(sql)
                preparedStatement.setInt(1, idCoche)

                val filasActualizadas = preparedStatement.executeUpdate()
                exito = filasActualizadas > 0

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                preparedStatement?.close()
                conexion?.close()
            }

            callback(exito)
        }.start()
    }

    fun insertarOActualizarCoche(
        context: Context,
        matricula: String,
        marca: String,
        modelo: String,
        idCliente: Int,
        imagen: String?,
        enGaraje: Boolean,
        callback: (Boolean, Int?, String?) -> Unit // Devuelve éxito y ID del coche
    ) {
        Thread {
            var connection: Connection? = null
            var statement: PreparedStatement? = null
            var resultSet: ResultSet? = null
            var idCoche: Int? = null

            try {
                Class.forName("com.mysql.jdbc.Driver")
                connection = DriverManager.getConnection(
                    Constantes.URLCONNECTION,
                    Constantes.USUARIO,
                    Constantes.CONTRASENIA
                )

                // 1. Verificar si el coche ya existe
                val queryExistencia = "SELECT id_coche FROM coches WHERE matricula = ?"
                statement = connection.prepareStatement(queryExistencia)
                statement.setString(1, matricula)
                resultSet = statement.executeQuery()

                if (resultSet.next()) {
                    // Coche existe - actualizar solo en_garaje
                    idCoche = resultSet.getInt("id_coche")
                    val updateQuery = "UPDATE coches SET en_garaje = ? WHERE id_coche = ?"
                    statement.close()
                    statement = connection.prepareStatement(updateQuery)
                    statement.setBoolean(1, enGaraje)
                    statement.setInt(2, idCoche)
                    statement.executeUpdate()
                } else {
                    // Coche no existe - insertar nuevo
                    val queryInsert = """
                    INSERT INTO coches 
                    (matricula, marca, modelo, id_cliente, imagen, en_garaje) 
                    VALUES (?, ?, ?, ?, ?, ?)
                """.trimIndent()
                    statement.close()
                    statement = connection.prepareStatement(queryInsert, PreparedStatement.RETURN_GENERATED_KEYS)
                    statement.setString(1, matricula)
                    statement.setString(2, marca)
                    statement.setString(3, modelo)
                    statement.setInt(4, idCliente)
                    statement.setString(5, imagen ?: "")
                    statement.setBoolean(6, enGaraje)
                    statement.executeUpdate()

                    // Obtener el ID generado
                    val generatedKeys = statement.generatedKeys
                    if (generatedKeys.next()) {
                        idCoche = generatedKeys.getInt(1)
                    }
                }

                callback(true, idCoche, null)

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

    fun consultarIdCochePorMatricula(matricula: String, callback: (Int?) -> Unit) {
        val query = "SELECT id_coche FROM coches WHERE matricula = ?"

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
                statement.setString(1, matricula)
                resultSet = statement.executeQuery()

                if (resultSet.next()) {
                    val idCoche = resultSet.getInt("id_coche")
                    callback(idCoche) // Devuelve el id_coche a través del callback
                } else {
                    callback(null) // Si no se encuentra el coche, devuelve null
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

    fun consultarCochePorMatricula(id: Int, callback: (Coche?) -> Unit) {
        val query = "SELECT * FROM coches WHERE id_coche = ?"

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
                statement.setInt(1, id)
                resultSet = statement.executeQuery()

                if (resultSet.next()) {
                    val coche = Coche(
                        idCoche = resultSet.getInt("id_coche"),
                        matricula = resultSet.getString("matricula"),
                        marca = resultSet.getString("marca"),
                        modelo = resultSet.getString("modelo"),
                        idCliente = resultSet.getInt("id_cliente"),
                        imagen = resultSet.getString("imagen"),
                        enGaraje = resultSet.getBoolean("en_garaje")
                    )
                    callback(coche)
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


    fun verificarCocheEnGaraje(matricula: String, callback: (Boolean, Int?) -> Unit) {
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

                val query = "SELECT id_coche, en_garaje FROM coches WHERE matricula = ?"
                statement = connection.prepareStatement(query)
                statement.setString(1, matricula)
                resultSet = statement.executeQuery()

                if (resultSet.next()) {
                    val enGaraje = resultSet.getBoolean("en_garaje")
                    val idCoche = resultSet.getInt("id_coche")
                    callback(enGaraje, idCoche)
                } else {
                    callback(false, null)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                callback(false, null)
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