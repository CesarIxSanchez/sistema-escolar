package mx.uacam.fi.its;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class Asistencias {

    @FXML
    private TextField dateTextField;

    @FXML
    private Button guardarButton;

    @FXML
    private TextField idInscripcionTextField;

    @FXML
    private TextArea resultadoTextArea;

    @FXML
    void guardarButtonPressed(ActionEvent event) {
        int id_inscripcion = Integer.parseInt(idInscripcionTextField.getText());
        String fecha = dateTextField.getText();

        MainSSH.sql = String.format("INSERT INTO asistencias(id_inscripcion, fecha) VALUES" +
                "(%d, %s)", id_inscripcion, fecha);
    }

    @FXML
    void insertarButtonPressed(ActionEvent event) {

    }

}
