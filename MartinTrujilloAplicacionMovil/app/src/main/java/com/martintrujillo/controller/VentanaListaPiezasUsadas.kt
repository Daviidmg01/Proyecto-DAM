package com.martintrujillo.controller

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.martintrujillo.R
import com.martintrujillo.adapter.DetalleReparacionAdapter
import com.martintrujillo.service.PiezasService

class VentanaListaPiezasUsadas : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ventana_lista_piezas_usadas)

        recyclerView = findViewById(R.id.recyclerViewPiezasUsadas)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val matricula = intent.getStringExtra("matricula") ?: run {
            mostrarError("No se recibió la matrícula del vehículo")
            finish()
            return
        }

        val service = PiezasService()
        service.obtenerPiezasDeUltimaReparacionActiva(matricula) { lista, error ->
            runOnUiThread {
                if (error == null && lista != null) {
                    recyclerView.adapter = DetalleReparacionAdapter(lista)
                } else {
                    Toast.makeText(this, error ?: "Error desconocido", Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    private fun mostrarError(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }
}
