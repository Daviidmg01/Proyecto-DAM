package com.martintrujillo.controller

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.martintrujillo.R
import com.martintrujillo.model.Cliente
import com.martintrujillo.model.Coche
import com.martintrujillo.model.Reparacion
import com.martintrujillo.service.ClienteService
import com.martintrujillo.service.CochesService
import com.martintrujillo.service.ContabilidadService
import com.martintrujillo.service.ReparacionesService
import com.squareup.picasso.Picasso

class VentanaVerDatosCoche : AppCompatActivity() {

    private lateinit var btnSacar:Button
    private lateinit var txtMatricula:TextView
    private lateinit var txtMarca:TextView
    private lateinit var txtModelo:TextView
    private lateinit var txtNombre:TextView
    private lateinit var txtDNI:TextView
    private lateinit var txtCorreo:TextView
    private lateinit var txtTelf:TextView
    private lateinit var txtEstado:TextView
    private lateinit var txtFecha:TextView
    private lateinit var txtMotivo:TextView
    private lateinit var imageCoche:ImageView
    private lateinit var coche:Coche
    private lateinit var reparacion: Reparacion
    val cochesService = CochesService()
    val clienteService = ClienteService()
    val contabilidadService = ContabilidadService()
    val reparacionesService = ReparacionesService(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ventana_ver_datos_coche)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        inicializar()

        btnSacar.setOnClickListener(){
            this.btnSacar.isEnabled = false
            actualizarFechaSalida()
        }

    }

    fun inicializar(){
        this.btnSacar = findViewById(R.id.btnSacarGaraje)
        this.txtMatricula = findViewById(R.id.txtMatriculaDatosCoche)
        this.txtMarca = findViewById(R.id.txtMarcaMostrarDatosCoche)
        this.txtModelo = findViewById(R.id.txtModeloMostrarDatosCoche)
        this.txtNombre = findViewById(R.id.txtNombreUsuarioMostrarDatosCoche)
        this.txtDNI = findViewById(R.id.txtDNIUsuarioMostrarDatosCoche)
        this.txtCorreo = findViewById(R.id.txtCorreoUsuarioMostrarDatosCoche)
        this.txtTelf = findViewById(R.id.txtTelefUsuarioMostrarDatosCoche)
        this.txtEstado = findViewById(R.id.txtEstadoReparacionMostrarDatosCoche)
        this.txtFecha = findViewById(R.id.txtFechaEntradaReparacionMostrarDatosCoche)
        this.txtMotivo = findViewById(R.id.txtMotivoReparacionMostrarDatosCoche)
        this.imageCoche = findViewById(R.id.imageCoche)
        obtenerCoche()
    }

    fun obtenerCoche() {
        val intent = intent
        val id = intent.getIntExtra("id_coche", 0)

        // Primero obtenemos el coche por el id
        cochesService.consultarCochePorMatricula(id) { cocheObtenido ->
            runOnUiThread {
                if (cocheObtenido != null) {
                    // Si el coche existe, lo asignamos
                    this.coche = cocheObtenido

                    // Actualizamos la UI con los datos del coche
                    txtMatricula.text = coche.matricula
                    txtMarca.text = coche.marca
                    txtModelo.text = coche.modelo
                    if (coche.imagen != null && coche.imagen.isNotEmpty()) {
                        Picasso.get()
                            .load(coche.imagen)
                            .placeholder(R.drawable.ic_carga) // imagen por defecto si no ha cargado
                            .error(R.drawable.ic_car)       // imagen por defecto si hay error
                            .into(imageCoche)
                    } else {
                        imageCoche.setImageResource(R.drawable.ic_car)
                    }
                    // Una vez cargado el coche, obtenemos la reparación activa
                    obtenerReparacionYCliente()
                } else {
                    // Si no se encuentra el coche, mostramos mensaje de error
                    txtMatricula.text = "No se encontró el coche"
                }
            }
        }
    }

    fun obtenerReparacionYCliente() {
        // Asegúrate de que el coche ya está cargado antes de proceder
        val idCoche = coche.idCoche ?: return // Si no hay coche, no hacemos nada

        // Ahora obtenemos la reparación activa para el coche
        reparacionesService.consultarReparacionActivaPorCoche(idCoche) { reparacion ->
            runOnUiThread {
                if (reparacion != null) {
                    // Si la reparación existe, mostramos sus detalles
                    txtEstado.text = reparacion.estado
                    txtFecha.text = reparacion.fechaEntrada.toString()
                    txtMotivo.text = reparacion.motivo
                    this.reparacion = reparacion
                    if(reparacion.estado.equals("finalizada")){
                        this.btnSacar.isEnabled = true
                    }

                } else {
                    // Si no hay reparación activa, mostramos mensaje por defecto
                    txtEstado.text = "Sin reparación activa"
                    txtFecha.text = "-"
                    txtMotivo.text = "-"
                }

                // Una vez que hemos manejado la reparación, obtenemos los datos del cliente
                obtenerCliente()
            }
        }
    }

    fun obtenerCliente() {
        // Asegúrate de que el coche tenga un cliente asignado
        val idCliente = coche.idCliente ?: return // Si no hay cliente, no hacemos nada

        // Ahora obtenemos el cliente asociado al coche
        clienteService.consultarClientePorId(idCliente) { cliente ->
            runOnUiThread {
                if (cliente != null) {
                    // Si encontramos el cliente, mostramos sus datos
                    txtNombre.text = cliente.nombre
                    txtDNI.text = cliente.dni
                    txtCorreo.text = cliente.email ?: "-"
                    txtTelf.text = cliente.telefono
                } else {
                    // Si no encontramos el cliente, mostramos mensaje de error
                    txtNombre.text = "Cliente no encontrado"
                    txtDNI.text = "-"
                    txtCorreo.text = "-"
                    txtTelf.text = "-"
                }
            }
        }
    }

    fun actualizarFechaSalida(){
        this.btnSacar.isEnabled = false // Deshabilitar botón para evitar múltiples clics

        reparacionesService.cerrarReparacion(reparacion.idReparacion!!) { exito ->
            runOnUiThread {
                if (exito) {
                    // Actualizar el estado de la reparación en la UI inmediatamente
                    txtEstado.text = "finalizada"
                    Toast.makeText(this, "Reparación cerrada correctamente", Toast.LENGTH_SHORT).show()
                    aniadiarIngreso()
                } else {
                    btnSacar.isEnabled = true // Rehabilitar si falla
                    Toast.makeText(this, "Error al cerrar reparación", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun aniadiarIngreso(){
        val cantidad = reparacion.costeTotalReparacion?.toDouble() ?: 0.0
        val concepto = "Reparación de coche ${coche.matricula}, ${reparacion.motivo}"
        val idUsuario = reparacion.idMecanico

        contabilidadService.insertarIngreso(cantidad, concepto, idUsuario) { exito ->
            runOnUiThread {
                if (exito) {
                    Toast.makeText(this, "Ingreso registrado correctamente", Toast.LENGTH_SHORT).show()
                    marcarCocheComoFueraDeGaraje()
                } else {
                    btnSacar.isEnabled = true // Rehabilitar si falla
                    Toast.makeText(this, "Error al registrar ingreso", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun marcarCocheComoFueraDeGaraje() {
        cochesService.actualizarCocheFueraDeGaraje(coche.idCoche!!) { exito ->
            runOnUiThread {
                if (exito) {

                    Toast.makeText(this, "Coche marcado como fuera de garaje", Toast.LENGTH_SHORT).show()


                    // Cerrar la actividad si lo deseas
                    finish()
                } else {
                    btnSacar.isEnabled = true // Rehabilitar si falla
                    Toast.makeText(this, "Error al actualizar coche", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}