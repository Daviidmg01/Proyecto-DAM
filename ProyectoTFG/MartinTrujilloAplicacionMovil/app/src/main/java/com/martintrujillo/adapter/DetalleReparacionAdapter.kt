package com.martintrujillo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.martintrujillo.controller.Dto.PiezaReparacionDetalle
import com.martintrujillo.R

class DetalleReparacionAdapter(private val listaDetalles: List<PiezaReparacionDetalle>) : RecyclerView.Adapter<DetalleReparacionAdapter.DetalleViewHolder>() {

    class DetalleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val matricula: TextView = itemView.findViewById(R.id.textMatricula)
        val nombrePieza: TextView = itemView.findViewById(R.id.textNombrePieza)
        val cantidadUsada: TextView = itemView.findViewById(R.id.textCantidadUsada)
        val precioUnidad: TextView = itemView.findViewById(R.id.textPrecioUnidad)
        val precioTotal: TextView = itemView.findViewById(R.id.textPrecioTotal)
        val mecanico: TextView = itemView.findViewById(R.id.textMecanico)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetalleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_item_detalle_reparacion, parent, false)
        return DetalleViewHolder(view)
    }

    override fun onBindViewHolder(holder: DetalleViewHolder, position: Int) {
        val detalle = listaDetalles[position]
        holder.matricula.text = "Matrícula: ${detalle.matricula}"
        holder.nombrePieza.text = "Pieza: ${detalle.nombrePieza}"
        holder.cantidadUsada.text = "Cantidad: ${detalle.cantidadUsada}"
        holder.precioUnidad.text = "${detalle.precioUnidad} €/ud€"
        holder.precioTotal.text = "Total: ${detalle.precioTotal}€"
        holder.mecanico.text = "Asignado por: ${detalle.nombreMecanico}"
    }

    override fun getItemCount(): Int = listaDetalles.size
}