/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.proyectofinal.controller.ventanaproveedores;

import com.mycompany.proyectofinal.controller.data.Global;
import com.mycompany.proyectofinal.model.Proveedor;
import com.mycompany.proyectofinal.service.ProveedorService;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author dav85
 */
public class VentanaGestionProveedoresController implements Initializable {

    @FXML
    private TableView<Proveedor> tabla;
    @FXML
    private TableColumn<Proveedor, Integer> tablaTelefono;
    @FXML
    private TableColumn<Proveedor, String> tabalEmail;
    @FXML
    private TableColumn<Proveedor, String> tablaNombre;

    private ObservableList<Proveedor> listaProveedores;
    @FXML
    private Button btnNuevo;
    @FXML
    private Button btnUsuario;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnUsuario.setText(Global.usuarioUser.getNombre() + "\n" + Global.usuarioUser.getEmail());
        listaProveedores = FXCollections.observableArrayList();
        tablaNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        tabalEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        tablaTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        cargarDatos();
    }

    /**
     * Metodo que carga todos los proveedores de la base de datos
     */
    public void cargarDatos() {
        listaProveedores.clear();
        listaProveedores = FXCollections.observableList(ProveedorService.getAllProveedores());
        tabla.setItems(listaProveedores);
    }

    /**
     * Metodo para abrir la ventara de nuevo proveedor
     */
    @FXML
    public void ventanaNuevoProveedor() {
        abrirVentana("/com/mycompany/proyectofinal/views/VentanaNuevoProveedor.fxml");
    }

    /**
     * Metodo que con la ruta de un <b>fxml</b> lo ejecuta para mostrar la
     * ventana
     *
     * @param fxmlFile (String)
     */
    private void abrirVentana(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/mycompany/proyectofinal/images/logo_taller.png")));
            stage.initModality(Modality.APPLICATION_MODAL.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            //Actualizo los datos
            cargarDatos();
        } catch (Exception ex) {
            ex.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir la ventana: " + fxmlFile, Alert.AlertType.ERROR);
        }
    }

    /**
     * Metodo para abrir la ventana para modificar un proveedor
     */
    @FXML
    public void modificarProveedor() {
        Proveedor proveedorSeleccionado = tabla.getSelectionModel().getSelectedItem();
        //System.out.println("Id del proveedor seleccionado: " + proveedorSeleccionado.getIdProveedor());

        if (proveedorSeleccionado == null) {
            mostrarAlerta("Error", "Debes seleccionar un proveedor para modificar.", Alert.AlertType.ERROR);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/proyectofinal/views/VentanaNuevoProveedor.fxml"));
            Parent root = loader.load();

            // Obtener el controlador y pasar el proveedor a modificar
            VentanaNuevoProveedorController controller = loader.getController();
            controller.cargarProveedor(proveedorSeleccionado);

            Stage stage = new Stage();
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/mycompany/proyectofinal/images/logo_taller.png")));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Actualizar la tabla después de modificar
            cargarDatos();
        } catch (Exception ex) {
            ex.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir la ventana de modificación.", Alert.AlertType.ERROR);
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

    @FXML
    private void eliminarProveedor(ActionEvent event) {
        // Obtener el usuario seleccionado
        Proveedor proveedorSeleccionado = tabla.getSelectionModel().getSelectedItem();

        if (proveedorSeleccionado != null) {
            // Mostrar la alerta de confirmación
            Optional<ButtonType> resultado = mostrarAlertaConfirmacion("Confirmación", "¿Estás seguro de que deseas eliminar este usuario?");

            // Verificar si el usuario presionó "Sí" o "No"
            if (resultado.isPresent() && resultado.get().getText().equalsIgnoreCase("Sí")) {
                // Si el usuario confirma la eliminación
                ProveedorService.deleteProveedor(proveedorSeleccionado.getIdProveedor());
                cargarDatos();
            }
        } else {
            mostrarAlerta("Error", "Debe seleccionar un usuario", Alert.AlertType.ERROR);
        }
    }

    /**
     * Método para mostrar una alerta de confirmación (Sí / No).
     *
     * @param titulo (String)
     * @param mensaje (String)
     * @return Optional<ButtonType> (Respuesta de la alerta)
     */
    private Optional<ButtonType> mostrarAlertaConfirmacion(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText(null);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);

        // Modificar los botones para que diga "Sí" y "No"
        ButtonType botonYes = new ButtonType("Sí");
        ButtonType botonNo = new ButtonType("No");

        alert.getButtonTypes().setAll(botonYes, botonNo);

        // Mostrar la alerta y esperar la respuesta del usuario
        return alert.showAndWait();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setHeaderText(null);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

}
