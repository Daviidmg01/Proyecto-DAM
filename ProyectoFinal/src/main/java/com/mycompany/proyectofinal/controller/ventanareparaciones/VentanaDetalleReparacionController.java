package com.mycompany.proyectofinal.controller.ventanareparaciones;

import com.mycompany.proyectofinal.controller.data.Global;
import com.mycompany.proyectofinal.controller.data.ReparacionesInfoAdc;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 */
public class VentanaDetalleReparacionController {

    @FXML
    private Text txt_title;
    @FXML
    private Text txt_horasTrabajo;
    @FXML
    private Text txt_reparacion;
    @FXML
    private Text txt_fechEntrada;
    @FXML
    private Text txt_estado;
    @FXML
    private Text txt_fechSalida;
    @FXML
    private Text txt_fechaSalidaTit;
    @FXML
    private Text txt_motivo;
    @FXML
    private Text txt_costManObra;
    @FXML
    private Text txt_costPiezas;
    @FXML
    private Text txt_costeTotal;
    @FXML
    private Button btnUsuario;

    /**
     * Initializes the controller class.
     */
    public void initialize(ReparacionesInfoAdc reparacion) {
        btnUsuario.setText(Global.usuarioUser.getNombre() + "\n" + Global.usuarioUser.getEmail());
        this.txt_title.setText("Información de la reparacion " + reparacion.getIdReparacion());
        this.txt_horasTrabajo.setText(String.valueOf(reparacion.getHorasTrabajo()));
        this.txt_reparacion.setText(String.valueOf(reparacion.getMatricula()));
        this.txt_fechEntrada.setText(reparacion.getFechaEntrada().toString());
        this.txt_estado.setText(reparacion.getEstado());
        if (reparacion.getFechaSalida() != null) {
            this.txt_fechSalida.setVisible(true);
            this.txt_fechSalida.setText(reparacion.getFechaSalida().toString());
            this.txt_fechaSalidaTit.setVisible(true);
        } else {
            this.txt_fechSalida.setVisible(false);
            this.txt_fechaSalidaTit.setVisible(false);
        }
        this.txt_motivo.setText(reparacion.getMotivo());
        this.txt_costManObra.setText(String.valueOf(reparacion.getCosteManoObra()) + " €");
        this.txt_costPiezas.setText(String.valueOf(reparacion.getCosteTotalPiezas()) + " €");
        this.txt_costeTotal.setText(String.valueOf(reparacion.getCosteTotalReparacion()) + " €");
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
