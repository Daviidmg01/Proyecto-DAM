/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectofinal.service;

import com.mycompany.proyectofinal.constantes.Constantes;
import com.mycompany.proyectofinal.controller.data.PiezaTableModel;
import com.mycompany.proyectofinal.model.Pieza;
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
 *
 * @author Usuario
 */
public class PiezaService {

    /**
     * Método que devuelve una lista con los nombres de las piezas cuyo stock
     * está por debajo del mínimo permitido.
     *
     * @return Lista de nombres de piezas con stock bajo
     */
    public static List<String> getPiezasConStockBajo() {
        List<String> piezasBajoStock = new ArrayList<>();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            connection = DriverManager.getConnection(Constantes.URLCONNECTION, Constantes.USUARIO, Constantes.CONTRASENIA);

            String sentencia = "SELECT nombre_pieza FROM piezas WHERE cantidad_disponible < stock_minimo";
            pstmt = connection.prepareStatement(sentencia);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                String nombre = rs.getString("nombre_pieza");
                piezasBajoStock.add(nombre);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
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

        return piezasBajoStock;
    }

    /**
     * Metodo que devuelve una lista con todas las piezas
     *
     * @return (ArrayList<Pieza>)
     */
    public static List<Pieza> getAllPiezas() {
        List<Pieza> listaPiezas = new ArrayList<>();
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            //Conexión con la base de datos
            connection = DriverManager.getConnection(Constantes.URLCONNECTION, Constantes.USUARIO, Constantes.CONTRASENIA);

            //Consulta
            String query = "SELECT * FROM piezas";
            stmt = connection.createStatement();
            rs = stmt.executeQuery(query);

            //Guardar las piezas en la lista
            while (rs.next()) {
                int idPieza = rs.getInt(1);
                String nombrePieza = rs.getString(2);
                int cantidadDisponible = rs.getInt(3);
                float precioCompra = rs.getFloat(4);
                float precioVenta = rs.getFloat(5);
                int stockMinimo = rs.getInt(6);
                int idProveedor = rs.getInt(7);

                //Creacion de nueva Pieza
                Pieza pieza = new Pieza(idPieza, nombrePieza, cantidadDisponible, precioCompra, precioVenta, stockMinimo, idProveedor);
                listaPiezas.add(pieza);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Hubo un problema al cargar las piezas.", Alert.AlertType.ERROR);
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

        return listaPiezas;
    }

    /**
     * Metodo que devuelve una lista con todas las piezas
     *
     * @return (ArrayList<Pieza>)
     */
    public static List<PiezaTableModel> getAllPiezasTableModel() {
        List<PiezaTableModel> listaPiezas = new ArrayList<>();
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            //Conexión con la base de datos
            connection = DriverManager.getConnection(Constantes.URLCONNECTION, Constantes.USUARIO, Constantes.CONTRASENIA);

            //Consulta
            String query = "SELECT id_pieza, nombre_pieza, cantidad_disponible, stock_minimo, precio_compra FROM piezas";
            stmt = connection.createStatement();
            rs = stmt.executeQuery(query);

            //Guardar las piezas en la lista
            while (rs.next()) {
                int idPieza = rs.getInt(1);
                String nombrePieza = rs.getString(2);
                int cantidadDisponible = rs.getInt(3);
                int stockMinimo = rs.getInt(4);
                float precio = rs.getFloat(5);

                //Creacion de nueva Pieza
                PiezaTableModel pieza = new PiezaTableModel(idPieza, nombrePieza, cantidadDisponible, stockMinimo, precio);
                listaPiezas.add(pieza);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Hubo un problema al cargar las piezas.", Alert.AlertType.ERROR);
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

        return listaPiezas;
    }

    /**
     * Metodo que inserta una pieza
     *
     * @param pieza (Pieza)
     */
    public static boolean insertarPieza(Pieza pieza) {
        Connection connection = null;
        PreparedStatement pstmt = null;
        boolean exito = false;

        try {
            connection = DriverManager.getConnection(Constantes.URLCONNECTION, Constantes.USUARIO, Constantes.CONTRASENIA);

            String sql = "INSERT INTO piezas (nombre_pieza, cantidad_disponible, precio_compra, precio_venta, stock_minimo, id_proveedor) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";

            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, pieza.getNombrePieza());
            pstmt.setInt(2, pieza.getCantidadDisponible());
            pstmt.setFloat(3, pieza.getPrecioCompra());
            pstmt.setFloat(4, pieza.getPrecioVenta());
            pstmt.setInt(5, pieza.getStockMinimo());
            pstmt.setInt(6, pieza.getIdProveedor());

            int filasAfectadas = pstmt.executeUpdate();
            exito = filasAfectadas > 0;

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return exito;
    }

    /**
     * Metodo que modifica un proveedor
     *
     * @param nombreProveedor (String)
     */
    public static int getIdProveedorPorNombre(String nombreProveedor) {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            connection = DriverManager.getConnection(Constantes.URLCONNECTION, Constantes.USUARIO, Constantes.CONTRASENIA);

            String sql = "SELECT id_proveedor FROM proveedores WHERE nombre = ?";
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, nombreProveedor);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id_proveedor");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
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
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return -1; // Si no lo encuentra
    }

    /**
     * Metodo que devuelve una lista con todos los proveedores
     *
     * @return (ArrayList<String>)
     */
    public static List<String> getProveedores() {
        List<String> proveedores = new ArrayList<>();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            connection = DriverManager.getConnection(Constantes.URLCONNECTION, Constantes.USUARIO, Constantes.CONTRASENIA);

            String sentencia = "SELECT nombre FROM proveedores";
            pstmt = connection.prepareStatement(sentencia);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                String nombre = rs.getString("nombre");
                proveedores.add(nombre);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
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

        return proveedores;
    }

    /**
     * Metodo que modifica un proveedor
     *
     * @param pieza (Pieza)
     */
    public static boolean modificarPieza(Pieza pieza) {
        Connection connection = null;
        PreparedStatement pstmt = null;
        boolean exito = false;

        try {
            connection = DriverManager.getConnection(Constantes.URLCONNECTION, Constantes.USUARIO, Constantes.CONTRASENIA);

            String sql = "UPDATE piezas SET nombre_pieza = ?, cantidad_disponible = ?, "
                    + "precio_compra = ?, precio_venta = ?, stock_minimo = ?, id_proveedor = ? "
                    + "WHERE id_pieza = ?";

            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, pieza.getNombrePieza());
            pstmt.setInt(2, pieza.getCantidadDisponible());
            pstmt.setFloat(3, pieza.getPrecioCompra());
            pstmt.setFloat(4, pieza.getPrecioVenta());
            pstmt.setInt(5, pieza.getStockMinimo());
            pstmt.setInt(6, pieza.getIdProveedor());
            pstmt.setInt(7, pieza.getIdPieza());

            int filasAfectadas = pstmt.executeUpdate();
            exito = filasAfectadas > 0;

        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert("Error", "Error al modificar la pieza", Alert.AlertType.ERROR);
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return exito;
    }

    /**
     * Metodo que modifica un proveedor
     *
     * @param idProveedor (Integer)
     */
    public static String getNombreProveedorPorId(int idProveedor) {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String nombreProveedor = null;

        try {
            connection = DriverManager.getConnection(Constantes.URLCONNECTION, Constantes.USUARIO, Constantes.CONTRASENIA);

            String sql = "SELECT nombre FROM proveedores WHERE id_proveedor = ?";
            pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, idProveedor);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                nombreProveedor = rs.getString("nombre");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
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
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return nombreProveedor;
    }

    public static List<PiezaTableModel> buscarPiezasFiltradas(boolean soloPorDebajoStockMinimo, String nombreProducto, String nombreProveedor) {
        List<PiezaTableModel> lista = new ArrayList<>();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            connection = DriverManager.getConnection(Constantes.URLCONNECTION, Constantes.USUARIO, Constantes.CONTRASENIA);

            StringBuilder sql = new StringBuilder("SELECT p.*, pr.nombre AS nombre_proveedor FROM piezas p "
                    + "JOIN proveedores pr ON p.id_proveedor = pr.id_proveedor WHERE 1=1");

            List<Object> parametros = new ArrayList<>();

            if (soloPorDebajoStockMinimo) {
                sql.append(" AND p.cantidad_disponible < p.stock_minimo");
            }

            if (nombreProducto != null && !nombreProducto.isBlank()) {
                sql.append(" AND p.nombre_pieza LIKE ?");
                parametros.add("%" + nombreProducto + "%");
            }

            if (nombreProveedor != null && !nombreProveedor.isBlank()) {
                sql.append(" AND pr.nombre LIKE ?");
                parametros.add("%" + nombreProveedor + "%");
            }

            pstmt = connection.prepareStatement(sql.toString());

            for (int i = 0; i < parametros.size(); i++) {
                pstmt.setObject(i + 1, parametros.get(i));
            }

            rs = pstmt.executeQuery();

            while (rs.next()) {
                Pieza pieza = new Pieza(
                        rs.getInt("id_pieza"),
                        rs.getString("nombre_pieza"),
                        rs.getInt("cantidad_disponible"),
                        rs.getFloat("precio_compra"),
                        rs.getFloat("precio_venta"),
                        rs.getInt("stock_minimo"),
                        rs.getInt("id_proveedor"));
                PiezaTableModel piezaTableModel = new PiezaTableModel(pieza.getIdPieza(), pieza.getNombrePieza(), pieza.getCantidadDisponible(), pieza.getStockMinimo(), pieza.getPrecioCompra());
                lista.add(piezaTableModel);
            }

        } catch (SQLException ex) {
            ex.printStackTrace(); // o tu método de alerta
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

    public static boolean actualizarStockPieza(int idPieza, int cantidad) {
        Connection connection = null;
        PreparedStatement pstmt = null;
        boolean exito = false;

        try {
            connection = DriverManager.getConnection(Constantes.URLCONNECTION, Constantes.USUARIO, Constantes.CONTRASENIA);

            String sql = "UPDATE piezas SET cantidad_disponible = cantidad_disponible + ? WHERE id_pieza = ?";
            pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, cantidad);
            pstmt.setInt(2, idPieza);

            int filasAfectadas = pstmt.executeUpdate();
            exito = filasAfectadas > 0;

        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert("Error", "No se pudo actualizar el stock de la pieza.", Alert.AlertType.ERROR);
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return exito;
    }

    /**
     * Metodo para mostrar una alerta, title -> titulo de la alerta, message ->
     * mensaje de la alerta
     *
     * @param title (String)
     * @param message (String)
     */
    private static void showAlert(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setHeaderText(null);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

}
