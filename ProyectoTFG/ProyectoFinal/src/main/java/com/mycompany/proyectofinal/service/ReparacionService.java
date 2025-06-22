package com.mycompany.proyectofinal.service;

import com.mycompany.proyectofinal.constantes.Constantes;
import com.mycompany.proyectofinal.controller.data.CocheInfoAdc;
import com.mycompany.proyectofinal.controller.data.ReparacionesInfoAdc;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javafx.scene.control.Alert;

/**
 * Clase para la gestión de la clase Usuario con la base de datos
 */
public class ReparacionService {

    /**
     * Método que consulta los detalles de la reparación de un coche.
     *
     * @param matricula (String).
     * @return (DetallesReparacionData)
     */
    public static CocheInfoAdc getDatosAdicionales(String matricula) {
        CocheInfoAdc detalles = null;
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {

            //Conexión con la base de datos
            connection = DriverManager.getConnection(Constantes.URLCONNECTION, Constantes.USUARIO, Constantes.CONTRASENIA);

            //Consulta
            String sentencia = "SELECT r.motivo, r.fecha_entrada, cl.nombre AS propietario "
                    + "FROM reparaciones r "
                    + "JOIN coches c ON r.id_coche = c.id_coche "
                    + "JOIN clientes cl ON c.id_cliente = cl.id_cliente "
                    + "WHERE c.matricula = '" + matricula + "'";
            stmt = connection.createStatement();
            rs = stmt.executeQuery(sentencia);

            //Guardar datos en el objeto
            if (rs.next()) {
                String motivo = rs.getString("motivo");
                Date fechaEntrada = rs.getDate("fecha_entrada");
                String propietario = rs.getString("propietario");

                // Crear el objeto DetallesReparacion con los datos obtenidos
                detalles = new CocheInfoAdc(motivo, fechaEntrada, propietario);
            }

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

        return detalles;
    }

    /**
     * Metodo para buscar una lista de reparaciones con determinados parametros
     *
     * @param idReparacion
     * @param estado
     * @param fechaEntrada
     * @param fechaSalida
     * @param matricula
     * @param nombreMecanico
     * @return
     */
    public static List<ReparacionesInfoAdc> buscarReparacionesFiltradas(int idReparacion, String estado, Date fechaEntrada,
            Date fechaSalida, String matricula, String nombreMecanico) {
        List<ReparacionesInfoAdc> lista = new ArrayList<>();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            connection = DriverManager.getConnection(Constantes.URLCONNECTION, Constantes.USUARIO, Constantes.CONTRASENIA);

            StringBuilder sql = new StringBuilder("SELECT r.*, c.matricula, u.nombre AS nombre_mecanico "
                    + "FROM reparaciones r "
                    + "JOIN coches c ON r.id_coche = c.id_coche "
                    + "LEFT JOIN usuarios u ON r.id_mecanico = u.id_usuario WHERE 1=1");

            List<Object> parametros = new ArrayList<>();

            if (idReparacion != -1) {
                sql.append(" AND r.id_reparacion = ?");
                parametros.add(idReparacion);
            }

            if (estado != null && !estado.isEmpty()) {
                sql.append(" AND r.estado LIKE ?");
                parametros.add("%" + estado + "%");
            }

            if (fechaEntrada != null) {
                sql.append(" AND DATE(r.fecha_entrada) = ?");
                parametros.add(fechaEntrada);
            }

            if (fechaSalida != null) {
                sql.append(" AND DATE(r.fecha_salida) = ?");
                parametros.add(fechaSalida);
            }

            if (matricula != null && !matricula.isEmpty()) {
                sql.append(" AND c.matricula LIKE ?");
                parametros.add("%" + matricula + "%");
            }

            if (nombreMecanico != null && !nombreMecanico.isEmpty()) {
                sql.append(" AND u.nombre LIKE ?");
                parametros.add("%" + nombreMecanico + "%");
            }

            pstmt = connection.prepareStatement(sql.toString());

            for (int i = 0; i < parametros.size(); i++) {
                pstmt.setObject(i + 1, parametros.get(i));
            }

            rs = pstmt.executeQuery();

            while (rs.next()) {
                String nombreMec = rs.getString("nombre_mecanico");
                if (nombreMec == null) {
                    nombreMec = "No hay mecanico asignado";
                }

                int idMecanico = rs.getInt("id_mecanico");
                if (rs.wasNull()) {
                    idMecanico = 0;
                }

                ReparacionesInfoAdc rep = new ReparacionesInfoAdc(
                        rs.getString("matricula"),
                        nombreMec,
                        rs.getInt("id_reparacion"),
                        rs.getInt("id_coche"),
                        idMecanico,
                        rs.getString("motivo"),
                        rs.getInt("horas_trabajo"),
                        rs.getFloat("coste_mano_obra"),
                        rs.getFloat("coste_total_piezas"),
                        rs.getFloat("coste_total_reparacion"),
                        rs.getDate("fecha_entrada"),
                        rs.getDate("fecha_salida"),
                        rs.getString("estado")
                );
                lista.add(rep);
            }

        } catch (SQLException ex) {
            showAlert("Error", "No se pudieron obtener las reparaciones.");
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

        return lista;
    }

    /**
     * Método que busca una reparación por matrícula y fecha de entrada.
     *
     * @param matricula Matrícula del coche
     * @param fechaEntrada Fecha en la que entró al taller
     * @return Objeto ReparacionesInfoAdc si se encuentra, null si no
     */
    public static ReparacionesInfoAdc buscarReparacionPorMatriculaYFecha(String matricula, Date fechaEntrada) {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ReparacionesInfoAdc reparacion = null;

        try {
            connection = DriverManager.getConnection(Constantes.URLCONNECTION, Constantes.USUARIO, Constantes.CONTRASENIA);
            String sql = "SELECT r.*, c.matricula, u.nombre AS nombre_mecanico "
                    + "FROM reparaciones r "
                    + "JOIN coches c ON r.id_coche = c.id_coche "
                    + "JOIN usuarios u ON r.id_mecanico = u.id_usuario "
                    + "WHERE c.matricula = ? AND DATE(r.fecha_entrada) = ?";

            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, matricula);
            pstmt.setDate(2, new java.sql.Date(fechaEntrada.getTime()));

            rs = pstmt.executeQuery();

            if (rs.next()) {
                reparacion = new ReparacionesInfoAdc(
                        rs.getString("matricula"),
                        rs.getString("nombre_mecanico"),
                        rs.getInt("id_reparacion"),
                        rs.getInt("id_coche"),
                        rs.getInt("id_mecanico"),
                        rs.getString("motivo"),
                        rs.getInt("horas_trabajo"),
                        rs.getFloat("coste_mano_obra"),
                        rs.getFloat("coste_total_piezas"),
                        rs.getFloat("coste_total_reparacion"),
                        rs.getDate("fecha_entrada"),
                        rs.getDate("fecha_salida"),
                        rs.getString("estado")
                );
            }

        } catch (SQLException ex) {
            showAlert("Error", "No se pudo obtener la reparación.");
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

        return reparacion;
    }

    /**
     * Método que obtiene una lista de todas las reparaciones incluyendo
     * matrícula del coche y nombre del mecánico.
     *
     * @return Lista de ReparacionesInfoAdc con toda la información combinada
     */
    public static List<ReparacionesInfoAdc> getReparacionesConInfoAdicional() {
        List<ReparacionesInfoAdc> lista = new ArrayList<>();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            connection = DriverManager.getConnection(Constantes.URLCONNECTION, Constantes.USUARIO, Constantes.CONTRASENIA);

            String sql = "SELECT r.*, c.matricula, u.nombre AS nombre_mecanico "
                    + "FROM reparaciones r "
                    + "JOIN coches c ON r.id_coche = c.id_coche "
                    + "LEFT JOIN usuarios u ON r.id_mecanico = u.id_usuario";

            pstmt = connection.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                String nombreMecanico = rs.getString("nombre_mecanico");
                if (nombreMecanico == null) {
                    nombreMecanico = "No hay mecanico asignado";
                }

                int idMecanico = rs.getInt("id_mecanico");
                if (rs.wasNull()) {
                    idMecanico = 0;
                }

                ReparacionesInfoAdc rep = new ReparacionesInfoAdc(
                        rs.getString("matricula"),
                        nombreMecanico,
                        rs.getInt("id_reparacion"),
                        rs.getInt("id_coche"),
                        idMecanico,
                        rs.getString("motivo"),
                        rs.getInt("horas_trabajo"),
                        rs.getFloat("coste_mano_obra"),
                        rs.getFloat("coste_total_piezas"),
                        rs.getFloat("coste_total_reparacion"),
                        rs.getDate("fecha_entrada"),
                        rs.getDate("fecha_salida"),
                        rs.getString("estado")
                );
                lista.add(rep);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert("Error", "No se pudieron obtener las reparaciones.");
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

        return lista;
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
