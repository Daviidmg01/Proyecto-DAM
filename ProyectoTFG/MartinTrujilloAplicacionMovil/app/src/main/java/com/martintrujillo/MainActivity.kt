// com.martintrujillo/MainActivity.kt
package com.martintrujillo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.martintrujillo.controller.VentanaPrincipal
import com.martintrujillo.model.Global
import com.martintrujillo.service.UsuarioService

class MainActivity : AppCompatActivity() {

    private lateinit var btnEntrar: Button
    private lateinit var editTextTextNombre: EditText
    private lateinit var editTextTextContrasenia: EditText
    private lateinit var usuarioService: UsuarioService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar el servicio
        usuarioService = UsuarioService(this)

        btnEntrar = findViewById(R.id.btnLogin)
        editTextTextNombre = findViewById(R.id.editTextTextNombre)
        editTextTextContrasenia = findViewById(R.id.editTextTextContrasenia)

        btnEntrar.setOnClickListener {
            val usuario = editTextTextNombre.text.toString().trim()
            val contrasenia = editTextTextContrasenia.text.toString().trim()

            if (usuario.isEmpty() || contrasenia.isEmpty()) {
                Toast.makeText(this, "Ingrese usuario y contraseña", Toast.LENGTH_SHORT).show()
            } else {
                usuarioService.autenticarUsuario(usuario, contrasenia) { success, user, message ->
                    runOnUiThread {
                        if (success && user != null) {
                            Global.usuarioUser = user
                            Toast.makeText(this, "Iniciando sesión...", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, VentanaPrincipal::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, message ?: "Error desconocido", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}