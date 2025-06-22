package com.martintrujillo.controller

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.martintrujillo.R
import com.martintrujillo.adapter.PiezasAsignarAdapter
import com.martintrujillo.constantes.Constantes
import com.martintrujillo.model.Global
import com.martintrujillo.model.Piezas
import com.martintrujillo.service.PiezasService
import com.martintrujillo.service.ReparacionService
import com.martintrujillo.service.ReparacionesService
import java.sql.Connection
import java.sql.DriverManager

class VentanaAsignarPiezas : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var piezasAsignarAdapter: PiezasAsignarAdapter
    private val piezasList = mutableListOf<Piezas>()
    private val cantidades = mutableListOf<Int>()
    private val seleccionadas = mutableListOf<Boolean>()
    private lateinit var reparacionesService: ReparacionService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ventana_asignar_piezas)

        recyclerView = findViewById(R.id.recyclerViewPiezas)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val btnConfirmarCompra: Button = findViewById(R.id.btnConfirmarCompra)
        reparacionesService = ReparacionService()

        // Llamada para obtener las piezas
        obtenerPiezas()

        // Configurar el botón Confirmar Compra
        btnConfirmarCompra.setOnClickListener {
            val piezasSeleccionadas = obtenerPiezasSeleccionadas()

            if (piezasSeleccionadas.isEmpty()) {
                Toast.makeText(this, "No se han seleccionado piezas", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Calcular total
            val total = piezasSeleccionadas.sumOf { (pieza, cantidad) ->
                pieza.precioVenta.toDouble() * cantidad
            }

            // Mostrar diálogo de confirmación
            AlertDialog.Builder(this)
                .setTitle("Confirmar asignacion")
                .setMessage("Total de piezas: $total €")
                .setPositiveButton("Confirmar") { _, _ ->
                    realizarCompra(piezasSeleccionadas)
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    private fun obtenerPiezas() {
        PiezasService().obtenerTodasLasPiezas { listaPiezas, error ->
            runOnUiThread {
                if (error != null) {
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                } else {
                    listaPiezas?.let {
                        piezasList.clear()
                        piezasList.addAll(it)

                        cantidades.clear()
                        seleccionadas.clear()
                        for (pieza in it) {
                            cantidades.add(1) // Por defecto, cantidad 1
                            seleccionadas.add(false) // Por defecto, no seleccionada
                        }

                        piezasAsignarAdapter = PiezasAsignarAdapter(piezasList, cantidades, seleccionadas)
                        recyclerView.adapter = piezasAsignarAdapter
                    }
                }
            }
        }
    }

    // Método para obtener las piezas seleccionadas con sus cantidades
    fun obtenerPiezasSeleccionadas(): List<Pair<Piezas, Int>> {
        return piezasList.mapIndexedNotNull { index, pieza ->
            if (seleccionadas[index]) pieza to cantidades[index] else null
        }
    }

    // Método para realizar la compra
    private fun realizarCompra(piezasSeleccionadas: List<Pair<Piezas, Int>>) {
        if (piezasSeleccionadas.isEmpty()) {
            Toast.makeText(this, "No se han seleccionado piezas", Toast.LENGTH_SHORT).show()
            return
        }

        val matricula = intent.getStringExtra("matricula") ?: run {
            Toast.makeText(this, "Error: No hay matrícula asociada", Toast.LENGTH_SHORT).show()
            return
        }

        var costeTotalPiezas = 0.0
        var todasActualizacionesExitosas = true
        var mensajeError: String? = null

        // Actualizar stock y calcular coste total de piezas
        piezasSeleccionadas.forEach { (pieza, cantidad) ->
            costeTotalPiezas += pieza.precioVenta * cantidad

            PiezasService().actualizarStockPieza(pieza.idPieza, cantidad) { success, error ->
                if (!success) {
                    todasActualizacionesExitosas = false
                    mensajeError = error ?: "Error al actualizar stock"
                    runOnUiThread {
                        Toast.makeText(this, "Error al actualizar ${pieza.nombrePieza}: $mensajeError", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        if (todasActualizacionesExitosas) {
            // Registrar las piezas en la reparación y actualizar costes
            registrarPiezasEnReparacion(matricula, piezasSeleccionadas, costeTotalPiezas)
        }
        finish()
    }

    private fun registrarPiezasEnReparacion(matricula: String, piezasSeleccionadas: List<Pair<Piezas, Int>>, costeTotalPiezas: Double) {
        Thread {
            var connection: Connection? = null
            try {
                Class.forName("com.mysql.jdbc.Driver")
                connection = DriverManager.getConnection(
                    Constantes.URLCONNECTION,
                    Constantes.USUARIO,
                    Constantes.CONTRASENIA
                )

                // 1. Obtener el ID de la última reparación
                val queryReparacion = """
                SELECT id_reparacion 
                FROM reparaciones r
                JOIN coches c ON r.id_coche = c.id_coche
                WHERE c.matricula = ?
                ORDER BY r.id_reparacion DESC LIMIT 1
            """.trimIndent()

                val statementReparacion = connection.prepareStatement(queryReparacion)
                statementReparacion.setString(1, matricula)
                val resultSet = statementReparacion.executeQuery()

                if (resultSet.next()) {
                    val idReparacion = resultSet.getInt("id_reparacion")
                    val idUsuario = Global.usuarioUser?.id ?: 0 // Usa 0 o maneja el error adecuadamente

                    // 2. Insertar cada pieza en reparaciones_piezas
                    val queryInsert = """
                    INSERT INTO reparaciones_piezas 
                    (id_reparacion, id_pieza, cantidad_usada, id_usuario)
                    VALUES (?, ?, ?, ?)
                """.trimIndent()

                    val insertStatement = connection.prepareStatement(queryInsert)

                    for ((pieza, cantidad) in piezasSeleccionadas) {
                        insertStatement.setInt(1, idReparacion)
                        insertStatement.setInt(2, pieza.idPieza)
                        insertStatement.setInt(3, cantidad)
                        insertStatement.setInt(4, idUsuario)
                        insertStatement.addBatch()
                    }

                    insertStatement.executeBatch()

                    // 3. Actualizar costes
                    val queryUpdate = """
                    UPDATE reparaciones 
                    SET coste_total_piezas = coste_total_piezas + ?
                    WHERE id_reparacion = ?
                """.trimIndent()

                    val updateStatement = connection.prepareStatement(queryUpdate)
                    updateStatement.setDouble(1, costeTotalPiezas)
                    updateStatement.setInt(2, idReparacion)
                    updateStatement.executeUpdate()

                    reparacionesService.actualizarCosteTotalReparacion(matricula) { exito, mensajeError ->
                        runOnUiThread {
                            if (exito) {
                                Toast.makeText(this, "Coste total de la reparación actualizado correctamente", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this, "Error: $mensajeError", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    runOnUiThread {
                        Toast.makeText(this, "Piezas asignadas correctamente", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this, "No se encontró la reparación", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this, "Error al registrar piezas: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } finally {
                connection?.close()

            }
        }.start()
    }
}
