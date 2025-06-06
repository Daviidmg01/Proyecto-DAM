/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.proyectofinal.controller.ventanapiezas;

import com.mycompany.proyectofinal.controller.data.Global;
import com.mycompany.proyectofinal.model.Pieza;
import com.mycompany.proyectofinal.service.PiezaService;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
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
public class VentanaGestionPiezasController implements Initializable {

    @FXML
    private TableView<Pieza> tabla;
    @FXML
    private TableColumn<Pieza, String> tabla_nombrePieza;
    @FXML
    private TableColumn<Pieza, Integer> tabla_cantidadDisponible;
    @FXML
    private TableColumn<Pieza, Float> talba_precioCompra;
    @FXML
    private TableColumn<Pieza, Float> tabla_precioVenta;

    private ObservableList<Pieza> listaPiezas;
    @FXML
    private Button btnComprarPiezas;
    @FXML
    private Button btnUsuario;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnUsuario.setText(Global.usuarioUser.getNombre() + "\n" + Global.usuarioUser.getEmail());
        listaPiezas = FXCollections.observableArrayList();
        tabla_nombrePieza.setCellValueFactory(new PropertyValueFactory<>("nombrePieza"));
        tabla_cantidadDisponible.setCellValueFactory(new PropertyValueFactory<>("cantidadDisponible"));
        talba_precioCompra.setCellValueFactory(new PropertyValueFactory<>("precioCompra"));
        tabla_precioVenta.setCellValueFactory(new PropertyValueFactory<>("precioVenta"));
        cargarDatos();
    }

    /**
     * Metodo para cargar las piezas de la base de datos
     */
    public void cargarDatos() {
        listaPiezas.clear();
        listaPiezas = FXCollections.observableList(PiezaService.getAllPiezas());
        tabla.setItems(listaPiezas);
    }

    /**
     * Metodo para abrir la ventana de una nueva pieza
     */
    @FXML
    public void ventanaNuevaPieza() {
        abrirVentana("/com/mycompany/proyectofinal/views/VentanaNuevaPieza.fxml");
    }

    /**
     * Metodo para abrir la ventana de una nueva pieza
     */
    @FXML
    public void ventanaCompraPieza() {
        abrirVentana("/com/mycompany/proyectofinal/views/VentanaComprarPiezas.fxml");
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
            stage.initModality(Modality.APPLICATION_MODAL);
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
     * Metodo para abrir la ventana para la modificación de una pienza 
     * en la que se va a cargar los datos de la pieza y se insertaran en 
     * sus respectivos campos
     */
    @FXML
    public void modificarPieza() {
        Pieza piezaSeleccionada = tabla.getSelectionModel().getSelectedItem();

        if (piezaSeleccionada == null) {
            mostrarAlerta("Error", "Debes seleccionar una pieza para modificar.", Alert.AlertType.ERROR);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/proyectofinal/views/VentanaNuevaPieza.fxml"));
            Parent root = loader.load();

            // Obtener el controlador y pasar la pieza a modificar
            VentanaNuevaPiezaController controller = loader.getController();
            controller.cargarPieza(piezaSeleccionada);

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
