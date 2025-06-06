package com.martintrujillo.controller

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.card.MaterialCardView
import com.martintrujillo.R
import com.martintrujillo.model.Coche
import com.martintrujillo.service.CochesService
import com.squareup.picasso.Picasso

class VentanaGargaje : AppCompatActivity() {

    private lateinit var btnPrincipal: MaterialCardView
    private lateinit var cochesService: CochesService
    private lateinit var listaCoches: List<Coche>
    private val imageButtonIds = arrayOf(
        R.id.imageButton1, R.id.imageButton2, R.id.imageButton3, R.id.imageButton4,
        R.id.imageButton5, R.id.imageButton6, R.id.imageButton7, R.id.imageButton8,
        R.id.imageButton9, R.id.imageButton10
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ventana_gargaje)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnPrincipal = findViewById(R.id.buttonMain)
        cochesService = CochesService()

        setupSquareImageButtons()
        cargarCochesEnGaraje()

        btnPrincipal.setOnClickListener(){
            if(listaCoches.size < 10){
                startActivity(Intent(this, VentanaCrearCliente::class.java))
            }else{
                Toast.makeText(this, "Ya no caben más coches en el garaje", Toast.LENGTH_SHORT).show()
            }
            onResume()
        }
    }

    private fun setupSquareImageButtons() {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        val marginInPx = (8 * resources.displayMetrics.density).toInt()
        val screenWidth = displayMetrics.widthPixels - (2 * marginInPx)
        val buttonSize = (screenWidth / 2) - marginInPx

        imageButtonIds.forEach { id ->
            findViewById<ImageButton>(id).apply {
                layoutParams.width = buttonSize
                layoutParams.height = buttonSize
                requestLayout()
            }
        }
    }


    private fun cargarCochesEnGaraje() {
        cochesService.obtenerCochesEnGaraje { listaCoches, error ->
            runOnUiThread {
                if (error != null) {
                    Toast.makeText(this, "Error cargando coches: $error", Toast.LENGTH_LONG).show()
                } else if (listaCoches != null) {
                    this.listaCoches = listaCoches
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
                    val intent = Intent(this, VentanaVerDatosCoche::class.java)
                    intent.putExtra("id_coche", coche.idCoche)
                    intent.putExtra("id_usuario", coche.idCliente)
                    startActivity(intent)
                }

                imageButton.visibility = View.VISIBLE

            } else {
                imageButton.visibility = View.GONE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        cargarCochesEnGaraje()
    }

}
