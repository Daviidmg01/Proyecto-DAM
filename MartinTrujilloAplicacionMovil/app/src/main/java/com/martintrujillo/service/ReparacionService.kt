package com.martintrujillo.service

import android.util.Log
import com.martintrujillo.constantes.Constantes
import com.martintrujillo.model.Coche
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

class ReparacionService {

    fun obtenerCochesEnReparacion(callback: (List<Coche>?, String?) -> Unit) {
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

    fun obtenerMotivoReparacion(matricula: String, callback: (String?, String?) -> Unit) {
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

                val query = """
                SELECT r.motivo 
                FROM reparaciones r 
                JOIN coches c ON r.id_coche = c.id_coche 
                WHERE c.matricula = ?
                ORDER BY r.id_reparacion DESC LIMIT 1
            """.trimIndent()
                statement = connection.prepareStatement(query)
                statement.setString(1, matricula)
                resultSet = statement.executeQuery()

                if (resultSet.next()) {
                    val motivo = resultSet.getString("motivo")
                    callback(motivo, null)
                } else {
                    callback(null, "No se encontró motivo para la matrícula $matricula")
                }

            } catch (e: Exception) {
                e.printStackTrace()
                callback(null, "Error: ${e.message}")
            } finally {
                resultSet?.close()
                statement?.close()
                connection?.close()
            }
        }.start()
    }

    fun asignarMecanicoAReparacion(matricula: String, idMecanico: Int, callback: (Boolean, String?) -> Unit) {
        Thread {
            var connection: Connection? = null
            var statement: PreparedStatement? = null

            try {
                Class.forName("com.mysql.jdbc.Driver")
                connection = DriverManager.getConnection(Constantes.URLCONNECTION, Constantes.USUARIO, Constantes.CONTRASENIA)

                // Asignar mecánico y cambiar estado a "en proceso"
                val query = """
                UPDATE reparaciones 
                SET id_mecanico = ?, estado = 'en proceso'
                WHERE id_reparacion = (
                    SELECT id_reparacion FROM (
                        SELECT r.id_reparacion 
                        FROM reparaciones r
                        JOIN coches c ON r.id_coche = c.id_coche 
                        WHERE c.matricula = ?
                        ORDER BY r.id_reparacion DESC LIMIT 1
                    ) AS ultima
                )
            """.trimIndent()

                statement = connection.prepareStatement(query)
                statement.setInt(1, idMecanico)
                statement.setString(2, matricula)


                println("Asignando mecánico $idMecanico a matrícula $matricula")
                val filasAfectadas = statement.executeUpdate()
                if (filasAfectadas > 0) {
                    callback(true, null)
                } else {
                    callback(false, "No se pudo asignar el mecánico")
                }

            } catch (e: Exception) {
                e.printStackTrace()
                callback(false, "Error: ${e.message}")
            } finally {
                statement?.close()
                connection?.close()
            }
        }.start()
    }


    fun finalizarReparacion(matricula: String, costeTotalPiezas: Double, costeTotal: Double, callback: (Boolean, String?) -> Unit) {
        Thread {
            var connection: Connection? = null
            var statement: PreparedStatement? = null

            try {
                Class.forName("com.mysql.jdbc.Driver")
                connection = DriverManager.getConnection(Constantes.URLCONNECTION, Constantes.USUARIO, Constantes.CONTRASENIA)

                val query = """
                UPDATE reparaciones 
                SET estado = 'finalizada',
                    coste_total_piezas = ?,
                    coste_mano_obra = ?,
                    coste_total_reparacion = ?
                WHERE id_reparacion = (
                    SELECT id_reparacion FROM (
                        SELECT r.id_reparacion 
                        FROM reparaciones r
                        JOIN coches c ON r.id_coche = c.id_coche 
                        WHERE c.matricula = ?
                        ORDER BY r.id_reparacion DESC LIMIT 1
                    ) AS ultima
                )
            """.trimIndent()

                // Calcular coste mano de obra (costeTotal - costeTotalPiezas)
                val costeManoObra = costeTotal - costeTotalPiezas

                statement = connection.prepareStatement(query)
                statement.setDouble(1, costeTotalPiezas)
                statement.setDouble(2, costeManoObra)
                statement.setDouble(3, costeTotal)
                statement.setString(4, matricula)

                val filasAfectadas = statement.executeUpdate()
                callback(filasAfectadas > 0, null)

            } catch (e: Exception) {
                e.printStackTrace()
                callback(false, "Error: ${e.message}")
            } finally {
                statement?.close()
                connection?.close()
            }
        }.start()
    }


    fun obtenerMecanicoAsignado(matricula: String, callback: (String?, String?) -> Unit) {
        Thread {
            var connection: Connection? = null
            var statement: PreparedStatement? = null
            var resultSet: ResultSet? = null

            try {
                Class.forName("com.mysql.jdbc.Driver")
                connection = DriverManager.getConnection(Constantes.URLCONNECTION, Constantes.USUARIO, Constantes.CONTRASENIA)

                val query = """
                SELECT u.nombre 
                FROM reparaciones r
                INNER JOIN coches c ON r.id_coche = c.id_coche
                LEFT JOIN usuarios u ON r.id_mecanico = u.id_usuario
                WHERE c.matricula = ?
                ORDER BY r.id_reparacion DESC
                LIMIT 1;
            """.trimIndent()

                statement = connection.prepareStatement(query)
                statement.setString(1, matricula)
                resultSet = statement.executeQuery()

                if (resultSet.next()) {
                    val nombre = resultSet.getString("nombre")
                    callback(nombre, null)
                } else {
                    callback(null, null)  // No hay mecánico asignado
                }

            } catch (e: Exception) {
                e.printStackTrace()
                callback(null, "Error: ${e.message}")
            } finally {
                resultSet?.close()
                statement?.close()
                connection?.close()
            }
        }.start()
    }

    fun obtenerMatriculas(callback: (List<String>?, String?) -> Unit) {
        Thread {
            var connection: Connection? = null
            var statement: PreparedStatement? = null
            var resultSet: ResultSet? = null
            val listaMatriculas = mutableListOf<String>()

            try {
                Class.forName("com.mysql.jdbc.Driver")
                connection = DriverManager.getConnection(
                    Constantes.URLCONNECTION,
                    Constantes.USUARIO,
                    Constantes.CONTRASENIA
                )

                val query = "SELECT matricula FROM coches"
                statement = connection.prepareStatement(query)
                resultSet = statement.executeQuery()

                while (resultSet.next()) {
                    val matricula = resultSet.getString("matricula")
                    listaMatriculas.add(matricula)
                }

                callback(listaMatriculas, null)

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


    fun actualizarHorasReparacion(matricula: String, horas: Int, callback: (Boolean, String?) -> Unit) {
        Thread {
            var connection: Connection? = null
            var statement: PreparedStatement? = null

            try {
                Class.forName("com.mysql.jdbc.Driver")
                connection = DriverManager.getConnection(Constantes.URLCONNECTION, Constantes.USUARIO, Constantes.CONTRASENIA)

                val query = """
                UPDATE reparaciones
                SET horas_trabajo = ?
                WHERE id_reparacion = (
                    SELECT id_reparacion FROM (
                        SELECT r.id_reparacion 
                        FROM reparaciones r
                        JOIN coches c ON r.id_coche = c.id_coche 
                        WHERE c.matricula = ?
                        ORDER BY r.id_reparacion DESC LIMIT 1
                    ) AS ultima
                )
            """.trimIndent()

                statement = connection.prepareStatement(query)
                statement.setInt(1, horas)
                statement.setString(2, matricula)

                val filasAfectadas = statement.executeUpdate()
                callback(filasAfectadas > 0, if (filasAfectadas > 0) null else "No se actualizó ninguna fila")

            } catch (e: Exception) {
                e.printStackTrace()
                callback(false, "Error: ${e.message}")
            } finally {
                statement?.close()
                connection?.close()
            }
        }.start()
    }

    fun actualizarCosteManoObra(matricula: String, callback: (Boolean, String?) -> Unit) {
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
                val conn = connection ?: throw SQLException("No se pudo establecer conexión")

                // 1. Obtener las horas de trabajo de la última reparación del coche
                val querySelect = """
                SELECT r.id_reparacion, r.horas_trabajo 
                FROM reparaciones r
                JOIN coches c ON r.id_coche = c.id_coche
                WHERE c.matricula = ?
                ORDER BY r.id_reparacion DESC
                LIMIT 1
            """.trimIndent()

                statementSelect = conn.prepareStatement(querySelect)
                statementSelect.setString(1, matricula)
                resultSet = statementSelect.executeQuery()

                if (resultSet.next()) {
                    val idReparacion = resultSet.getInt("id_reparacion")
                    val horasTrabajo = resultSet.getDouble("horas_trabajo")
                    val costeManoObra = horasTrabajo * 20.0

                    // 2. Actualizar el campo coste_mano_obra
                    val queryUpdate = """
                    UPDATE reparaciones 
                    SET coste_mano_obra = ?
                    WHERE id_reparacion = ?
                """.trimIndent()

                    statementUpdate = conn.prepareStatement(queryUpdate)
                    statementUpdate.setDouble(1, costeManoObra)
                    statementUpdate.setInt(2, idReparacion)

                    val filasAfectadas = statementUpdate.executeUpdate()
                    callback(filasAfectadas > 0, null)
                } else {
                    callback(false, "No se encontró la reparación para esa matrícula")
                }

            } catch (e: Exception) {
                e.printStackTrace()
                callback(false, "Error al actualizar coste de mano de obra: ${e.message}")
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

    fun actualizarCosteTotalReparacion(matricula: String, callback: (Boolean, String?) -> Unit) {
        Thread {
            var connection: Connection? = null
            var statementSelect: PreparedStatement? = null
            var resultSet: ResultSet? = null
            var statementUpdate: PreparedStatement? = null

            try {
                Class.forName("com.mysql.jdbc.Driver")
                connection = DriverManager.getConnection(
                    Constantes.URLCONNECTION,
                    Constantes.USUARIO,
                    Constantes.CONTRASENIA
                )

                val querySelect = """
                SELECT id_reparacion 
                FROM reparaciones r
                JOIN coches c ON r.id_coche = c.id_coche
                WHERE c.matricula = ?
                ORDER BY r.id_reparacion DESC LIMIT 1
            """.trimIndent()

                statementSelect = connection.prepareStatement(querySelect)
                statementSelect.setString(1, matricula)
                resultSet = statementSelect.executeQuery()

                if (resultSet.next()) {
                    val idReparacion = resultSet.getInt("id_reparacion")

                    val queryUpdate = """
                    UPDATE reparaciones 
                    SET coste_total_reparacion = coste_total_piezas + coste_mano_obra
                    WHERE id_reparacion = ?
                """.trimIndent()

                    statementUpdate = connection.prepareStatement(queryUpdate)
                    statementUpdate.setInt(1, idReparacion)
                    val rowsAffected = statementUpdate.executeUpdate()

                    if (rowsAffected > 0) {
                        callback(true, null)
                    } else {
                        callback(false, "No se pudo actualizar el coste total de la reparación")
                    }
                } else {
                    callback(false, "No se encontró ninguna reparación para la matrícula dada")
                }

            } catch (e: Exception) {
                e.printStackTrace()
                callback(false, "Error al actualizar coste total: ${e.message}")
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

    fun obtenerHorasTrabajo(matricula: String, callback: (Int?, String?) -> Unit) {
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

                val query = """
                SELECT horas_trabajo 
                FROM reparaciones r
                JOIN coches c ON r.id_coche = c.id_coche
                WHERE c.matricula = ?
                ORDER BY r.id_reparacion DESC LIMIT 1
            """.trimIndent()

                statement = connection.prepareStatement(query)
                statement.setString(1, matricula)
                resultSet = statement.executeQuery()

                if (resultSet.next()) {
                    val horas = resultSet.getInt("horas_trabajo")
                    callback(horas, null)
                } else {
                    callback(null, "No se encontraron horas registradas")
                }

            } catch (e: Exception) {
                e.printStackTrace()
                callback(null, "Error al obtener horas: ${e.message}")
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

    fun obtenerEstadoReparacion(matricula: String, callback: (String?, String?) -> Unit) {
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

                val query = """
                SELECT estado 
                FROM reparaciones r
                JOIN coches c ON r.id_coche = c.id_coche
                WHERE c.matricula = ?
                ORDER BY r.id_reparacion DESC LIMIT 1
            """.trimIndent()

                statement = connection.prepareStatement(query)
                statement.setString(1, matricula)
                resultSet = statement.executeQuery()

                if (resultSet.next()) {
                    val estado = resultSet.getString("estado")
                    callback(estado, null)
                } else {
                    callback(null, "No se encontraron horas registradas")
                }

            } catch (e: Exception) {
                e.printStackTrace()
                callback(null, "Error al obtener horas: ${e.message}")
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

    fun calcularYActualizarCostos(matricula: String, horasTrabajo: Int, callback: (Boolean, String?) -> Unit) {
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

                // 1. Obtener el coste total de las piezas para esta reparación
                val queryPiezas = """
                SELECT SUM p.precio_venta as total_piezas
                FROM reparaciones_piezas rp
                JOIN piezas p ON rp.id_pieza = p.id_pieza
                JOIN reparaciones r ON rp.id_reparacion = r.id_reparacion
                JOIN coches c ON r.id_coche = c.id_coche
                WHERE c.matricula = ?
                AND r.id_reparacion = (
                    SELECT id_reparacion FROM (
                        SELECT r.id_reparacion 
                        FROM reparaciones r
                        JOIN coches c ON r.id_coche = c.id_coche 
                        WHERE c.matricula = ?
                        ORDER BY r.id_reparacion DESC LIMIT 1
                    ) AS ultima
                )
            """.trimIndent()

                statement = connection.prepareStatement(queryPiezas)
                statement.setString(1, matricula)
                statement.setString(2, matricula)
                resultSet = statement.executeQuery()

                var costeTotalPiezas = 0.0
                if (resultSet.next()) {
                    costeTotalPiezas = resultSet.getDouble("total_piezas")
                }

                // 2. Calcular coste de mano de obra (horas * tarifa por hora)
                val costeManoObra = horasTrabajo * Constantes.TARIFA_POR_HORA

                // 3. Calcular coste total (mano de obra + piezas)
                val costeTotalReparacion = costeManoObra + costeTotalPiezas

                // 4. Actualizar la reparación con los costos calculados
                val queryActualizar = """
                UPDATE reparaciones 
                SET coste_mano_obra = ?,
                    coste_total_piezas = ?,
                    coste_total_reparacion = ?,
                    estado = 'finalizada'
                WHERE id_reparacion = (
                    SELECT id_reparacion FROM (
                        SELECT r.id_reparacion 
                        FROM reparaciones r
                        JOIN coches c ON r.id_coche = c.id_coche 
                        WHERE c.matricula = ?
                        ORDER BY r.id_reparacion DESC LIMIT 1
                    ) AS ultima
                )
            """.trimIndent()

                statement = connection.prepareStatement(queryActualizar)
                statement.setDouble(1, costeManoObra)
                statement.setDouble(2, costeTotalPiezas)
                statement.setDouble(3, costeTotalReparacion)
                statement.setString(4, matricula)

                val filasAfectadas = statement.executeUpdate()
                callback(filasAfectadas > 0, null)

            } catch (e: Exception) {
                e.printStackTrace()
                callback(false, "Error al calcular costos: ${e.message}")
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

    fun calcularCostesReparacion(matricula: String, callback: (Pair<Double, Double>?, String?) -> Unit) {
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

                // 1. Obtener el ID de la reparación actual
                val queryReparacion = """
                SELECT r.id_reparacion, r.horas_trabajo
                FROM reparaciones r
                JOIN coches c ON r.id_coche = c.id_coche
                WHERE c.matricula = ?
                ORDER BY r.id_reparacion DESC LIMIT 1
            """.trimIndent()

                statement = connection.prepareStatement(queryReparacion)
                statement.setString(1, matricula)
                resultSet = statement.executeQuery()

                if (resultSet.next()) {
                    val idReparacion = resultSet.getInt("id_reparacion")
                    val horasTrabajo = resultSet.getInt("horas_trabajo")

                    // 2. Calcular coste total de piezas
                    val queryPiezas = """
                    SELECT SUM(p.precio_venta * rp.cantidad_usada) as total_piezas
                    FROM reparaciones_piezas rp
                    JOIN piezas p ON rp.id_pieza = p.id_pieza
                    WHERE rp.id_reparacion = ?
                """.trimIndent()

                    statement = connection.prepareStatement(queryPiezas)
                    statement.setInt(1, idReparacion)
                    resultSet = statement.executeQuery()

                    var costeTotalPiezas = 0.0
                    if (resultSet.next()) {
                        costeTotalPiezas = resultSet.getDouble("total_piezas")
                    }

                    // 3. Calcular coste de mano de obra usando la constante
                    val costeManoObra = horasTrabajo * Constantes.TARIFA_POR_HORA

                    // 4. Calcular coste total
                    val costeTotal = costeTotalPiezas + costeManoObra

                    callback(Pair(costeTotalPiezas, costeTotal), null)
                } else {
                    callback(null, "No se encontró la reparación")
                }

            } catch (e: Exception) {
                e.printStackTrace()
                callback(null, "Error al calcular costes: ${e.message}")
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