package com.martintrujillo.service

import android.content.Context
import com.martintrujillo.model.Reparacion
import com.martintrujillo.constantes.Constantes
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

class ReparacionesService(private val context: Context) {

    fun obtenerReparaciones(callback: (List<Reparacion>?, List<String>?, String?) -> Unit) {
        Thread {
            var connection: Connection? = null
            var statement: PreparedStatement? = null
            var resultSet: ResultSet? = null
            val listaReparaciones = mutableListOf<Reparacion>()
            val listaModelos = mutableListOf<String>() // <- Aquí almacenamos los modelos

            try {
                Class.forName("com.mysql.jdbc.Driver")
                connection = DriverManager.getConnection(
                    Constantes.URLCONNECTION,
                    Constantes.USUARIO,
                    Constantes.CONTRASENIA
                )

                val query = """
                    SELECT r.id_reparacion, r.id_coche, r.id_mecanico, r.motivo, r.horas_trabajo,
                   r.coste_mano_obra, r.coste_total_piezas, r.coste_total_reparacion,
                   r.fecha_entrada, r.fecha_salida, r.estado,
                   c.marca AS marca_coche,
                   c.modelo AS modelo_coche
                   FROM reparaciones r
                   INNER JOIN coches c ON r.id_coche = c.id_coche
                """.trimIndent()

                statement = connection.prepareStatement(query)
                resultSet = statement.executeQuery()

                while (resultSet.next()) {
                    listaReparaciones.add(
                        Reparacion(
                            idReparacion = resultSet.getInt("id_reparacion"),
                            idCoche = resultSet.getInt("id_coche"),
                            idMecanico = resultSet.getInt("id_mecanico"),
                            motivo = resultSet.getString("motivo"),
                            horasTrabajo = resultSet.getInt("horas_trabajo"),
                            costeManoObra = resultSet.getFloat("coste_mano_obra"),
                            costeTotalPiezas = resultSet.getFloat("coste_total_piezas"),
                            costeTotalReparacion = resultSet.getFloat("coste_total_reparacion"),
                            fechaEntrada = resultSet.getDate("fecha_entrada"),
                            fechaSalida = resultSet.getDate("fecha_salida"),
                            estado = resultSet.getString("estado")
                        )
                    )

                    // Guardamos el modelo y la marca del coche en la lista de modelos
                    val marca = resultSet.getString("marca_coche")
                    val modelo = resultSet.getString("modelo_coche")
                    listaModelos.add("$marca $modelo")
                }

                // Llamamos al callback con ambas listas
                callback(listaReparaciones, listaModelos, null)

            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
                callback(null, null, "Error: Driver JDBC no encontrado")
            } catch (e: SQLException) {
                e.printStackTrace()
                callback(null, null, "Error de base de datos: ${e.message}")
            } catch (e: Exception) {
                e.printStackTrace()
                callback(null, null, "Error inesperado: ${e.message}")
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

    fun insertarReparacion(
        idCoche: Int,
        idMecanico: Int?,
        motivo: String,
        horasTrabajo: Double,
        costeManoObra: Double,
        costeTotalPiezas: Double,
        costeTotalReparacion: Double,
        estado: String,
        callback: (Boolean, String?) -> Unit
    ) {
        Thread {
            var connection: Connection? = null
            var statement: PreparedStatement? = null

            try {
                Class.forName("com.mysql.jdbc.Driver")
                connection = DriverManager.getConnection(
                    Constantes.URLCONNECTION,
                    Constantes.USUARIO,
                    Constantes.CONTRASENIA
                )

                val query = """
                INSERT INTO reparaciones 
                (id_coche, id_mecanico, motivo, horas_trabajo, coste_mano_obra, coste_total_piezas, coste_total_reparacion, fecha_entrada, estado)
                VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), ?)
            """.trimIndent()

                statement = connection.prepareStatement(query)
                statement.setInt(1, idCoche)

                if (idMecanico != null) {
                    statement.setInt(2, idMecanico)
                } else {
                    statement.setNull(2, java.sql.Types.INTEGER)
                }

                statement.setString(3, motivo)
                statement.setDouble(4, horasTrabajo)
                statement.setDouble(5, costeManoObra)
                statement.setDouble(6, costeTotalPiezas)
                statement.setDouble(7, costeTotalReparacion)
                statement.setString(8, estado)

                val filasInsertadas = statement.executeUpdate()
                if (filasInsertadas > 0) {
                    callback(true, null)
                } else {
                    callback(false, "No se pudo insertar la reparación.")
                }

            } catch (e: Exception) {
                e.printStackTrace()
                callback(false, "Error al insertar reparación: ${e.message}")
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

    fun consultarReparacionActivaPorCoche(idCoche: Int, callback: (Reparacion?) -> Unit) {

        val query = """
        SELECT * FROM reparaciones 
        WHERE id_coche = ? 
        AND (fecha_salida IS NULL OR estado = 'pendiente' OR estado = 'en proceso')
        ORDER BY id_reparacion DESC 
        LIMIT 1
    """.trimIndent()

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
                statement.setInt(1, idCoche)
                resultSet = statement.executeQuery()

                if (resultSet.next()) {
                    val reparacion = Reparacion(
                        idReparacion = resultSet.getInt("id_reparacion"),
                        idCoche = resultSet.getInt("id_coche"),
                        idMecanico = resultSet.getInt("id_mecanico"),
                        motivo = resultSet.getString("motivo"),
                        horasTrabajo = resultSet.getInt("horas_trabajo"),
                        costeManoObra = resultSet.getFloat("coste_mano_obra"),
                        costeTotalPiezas = resultSet.getFloat("coste_total_piezas"),
                        costeTotalReparacion = resultSet.getFloat("coste_total_reparacion"),
                        fechaEntrada = resultSet.getDate("fecha_entrada"),
                        fechaSalida = resultSet.getDate("fecha_salida"),
                        estado = resultSet.getString("estado")
                    )
                    callback(reparacion)
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

    fun cerrarReparacion(idReparacion: Int, callback: (Boolean) -> Unit) {
        Thread {
            var connection: Connection? = null
            var statement: PreparedStatement? = null
            try {
                connection = DriverManager.getConnection(
                    Constantes.URLCONNECTION,
                    Constantes.USUARIO,
                    Constantes.CONTRASENIA)
                val sql = "UPDATE reparaciones SET fecha_salida = NOW() WHERE id_reparacion = ?"
                statement = connection.prepareStatement(sql)
                statement.setInt(1, idReparacion)

                val filasActualizadas = statement.executeUpdate()
                callback(filasActualizadas > 0)
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

    fun obtenerMatriculasEnReparacion(callback: (List<String>?, String?) -> Unit) {
        Thread {
            var connection: Connection? = null
            var statement: PreparedStatement? = null
            var resultSet: ResultSet? = null
            val matriculas = mutableListOf<String>()

            try {
                Class.forName("com.mysql.jdbc.Driver")
                connection = DriverManager.getConnection(
                    Constantes.URLCONNECTION,
                    Constantes.USUARIO,
                    Constantes.CONTRASENIA
                )

                val query = """
                    SELECT DISTINCT c.matricula 
                    FROM reparaciones r
                    JOIN coches c ON r.id_coche = c.id_coche
                    WHERE r.estado = 'en proceso'
                """.trimIndent()

                statement = connection.prepareStatement(query)
                resultSet = statement.executeQuery()

                while (resultSet.next()) {
                    matriculas.add(resultSet.getString("matricula"))
                }

                callback(matriculas, null)
            } catch (e: Exception) {
                e.printStackTrace()
                callback(null, "Error al obtener matrículas: ${e.message}")
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

    fun actualizarMotivoReparacion(idReparacion: Int, nuevoMotivo: String, callback: (Boolean, String?) -> Unit) {
        Thread {
            var connection: Connection? = null
            var statementSelect: PreparedStatement? = null
            var statementUpdate: PreparedStatement? = null
            var resultSet: ResultSet? = null

            try {
                Class.forName("com.mysql.jdbc.Driver")
                connection = DriverManager.getConnection(
                    Constantes.URLCONNECTION,
                    Constantes.USUARIO,
                    Constantes.CONTRASENIA
                )

                // 1. Obtener el motivo actual
                val querySelect = "SELECT motivo FROM reparaciones WHERE id_reparacion = ?"
                statementSelect = connection.prepareStatement(querySelect)
                statementSelect.setInt(1, idReparacion)
                resultSet = statementSelect.executeQuery()

                if (resultSet.next()) {
                    val motivoActual = resultSet.getString("motivo")
                    val motivoCombinado = if (motivoActual.isNullOrEmpty()) {
                        nuevoMotivo
                    } else {
                        "$motivoActual, $nuevoMotivo"
                    }

                    // 2. Actualizar con el motivo combinado
                    val queryUpdate = "UPDATE reparaciones SET motivo = ? WHERE id_reparacion = ?"
                    statementUpdate = connection.prepareStatement(queryUpdate)
                    statementUpdate.setString(1, motivoCombinado)
                    statementUpdate.setInt(2, idReparacion)

                    val filasActualizadas = statementUpdate.executeUpdate()
                    callback(filasActualizadas > 0, null)
                } else {
                    callback(false, "No se encontró la reparación")
                }

            } catch (e: Exception) {
                e.printStackTrace()
                callback(false, "Error al actualizar reparación: ${e.message}")
            } finally {
                try {
                    resultSet?.close()
                    statementSelect?.close()
                    statementUpdate?.close()
                    connection?.close()
                } catch (e: SQLException) {
                    e.printStackTrace()
                }
            }
        }.start()
    }

}
