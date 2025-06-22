    package com.martintrujillo.controller

    import android.os.Bundle
    import androidx.appcompat.app.AppCompatActivity
    import androidx.recyclerview.widget.LinearLayoutManager
    import androidx.recyclerview.widget.RecyclerView
    import com.martintrujillo.R
    import com.martintrujillo.adapter.PiezasAdapter
    import com.martintrujillo.model.Piezas
    import com.martintrujillo.service.PiezasService

    class VentanaListaPiezas : AppCompatActivity() {

        private lateinit var recyclerPiezas: RecyclerView

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_ventana_lista_piezas)

            recyclerPiezas = findViewById(R.id.recyclerViewPiezas)
            recyclerPiezas.layoutManager = LinearLayoutManager(this)

            val service = PiezasService()
            service.obtenerTodasLasPiezas { lista, error ->
                runOnUiThread {
                    if (error == null && lista != null) {
                        val adapter = PiezasAdapter(lista)
                        recyclerPiezas.adapter = adapter
                    } else {
                        // Aquí puedes mostrar un Toast o mensaje de error
                    }
                }
            }
        }
    }
