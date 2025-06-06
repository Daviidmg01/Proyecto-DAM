package com.mycompany.proyectofinal.service;

import com.mycompany.proyectofinal.constantes.Constantes;
import com.mycompany.proyectofinal.model.Coche;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Alert;

/**
 * Clase para la gestión de la clase Coche con la base de datos
 */
public class CocheService {

    /**
     * Metodo que devuelve una lista con los coches en el garaje y sus datos
     *
     * @return (ArrayList<Usuario>)
     */
    public static List<Coche> getAllCochesInGaraje() {
        List<Coche> listaCocheGaraje = new ArrayList<>();
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {

            //Conexión con la base de datos
            connection = DriverManager.getConnection(Constantes.URLCONNECTION, Constantes.USUARIO, Constantes.CONTRASENIA);

            //Consulta
            String sentencia = "SELECT * FROM coches WHERE en_garaje = true ";
            stmt = connection.createStatement();
            rs = stmt.executeQuery(sentencia);

            //Guardar los coches en la lista
            while (rs.next()) {
                int idCoche = rs.getInt("id_coche");
                String matricula = rs.getString("matricula");
                String marca = rs.getString("marca");
                String modelo = rs.getString("modelo");
                int id_cliente = rs.getInt("id_cliente");
                String imagen;
                if(!rs.getString("imagen").contains(".png") && !rs.getString("imagen").contains(".jpg")){
                    imagen = "https://cdn-icons-png.flaticon.com/512/2211/2211287.png";
                }else{
                    imagen = rs.getString("imagen");
                }
                Coche coche = new Coche(idCoche, matricula, marca, modelo, id_cliente, imagen);
                listaCocheGaraje.add(coche);
            }

            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert("Error", "Hubo un problema con la carga de datos.");
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
        return listaCocheGaraje;
    }

    /**
     * Método que devuelve la matrícula de un coche a partir de su ID
     *
     * @param idCoche ID del coche
     * @return matrícula del coche o null si no se encuentra
     */
    public static String getMatriculaById(int idCoche) {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String matricula = null;

        try {
            // Conexión con la base de datos
            connection = DriverManager.getConnection(Constantes.URLCONNECTION, Constantes.USUARIO, Constantes.CONTRASENIA);

            // Consulta con parámetro
            String sentencia = "SELECT matricula FROM coches WHERE id_coche = ?";
            pstmt = connection.prepareStatement(sentencia);
            pstmt.setInt(1, idCoche);

            rs = pstmt.executeQuery();

            // Obtener la matrícula si existe
            if (rs.next()) {
                matricula = rs.getString("matricula");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert("Error", "Hubo un problema al obtener la matrícula.");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
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

        return matricula;
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
