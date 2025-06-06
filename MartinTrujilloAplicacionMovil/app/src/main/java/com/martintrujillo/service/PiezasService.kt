package com.martintrujillo.service

import android.os.Looper
import android.util.Log
import com.martintrujillo.constantes.Constantes
import com.martintrujillo.controller.Dto.PiezaReparacionDetalle
import com.martintrujillo.model.Piezas
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.util.logging.Handler

class PiezasService {

    fun obtenerTodasLasPiezas(callback: (List<Piezas>?, String?) -> Unit) {
        Thread {
            var connection: Connection? = null
            var statement: PreparedStatement? = null
            var resultSet: ResultSet? = null
            val listaPiezas = mutableListOf<Piezas>()

            try {
                Class.forName("com.mysql.jdbc.Driver")
                connection = DriverManager.getConnection(
                    Constantes.URLCONNECTION,
                    Constantes.USUARIO,
                    Constantes.CONTRASENIA
                )

                val query = "SELECT * FROM piezas"
                statement = connection.prepareStatement(query)
                resultSet = statement.executeQuery()

                while (resultSet.next()) {
                    val pieza = Piezas(
                        idPieza = resultSet.getInt("id_pieza"),
                        nombrePieza = resultSet.getString("nombre_pieza"),
                        cantidadDisponible = resultSet.getInt("cantidad_disponible"),
                        precioCompra = resultSet.getFloat("precio_compra"),
                        precioVenta = resultSet.getFloat("precio_venta"),
                        stockMinimo = resultSet.getInt("stock_minimo"),
                        idProveedor = resultSet.getInt("id_proveedor")
                    )
                    listaPiezas.add(pieza)
                }

                callback(listaPiezas, null)

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

    // Método para actualizar el stock de una pieza
    fun actualizarStockPieza(idPieza: Int, cantidadVendida: Int, callback: (Boolean, String?) -> Unit) {
        // Crear un Handler para ejecutar en el hilo principal
        val handler = android.os.Handler(Looper.getMainLooper())

        // Usamos un hilo para realizar la operación en segundo plano y evitar el error en el hilo principal
        Thread {
            try {
                // Establecemos la conexión con la base de datos
                val connection: Connection = DriverManager.getConnection(Constantes.URLCONNECTION, Constantes.USUARIO, Constantes.CONTRASENIA)

                // Obtener la cantidad actual de la pieza para realizar el cálculo del nuevo stock
                val selectQuery = "SELECT cantidad_disponible FROM piezas WHERE id_pieza = ?"
                val preparedSelectStatement: PreparedStatement = connection.prepareStatement(selectQuery)
                preparedSelectStatement.setInt(1, idPieza)
                val resultSet = preparedSelectStatement.executeQuery()

                if (resultSet.next()) {
                    val cantidadActual = resultSet.getInt("cantidad_disponible")
                    val nuevoStock = cantidadActual - cantidadVendida

                    if (nuevoStock >= 0) {
                        // Crear la consulta SQL para actualizar el stock
                        val updateQuery = "UPDATE piezas SET cantidad_disponible = ? WHERE id_pieza = ?"
                        val preparedUpdateStatement: PreparedStatement = connection.prepareStatement(updateQuery)
                        preparedUpdateStatement.setInt(1, nuevoStock)
                        preparedUpdateStatement.setInt(2, idPieza)

                        // Ejecutar la actualización
                        val rowsAffected = preparedUpdateStatement.executeUpdate()

                        // Si se actualizó correctamente, se devuelve true
                        handler.post {
                            if (rowsAffected > 0) {
                                callback(true, null)
                            } else {
                                callback(false, "No se pudo actualizar el stock. Pieza no encontrada.")
                            }
                        }
                    } else {
                        handler.post {
                            callback(false, "No hay suficiente stock disponible para esta pieza.")
                        }
                    }
                } else {
                    handler.post {
                        callback(false, "Pieza no encontrada en la base de datos.")
                    }
                }

                // Cerrar la conexión
                connection.close()

            } catch (e: Exception) {
                // En caso de error, devolver false y mostrar el error
                Log.e("PiezasService", "Error al actualizar el stock", e)
                handler.post {
                    callback(false, "Error al actualizar el stock: ${e.message}")
                }
            }
        }.start() // Iniciar el hilo
    }

    fun obtenerPiezasDeUltimaReparacionActiva(matricula: String, callback: (List<PiezaReparacionDetalle>?, String?) -> Unit) {
        Thread {
            var connection: Connection? = null
            var statement: PreparedStatement? = null
            var resultSet: ResultSet? = null
            val listaDetalles = mutableListOf<PiezaReparacionDetalle>()

            try {
                Class.forName("com.mysql.jdbc.Driver")
                connection = DriverManager.getConnection(
                    Constantes.URLCONNECTION,
                    Constantes.USUARIO,
                    Constantes.CONTRASENIA
                )

                val query = """
                SELECT 
    c.matricula,
    p.nombre_pieza,
    rp.cantidad_usada,
    p.precio_venta,
    (rp.cantidad_usada * p.precio_venta) AS precio_total,
    u.nombre AS nombre_mecanico
FROM reparaciones r
JOIN coches c ON r.id_coche = c.id_coche
JOIN reparaciones_piezas rp ON rp.id_reparacion = r.id_reparacion
JOIN piezas p ON p.id_pieza = rp.id_pieza
JOIN usuarios u ON u.id_usuario = rp.id_usuario
WHERE c.matricula = ?
  AND r.id_reparacion = (
      SELECT r2.id_reparacion
      FROM reparaciones r2
      JOIN coches c2 ON r2.id_coche = c2.id_coche
      WHERE c2.matricula = ?
        AND r2.estado <> 'finalizada'
      ORDER BY r2.fecha_entrada DESC
      LIMIT 1
  );
            """.trimIndent()

                statement = connection.prepareStatement(query)
                statement.setString(1, matricula)
                statement.setString(2, matricula)
                resultSet = statement.executeQuery()

                while (resultSet.next()) {
                    val detalle = PiezaReparacionDetalle(
                        matricula = resultSet.getString("matricula"),
                        nombrePieza = resultSet.getString("nombre_pieza"),
                        cantidadUsada = resultSet.getInt("cantidad_usada"),
                        precioUnidad = resultSet.getFloat("precio_venta"),
                        precioTotal = resultSet.getFloat("precio_total"),
                        nombreMecanico = resultSet.getString("nombre_mecanico")
                    )
                    listaDetalles.add(detalle)
                }

                callback(listaDetalles, null)

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


}
