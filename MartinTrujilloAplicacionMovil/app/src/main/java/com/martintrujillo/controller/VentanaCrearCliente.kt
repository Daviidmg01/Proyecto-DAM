package com.martintrujillo.controller

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.martintrujillo.R
import com.martintrujillo.model.Cliente

class VentanaCrearCliente : AppCompatActivity() {

    private lateinit var editTextNombre:EditText
    private lateinit var editTextDNI:EditText
    private lateinit var editTextTelf:EditText
    private lateinit var editTextEmail:EditText
    private lateinit var btnSiguiente:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ventana_crear_cliente)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        inicializar()

        findViewById<Button>(R.id.btnSiguiente).setOnClickListener {
            crearCliente()
            //finish()
        }

    }

    private fun inicializar(){
        this.editTextNombre = findViewById(R.id.editTextNombre)
        this.editTextDNI = findViewById(R.id.editTextDNI)
        this.editTextTelf = findViewById(R.id.editTextTelf)
        this.editTextEmail = findViewById(R.id.editTextEmail)
        this.btnSiguiente = findViewById(R.id.btnSiguiente)
    }
    private fun crearCliente() {
        val nombre = editTextNombre.text.toString().trim()
        val dni = editTextDNI.text.toString().trim()
        val telefono = editTextTelf.text.toString().trim()
        val email = editTextEmail.text.toString().trim()

        // Validaciones básicas
        if (nombre.isEmpty() || dni.isEmpty() || telefono.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        // Validar longitud máxima del DNI (15 caracteres)
        if (dni.length > 15) {
            Toast.makeText(this, "El DNI no puede tener más de 15 caracteres", Toast.LENGTH_SHORT).show()
            editTextDNI.text.clear() // Limpiamos el campo
            editTextDNI.requestFocus()
            return
        }

        // Validar longitud máxima del teléfono (15 caracteres)
        if (telefono.length > 15) {
            Toast.makeText(this, "El teléfono no puede tener más de 15 caracteres", Toast.LENGTH_SHORT).show()
            editTextTelf.text.clear() // Limpiamos el campo
            editTextTelf.requestFocus()
            return
        }

        val cliente = Cliente(
            nombre = nombre,
            dni = dni,
            telefono = telefono,
            email = if (email.isEmpty()) null else email
        )

        val intent = Intent(this, VentanaCrearCoche::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("dni", dni)
        intent.putExtra("telefono", telefono)
        intent.putExtra("email", email)
        startActivity(intent)
        finish() // Esto sí cerrará la actividad cuando todo esté correcto
    }

}