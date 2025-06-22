/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectofinal.service;

import com.mycompany.proyectofinal.constantes.Constantes;
import com.mycompany.proyectofinal.controller.data.ContabilidadInfoAdc;
import java.sql.Connection;
import java.sql.Date;
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
public class ContabilidadService {

    /**
     * Método que devuelve una lista con todos los registros de contabilidad y
     * el nombre del usuario
     *
     * @return (ArrayList<ContabilidadInfoAdc>)
     */
    public static List<ContabilidadInfoAdc> getAllTransaccionesContabilidad() {
        List<ContabilidadInfoAdc> listaTransacciones = new ArrayList<>();
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            // Conexión con la base de datos
            connection = DriverManager.getConnection(Constantes.URLCONNECTION, Constantes.USUARIO, Constantes.CONTRASENIA);

            // Consulta con JOIN para obtener el nombre del usuario
            String sentencia = "SELECT c.*, u.nombre as nombre_usuario "
                    + "FROM contabilidad c "
                    + "LEFT JOIN usuarios u ON c.id_usuario = u.id_usuario "
                    + "ORDER BY c.fecha DESC";

            stmt = connection.createStatement();
            rs = stmt.executeQuery(sentencia);

            // Guardar las transacciones en la lista
            while (rs.next()) {
                int idTransaccion = rs.getInt("id_transaccion");
                String tipo = rs.getString("tipo");
                int cantidad = rs.getInt("cantidad");
                Date fecha = rs.getDate("fecha");
                String concepto = rs.getString("concepto");
                int idUsuario = rs.getInt("id_usuario");
                String nombreUsuario = rs.getString("nombre_usuario");

                ContabilidadInfoAdc transaccion = new ContabilidadInfoAdc(
                        nombreUsuario,
                        idTransaccion,
                        tipo,
                        cantidad, // Asumo que cantidad es int en tu clase
                        fecha,
                        concepto,
                        idUsuario
                );

                listaTransacciones.add(transaccion);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert("Error", "Hubo un problema al cargar los registros de contabilidad.");
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

        return listaTransacciones;
    }

    /**
     * Inserta una nueva transacción en la tabla contabilidad
     *
     * @param tipo Tipo de transacción ("ingreso" o "gasto")
     * @param cantidad Cantidad de dinero (double)
     * @param fecha Fecha de la transacción (java.sql.Date)
     * @param concepto Concepto de la transacción (String)
     * @param idUsuario ID del usuario (int)
     * @return true si la inserción fue exitosa, false en caso contrario
     */
    public static boolean insertarTransaccionContabilidad(String tipo, double cantidad, Date fecha, String concepto, int idUsuario) {
        Connection connection = null;
        PreparedStatement pstmt = null;

        try {
            // Conexión con la base de datos
            connection = DriverManager.getConnection(Constantes.URLCONNECTION, Constantes.USUARIO, Constantes.CONTRASENIA);

            String sentencia = "INSERT INTO contabilidad (tipo, cantidad, fecha, concepto, id_usuario) "
                    + "VALUES (?, ?, ?, ?, ?)";

            pstmt = connection.prepareStatement(sentencia);
            pstmt.setString(1, tipo);
            pstmt.setDouble(2, cantidad);
            pstmt.setDate(3, fecha);
            pstmt.setString(4, concepto);
            pstmt.setInt(5, idUsuario);

            int filasInsertadas = pstmt.executeUpdate();
            return filasInsertadas > 0;

        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert("Error", "No se pudo insertar la transacción.");
            return false;
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
    }

    /**
     * Método para buscar transacciones de contabilidad con determinados
     * parámetros
     *
     * @param nombreUsuario (String) - Nombre del usuario (opcional)
     * @param tipo (String) - Tipo de transacción ('ingreso'/'gasto') (opcional)
     * @param fecha (Date) - Fecha de la transacción (opcional)
     * @return List<ContabilidadInfoAdc> - Lista de transacciones que cumplen
     * los filtros
     */
    public static List<ContabilidadInfoAdc> buscarTransaccionesFiltradas(String nombreUsuario, String tipo, Date fecha) {
        List<ContabilidadInfoAdc> listaTransacciones = new ArrayList<>();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            connection = DriverManager.getConnection(Constantes.URLCONNECTION, Constantes.USUARIO, Constantes.CONTRASENIA);

            // Construcción dinámica de la consulta
            StringBuilder sql = new StringBuilder("SELECT c.*, u.nombre AS nombre_usuario "
                    + "FROM contabilidad c "
                    + "LEFT JOIN usuarios u ON c.id_usuario = u.id_usuario "
                    + "WHERE 1=1"); // WHERE 1=1 para facilitar añadir condiciones

            List<Object> parametros = new ArrayList<>();

            // Filtro por nombre de usuario
            if (nombreUsuario != null && !nombreUsuario.isEmpty()) {
                sql.append(" AND u.nombre LIKE ?");
                parametros.add("%" + nombreUsuario + "%");
            }

            // Filtro por tipo de transacción
            if (tipo != null && !tipo.isEmpty()) {
                sql.append(" AND c.tipo = ?");
                parametros.add(tipo);
            }

            // Filtro por fecha
            if (fecha != null) {
                sql.append(" AND DATE(c.fecha) = ?");
                parametros.add(new java.sql.Date(fecha.getTime()));
            }

            // Ordenar por fecha descendente por defecto
            sql.append(" ORDER BY c.fecha DESC");

            pstmt = connection.prepareStatement(sql.toString());

            // Asignar parámetros
            for (int i = 0; i < parametros.size(); i++) {
                pstmt.setObject(i + 1, parametros.get(i));
            }

            rs = pstmt.executeQuery();

            // Mapear resultados
            while (rs.next()) {
                ContabilidadInfoAdc transaccion = new ContabilidadInfoAdc(
                        rs.getString("nombre_usuario"),
                        rs.getInt("id_transaccion"),
                        rs.getString("tipo"),
                        rs.getBigDecimal("cantidad").intValue(), // Convertir BigDecimal a int
                        new Date(rs.getTimestamp("fecha").getTime()),
                        rs.getString("concepto"),
                        rs.getInt("id_usuario")
                );
                listaTransacciones.add(transaccion);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert("Error", "No se pudieron obtener las transacciones de contabilidad.");
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
        return listaTransacciones;
    }

    /**
     * Método para obtener el saldo actual del taller
     *
     * @return Objeto con el saldo y fecha de actualización
     */
    public static double getSaldoActual() {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        double saldo = 0;

        try {
            connection = DriverManager.getConnection(Constantes.URLCONNECTION, Constantes.USUARIO, Constantes.CONTRASENIA);

            String sql = "SELECT saldo FROM saldo_taller WHERE id = 1";
            pstmt = connection.prepareStatement(sql);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                saldo = rs.getDouble("saldo");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert("Error", "No se pudo obtener el saldo actual.");
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
        return saldo;
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
