package mx.uacam.fi.its;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Menu {

    @FXML
    void asistenciasButtonPressed(ActionEvent event) {
        cambiarVentana(event, "/Asistencias.fxml");
    }

    @FXML
    void inscripcionesButtonPressed(ActionEvent event) {
        cambiarVentana(event, "/Inscripción.fxml");
    }

    @FXML
    void materiasButtonPressed(ActionEvent event) {
        cambiarVentana(event, "/materias.fxml");
    }

    @FXML
    void personasButtonPressed(ActionEvent event) {
        cambiarVentana(event, "/vistaPersonas.fxml");
    }

    private void cambiarVentana(ActionEvent event, String vistaFXML) {
        try {
            Parent nuevaVista = FXMLLoader.load(getClass().getResource(vistaFXML));
            Scene nuevaEscena = new Scene(nuevaVista);
            Stage ventana = (Stage) ((Node) event.getSource()).getScene().getWindow();
            ventana.setScene(nuevaEscena);
            ventana.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
