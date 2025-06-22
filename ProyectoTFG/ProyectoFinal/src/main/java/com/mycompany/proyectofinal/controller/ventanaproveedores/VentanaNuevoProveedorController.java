/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.proyectofinal.controller.ventanaproveedores;

import com.mycompany.proyectofinal.controller.data.Global;
import com.mycompany.proyectofinal.model.Proveedor;
import com.mycompany.proyectofinal.service.ProveedorService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author dav85
 */
public class VentanaNuevoProveedorController {

    @FXML
    private TextField textFieldNombre;
    @FXML
    private TextField textFieldTelefono;
    @FXML
    private TextField textFieldEmail;

    private Proveedor proveedorAModificar;
    private boolean esModificacion = false;
    @FXML
    private Button btnNuevo;
    @FXML
    private Label txtTitle;
    @FXML
    private Button btnUsuario;

    /**
     * Initializes the controller class.
     */
    public void initialize() {
        btnUsuario.setText(Global.usuarioUser.getNombre() + "\n" + Global.usuarioUser.getEmail());
    }

    
     /**
     * Metodo para cargar el proveedor seleccionado
     *
     * @param proveedor (Proveedor)
     */
    public void cargarProveedor(Proveedor proveedor) {
        this.proveedorAModificar = proveedor;
        this.esModificacion = true;
        this.txtTitle.setText("MODIFICACIÓN PROVEEDOR");
        // Llenar los campos con los datos del proveedor
        textFieldNombre.setText(proveedor.getNombre());
        textFieldTelefono.setText(String.valueOf(proveedor.getTelefono()));
        textFieldEmail.setText(proveedor.getEmail());
    }

    /**
     * Metodo para insertar o modificar un proveedor 
     */
    @FXML
    public void insertarProveedor() {
        try {
            String nombre = textFieldNombre.getText();
            int telefono = Integer.parseInt(textFieldTelefono.getText());
            String email = textFieldEmail.getText();

            if (nombre.isBlank() || email.isBlank()) {
                mostrarAlerta("Error", "Debes introducir todos los campos.", Alert.AlertType.ERROR);
                return;
            }

            //En caso de que haya sido modificado
            if (esModificacion) {
                proveedorAModificar.setNombre(nombre);
                proveedorAModificar.setTelefono(telefono);
                proveedorAModificar.setEmail(email);
                ProveedorService.modificarProveedor(proveedorAModificar);
            } else {
                Proveedor proveedor = new Proveedor();
                proveedor.setNombre(nombre);
                proveedor.setTelefono(telefono);
                proveedor.setEmail(email);
                ProveedorService.insertProveedor(proveedor);
            }

            // Cerrar la ventana
            ((Stage) textFieldNombre.getScene().getWindow()).close();
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "El teléfono debe ser un número válido.", Alert.AlertType.ERROR);
        } catch (Exception e) {
            mostrarAlerta("Error", "Ocurrió un error inesperado: " + e.getMessage(), Alert.AlertType.ERROR);
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
     */
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setHeaderText(null);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

}
