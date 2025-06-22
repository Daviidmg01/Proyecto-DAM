package com.mycompany.proyectofinal.controller.login;
import com.mycompany.proyectofinal.controller.data.Global;
import com.mycompany.proyectofinal.model.Usuario;
import com.mycompany.proyectofinal.service.UserService;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Clase controladora del Login
 */
public class LoginController {

    @FXML
    private TextField txtFieldNombre;
    @FXML
    private PasswordField txtFieldContrasenia;
    @FXML
    private Button btnEntrar;

    private Connection connection;

    private List<Usuario> listaUsuarios = new ArrayList<>();

    /**
     * Initializes the controller class.
     */
    public void initialize() {
        // TODO
    }

    
    /**
     * Metodo que carga los usuarios en una lista para comprobar el inicio de sesion
     */
    @FXML
    public void iniciarSesion() {
        //Guardamos todos los usuarios en una lista
        listaUsuarios = UserService.getAllUsers();
        comprobarExiste();
    }

    
    /**
     * Metodo que comprueba si el usuario introducido existe
     * <b>true</b> -> muestra la ventana principal (Solo admin)
     * <b>false</b> -> muestra un error, <b>existe</b> -> app movil, <b>no existe</b> -> Login incorrecto
     */
    public void comprobarExiste() {
        boolean existe = false;
        int i = 0;
        String usuario = txtFieldNombre.getText();
        String contrasenia = txtFieldContrasenia.getText();
        Usuario aux = null;
        
        //Comprobamos si el usuario existe
        while (!existe && i < listaUsuarios.size()) {
            if ((listaUsuarios.get(i).getNombre().equalsIgnoreCase(usuario)|| listaUsuarios.get(i).getEmail().equalsIgnoreCase(usuario)) && listaUsuarios.get(i).getContrasenia().equalsIgnoreCase(contrasenia)) {
                existe = true;
                aux = listaUsuarios.get(i);
            } else {
                i++;
            }
        }
        
        //En el caso de que exita comprobamos si es admin, si es admin se abre la ventana principal
        if (existe) {
            crearVentana(aux);
        } else {
            showAlert("Error", "El usuario no existe");
        }

    }

    /**
     * Metodo que comprueba si el usuario es administrador
     *  <b>true</b> -> muestra la ventana princ, <b>existe</b> -> app movil, <b>no existe</b> -> Login incorrecto
     * @param user ipal (Solo admin)
     * <b>false</b> -> muestra un error -> app movil
     * @param user 
     */
    public void crearVentana(Usuario user) {
        if (user.getRol().equalsIgnoreCase("admin")) {
            Global.usuarioUser = UserService.obtenerUsuarioNombreYContrasena(user.getNombre(), user.getContrasenia());
            showAdminWindow();
        } else{
            showAlert("Error", "Por favor, utilice la aplicación movil");
        }
    }
    
    
    /**
     * Metodo para mostrar la ventana principal
     */
    private void showAdminWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/proyectofinal/views/VentanaAdmin.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Ventana Administrador");
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/mycompany/proyectofinal/images/logo_taller.png")));
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "No se pudo abrir la ventana del Administrador.");
        }
    }
    
    
    /**
     * Metodo para mostrar una alerta, title -> titulo de la alerta, message -> mensaje de la alerta
     * @param title (String)
     * @param message (String)
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
