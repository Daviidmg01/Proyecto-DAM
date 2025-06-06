package com.mycompany.proyectofinal.controller.ventanaempleados;

import com.mycompany.proyectofinal.controller.data.Global;
import com.mycompany.proyectofinal.model.Usuario;
import com.mycompany.proyectofinal.service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Clase controladora del alta de un empleado
 */
public class VentanaAltaEmpleadoController {

    @FXML
    private TextField textFieldNombre;
    @FXML
    private TextField textFieldEmail;
    @FXML
    private PasswordField textFieldContrasena;
    @FXML
    private ComboBox<String> comboBoxRol;
    @FXML
    private Button btnUsuario;
    @FXML
    private Label txtTitle;

    private String modo;
    private Usuario user;

    private String[] rol = {"Admin", "Mecanico"};

    /**
     * Metodo para inicializar la clase
     *
     * @param modo (String)
     * @param user (Usuario)
     */
    public void initialize(String modo, Usuario user) {
        btnUsuario.setText(Global.usuarioUser.getNombre() + "\n" + Global.usuarioUser.getEmail());
        this.user = user;
        comboBoxRol.getItems().addAll(rol);
        comboBoxRol.setValue("Mecanico");
        this.modo = modo;
        switch (modo.toLowerCase()) {
            case "crear":
                this.textFieldContrasena.setEditable(true);
                this.txtTitle.setText("CREACIÓN EMPLEADOS");
                break;
            case "editar":
                this.textFieldNombre.setText(user.getNombre());
                this.textFieldEmail.setText(user.getEmail());
                this.textFieldContrasena.setText(user.getContrasenia());
                this.textFieldContrasena.setEditable(false);
                this.comboBoxRol.setValue(user.getRol());
                this.txtTitle.setText("MODIFICACIÓN EMPLEADOS");
                break;
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

    /**
     * Metodo para insertar un usuario con los parametros del FXML
     */
    @FXML
    public void insertarUsuario(ActionEvent event) {
        Usuario user = null;
        String nombre = textFieldNombre.getText();
        String email = textFieldEmail.getText();
        String contrasenia = textFieldContrasena.getText();
        String rolSeleccionado = comboBoxRol.getValue();

        if (nombre.isBlank() || email.isBlank() || contrasenia.isBlank() || rolSeleccionado.isBlank()) {
            showAlert("Error", "Debes introducir todos los campos.");
        } else {
            user = new Usuario(nombre, email, contrasenia, rolSeleccionado);
            if (modo.equalsIgnoreCase("crear")) {
                UserService.insertUser(user);
            } else {
                user.setId(this.user.getId());
                UserService.editarUsuario(user);
            }
        }
        cerrarVentana(event);
    }

    /**
     * Metodo para cerrar la ventana actual
     *
     * @param event (ActionEvent)
     */
    private void cerrarVentana(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    /**
     * Metodo para mostrar una alerta, title -> titulo de la alerta, message ->
     * mensaje de la alerta
     *
     * @param title (String)
     * @param message (String)
     */
    private static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
