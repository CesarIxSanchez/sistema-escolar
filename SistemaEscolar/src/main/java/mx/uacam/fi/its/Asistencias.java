package mx.uacam.fi.its;

import com.jcraft.jsch.JSchException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Asistencias {

    @FXML
    private DatePicker datePicker;

    public String date;

    @FXML
    private Button guardarButton;

    @FXML
    private TextField idInscripcionTextField;

    @FXML
    public TextArea resultadoTextArea;

    @FXML
    public void obtenerFecha(){
        LocalDate fechaSeleccionada = datePicker.getValue();

        if(fechaSeleccionada != null){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            date = fechaSeleccionada.format(formatter);
            System.out.println("Fecha seleccionada (formato YYYY/MM/DD): " + date);
        } else{
            System.out.println("No se ha seleccionado una fecha.");
        }
    }

    @FXML
    void guardarButtonPressed(ActionEvent event) {
        resultadoTextArea.setText("");
        logicAsistencias();
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

    private void logicAsistencias(){
        obtenerFecha();
        if (idInscripcionTextField.getText().isEmpty() || date.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Por favor, ingrese todos los datos.");
            idInscripcionTextField.setText("");
            datePicker.setValue(null);
            return;
        }

        try {
            MainSSH.ejecutarConexion();

            int id_inscripcion = Integer.parseInt(idInscripcionTextField.getText());
            String fecha = date;

            MainSSH.sql = String.format("INSERT INTO asistencias(id_inscripcion, fecha) VALUES (%d, '%s');", id_inscripcion, fecha);
            System.out.println(MainSSH.sql);

            MainSSH.ejecutarComandoUpdate();

            datePicker.setValue(null);
            idInscripcionTextField.setText("");

            MainSSH.sql = "SELECT * FROM asistencias;";

            MainSSH.ejecutarComandoSelect();

            resultadoTextArea.setText(MainSSH.texto);

            MainSSH.desconectar();
        } catch (Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Hubo un error al realizar la operación.");
        }
    }

}
