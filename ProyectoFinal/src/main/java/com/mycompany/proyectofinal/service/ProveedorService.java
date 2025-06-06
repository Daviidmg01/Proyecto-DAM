/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectofinal.service;

import com.mycompany.proyectofinal.constantes.Constantes;
import com.mycompany.proyectofinal.model.Proveedor;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Alert;

/**
 *
 * @author dav85
 */
public class ProveedorService {

    /**
     * Metodo que devuelve una lista con todos los proveedores
     *
     * @return (ArrayList<Proveedor>)
     */
    public static List<Proveedor> getAllProveedores() {
        List<Proveedor> listaProveedores = new ArrayList<>();
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            // Conexión con la base de datos
            connection = DriverManager.getConnection(Constantes.URLCONNECTION, Constantes.USUARIO, Constantes.CONTRASENIA);

            // Consulta
            String query = "SELECT id_proveedor, nombre, telefono, email FROM proveedores";
            stmt = connection.createStatement();
            rs = stmt.executeQuery(query);

            // Guardar los proveedores en la lista
            while (rs.next()) {
                int idProveedor = rs.getInt("id_proveedor");
                String nombre = rs.getString("nombre");
                int telefono = rs.getInt("telefono");
                String email = rs.getString("email");

                // Creación de un nuevo proveedor con el ID
                Proveedor proveedor = new Proveedor(idProveedor, nombre, telefono, email);
                listaProveedores.add(proveedor);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Hubo un problema al cargar los proveedores.", Alert.AlertType.ERROR);
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

        return listaProveedores;
    }

    /**
     * Método que elimina un proveedor por su ID
     *
     * @param idProveedor ID del proveedor a eliminar
     * @return true si se eliminó correctamente, false si no se pudo eliminar
     */
    public static boolean deleteProveedor(int idProveedor) {
        Connection connection = null;
        PreparedStatement pstmt = null;
        boolean eliminado = false;

        try {
            // Conexión con la base de datos
            connection = DriverManager.getConnection(Constantes.URLCONNECTION, Constantes.USUARIO, Constantes.CONTRASENIA);
            connection.setAutoCommit(false); // Iniciar transacción

            // 1. Primero verificamos si el proveedor tiene piezas asociadas
            String checkQuery = "SELECT COUNT(*) FROM piezas WHERE id_proveedor = ?";
            pstmt = connection.prepareStatement(checkQuery);
            pstmt.setInt(1, idProveedor);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                showAlert("Error", "No se puede eliminar el proveedor porque tiene piezas asociadas.", Alert.AlertType.ERROR);
                return false;
            }

            // 2. Consulta para eliminar
            String deleteQuery = "DELETE FROM proveedores WHERE id_proveedor = ?";
            pstmt = connection.prepareStatement(deleteQuery);
            pstmt.setInt(1, idProveedor);

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas > 0) {
                connection.commit(); // Confirmar transacción
                eliminado = true;
                showAlert("Éxito", "Proveedor eliminado correctamente.", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Advertencia", "No se encontró ningún proveedor con ese ID.", Alert.AlertType.WARNING);
            }

        } catch (SQLIntegrityConstraintViolationException e) {
            try {
                if (connection != null) {
                    connection.rollback(); // Revertir en caso de error
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            showAlert("Error", "No se puede eliminar el proveedor porque tiene relaciones con otras tablas.", Alert.AlertType.ERROR);
        } catch (Exception e) {
            try {
                if (connection != null) {
                    connection.rollback(); // Revertir en caso de error
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            showAlert("Error", "Hubo un problema al eliminar el proveedor.", Alert.AlertType.ERROR);
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return eliminado;
    }

    /**
     * Metodo que inserta un proveedor
     *
     * @param proveedor (Proveedor)
     */
    public static void insertProveedor(Proveedor proveedor) {
        PreparedStatement sentencia = null;
        Connection connection = null;
        try {
            //Conexión con la base de datos
            connection = DriverManager.getConnection(Constantes.URLCONNECTION, Constantes.USUARIO, Constantes.CONTRASENIA);

            //Consulta
            String sql = "INSERT INTO proveedores (nombre, telefono, email) VALUES (?, ?, ?)";
            sentencia = connection.prepareStatement(sql);

            sentencia.setString(1, proveedor.getNombre());
            sentencia.setInt(2, proveedor.getTelefono());
            sentencia.setString(3, proveedor.getEmail());

            //Ejecucion
            int filasInsertadas = sentencia.executeUpdate();
            if (filasInsertadas > 0) {
                showAlert("Proveedor Creado", "El proveedor se ha creado correctamente.", Alert.AlertType.INFORMATION);
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
     * Metodo que modifica un proveedor
     *
     * @param proveedor (Proveedor)
     */
    public static void modificarProveedor(Proveedor proveedor) {
        PreparedStatement sentencia = null;
        Connection connection = null;
        try {
            // Conexión con la base de datos
            connection = DriverManager.getConnection(Constantes.URLCONNECTION, Constantes.USUARIO, Constantes.CONTRASENIA);

            // Consulta UPDATE
            String sql = "UPDATE proveedores SET nombre = ?, telefono = ?, email = ? WHERE id_proveedor = ?";
            sentencia = connection.prepareStatement(sql);

            sentencia.setString(1, proveedor.getNombre());
            sentencia.setInt(2, proveedor.getTelefono());
            sentencia.setString(3, proveedor.getEmail());
            sentencia.setInt(4, proveedor.getIdProveedor());

            int filasActualizadas = sentencia.executeUpdate();
            if (filasActualizadas > 0) {
                showAlert("Proveedor Modificado", "El proveedor se ha modificado correctamente.", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Proveedor No Encontrado", "No se encontró un proveedor con ese ID.", Alert.AlertType.WARNING);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert("Error", "Hubo un problema al modificar el proveedor.", Alert.AlertType.ERROR);
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

    private static void showAlert(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setHeaderText(null);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
