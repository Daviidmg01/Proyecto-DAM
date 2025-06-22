package com.mycompany.proyectofinal.controller.ventanainfoaplicacion;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * FXML Controller class
 *
 * @author Usuario
 */
public class VentanaMostrarInformacionController{

    @FXML
    private Label txt_titulo;
    @FXML
    private Label txt_objetivo;
    @FXML
    private Label txt_funcion;

    /**
     * Initializes the controller class.
     */
    public void initialize(String modo) {
        switch (modo.toLowerCase()) {
            case "usuarios":
                this.txt_titulo.setText("INF. EMPLEADOS");
                this.txt_objetivo.setText("Esta parte de la aplicación sirve para crear, editar y eliminar los trabajadores del taller, "
                        + "en ella podremos ver sus datos. Tambien lo podemos encontrar como usuarios, ya que los empleados son usuarios de la aplicación.");
                this.txt_funcion.setText("Dentro del apartado empleados hay tres botones: \n\n"
                        + "\t Alta -> Con este botón puedes añadir un nuevo empleado a la base de datos. \n\n"
                        + "\t Modificar -> Con este botón puedes editar todo del usuario menos su contraseña, solo la podrá cambiar el propio usuario. \n\n"
                        + "\t Eliminar -> Con este botón puedes eliminar un usuario, antes de eliminarlo saldrá un mensaje de confirmacion.");
                break;
            case "menuprincipal":
                this.txt_titulo.setText("INF. MENÚ PRINCIPAL");
                this.txt_objetivo.setText("En el menú principal veras todas las opciones de la aplicación disponible, aparte en ella encontraras la parte del garaje,"
                        + " en la cual están los coches que estan dentro del taller.");
                this.txt_funcion.setText("Aquí encontraras dos partes: \n\n"
                        + "\t GARAJE: Todos los coches dentro del taller -> \n"
                        + "\t\t Cada coche es un botón, al pulsarlo podras ver información adicional sobre el coche.\n\n"
                        + "\t MENÚ PRINCIPAL: Todas las opciones de la aplicación ->\n"
                        + "\t\t Gestión Empleados , Gestión Reparaciones, Gestión Piezas, Gestión Proveedores, Gestión Contabilidad.");
                break;
            case "reparacioncoches":
                this.txt_titulo.setText("INF. REPARACIONES");
                this.txt_objetivo.setText("Aquí podras ver todas las reparaciones realizadas por el taller desde sus inicios");
                this.txt_funcion.setText("Dentro de este apartado veras dos botones: \n\n"
                        + "\t Ver más información -> Al usar este botón se te abrira una pestaña con todos los datos de la reparación. \n\n"
                        + "\t Buscador -> Con este botón abriras un buscador, el cual tiene varios campos para poder buscar o filtrar las reparaciones, "
                        + "en el caso de que todos los campos estén vacios se mostraran todas las reparaciones");
                break;
            case "proveedores":
                this.txt_titulo.setText("INF. PROVEEDORES");
                this.txt_objetivo.setText("Esta parte de la aplicación sirve para crear, editar y eliminar los proveedores del taller, "
                        + "los proveedores son los que nos venden las piezas.");
                this.txt_funcion.setText("Dentro del apartado proveedores hay tres botones: \n\n"
                        + "\t Alta -> Con este botón puedes añadir un nuevo proveedor a la base de datos. \n\n"
                        + "\t Modificar -> Con este botón puedes editar los datos del proveedor. \n\n"
                        + "\t Eliminar -> Con este botón puedes eliminar un proveedor seleccionado de la tabla.");
                break;
            case "contabilidad":
                this.txt_titulo.setText("INF. CONTABILIDAD");
                this.txt_objetivo.setText("Esta parte de la aplicación sirve para observar los movimientos economicos en la empresa, tanto gastos como ingresos");
                this.txt_funcion.setText("Dentro del apartado empleados hay un campo con el dinero total de la empresa, tabla con todos los movimientos y un buscador para buscar o filtrar la busqueda");
                
                break;
            case "piezas":
                this.txt_titulo.setText("INF. PIEZAS");
                this.txt_objetivo.setText("Este apartado esta dedicado a la gestión de piezas, en el puedes añadir una nueva pieza de un proovedor al almacen, editar las piezas y comprar estas mismas");
                this.txt_funcion.setText("Dentro de este apartado, podremos encontrar 3 botones:\n"
                        + "\tNueva pieza -> Con este boton, puede añadir una nueva pieza al almacén, el unico campo que no se puede modificar es el stock/cantidad actual\n"
                        + "\tEditar pieza -> Con este boton, puede editar una pieza de almacén, el unico campo que no se puede modificar es el stock/cantidad actual\n"
                        + "\t\t¡¡¡¡IMPORTANTE!!!!. Para comprar una pieza necesitas seleccionarla y que el pedido sea mayor de 0 piezas \n"
                        + "\tComprar pieza -> Se abrirá una nueva pestana, con un buscador de piezas, en el encontraremos varias opciones:\n"
                        + "\t\tBuscar -> Sirve para buscar una pieza o filtrar la busqueda, se guardaran los elementos seleccionado con la cantidad a comprar\n"
                        + "\t\tComprar -> ¡¡¡¡IMPORTANTE!!!!. Para comprar una pieza necesitas seleccionarla y que el pedido sea mayor de 0 piezas");
                break;
            case "datospersonales":
                this.txt_titulo.setText("INF. DATOS PERSONALES");
                this.txt_objetivo.setText("Esté apartado se encuentra en la parte superior derecha de toda la aplicación, exactamente donde pone tus datos. "
                        + "Sirve para que puedas editar tus datos");
                this.txt_funcion.setText("Dentro de este apartado veras varios campos deshabilitados, para habilitarlos tienes que pulsar el botón editar, "
                        + "tras pulsar ese botón, podras editar tus datos, para guardar tus datos tendras que volver a dar al mismo botón, "
                        + "que veras que a cambiado de funcion y nombre, ahora se llama guardar.");
                break;
        }
    }    
    
}
