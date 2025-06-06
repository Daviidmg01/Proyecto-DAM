package com.mycompany.proyectofinal.controller.ventanaprincipal;

import com.mycompany.proyectofinal.controller.data.CocheInfoAdc;
import com.mycompany.proyectofinal.controller.data.Global;
import com.mycompany.proyectofinal.model.Coche;
import com.mycompany.proyectofinal.service.CocheService;
import com.mycompany.proyectofinal.service.PiezaService;
import com.mycompany.proyectofinal.service.ReparacionService;
import java.io.IOException;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Clase controladora de la ventana principal de los administradores
 */
public class VentanaAdminController {

    @FXML
    private Button coche1, coche2, coche3, coche4, coche5, coche6, coche7, coche8, coche9, coche10;
    @FXML
    private Button btnUsuario;
    private ObservableList<Coche> listaCochesGaraje;
    private Button[] botones;

    /**
     * Metodo que inicializa las lista con los coches y los botones Carga todos
     * los datos
     */
    public void initialize() {
        btnUsuario.setText(Global.usuarioUser.getNombre() + "\n" + Global.usuarioUser.getEmail());
        listaCochesGaraje = FXCollections.observableArrayList();
        botones = new Button[]{coche1, coche2, coche3, coche4, coche5, coche6, coche7, coche8, coche9, coche10};
        cargarDatos();
        mostrarErrorStock();
    }

    public void mostrarErrorStock() {
        List<String> listaPiezasStockMin = PiezaService.getPiezasConStockBajo();
        if (!listaPiezasStockMin.isEmpty()) {
            StringBuilder mostrar = new StringBuilder();
            listaPiezasStockMin.forEach(nombre -> mostrar.append("\t- ").append(nombre).append("\n"));
            mostrarAlerta("Error", "Hay stock bajo de las siguientes piezas: \n" + mostrar, Alert.AlertType.ERROR);
        }
    }

    /**
     * Metodo para cargar los datos en los botones
     */
    public void cargarDatos() {
        listaCochesGaraje.clear();
        listaCochesGaraje = FXCollections.observableArrayList(CocheService.getAllCochesInGaraje());

        int i = 0;
        //recorremos los botones y le asignamos un coche
        while (i < botones.length) {

            //en el caso de que existan coches para asignar a los botones se asigna, en caso contrario se quita el boton
            if (listaCochesGaraje.size() > i) {
                botones[i].setVisible(true);
                actualizarBoton(botones[i], listaCochesGaraje.get(i).getImagen());

                //lambda -> le asigna la funcion mostrar detalles del coche a cada boton
                int index = i;
                botones[i].setOnAction(e -> mostrarDetallesCoche(index));
                i++;

            } else {
                botones[i].setVisible(false);
                i++;
            }
        }
        System.out.println("Carga de datos completada. Total registros: " + listaCochesGaraje.size());
    }

    /**
     * Metodo para actualizar las imagenes del boton
     *
     * @param boton (Button)
     * @param urlImagen (String)
     */
    private void actualizarBoton(Button boton, String urlImagen) {
        if (urlImagen != null && !urlImagen.isEmpty()) {
            Image img = new Image(urlImagen);
            ImageView imageView = new ImageView(img);
            imageView.setFitHeight(80);
            imageView.setFitWidth(120);
            boton.setGraphic(imageView);
            boton.setText("");
        } else {
            boton.setGraphic(null);
            boton.setText("Sin imagen");
        }
    }

    /**
     * Metodo que con el numero del coche en la lista, te abre una ventana con
     * los datos adicionales
     *
     * @param numInList (int)
     */
    private void mostrarDetallesCoche(int numInList) {
        // Obtener el coche seleccionado
        Coche coche = listaCochesGaraje.get(numInList);
        CocheInfoAdc detallesAdicionales = ReparacionService.getDatosAdicionales(coche.getMatricula());

        if (detallesAdicionales != null) {

            // Cargar la nueva ventana
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/proyectofinal/views/VentanaDetallesGaraje.fxml"));
                Parent root;
                root = loader.load();

                // Pasar los datos al controller
                VentanaDetallesGarajeController controller = loader.getController();
                controller.cargarDatos(coche, detallesAdicionales.getMotivo(), detallesAdicionales.getFechaEntrada(), detallesAdicionales.getPropietario());

                // Mostrar la ventana
                Stage stage = new Stage();
                stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/mycompany/proyectofinal/images/logo_taller.png")));
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException ex) {
                ex.printStackTrace();
                mostrarAlerta("Error", "No se pudo abrir la ventana.", Alert.AlertType.ERROR);
            }
        } else {
            mostrarAlerta("Info", "No se encontraron datos adicionales para este coche.", Alert.AlertType.INFORMATION);
        }

    }

    /**
     * Metodo para abrir la ventana de gestion de empleados
     */
    @FXML
    public void ventanaGestionEmpleados() {
        abrirVentana("/com/mycompany/proyectofinal/views/VentanaGestionEmpleados.fxml");
    }

    /**
     * Metodo para abrir la ventana de gestion de contabilidad
     */
    @FXML
    public void ventanaGestionContabilidad() {
        abrirVentana("/com/mycompany/proyectofinal/views/VentanaGestionContabilidad.fxml");
    }

    /**
     * Metodo para abrir la ventana de gestion de reparaciones
     */
    @FXML
    public void ventanaGestionReparaciones() {
        abrirVentana("/com/mycompany/proyectofinal/views/VentanaGestionReparaciones.fxml");
    }

    /**
     * Metodo para abrir la ventana de gestion de las piezas
     */
    @FXML
    public void ventanaGestionPiezas() {
        abrirVentana("/com/mycompany/proyectofinal/views/VentanaGestionPiezas.fxml");
    }

    /**
     * Metodo para abrir la ventana de gestion de los proveedores
     */
    @FXML
    public void ventanaGestionProveedores() {
        abrirVentana("/com/mycompany/proyectofinal/views/VentanaGestionProveedores.fxml");
    }

    /**
     * Metodo para abrir la ventana de gestion de la información
     */
    @FXML
    public void ventanaGestionInformacion() {
        abrirVentana("/com/mycompany/proyectofinal/views/VentanaInformacionAplicacion.fxml");
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
     * Metodo que con la ruta de un <b>fxml</b> lo ejecuta para mostrar la
     * ventana
     *
     * @param fxmlFile (String)
     */
    private void abrirVentana(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/mycompany/proyectofinal/images/logo_taller.png")));
            stage.initModality(Modality.APPLICATION_MODAL.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (Exception ex) {
            ex.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir la ventana: " + fxmlFile, Alert.AlertType.ERROR);
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
