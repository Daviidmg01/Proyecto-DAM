package com.mycompany.proyectofinal.controller.ventanareparaciones;

import com.mycompany.proyectofinal.controller.data.Global;
import com.mycompany.proyectofinal.controller.data.ReparacionesInfoAdc;
import com.mycompany.proyectofinal.service.ReparacionService;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 */
public class VentanaBuscadorReparacionesController {

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
    private TextField txtField_idReparacion;
    @FXML
    private TextField txtField_matricula;
    @FXML
    private TextField txtField_mecanico;
    @FXML
    private DatePicker dPicker_fecEntrada;
    @FXML
    private DatePicker dPicker_fecSalida;
    @FXML
    private Button buscarReparacion;
    @FXML
    private ComboBox<String> comboBoxEstado;
    @FXML
    private Button btnUsuario;

    private ObservableList<ReparacionesInfoAdc> listaReparacion;
    private String[] estadoComboBox = {"", "Pendiente", "En proceso", "Finalizada"};

    /**
     * Initializes the controller class.
     */
    public void initialize() {
        btnUsuario.setText(Global.usuarioUser.getNombre() + "\n" + Global.usuarioUser.getEmail());
        dPicker_fecEntrada.getEditor().textProperty().addListener((obs, oldText, newText) -> {
            if (newText == null || newText.trim().isEmpty()) {
                dPicker_fecEntrada.setValue(null);
            }
        });
        dPicker_fecSalida.getEditor().textProperty().addListener((obs, oldText, newText) -> {
            if (newText == null || newText.trim().isEmpty()) {
                dPicker_fecSalida.setValue(null);
            }
        });
        comboBoxEstado.getItems().addAll(estadoComboBox);
        comboBoxEstado.setValue("");
        listaReparacion = FXCollections.observableArrayList(ReparacionService.getReparacionesConInfoAdicional());
        tabla_matricula.setCellValueFactory(new PropertyValueFactory<>("matricula"));
        tabla_mecanico.setCellValueFactory(new PropertyValueFactory<>("nombreMecanico"));
        tabla_motivo.setCellValueFactory(new PropertyValueFactory<>("motivo"));
        tabla_estado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        this.tabla.setItems(listaReparacion);
    }

    /**
     * Metodo para buscar las reparaciones con las especificaciones indicadas en
     * los textFields
     */
    @FXML
    private void buscarReparacion() {
        int idReparacion = formatoIdReparacion();
        String estado = this.comboBoxEstado.getValue();
        LocalDate fecLocEntrada = dPicker_fecEntrada.getValue();
        Date fechaEntrada = formatoFecha(fecLocEntrada);
        LocalDate fecLocSalida = dPicker_fecSalida.getValue();
        Date fechaSalida = formatoFecha(fecLocSalida);
        String matricula = this.txtField_matricula.getText();
        String nombreMecanico = this.txtField_mecanico.getText();
        listaReparacion = FXCollections.observableArrayList(
                ReparacionService.buscarReparacionesFiltradas(idReparacion, estado, fechaEntrada, fechaSalida, matricula, nombreMecanico));
        if (listaReparacion.isEmpty()) {
            mostrarAlerta("Info", "No se ha encontrado ninguna reparación con estas especificaciones", Alert.AlertType.ERROR);
        }
        this.tabla.setItems(listaReparacion);
    }

    /**
     * Metodo para mostrar los detalles de una reparacion
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
     * Metodo para dar formato al idReparacion o avisar al usuario del que el
     * formato es incorrecto
     *
     * @return (Int)
     */
    private int formatoIdReparacion() {
        int idReparacion = -1;
        try {
            idReparacion = Integer.parseInt(this.txtField_idReparacion.getText());
        } catch (NumberFormatException ex) {
            if (this.txtField_idReparacion.getText() != null && !this.txtField_idReparacion.getText().isEmpty()) {
                mostrarAlerta("ERROR", "Debes introducir un numero", Alert.AlertType.ERROR);
            }
            idReparacion = -1;
        }
        return idReparacion;
    }

    /**
     * Metodo para transformar un LocalDate a sql Date
     *
     * @param localDate
     * @return (Date)
     */
    private Date formatoFecha(LocalDate localDate) {
        Date fecha = null;
        if (localDate != null) {
            fecha = Date.valueOf(localDate);
        }
        return fecha;
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
