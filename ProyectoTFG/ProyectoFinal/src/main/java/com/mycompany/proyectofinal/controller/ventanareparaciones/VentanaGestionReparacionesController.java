package com.mycompany.proyectofinal.controller.ventanareparaciones;

import com.mycompany.proyectofinal.controller.data.Global;
import com.mycompany.proyectofinal.controller.data.ReparacionesInfoAdc;
import com.mycompany.proyectofinal.service.ReparacionService;
import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
 * Clase controladora de la gestion de reparaciones
 */
public class VentanaGestionReparacionesController {

    @FXML
    private TableView<ReparacionesInfoAdc> tabla;
    @FXML
    private TableColumn<ReparacionesInfoAdc, String> tabla_matricula;
    @FXML
    private TableColumn<ReparacionesInfoAdc, String> tabla_mecanico;
    @FXML
    private TableColumn<ReparacionesInfoAdc, String> tabla_motivo;
    @FXML
    private TableColumn<ReparacionesInfoAdc, String> tabla_estado;
    @FXML
    private Button btnInfo;
    @FXML
    private Button btnBuscador;

    private ObservableList<ReparacionesInfoAdc> listaReparacion;
    @FXML
    private Button btnUsuario;

    /**
     * Metodo para inicializar la tabla
     */
    public void initialize() {
        btnUsuario.setText(Global.usuarioUser.getNombre() + "\n" + Global.usuarioUser.getEmail());
        listaReparacion = FXCollections.observableArrayList(ReparacionService.getReparacionesConInfoAdicional());
        tabla_matricula.setCellValueFactory(new PropertyValueFactory<>("matricula"));
        tabla_mecanico.setCellValueFactory(new PropertyValueFactory<>("nombreMecanico"));
        tabla_motivo.setCellValueFactory(new PropertyValueFactory<>("motivo"));
        tabla_estado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        this.tabla.setItems(listaReparacion);
    }

    /**
     * Metodo para mostrar un buscador de reparaciones
     */
    @FXML
    private void mostrarBuscador() {
        // Cargar la nueva ventana
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/proyectofinal/views/VentanaBuscadorReparaciones.fxml"));
            Parent root;
            root = loader.load();

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

    /**
     * Metodo para mostarar los datos adicionales de una reparacion
     */
    @FXML
    private void mostrarDetallesReparacion() {
        ReparacionesInfoAdc reparacion = this.tabla.getSelectionModel().getSelectedItem();

        if (null != reparacion) {

            // Cargar la nueva ventana
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/proyectofinal/views/VentanaDetalleReparacion.fxml"));
                Parent root;
                root = loader.load();

                // Pasar los datos al controller
                VentanaDetalleReparacionController controller = loader.getController();
                controller.initialize(reparacion);

                // Mostrar la ventana
                Stage stage = new Stage();
                stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/mycompany/proyectofinal/images/logo_taller.png")));
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException ex) {
                ex.printStackTrace();
                mostrarAlerta("Error", "No se pudo abrir la ventana.", Alert.AlertType.ERROR);
            }
        } else {
            mostrarAlerta("Error", "Seleccione una reparación.", Alert.AlertType.ERROR);
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
