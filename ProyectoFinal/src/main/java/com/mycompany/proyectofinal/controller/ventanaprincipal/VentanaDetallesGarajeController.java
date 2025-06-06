package com.mycompany.proyectofinal.controller.ventanaprincipal;

import com.mycompany.proyectofinal.controller.data.Global;
import com.mycompany.proyectofinal.controller.data.ReparacionesInfoAdc;
import com.mycompany.proyectofinal.model.Coche;
import com.mycompany.proyectofinal.service.ReparacionService;
import java.util.Date;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Clase controladora de la ventana detalles del garage
 */
public class VentanaDetallesGarajeController {

    @FXML
    private Label labelMatricula;
    @FXML
    private Label labelPropietario;
    @FXML
    private Label labelFechaEntrada;
    @FXML
    private Label labelMotivo;
    @FXML
    private ImageView imagenCoche;
    @FXML
    private Button btnUsuario;
    @FXML
    private Label labelPrecioTotal;
    @FXML
    private Label labelPrecioPiezas;
    @FXML
    private Label labelPrecioManoObra;

    private Coche cocheSeleccionado;

    /**
     * Metodo para cargar los datos de un coche
     *
     * @param coche (Coche)
     * @param motivo (String)
     * @param fechaEntrada (String)
     * @param propietario (String)
     */
    public void cargarDatos(Coche coche, String motivo, Date fechaEntrada, String propietario) {
        btnUsuario.setText(Global.usuarioUser.getNombre() + "\n" + Global.usuarioUser.getEmail());
        this.cocheSeleccionado = coche;
        ReparacionesInfoAdc reparacion = ReparacionService.buscarReparacionPorMatriculaYFecha(coche.getMatricula(), fechaEntrada);
        // Actualizar los elementos de la interfaz con los datos del coche
        labelMatricula.setText(coche.getMatricula());
        labelPropietario.setText(propietario);
        labelFechaEntrada.setText(fechaEntrada.toString());
        labelMotivo.setText(motivo);
        if (reparacion != null) {
            labelPrecioManoObra.setText(reparacion.getCosteManoObra() + " €");
            labelPrecioPiezas.setText(reparacion.getCosteTotalPiezas() + " €");
            labelPrecioTotal.setText(reparacion.getCosteTotalReparacion() + " €");
        }else{
            mostrarAlerta("Info", "Todavia no hay una reparacion activa", Alert.AlertType.INFORMATION);
            labelPrecioManoObra.setText("0€");
            labelPrecioPiezas.setText("0€");
            labelPrecioTotal.setText("0€");
        }

        // Cargar la imagen del coche
        if (coche.getImagen() != null && !coche.getImagen().isEmpty()) {
            Image imagen = new Image(coche.getImagen());
            imagenCoche.setImage(imagen);
        } else {
            // Imagen por defecto si no hay imagen
            imagenCoche.setImage(new Image("https://img.freepik.com/vector-gratis/coche-sedan-rojo-estilo-dibujos-animados-aislado-sobre-fondo-blanco_1308-64900.jpg"));
        }
    }

    /**
     * Metodo que abre la ventana de la gestión personal
     */
    @FXML
    public void ventanaGestionPersonal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/proyectofinal/views/VentanaInformacionPersonal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/mycompany/proyectofinal/images/logo_taller.png")));
            stage.initModality(Modality.APPLICATION_MODAL.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
            btnUsuario.setText(Global.usuarioUser.getNombre() + "\n" + Global.usuarioUser.getEmail());
        } catch (Exception ex) {
            ex.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir la ventana.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Metodo para mostrar una alerta, title -> titulo de la alerta, message ->
     * mensaje de la alerta
     *
     * @param title (String)
     * @param message (String)
     * @param tipo (Alert.AlertType)
     */
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setHeaderText(null);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

}
