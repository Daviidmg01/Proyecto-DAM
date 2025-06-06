package com.martintrujillo.controller

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.card.MaterialCardView
import com.martintrujillo.R
import com.martintrujillo.model.Global

class VentanaPrincipal : AppCompatActivity() {

    private lateinit var btnReparaciones: MaterialCardView
    private lateinit var btnGaraje: MaterialCardView
    private lateinit var btnPiezas: MaterialCardView
    private lateinit var btnNombreApellido: TextView
    private lateinit var topBarCard: MaterialCardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ventana_principal)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicialización de vistas
        topBarCard = findViewById(R.id.topBarCard)
        btnReparaciones = findViewById(R.id.btnReparaciones)
        btnGaraje = findViewById(R.id.btnGaraje)
        btnPiezas = findViewById(R.id.btnPiezas)
        btnNombreApellido = findViewById(R.id.btnNombreApellidos)

        // Configuración del nombre de usuario
        val nombreUsuario = Global.usuarioUser?.nombre ?: "Usuario"
        btnNombreApellido.text = "Hola $nombreUsuario"

        // Listeners de click
        btnReparaciones.setOnClickListener {
            startActivity(Intent(this, VentanaReparacion::class.java))
        }

        btnGaraje.setOnClickListener {
            startActivity(Intent(this, VentanaGargaje::class.java))
        }

        btnPiezas.setOnClickListener {
            startActivity(Intent(this, VentanaListaPiezas::class.java))
        }

        // Listener para la barra superior (si es necesario)
        topBarCard.setOnClickListener {
            startActivity(Intent(this, VentanaNombreApellido::class.java))
        }
    }
}