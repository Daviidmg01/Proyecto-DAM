package com.martintrujillo.controller

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.martintrujillo.R
import com.martintrujillo.service.ClienteService
import com.martintrujillo.service.CochesService
import com.martintrujillo.service.ReparacionesService

class VentanaCrearCoche : AppCompatActivity() {

    private lateinit var btnAniadirCoche: Button
    private lateinit var editTextMatricula: EditText
    private lateinit var editTextMarca: EditText
    private lateinit var editTextModelo: EditText
    private lateinit var editTextImagen: EditText
    private lateinit var editTextDescripcionReparacion: EditText

    private lateinit var nombreCliente: String
    private lateinit var dniCliente: String
    private lateinit var telfCliente: String
    private var emailCliente: String? = null

    private lateinit var cochesService: CochesService
    private lateinit var clienteService: ClienteService
    private lateinit var reparacionService: ReparacionesService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ventana_crear_coche)

        inicializar()

        btnAniadirCoche.setOnClickListener {
            insertarTodo()
            finish()
        }
    }

    private fun inicializar() {
        val intent = intent
        nombreCliente = intent.getStringExtra("nombre")!!
        dniCliente = intent.getStringExtra("dni")!!
        telfCliente = intent.getStringExtra("telefono")!!
        emailCliente = intent.getStringExtra("email")

        btnAniadirCoche = findViewById(R.id.btnAniadirCoche)
        editTextMatricula = findViewById(R.id.editTextMatricula)
        editTextMarca = findViewById(R.id.editTextMarca)
        editTextModelo = findViewById(R.id.editTextModelo)
        editTextImagen = findViewById(R.id.editTextImagen)
        editTextDescripcionReparacion = findViewById(R.id.editTextMultiLineDescripcionReparacion)

        cochesService = CochesService()
        clienteService = ClienteService()
        reparacionService = ReparacionesService(this)
    }

    private fun insertarTodo() {
        val matricula = editTextMatricula.text.toString().trim()
        val marca = editTextMarca.text.toString().trim()
        val modelo = editTextModelo.text.toString().trim()
        val imagen = editTextImagen.text.toString().trim()
        val nuevoMotivo = editTextDescripcionReparacion.text.toString().trim()

        // Validar longitud máxima de la matrícula (20 caracteres)
        if (matricula.length > 20) {
            Toast.makeText(this, "La matrícula no puede tener más de 20 caracteres", Toast.LENGTH_SHORT).show()
            editTextMatricula.requestFocus()
            return
        }

        // Validar campos obligatorios
        if (matricula.isEmpty() || marca.isEmpty() || modelo.isEmpty() || nuevoMotivo.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        // 1. Verificar estado del coche primero
        cochesService.verificarCocheEnGaraje(matricula) { enGaraje, idCocheExistente ->
            if (enGaraje && idCocheExistente != null) {
                // Coche ya está en garaje - buscar reparación activa
                reparacionService.consultarReparacionActivaPorCoche(idCocheExistente) { reparacionExistente ->
                    if (reparacionExistente != null) {
                        // Actualizar reparación existente
                        reparacionService.actualizarMotivoReparacion(
                            reparacionExistente.idReparacion,
                            nuevoMotivo
                        ) { exito, mensaje ->
                            runOnUiThread {
                                if (exito) {
                                    Toast.makeText(
                                        this,
                                        "Motivo actualizado en reparación existente",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    finish()
                                } else {
                                    Toast.makeText(
                                        this,
                                        mensaje ?: "Error al actualizar reparación",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    } else {
                        // No hay reparación activa - crear nueva
                        crearNuevaReparacion(idCocheExistente, nuevoMotivo)
                    }
                }
            } else {
                // Flujo normal (coche no está en garaje o no existe)
                procesarNuevoCoche(matricula, marca, modelo, imagen, nuevoMotivo)
            }
        }
    }

    private fun procesarNuevoCoche(
        matricula: String,
        marca: String,
        modelo: String,
        imagen: String,
        motivo: String
    ) {
        clienteService.insertarOActualizarCliente(
            this, nombreCliente, dniCliente, telfCliente, emailCliente
        ) { clienteExito, idCliente, mensajeError ->
            if (!clienteExito || idCliente == null) {
                mostrarToast(mensajeError ?: "Error al procesar el cliente")
                return@insertarOActualizarCliente
            }

            cochesService.insertarOActualizarCoche(
                this, matricula, marca, modelo, idCliente, imagen, true
            ) { cocheExito, idCoche, mensajeError ->
                if (!cocheExito || idCoche == null) {
                    mostrarToast(mensajeError ?: "Error al procesar el coche")
                    return@insertarOActualizarCoche
                }

                crearNuevaReparacion(idCoche, motivo)
            }
        }
    }

    private fun crearNuevaReparacion(idCoche: Int, motivo: String) {
        reparacionService.insertarReparacion(
            idCoche = idCoche,
            idMecanico = null,
            motivo = motivo,
            horasTrabajo = 0.0,
            costeManoObra = 0.0,
            costeTotalPiezas = 0.0,
            costeTotalReparacion = 0.0,
            estado = "pendiente"
        ) { exito, mensaje ->
            runOnUiThread {
                if (exito) {
                    Toast.makeText(this, "Reparación creada correctamente", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(this, mensaje ?: "Error al crear la reparación", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun mostrarToast(mensaje: String) {
        runOnUiThread {
            Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
        }
    }
}