module com.mycompany.proyectofinal {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.base;
    
    opens com.mycompany.proyectofinal.controller.login to javafx.fxml;
    opens com.mycompany.proyectofinal.controller.ventanaprincipal to javafx.fxml;
    opens com.mycompany.proyectofinal.controller.ventanaempleados to javafx.fxml;
    opens com.mycompany.proyectofinal.controller.ventanapiezas to javafx.fxml;
    opens com.mycompany.proyectofinal.controller.ventanareparaciones to javafx.fxml;
    opens com.mycompany.proyectofinal.controller.ventanaproveedores to javafx.fxml;
    opens com.mycompany.proyectofinal.controller.ventanacontabilidad to javafx.fxml;
    opens com.mycompany.proyectofinal.controller.ventanainfopersonal to javafx.fxml;
    opens com.mycompany.proyectofinal.controller.ventanainfoaplicacion to javafx.fxml;
    opens com.mycompany.proyectofinal.model to javafx.base;
    opens com.mycompany.proyectofinal.controller.data to javafx.base;
    
    exports com.mycompany.proyectofinal;
}
