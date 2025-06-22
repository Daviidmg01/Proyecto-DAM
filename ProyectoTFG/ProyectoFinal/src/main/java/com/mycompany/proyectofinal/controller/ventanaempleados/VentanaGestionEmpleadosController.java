package com.mycompany.proyectofinal.controller.ventanaempleados;

import com.mycompany.proyectofinal.controller.data.Global;
import com.mycompany.proyectofinal.model.Usuario;
import com.mycompany.proyectofinal.service.UserService;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
 * Clase controladora de la gestion de los empleados
 */
public class VentanaGestionEmpleadosController {

    @FXML
    private TableView<Usuario> tabla;
    @FXML
    private TableColumn<Usuario, String> tabla_nombre;
    @FXML
    private TableColumn<Usuario, String> tabla_email;
    @FXML
    private TableColumn<Usuario, String> tabla_rol;
    @FXML
    private Button btnModificar;
    @FXML
    private Button btnUsuario;

    private ObservableList<Usuario> listaUsuarios;

    /**
     * Metodo para inicializar la tabla con los usuarios
     */
    public void initialize() {
        btnUsuario.setText(Global.usuarioUser.getNombre() + "\n" + Global.usuarioUser.getEmail());
        listaUsuarios = FXCollections.observableArrayList();
        tabla_nombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        tabla_email.setCellValueFactory(new PropertyValueFactory<>("email"));
        tabla_rol.setCellValueFactory(new PropertyValueFactory<>("rol"));
        cargarDatos();
    }

    /**
     * Metodo para cargar los datos de la BBDD a una lista
     */
    public void cargarDatos() {
        listaUsuarios.clear();
        listaUsuarios = FXCollections.observableList(UserService.getAllUsers());
        tabla.setItems(listaUsuarios);
    }

    /**
     * Metodo para crear un usuario
     */
    @FXML
    public void crearUsuario() {
        ventanaAltaEmpleado("crear", null);
    }

    /**
     * Metodo para editar un usuario
     */
    @FXML
    public void editarUsuario() {
        Usuario user = this.tabla.getSelectionModel().getSelectedItem();
        if (null != user) {
            ventanaAltaEmpleado("editar", user);
        } else {
            mostrarAlerta("Error", "Seleccione un usuario para editarlo", Alert.AlertType.ERROR);
        }
    }

    /**
     * Metodo para abrir la ventana alta de empleado
     *
     * @param modo (String)
     * @param user (Usuario)
     */
    private void ventanaAltaEmpleado(String modo, Usuario user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/proyectofinal/views/VentanaAltaEmpleado.fxml"));
            Parent root = loader.load();

            // Pasar los datos al controller
            VentanaAltaEmpleadoController controller = loader.getController();
            controller.initialize(modo, user);

            Stage stage = new Stage();
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/mycompany/proyectofinal/images/logo_taller.png")));
            stage.initModality(Modality.APPLICATION_MODAL.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            //Actualizo los datos
            cargarDatos();
        } catch (Exception ex) {
            ex.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir la ventana: ", Alert.AlertType.ERROR);
        }
    }

    /**
     * Método para eliminar a un usuario
     */
    @FXML
    public void eliminarUsuario() {
        // Obtener el usuario seleccionado
        Usuario usuarioSeleccionado = tabla.getSelectionModel().getSelectedItem();

        if (usuarioSeleccionado != null) {
            // Mostrar la alerta de confirmación
            Optional<ButtonType> resultado = mostrarAlertaConfirmacion("Confirmación", "¿Estás seguro de que deseas eliminar este usuario?");

            // Verificar si el usuario presionó "Sí" o "No"
            if (resultado.isPresent() && resultado.get().getText().equalsIgnoreCase("Sí")) {
                // Si el usuario confirma la eliminación
                UserService.deleteUser(usuarioSeleccionado);
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
