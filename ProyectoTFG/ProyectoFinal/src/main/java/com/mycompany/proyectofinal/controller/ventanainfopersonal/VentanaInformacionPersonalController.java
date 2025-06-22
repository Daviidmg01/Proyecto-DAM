package com.mycompany.proyectofinal.controller.ventanainfopersonal;

import com.mycompany.proyectofinal.controller.data.Global;
import com.mycompany.proyectofinal.model.Usuario;
import com.mycompany.proyectofinal.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 */
public class VentanaInformacionPersonalController {

    @FXML
    private TextField txtField_nombre;
    @FXML
    private TextField txtField_email;
    @FXML
    private ComboBox<String> comboBox_rol;
    @FXML
    private PasswordField txtField_contrasenia;
    @FXML
    private Button btnEditar;

    private String[] rol = {"Admin", "Mecanico"};

    private Usuario userPrincipal = Global.usuarioUser;

    private boolean editar = false;

    /**
     * Initializes the controller class.
     */
    public void initialize() {
        comboBox_rol.getItems().addAll(rol);
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        this.txtField_nombre.setText(userPrincipal.getNombre());
        this.txtField_email.setText(userPrincipal.getEmail());
        this.txtField_contrasenia.setText(userPrincipal.getContrasenia());
        this.comboBox_rol.setValue(userPrincipal.getRol());
        this.btnEditar.setText("Editar");
        this.txtField_nombre.setEditable(false);
        this.txtField_email.setEditable(false);
        this.txtField_contrasenia.setEditable(false);
        this.comboBox_rol.setDisable(true);
    }

    private void inicializarEdicion() {
        this.txtField_nombre.setText(userPrincipal.getNombre());
        this.txtField_email.setText(userPrincipal.getEmail());
        this.txtField_contrasenia.setText(userPrincipal.getContrasenia());
        this.comboBox_rol.setValue(userPrincipal.getRol());
        this.btnEditar.setText("Guardar");
        this.txtField_nombre.setEditable(true);
        this.txtField_email.setEditable(true);
        this.txtField_contrasenia.setEditable(true);
        this.comboBox_rol.setDisable(false);
    }

    private Usuario obtenerUsuarioEditado() {
        Usuario editUser = userPrincipal;
        String nombre = this.txtField_nombre.getText();
        String email = this.txtField_email.getText();
        String contrasenia = this.txtField_contrasenia.getText();
        String rol = this.comboBox_rol.getValue();
        if (nombre != null && !nombre.isEmpty() || email != null && !email.isEmpty() ||contrasenia != null && !contrasenia.isEmpty() || rol != null && !rol.isEmpty()) {
            editUser.setNombre(nombre);
            editUser.setEmail(email);
            editUser.setContrasenia(contrasenia);
            editUser.setRol(rol);
        } else {
            mostrarAlerta("ERROR", "Todos los campos tienen que estar completos", Alert.AlertType.ERROR);
            editUser = null;
        }
        return editUser;
    }

    @FXML
    private void editarUsuario() {
        if (!editar) {
            inicializarEdicion();
            editar = true;
        } else {
            if (obtenerUsuarioEditado() != null) {
                UserService.editarUsuario(obtenerUsuarioEditado());
                Global.usuarioUser = obtenerUsuarioEditado();
            }
            inicializarComponentes();
            editar = false;
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
