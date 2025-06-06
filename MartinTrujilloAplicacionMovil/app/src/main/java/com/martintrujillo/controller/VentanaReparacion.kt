package com.martintrujillo.controller

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.martintrujillo.R
import com.martintrujillo.model.Coche
import com.martintrujillo.service.ReparacionService
import com.squareup.picasso.Picasso

class VentanaReparacion : AppCompatActivity() {


    private val imageButtonIds = arrayOf(
        R.id.btnReparacion1, R.id.btnReparacion2, R.id.btnReparacion3, R.id.btnReparacion4,
        R.id.btnReparacion5, R.id.btnReparacion6, R.id.btnReparacion7, R.id.btnReparacion8,
        R.id.btnReparacion9, R.id.btnReparacion10
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ventana_reparacion)

        // Configuración del edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configurar ImageButtons cuadrados
        setupSquareImageButtons()
        cargarCochesEnReparacion()
    }

    private fun setupSquareImageButtons() {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        // Calculamos el tamaño considerando márgenes (8dp convertidos a píxeles)
        val marginPx = (8 * resources.displayMetrics.density).toInt()
        val screenWidth = displayMetrics.widthPixels - (2 * marginPx)
        val buttonSize = (screenWidth / 2) - marginPx

        // Lista de todos los botones de reparación
        val repairButtonIds = arrayOf(
            R.id.btnReparacion1, R.id.btnReparacion2, R.id.btnReparacion3, R.id.btnReparacion4,
            R.id.btnReparacion5, R.id.btnReparacion6, R.id.btnReparacion7, R.id.btnReparacion8,
            R.id.btnReparacion9, R.id.btnReparacion10
        )

        // Aplicar tamaño cuadrado a cada botón
        repairButtonIds.forEach { id ->
            findViewById<ImageButton>(id).apply {
                layoutParams.width = buttonSize
                layoutParams.height = buttonSize
                requestLayout()
            }
        }
    }

    // Manejo de clicks para los botones de reparación
    fun onReparacionClick(view: View) {
        when (view.id) {
            R.id.btnReparacion1 -> { /* Acción para reparación 1 */ }
            R.id.btnReparacion2 -> { /* Acción para reparación 2 */ }
            R.id.btnReparacion3 -> { /* Acción para reparación 3 */ }
            R.id.btnReparacion4 -> { /* Acción para reparación 4 */ }
            R.id.btnReparacion5 -> { /* Acción para reparación 5 */ }
            R.id.btnReparacion6 -> { /* Acción para reparación 6 */ }
            R.id.btnReparacion7 -> { /* Acción para reparación 7 */ }
            R.id.btnReparacion8 -> { /* Acción para reparación 8 */ }
            R.id.btnReparacion9 -> { /* Acción para reparación 9 */ }
            R.id.btnReparacion10 -> { /* Acción para reparación 10 */ }
        }
    }


    private fun cargarCochesEnReparacion() {
        val reparacionService = ReparacionService()  // Creamos una instancia
        reparacionService.obtenerCochesEnReparacion { listaCoches, error ->
            runOnUiThread {
                if (error != null) {
                    Toast.makeText(this, "Error cargando coches: $error", Toast.LENGTH_LONG).show()
                } else if (listaCoches != null) {
                    mostrarCochesEnBotones(listaCoches)
                }
            }
        }
    }


    private fun mostrarCochesEnBotones(coches: List<Coche>) {
        for (i in imageButtonIds.indices) {
            val imageButton = findViewById<ImageButton>(imageButtonIds[i])
            if (i < coches.size) {
                val coche = coches[i]

                // Usamos Picasso para cargar la imagen del coche
                if (coche.imagen != null && coche.imagen.isNotEmpty()) {
                    Picasso.get()
                        .load(coche.imagen)
                        .placeholder(R.drawable.ic_carga) // imagen por defecto si no ha cargado
                        .error(R.drawable.ic_car)       // imagen por defecto si hay error
                        .into(imageButton)
                } else {
                    imageButton.setImageResource(R.drawable.ic_car)
                }

                imageButton.setOnClickListener {
                    Toast.makeText(this, "Coche: ${coche.marca} ${coche.modelo}", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, VentanaGestionReparacion::class.java)
                    intent.putExtra("matricula", coche.matricula)
                    startActivity(intent)
                }

                imageButton.visibility = View.VISIBLE

            } else {
                imageButton.visibility = View.GONE
            }
        }
    }

}