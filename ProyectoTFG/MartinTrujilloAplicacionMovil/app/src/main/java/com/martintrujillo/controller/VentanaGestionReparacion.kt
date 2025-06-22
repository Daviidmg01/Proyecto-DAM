package com.martintrujillo.controller

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.martintrujillo.R
import com.martintrujillo.model.Global
import com.martintrujillo.service.ReparacionService

class VentanaGestionReparacion : AppCompatActivity() {

    private lateinit var textViewMatricula: TextView
    private lateinit var textViewMotivo: TextView
    private lateinit var btnAsignarMecanico: Button
    private lateinit var btnAsignarPieza: Button
    private lateinit var btnMostrarPiezas: Button
    private lateinit var btnFinalizar: Button
    private lateinit var btnAsignarHoras: Button
    private lateinit var cardViewAsignarMecanico: CardView
    private lateinit var cardViewAsignarPieza: CardView
    private lateinit var cardViewMostrarPiezas:CardView
    private lateinit var cardViewFinalizar: CardView
    private lateinit var cardViewAsignarHoras: CardView
    private lateinit var textViewCocheAsignado: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ventana_gestion_reparacion)

        // Inicialización de vistas
        textViewMatricula = findViewById(R.id.textViewMatricula)
        textViewMotivo = findViewById(R.id.textViewMotivo)
        btnAsignarMecanico = findViewById(R.id.btnAsignarMecanico)
        textViewCocheAsignado = findViewById(R.id.textViewCocheAsignado)
        btnFinalizar = findViewById(R.id.btnFinalizar)
        btnAsignarPieza = findViewById(R.id.btnAsignarPieza)
        btnMostrarPiezas = findViewById(R.id.btnExtraPiezasAsignadas)
        btnAsignarHoras = findViewById(R.id.btnAsignarHoras)
        cardViewAsignarMecanico = findViewById(R.id.cardViewAsignarMecanico)
        cardViewFinalizar = findViewById(R.id.cardViewFinalizar)
        cardViewAsignarPieza = findViewById(R.id.cardViewAsignarPieza)
        cardViewMostrarPiezas = findViewById(R.id.btnPiezasAsignadas)
        cardViewAsignarHoras = findViewById(R.id.cardViewAsignarHoras)


        // Recuperar y mostrar matrícula
        val matricula = intent.getStringExtra("matricula")
        textViewMatricula.text = matricula

        comprobarSiFinalizado()

        // Cargar datos iniciales
        mostrarMotivoReparacion(matricula)
        mostrarMecanicoAsignado(matricula)

        // Listeners de los botones
        btnAsignarMecanico.setOnClickListener {
            asignarMecanico()
        }

        btnFinalizar.setOnClickListener {
            val texto = textViewCocheAsignado.text.toString()
            if(!texto.equals("Sin mecánico asignado")){
                finalizarReparacion()
                this.btnFinalizar.isEnabled = false
                this.btnAsignarHoras.isEnabled = false
                this.btnAsignarPieza.isEnabled = false
                this.btnMostrarPiezas.isEnabled = false
                this.btnAsignarMecanico.isEnabled = false
                this.cardViewFinalizar.isEnabled = false
                this.cardViewAsignarHoras.isEnabled = false
                this.cardViewAsignarPieza.isEnabled = false
                this.cardViewAsignarMecanico.isEnabled = false
                this.cardViewMostrarPiezas.isEnabled = false
            }

            comprobarSiFinalizado()
        }

        btnAsignarPieza.setOnClickListener {
            val matriculaActual = textViewMatricula.text.toString()
            if (matriculaActual.isNotEmpty()) {
                val intent = Intent(this, VentanaAsignarPiezas::class.java).apply {
                    putExtra("matricula", matriculaActual)
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "No hay matrícula disponible", Toast.LENGTH_SHORT).show()
            }
        }


        btnAsignarHoras.setOnClickListener {
            val matriculaActual = textViewMatricula.text.toString()
            if (matriculaActual.isNotEmpty()) {
                val intent = Intent(this, VentanaHoras::class.java).apply {
                    putExtra("matricula", matriculaActual)
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "No hay matrícula disponible", Toast.LENGTH_SHORT).show()
            }
        }

        btnMostrarPiezas.setOnClickListener(){
            val matriculaActual = textViewMatricula.text.toString()
            if (matriculaActual.isNotEmpty()) {
                val intent = Intent(this, VentanaListaPiezasUsadas::class.java).apply {
                    putExtra("matricula", matriculaActual)
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "No hay matrícula disponible", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun asignarMecanico() {
        val matricula = textViewMatricula.text.toString()
        val idUsuario = Global.usuarioUser?.id

        if (matricula.isNotEmpty() && idUsuario != null) {
            ReparacionService().asignarMecanicoAReparacion(matricula, idUsuario) { success, error ->
                runOnUiThread {
                    if (success) {
                        Toast.makeText(this, "Mecánico asignado con éxito", Toast.LENGTH_SHORT).show()
                        mostrarMecanicoAsignado(matricula)
                        this.cardViewFinalizar.isEnabled = true;
                        this.btnFinalizar.isEnabled = true;
                    } else {
                        Toast.makeText(this, error ?: "Error al asignar mecánico", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            Toast.makeText(this, "Error: datos insuficientes", Toast.LENGTH_SHORT).show()
        }
    }


    fun comprobarSiFinalizado(){
        val matricula = textViewMatricula.text.toString()
        ReparacionService().obtenerEstadoReparacion(matricula) { estado, error ->
            runOnUiThread {
                if (estado.equals("finalizada")) {
                    this.btnFinalizar.isEnabled = false
                    this.btnAsignarHoras.isEnabled = false
                    this.btnAsignarPieza.isEnabled = false
                    this.btnMostrarPiezas.isEnabled = false
                    this.btnAsignarMecanico.isEnabled = false
                    this.cardViewFinalizar.isEnabled = false
                    this.cardViewAsignarHoras.isEnabled = false
                    this.cardViewAsignarPieza.isEnabled = false
                    this.cardViewAsignarMecanico.isEnabled = false
                    this.cardViewMostrarPiezas.isEnabled = false
                }
            }
        }
    }


    private fun finalizarReparacion() {
        val matricula = textViewMatricula.text.toString()
        if (matricula.isNotEmpty()) {
            // Primero calculamos los costes
            ReparacionService().calcularCostesReparacion(matricula) { costes, error ->
                runOnUiThread {
                    if (costes != null) {
                        val (costeTotalPiezas, costeTotal) = costes
                        // Luego finalizamos la reparación con los costes calculados
                        ReparacionService().finalizarReparacion(
                            matricula,
                            costeTotalPiezas,
                            costeTotal
                        ) { success, error ->
                            runOnUiThread {
                                if (success) {
                                    Toast.makeText(
                                        this,
                                        "Reparación finalizada. Coste total: $costeTotal €",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    btnAsignarMecanico.isEnabled = false
                                    btnAsignarPieza.isEnabled = false
                                    btnMostrarPiezas.isEnabled = false
                                    btnFinalizar.isEnabled = false
                                } else {
                                    Toast.makeText(
                                        this,
                                        error ?: "Error al finalizar reparación",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    } else {
                        Toast.makeText(
                            this,
                            error ?: "Error al calcular costes",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        } else {
            Toast.makeText(this, "Matrícula no válida", Toast.LENGTH_SHORT).show()
        }
    }

    private fun mostrarMecanicoAsignado(matricula: String?) {
        if (matricula != null) {
            ReparacionService().obtenerMecanicoAsignado(matricula) { nombre, error ->
                runOnUiThread {
                    if (error != null) {
                        textViewCocheAsignado.text = "Error: $error"
                    } else if (nombre != null) {
                        textViewCocheAsignado.text = "Asignado a: $nombre"
                        btnAsignarMecanico.isEnabled = false
                        cardViewAsignarMecanico.isEnabled = false
                    } else {
                        textViewCocheAsignado.text = "Sin mecánico asignado"
                        this.cardViewFinalizar.isEnabled = false
                        this.btnFinalizar.isEnabled = false
                    }
                }
            }
        } else {
            textViewCocheAsignado.text = "Matrícula no disponible"
        }
    }


    private fun mostrarMotivoReparacion(matricula: String?) {
        if (matricula != null) {
            ReparacionService().obtenerMotivoReparacion(matricula) { motivo, error ->
                runOnUiThread {
                    if (error != null) {
                        textViewMotivo.text = "Error: $error"
                    } else if (motivo != null) {
                        textViewMotivo.text = motivo
                    } else {
                        textViewMotivo.text = "Sin motivo registrado"
                    }
                }
            }
        } else {
            textViewMotivo.text = "Matrícula no disponible"
        }
    }

}