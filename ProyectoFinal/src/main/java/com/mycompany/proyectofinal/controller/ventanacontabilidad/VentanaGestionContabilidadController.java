package com.mycompany.proyectofinal.controller.ventanacontabilidad;

import com.mycompany.proyectofinal.controller.data.ContabilidadInfoAdc;
import com.mycompany.proyectofinal.controller.data.Global;
import com.mycompany.proyectofinal.service.ContabilidadService;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Usuario
 */
public class VentanaGestionContabilidadController{

    @FXML
    private TableView<ContabilidadInfoAdc> tableContabilidad;
    @FXML
    private TableColumn<ContabilidadInfoAdc, String> columnTipo;
    @FXML
    private TableColumn<ContabilidadInfoAdc, Integer> columnCantidad;
    @FXML
    private TableColumn<ContabilidadInfoAdc, Date> columnFecha;
    @FXML
    private TableColumn<ContabilidadInfoAdc, String> columnConcepto;
    @FXML
    private TableColumn<ContabilidadInfoAdc, String> columnNombreUsuario;
    @FXML
    private Button btnUsuario;
    @FXML
    private ComboBox<String> comboBoxTipo;
    @FXML
    private DatePicker dtPickerFecha;
    @FXML
    private TextField txtFieldNombre;
    @FXML
    private Label txtDineroActual;
    
    private final String[] estadoComboBox = {"", "ingreso", "gasto"};
    private ObservableList<ContabilidadInfoAdc> listaContabilidad;

    /**
     * Initializes the controller class.
     */
    public void initialize() {
        btnUsuario.setText(Global.usuarioUser.getNombre() + "\n" + Global.usuarioUser.getEmail());
        dtPickerFecha.getEditor().textProperty().addListener((obs, oldText, newText) -> {
            if (newText == null || newText.trim().isEmpty()) {
                dtPickerFecha.setValue(null);
            }
        });
        comboBoxTipo.getItems().addAll(estadoComboBox);
        comboBoxTipo.setValue("");
        columnTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        columnCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        columnFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        columnConcepto.setCellValueFactory(new PropertyValueFactory<>("concepto"));
        columnNombreUsuario.setCellValueFactory(new PropertyValueFactory<>("nombreUsuario"));
        this.txtDineroActual.setText(String.valueOf(ContabilidadService.getSaldoActual()) + " €");
        cargarDatos();
    }    

    /**
     * Metodo para cargar los datos de la contabilidad de la base de datos
     */
    public void cargarDatos() {
        listaContabilidad = FXCollections.observableList(ContabilidadService.getAllTransaccionesContabilidad());
        tableContabilidad.setItems(listaContabilidad);
    }
    
    /**
     * Metodo para buscar datos de la contabilidad
     */
    @FXML
    public void buscarDatosContabilidad(){
        String nombreUsuario = this.txtFieldNombre.getText();
        String tipo = this.comboBoxTipo.getValue();
        LocalDate fecLocEntrada = dtPickerFecha.getValue();
        Date fecha = formatoFecha(fecLocEntrada);
        listaContabilidad = FXCollections.observableList(ContabilidadService.buscarTransaccionesFiltradas(nombreUsuario, tipo, fecha));
        tableContabilidad.setItems(listaContabilidad);
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
