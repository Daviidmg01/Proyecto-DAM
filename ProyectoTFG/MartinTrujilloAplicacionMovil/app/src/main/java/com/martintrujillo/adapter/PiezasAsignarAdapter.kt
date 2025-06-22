package com.martintrujillo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.NumberPicker
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.martintrujillo.R
import com.martintrujillo.model.Piezas

class PiezasAsignarAdapter(
    private val listaPiezas: List<Piezas>,
    private val cantidadesSeleccionadas: MutableList<Int>,
    private val piezasSeleccionadas: MutableList<Boolean>
) : RecyclerView.Adapter<PiezasAsignarAdapter.PiezaViewHolder>() {

    inner class PiezaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBoxSeleccion)
        val nombreTextView: TextView = itemView.findViewById(R.id.textViewNombrePieza)
        val precioTextView: TextView = itemView.findViewById(R.id.textViewPrecio)
        val numberPicker: NumberPicker = itemView.findViewById(R.id.numberPickerCantidad)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PiezaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_item_asignar_pieza, parent, false)
        return PiezaViewHolder(view)
    }

    override fun onBindViewHolder(holder: PiezaViewHolder, position: Int) {
        val pieza = listaPiezas[position]

        holder.nombreTextView.text = pieza.nombrePieza
        holder.precioTextView.text = "Precio: %.2f€".format(pieza.precioVenta)

        holder.numberPicker.minValue = 1
        holder.numberPicker.maxValue = pieza.cantidadDisponible
        holder.numberPicker.value = cantidadesSeleccionadas[position]

        holder.checkBox.isChecked = piezasSeleccionadas[position]

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            piezasSeleccionadas[position] = isChecked
        }

        holder.numberPicker.setOnValueChangedListener { _, _, newVal ->
            cantidadesSeleccionadas[position] = newVal
        }
    }

    override fun getItemCount(): Int = listaPiezas.size

    fun obtenerPiezasSeleccionadas(): List<Pair<Piezas, Int>> {
        return listaPiezas.mapIndexedNotNull { index, pieza ->
            if (piezasSeleccionadas[index]) pieza to cantidadesSeleccionadas[index] else null
        }
    }
}
