package com.martintrujillo.controller

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.martintrujillo.R
import com.martintrujillo.model.Global
import com.martintrujillo.model.Usuario
import com.martintrujillo.service.NombreApellidoService

class VentanaNombreApellido : AppCompatActivity() {

    private lateinit var editTextNombre: EditText
    private lateinit var editTextCorreo: EditText
    private lateinit var editTextContrasenia: EditText
    private lateinit var editTextPuesto: EditText
    private lateinit var btnGuardar: Button

    private val nombreApellidoService = NombreApellidoService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ventana_nombre_apellido)

        editTextNombre = findViewById(R.id.editTextNombre)
        editTextCorreo = findViewById(R.id.editTextCorreo)
        editTextContrasenia = findViewById(R.id.editTextContrasenia)
        editTextPuesto = findViewById(R.id.editTextPuesto)
        btnGuardar = findViewById(R.id.btnGuardar)

        // Cargar datos del usuario actual
        cargarDatosUsuario()

        btnGuardar.setOnClickListener {
            guardarDatosUsuario()
        }
    }

    private fun cargarDatosUsuario() {
        val usuario = Global.usuarioUser
        if (usuario != null) {
            editTextNombre.setText(usuario.nombre)
            editTextCorreo.setText(usuario.email)
            editTextContrasenia.setText(usuario.contrasenia)
            editTextPuesto.setText(usuario.rol)
            editTextPuesto.isEnabled = false
        } else {
            Toast.makeText(this, "No hay usuario logueado", Toast.LENGTH_SHORT).show()
            finish()
        }
    }


    private fun guardarDatosUsuario() {
        val nombre = editTextNombre.text.toString()
        val correo = editTextCorreo.text.toString()
        val contrasenia = editTextContrasenia.text.toString()
        val puesto = editTextPuesto.text.toString()

        // Validar campos
        if (nombre.isEmpty() || correo.isEmpty() || contrasenia.isEmpty() || puesto.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        // Obtener el usuario actual de Global
        val usuarioActual = Global.usuarioUser ?: run {
            Toast.makeText(this, "No hay usuario logueado", Toast.LENGTH_SHORT).show()
            return
        }

        // Crear objeto Usuario con los datos actualizados
        val usuarioActualizado = Usuario(
            id = usuarioActual.id,
            nombre = nombre,
            email = correo,
            contrasenia = contrasenia,
            rol = puesto
        )

        // Ejecutar en un hilo secundario
        Thread {
            try {
                val resultado = nombreApellidoService.actualizarUsuario(usuarioActualizado)
                runOnUiThread {
                    if (resultado > 0) {
                        // Actualizar el usuario en Global con los nuevos datos
                        Global.usuarioUser = usuarioActualizado
                        Toast.makeText(this, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Error al actualizar los datos", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
        finish()
    }
}