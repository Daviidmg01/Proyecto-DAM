package com.martintrujillo.controller

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.martintrujillo.R
import com.martintrujillo.service.ReparacionService

class VentanaHoras : AppCompatActivity() {

    private lateinit var matricula: String
    private lateinit var editTextHoras: EditText
    private lateinit var btnActualizar: Button
    private lateinit var tvMatricula: TextView

    private val reparacionService = ReparacionService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ventana_horas)

        // Inicializar vistas
        editTextHoras = findViewById(R.id.editTextHoras)
        btnActualizar = findViewById(R.id.btnGuardar)
        tvMatricula = findViewById(R.id.tvMatricula)

        // Obtener la matrícula del intent
        matricula = intent.getStringExtra("matricula") ?: run {
            mostrarError("No se recibió la matrícula del vehículo")
            finish()
            return
        }

        // Mostrar la matrícula
        tvMatricula.text = "Matrícula: $matricula"

        // Cargar horas existentes
        cargarHorasActuales()

        // Configurar listeners
        btnActualizar.setOnClickListener {
            actualizarHoras()
        }
    }

    private fun cargarHorasActuales() {
        reparacionService.obtenerHorasTrabajo(matricula) { horas, error ->
            runOnUiThread {
                if (error != null) {
                    mostrarError(error)
                } else {
                    if (horas != null) {
                        editTextHoras.setText(horas.toString())
                    } else {
                        editTextHoras.setText("0") // Valor por defecto si no hay horas
                    }
                }
            }
        }
    }


    private fun actualizarHoras() {
        val horasTexto = editTextHoras.text.toString().trim()

        when {
            horasTexto.isEmpty() -> {
                mostrarError("Por favor ingrese el número de horas")
                return
            }
            horasTexto.toIntOrNull() == null -> {
                mostrarError("Debe ingresar un número válido")
                return
            }
            horasTexto.toInt() <= 0 -> {
                mostrarError("Las horas deben ser mayores a 0")
                return
            }
        }

        val horas = horasTexto.toInt()

        reparacionService.actualizarHorasReparacion(matricula, horas) { success, error ->
            runOnUiThread {
                if (success) {
                    mostrarMensaje("Horas actualizadas correctamente")
                    reparacionService.actualizarCosteManoObra(matricula) { exito, mensaje ->
                        runOnUiThread {
                            // Comprobamos si se realizó con éxito la actualización
                            if (exito) {
                                // Si todo fue bien, mostramos un mensaje de éxito
                                Toast.makeText(this, "Coste de mano de obra actualizado correctamente", Toast.LENGTH_SHORT).show()
                                reparacionService.actualizarCosteTotalReparacion(matricula) { exito, mensajeError ->
                                    runOnUiThread {
                                        if (exito) {
                                            Toast.makeText(this, "Coste total de la reparación actualizado correctamente", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(this, "Error: $mensajeError", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            } else {
                                // Si hubo algún problema, mostramos el mensaje de error
                                Toast.makeText(this, mensaje ?: "Error desconocido", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    finish()
                } else {
                    mostrarError(error ?: "Error desconocido al actualizar horas")
                }
            }
        }
    }


    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }

    private fun mostrarError(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }
}