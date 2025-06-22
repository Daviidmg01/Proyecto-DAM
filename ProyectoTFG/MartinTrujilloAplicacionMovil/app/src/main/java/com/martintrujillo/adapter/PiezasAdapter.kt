package com.martintrujillo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.martintrujillo.R
import com.martintrujillo.model.Piezas

class PiezasAdapter(private val listaPiezas: List<Piezas>) : RecyclerView.Adapter<PiezasAdapter.PiezaViewHolder>() {

    class PiezaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombrePieza: TextView = itemView.findViewById(R.id.textNombrePieza)
        val cantidad: TextView = itemView.findViewById(R.id.textCantidad)
        val precioVenta: TextView = itemView.findViewById(R.id.textPrecioVenta)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PiezaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_item_pieza, parent, false)
        return PiezaViewHolder(view)
    }

    override fun onBindViewHolder(holder: PiezaViewHolder, position: Int) {
        val pieza = listaPiezas[position]
        holder.nombrePieza.text = pieza.nombrePieza
        holder.cantidad.text = "Cantidad disponible: ${pieza.cantidadDisponible}"
        holder.precioVenta.text = "Precio venta: ${pieza.precioVenta}€"
    }

    override fun getItemCount(): Int = listaPiezas.size
}
