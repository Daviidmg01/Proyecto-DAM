package com.mycompany.proyectofinal.controller.ventanapiezas;

import com.mycompany.proyectofinal.controller.data.Global;
import com.mycompany.proyectofinal.controller.data.PiezaTableModel;
import com.mycompany.proyectofinal.controller.data.SpinnerTableCell;
import com.mycompany.proyectofinal.service.ContabilidadService;
import com.mycompany.proyectofinal.service.PiezaService;
import java.sql.Date;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class VentanComprarPiezasController {

    // Componentes de la interfaz definidos en el FXML
    @FXML
    private TableView<PiezaTableModel> tablaPiezas;
    @FXML
    private TableColumn<PiezaTableModel, Boolean> columnaSeleccion;
    @FXML
    private TableColumn<PiezaTableModel, String> columnaNombre;
    @FXML
    private TableColumn<PiezaTableModel, Integer> columnaStock;
    @FXML
    private TableColumn<PiezaTableModel, Integer> columnaMinimo;
    @FXML
    private TableColumn<PiezaTableModel, Integer> columnaCantidad;

    @FXML
    private TableColumn<PiezaTableModel, Float> columnaPrecio;

    private Map<Integer, PiezaTableModel> piezasSeleccionadas = new HashMap<>();

    @FXML
    private Button btnUsuario;
    @FXML
    private CheckBox checkBoxStock;
    @FXML
    private ComboBox<String> comboBoxProveedor;
    @FXML
    private TextField txtFieldNombrePieza;
    @FXML
    private Label txtPrecioTotalCompra;
    @FXML
    private Button btnRealizarCompra;

    private ObservableList<PiezaTableModel> listaPiezas;

    @FXML
    private Button buscarPiezas;
    @FXML
    private Label txtDineroActual;

    /**
     * Método que se ejecuta al inicializar la vista
     */
    public void initialize() {
        btnUsuario.setText(Global.usuarioUser.getNombre() + "\n" + Global.usuarioUser.getEmail());
        List<String> proveedores = PiezaService.getProveedores();
        proveedores.add("");
        comboBoxProveedor.getItems().addAll(proveedores);
        this.txtDineroActual.setText(String.valueOf(ContabilidadService.getSaldoActual()) + " €");

        // Configurar las columnas de la tabla
        configurarColumnas();

        // Añadir datos de ejemplo a la tabla
        cargarDatos();

        // Escuchar cambios en la lista para actualizar el precio total
        listaPiezas.addListener((ListChangeListener.Change<? extends PiezaTableModel> change) -> {
            actualizarPrecioTotal();
        });

        // Inicializar el precio total
        actualizarPrecioTotal();
    }

    /**
     * Método para actualizar el precio total
     */
    private void actualizarPrecioTotal() {
        float total = 0;

        try {
            for (PiezaTableModel pieza : listaPiezas) {
                if (pieza.isSeleccionado()) {
                    total += pieza.getPrecio() * pieza.getCantidad();
                }
            }

            // Usa NumberFormat para evitar problemas regionales
            NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
            nf.setMaximumFractionDigits(2);
            nf.setMinimumFractionDigits(2);
            txtPrecioTotalCompra.setText(nf.format(total) + " €");

        } catch (Exception e) {
            txtPrecioTotalCompra.setText("0.00 €");
            mostrarAlerta("Error", "Error al calcular el total: " + e.getMessage(), Alert.AlertType.ERROR);
        }

    }

    /**
     * Metodo que configura las columnas para que se cargen y aparezca el
     * comboBox y el Spinner
     */
    private void configurarColumnas() {
        // Columna de selección (CheckBox)
        columnaSeleccion.setCellValueFactory(cellData -> cellData.getValue().seleccionadoProperty());
        columnaSeleccion.setCellFactory(CheckBoxTableCell.forTableColumn(columnaSeleccion));

        // Columnas normales (id, nombre, stock)
        columnaNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        columnaStock.setCellValueFactory(cellData -> cellData.getValue().stockProperty().asObject());
        columnaMinimo.setCellValueFactory(cellData -> cellData.getValue().minimoProperty().asObject());
        columnaPrecio.setCellValueFactory(cellData -> cellData.getValue().precioProperty().asObject());

        // Columna con Spinner para cantidad a comprar
        columnaCantidad.setCellValueFactory(cellData -> cellData.getValue().cantidadProperty().asObject());
        columnaCantidad.setCellFactory(column -> new SpinnerTableCell());
    }

    /**
     * Metodo que carga los datos desde la bbdd para mostrarlos en la tabla
     */
    private void cargarDatos() {
        listaPiezas = FXCollections.observableArrayList(PiezaService.getAllPiezasTableModel());

        // Restaurar selecciones y cantidades si existen
        for (PiezaTableModel pieza : listaPiezas) {
            if (piezasSeleccionadas.containsKey(pieza.getId())) {
                PiezaTableModel seleccionada = piezasSeleccionadas.get(pieza.getId());
                pieza.seleccionadoProperty().set(seleccionada.isSeleccionado());
                pieza.cantidadProperty().set(seleccionada.getCantidad());
            }
        }

        tablaPiezas.setItems(listaPiezas);

        // Añadir listeners
        listaPiezas.forEach(pieza -> {
            pieza.seleccionadoProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) {
                    piezasSeleccionadas.put(pieza.getId(), pieza);
                } else {
                    piezasSeleccionadas.remove(pieza.getId());
                }
                actualizarPrecioTotal();
            });

            pieza.cantidadProperty().addListener((obs, oldVal, newVal) -> {
                if (pieza.isSeleccionado()) {
                    piezasSeleccionadas.put(pieza.getId(), pieza);
                    actualizarPrecioTotal();
                }
            });
        });
    }

    /**
     * Metodo busca las piezas en la bbdd con los parametros introducidos
     */
    @FXML
    public void buscarPiezas() {
        boolean debajoMinimo = this.checkBoxStock.isSelected();
        String nombrePieza = this.txtFieldNombrePieza.getText();
        String nombreProveedor = this.comboBoxProveedor.getValue();

        // Guardar las selecciones actuales antes de buscar
        List<PiezaTableModel> piezasFiltradas = PiezaService.buscarPiezasFiltradas(debajoMinimo, nombrePieza, nombreProveedor);

        // Crear nueva lista manteniendo las selecciones
        listaPiezas = FXCollections.observableArrayList();
        for (PiezaTableModel pieza : piezasFiltradas) {
            PiezaTableModel nuevaPieza = new PiezaTableModel(
                    pieza.getId(),
                    pieza.getNombre(),
                    pieza.getStock(),
                    pieza.getMinimo(),
                    pieza.getPrecio()
            );

            // Restaurar selección y cantidad si estaba seleccionada antes
            if (piezasSeleccionadas.containsKey(pieza.getId())) {
                PiezaTableModel seleccionada = piezasSeleccionadas.get(pieza.getId());
                nuevaPieza.seleccionadoProperty().set(true);
                nuevaPieza.cantidadProperty().set(seleccionada.getCantidad());
            }

            listaPiezas.add(nuevaPieza);
        }

        this.tablaPiezas.setItems(listaPiezas);

        // Volver a añadir los listeners
        listaPiezas.forEach(pieza -> {
            pieza.seleccionadoProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) {
                    piezasSeleccionadas.put(pieza.getId(), pieza);
                } else {
                    piezasSeleccionadas.remove(pieza.getId());
                }
                actualizarPrecioTotal();
            });

            pieza.cantidadProperty().addListener((obs, oldVal, newVal) -> {
                if (pieza.isSeleccionado()) {
                    piezasSeleccionadas.put(pieza.getId(), pieza);
                    actualizarPrecioTotal();
                }
            });
        });

        actualizarPrecioTotal();
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
     * Metodo que comprueba si el precio de la compra es menor que el dinero del
     * taller
     *
     * @return <b>True</b> Si se puede realizar la compra, <b>False</b> sino se
     * puede realizar la compra
     */
    private boolean comprobarPrecioCompra() {
        try {
            // Elimina símbolos no numéricos antes de parsear
            String precioTexto = txtPrecioTotalCompra.getText().replace("€", "").trim();
            precioTexto = precioTexto.replace(",", "").trim();
            float precioCompra = Float.parseFloat(precioTexto);

            String saldoTexto = txtDineroActual.getText().replace("€", "").trim();
            float saldoActual = Float.parseFloat(saldoTexto);

            return precioCompra <= saldoActual;

        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Formato numérico inválido", Alert.AlertType.ERROR);
            return false;
        }
    }

    /**
     * Metodo que realiza la compra, si la cantidad de piezas pedias es mayor de
     * 0, actualiza el stock de las piezas y registra la compra de las piezas,
     * al registrar la compra salta un trigger y se actualiza el dinero del
     * taller
     */
    @FXML
    public void realizarCompra(ActionEvent event) {
        float precioTotal = 0;
        if (piezasSeleccionadas.isEmpty()) {
            mostrarAlerta("Error", "No hay piezas seleccionadas para comprar.", Alert.AlertType.ERROR);
            return;
        }
        if (comprobarPrecioCompra()) {
            boolean exito = true;
            StringBuilder conceptoCompra = new StringBuilder("Compra de:");

            // Usamos un contador para saber si es el primer elemento
            int contador = 0;

            for (PiezaTableModel pieza : piezasSeleccionadas.values()) {
                if (pieza.getCantidad() <= 0) {
                    mostrarAlerta("Error", "La cantidad para '" + pieza.getNombre() + "' debe ser mayor que 0.", Alert.AlertType.ERROR);
                    exito = false;
                    break;
                }

                // Actualizar el stock en la base de datos
                if (!PiezaService.actualizarStockPieza(pieza.getId(), pieza.getCantidad())) {
                    exito = false;
                    mostrarAlerta("Error", "No se pudo actualizar el stock de '" + pieza.getNombre() + "'.", Alert.AlertType.ERROR);
                    break;
                } else {
                    precioTotal += pieza.getPrecio() * pieza.getCantidad();

                    // Añadimos coma solo si no es el primer elemento
                    if (contador > 0) {
                        conceptoCompra.append(",");
                    }
                    conceptoCompra.append(" ").append(pieza.getNombre());
                    contador++;
                }
            }

            conceptoCompra.append(".");

            if (exito) {
                // Actualizar el stock en la base de datos
                if (!ContabilidadService.insertarTransaccionContabilidad("gasto", precioTotal, Date.valueOf(LocalDate.now()), conceptoCompra.toString(), Global.usuarioUser.getId())) {
                    mostrarAlerta("Error", "No se pudo registrar la compra.", Alert.AlertType.ERROR);
                } else {
                    mostrarAlerta("Éxito", "Compra realizada correctamente. Stock actualizado.\n\n" + conceptoCompra.toString(), Alert.AlertType.INFORMATION);
                    piezasSeleccionadas.clear(); // Limpiar selecciones
                    cargarDatos(); // Refrescar la tabla
                    actualizarPrecioTotal(); // Resetear el precio total
                    cerrarVentana(event);
                }
            }
        } else {
            mostrarAlerta("Error", "No puedes gastar más dinero del que tiene el taller.", Alert.AlertType.ERROR);
        }
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
