/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.proyectofinal.controller.ventanapiezas;

import com.mycompany.proyectofinal.controller.data.Global;
import com.mycompany.proyectofinal.model.Pieza;
import com.mycompany.proyectofinal.service.PiezaService;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 */
public class VentanaNuevaPiezaController implements Initializable {

    @FXML
    private ComboBox<String> comboBoxProveedor;
//    @FXML
//    private TextField textFieldNombre;
//    private Spinner<Integer> spinnerCantidadDisponible;
//    private Spinner<Float> spinnerPrecioCompra;
//    private Spinner<Float> spinnerPrecioVenta;
//    private Spinner<Integer> spinnerStockMinimo;

    @FXML
    private TextField textFieldNombre;
    @FXML
    private TextField textFieldCantidadDisponible;
    @FXML
    private TextField textFieldPrecioCompra;
    @FXML
    private TextField textFieldPrecioVenta;
    @FXML
    private TextField textFieldStockMinimo;
    @FXML
    private Label txtTitle;
    @FXML
    private Button btnUsuario;

    private Pieza piezaAModificar;
    private boolean esModificacion = false;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnUsuario.setText(Global.usuarioUser.getNombre() + "\n" + Global.usuarioUser.getEmail());
        textFieldCantidadDisponible.setEditable(false);
        textFieldCantidadDisponible.setText("0");
        List<String> proveedores = PiezaService.getProveedores();
        comboBoxProveedor.getItems().addAll(proveedores);
    }

    /**
     * Carga los datos de una pieza existente en los campos del formulario para
     * su modificación.
     *
     * @param pieza (Pieza)
     */
    public void cargarPieza(Pieza pieza) {
        this.piezaAModificar = pieza;
        this.esModificacion = true;
        this.txtTitle.setText("MODIFICACIÓN PIEZA");
        // Llenar los campos con los datos de la pieza
        textFieldNombre.setText(pieza.getNombrePieza());
        textFieldCantidadDisponible.setEditable(false);
        textFieldCantidadDisponible.setText(String.valueOf(pieza.getCantidadDisponible()));
        textFieldPrecioCompra.setText(String.valueOf(pieza.getPrecioCompra()));
        textFieldPrecioVenta.setText(String.valueOf(pieza.getPrecioVenta()));
        textFieldStockMinimo.setText(String.valueOf(pieza.getStockMinimo()));

        // Seleccionar el proveedor correspondiente en el ComboBox
        String nombreProveedor = obtenerNombreProveedor(pieza.getIdProveedor());
        if (nombreProveedor != null) {
            comboBoxProveedor.setValue(nombreProveedor);
        }
    }

    private String obtenerNombreProveedor(int idProveedor) {
        return PiezaService.getNombreProveedorPorId(idProveedor);
    }

    /**
     * Gestiona la inserción o modificación de piezas.
     */
    @FXML
    public void insertarPieza() {
        try {
            String nombre = textFieldNombre.getText();
            int cantidadDisponible = Integer.parseInt(textFieldCantidadDisponible.getText());
            float precioCompra = Float.parseFloat(textFieldPrecioCompra.getText());
            float precioVenta = Float.parseFloat(textFieldPrecioVenta.getText());
            int stockMinimo = Integer.parseInt(textFieldStockMinimo.getText());
            String proveedorSeleccionado = comboBoxProveedor.getValue();

            if (nombre.isBlank() || proveedorSeleccionado == null) {
                mostrarAlerta("Error", "Debes introducir todos los campos y seleccionar un proveedor.", Alert.AlertType.ERROR);
                return;
            }

            int idProveedor = PiezaService.getIdProveedorPorNombre(proveedorSeleccionado);

            if (esModificacion) {
                // Modificar pieza existente
                piezaAModificar.setNombrePieza(nombre);
                piezaAModificar.setCantidadDisponible(cantidadDisponible);
                piezaAModificar.setPrecioCompra(precioCompra);
                piezaAModificar.setPrecioVenta(precioVenta);
                piezaAModificar.setStockMinimo(stockMinimo);
                piezaAModificar.setIdProveedor(idProveedor);

                boolean exito = PiezaService.modificarPieza(piezaAModificar);

                if (exito) {
                    mostrarAlerta("Éxito", "La pieza se ha modificado correctamente.", Alert.AlertType.INFORMATION);
                } else {
                    mostrarAlerta("Error", "No se pudo modificar la pieza.", Alert.AlertType.ERROR);
                }
            } else {
                // Crear nueva pieza
                Pieza pieza = new Pieza(0, nombre, cantidadDisponible, precioCompra, precioVenta, stockMinimo, idProveedor);
                boolean exito = PiezaService.insertarPieza(pieza);

                if (exito) {
                    mostrarAlerta("Éxito", "La pieza se ha insertado correctamente.", Alert.AlertType.INFORMATION);
                } else {
                    mostrarAlerta("Error", "No se pudo insertar la pieza.", Alert.AlertType.ERROR);
                }
            }

            // Cerrar la ventana
            ((Stage) textFieldNombre.getScene().getWindow()).close();
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Los valores numéricos deben ser válidos.", Alert.AlertType.ERROR);
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
