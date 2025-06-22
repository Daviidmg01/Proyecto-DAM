package com.mycompany.proyectofinal.service;

import com.mycompany.proyectofinal.constantes.Constantes;
import com.mycompany.proyectofinal.controller.ventanaempleados.VentanaGestionEmpleadosController;
import com.mycompany.proyectofinal.model.Usuario;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Alert;

/**
 * Clase para la gestión de la clase Usuario con la base de datos
 */
public class UserService {

    /**
     * Metodo que devuelve una lista con todos los usuarios y sus datos
     *
     * @return (ArrayList<Usuario>)
     */
    public static List<Usuario> getAllUsers() {
        List<Usuario> listaUsuarios = new ArrayList<>();
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            //Conexión con la base de datos
            connection = DriverManager.getConnection(Constantes.URLCONNECTION, Constantes.USUARIO, Constantes.CONTRASENIA);

            //Consulta
            String query = "SELECT * FROM usuarios";
            stmt = connection.createStatement();
            rs = stmt.executeQuery(query);

            //Guardar los usuarios en la lista
            while (rs.next()) {
                int idUsuario = rs.getInt(1);
                String nombreUsuario = rs.getString(2);
                String email = rs.getString(3);
                String password = rs.getString(4);
                String rol = rs.getString(5);

                //Creacion de nuevo usuario
                Usuario user = new Usuario(idUsuario, nombreUsuario, email, password, rol);
                listaUsuarios.add(user);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Hubo un problema al iniciar sesión.", Alert.AlertType.ERROR);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return listaUsuarios;
    }

    /**
     * Método que edita un usuario en la base de datos.
     *
     * @param user
     */
    public static void editarUsuario(Usuario user) {
        PreparedStatement sentencia = null;
        Connection connection = null;

        try {
            // Conexión con la base de datos
            connection = DriverManager.getConnection(Constantes.URLCONNECTION, Constantes.USUARIO, Constantes.CONTRASENIA);

            // Consulta UPDATE
            String sql = "UPDATE usuarios SET nombre = ?, email = ?, contraseña = ?, rol = ? WHERE id_usuario = ?";
            sentencia = connection.prepareStatement(sql);

            sentencia.setString(1, user.getNombre());
            sentencia.setString(2, user.getEmail());
            sentencia.setString(3, user.getContrasenia());
            sentencia.setString(4, user.getRol());
            sentencia.setInt(5, user.getId()); 

            // Ejecución
            int filasActualizadas = sentencia.executeUpdate();
            if (filasActualizadas > 0) {
                showAlert("Usuario Actualizado", "El usuario se ha actualizado correctamente.", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Usuario no encontrado", "No se encontró un usuario con ese ID.", Alert.AlertType.WARNING);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert("Error", "Hubo un problema al actualizar los datos.", Alert.AlertType.ERROR);
        } finally {
            try {
                if (sentencia != null) {
                    sentencia.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Método que obtiene un usuario a partir de su nombre y contraseña.
     *
     * @param nombre Nombre del usuario
     * @param contrasena Contraseña del usuario
     * @return usuario
     */
    public static Usuario obtenerUsuarioNombreYContrasena(String nombre, String contrasena) {
        PreparedStatement sentencia = null;
        Connection connection = null;
        ResultSet resultado = null;
        Usuario user = null;
        try {
            // Conexión con la base de datos
            connection = DriverManager.getConnection(Constantes.URLCONNECTION, Constantes.USUARIO, Constantes.CONTRASENIA);

            // Consulta SELECT
            String sql = "SELECT * FROM usuarios WHERE nombre = ? AND contraseña = ?";
            sentencia = connection.prepareStatement(sql);
            sentencia.setString(1, nombre);
            sentencia.setString(2, contrasena);

            // Ejecución
            resultado = sentencia.executeQuery();

            if (resultado.next()) {
                int idUsuario = resultado.getInt(1);
                String nombreUsuario = resultado.getString(2);
                String email = resultado.getString(3);
                String password = resultado.getString(4);
                String rol = resultado.getString(5);

                //Creacion de nuevo usuario
                user = new Usuario(idUsuario, nombreUsuario, email, password, rol);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert("Error", "Hubo un problema al obtener el ID del usuario.", Alert.AlertType.ERROR);
        } finally {
            try {
                if (resultado != null) {
                    resultado.close();
                }
                if (sentencia != null) {
                    sentencia.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return user;
    }

    /**
     * Metodo que inserta un usuario
     *
     * @param user (Usuario)
     */
    public static void insertUser(Usuario user) {
        PreparedStatement sentencia = null;
        Connection connection = null;
        try {
            //Conexión con la base de datos
            connection = DriverManager.getConnection(Constantes.URLCONNECTION, Constantes.USUARIO, Constantes.CONTRASENIA);

            //Consulta
            String sql = "INSERT INTO usuarios (nombre, email, contraseña, rol) VALUES (?, ?, ?, ?)";
            sentencia = connection.prepareStatement(sql);

            sentencia.setString(1, user.getNombre());
            sentencia.setString(2, user.getEmail());
            sentencia.setString(3, user.getContrasenia());
            sentencia.setString(4, user.getRol());

            //Ejecucion
            int filasInsertadas = sentencia.executeUpdate();
            if (filasInsertadas > 0) {
                showAlert("Usuario Creado", "El usuario se ha creado correctamente.", Alert.AlertType.INFORMATION);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert("Error", "Hubo un problema con la carga de datos.", Alert.AlertType.ERROR);
        } finally {
            try {
                if (sentencia != null) {
                    sentencia.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Metodo para eliminar un usuario
     *
     * @param user (Usuario)
     */
    public static void deleteUser(Usuario user) {
        Connection connection = null;
        PreparedStatement sentencia = null;
        try {
            //Conexión con la base de datos
            connection = DriverManager.getConnection(Constantes.URLCONNECTION, Constantes.USUARIO, Constantes.CONTRASENIA);

            //Consulta
            String sql = "DELETE FROM usuarios WHERE id_usuario = ?";
            sentencia = connection.prepareStatement(sql);
            sentencia.setInt(1, user.getId());

            //Ejecucion
            int filasAfectadas = sentencia.executeUpdate();
            if (filasAfectadas > 0) {
                showAlert("Usuario Borrado", "Se ha eliminado corractamente el usuario", Alert.AlertType.INFORMATION);
            }

        } catch (SQLException ex) {
            Logger.getLogger(VentanaGestionEmpleadosController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (sentencia != null) {
                    sentencia.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
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
    private static void showAlert(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setHeaderText(null);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

}
