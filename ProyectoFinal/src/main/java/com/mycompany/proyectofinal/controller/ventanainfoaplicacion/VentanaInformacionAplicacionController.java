/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.proyectofinal.controller.ventanainfoaplicacion;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Usuario
 */
public class VentanaInformacionAplicacionController {

    @FXML
    private Button btnUsuarios;
    @FXML
    private Button btnReparacion;
    @FXML
    private Button btnPiezas;
    @FXML
    private Button btnProveedores;
    @FXML
    private Button btnContabilidad;
    @FXML
    private Button btnMenuPrincipal;
    @FXML
    private Button btnDatosPersonales;

    /**
     * Initializes the controller class.
     */
    public void initialize() {
        // TODO
    }

    /**
     * Metodo para inicializar la ventana para mostrar la informacion
     *
     * @param modo (String)
     */
    private void mostrarInfo(String modo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/proyectofinal/views/VentanaMostrarInformacion.fxml"));
            Parent root;
            root = loader.load();

            // Pasar los datos al controller
            VentanaMostrarInformacionController controller = loader.getController();
            controller.initialize(modo);

            // Mostrar la ventana
            Stage stage = new Stage();
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/mycompany/proyectofinal/images/logo_taller.png")));
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir la ventana.", Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    public void informacionUsuarios(){
        mostrarInfo("usuarios");
    }
    
    @FXML
    public void informacionMenuPrincipal(){
        mostrarInfo("menuprincipal");
    }
    
    @FXML
    public void informacionReparacion(){
        mostrarInfo("reparacioncoches");
    }
    
    @FXML
    public void informacionProveedores(){
        mostrarInfo("proveedores");
    }
    
    @FXML
    public void informacionContabilidad(){
        mostrarInfo("contabilidad");
    }
    
    @FXML
    public void informacionPiezas(){
        mostrarInfo("piezas");
    }
    
    @FXML
    public void informacionDatosPersonales(){
        mostrarInfo("datospersonales");
    }

    /**
     * Metodo para mostrar una alerta, title -> titulo de la alerta, message ->
     * mensaje de la alerta
     *
     * @param title (String)
     * @param message (String)
     */
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setHeaderText(null);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

}
