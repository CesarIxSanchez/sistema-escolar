package mx.uacam.fi.its;

import javafx.application.Platform;
import javafx.scene.control.*;
import mx.uacam.fi.its.Asistencia;
import com.jcraft.jsch.JSchException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class Asistencias {

    public static String id_inscripcion;
    public static String fecha;

    @FXML
    private TableView<Asistencia> tabla;

    @FXML
    private TableColumn<Asistencia, Number> colIdAsistencia;

    @FXML
    private TableColumn<Asistencia, Number> colIdInscripcion;

    @FXML
    private TableColumn<Asistencia, String> colFecha;

    @FXML
    private TableColumn<Asistencia, java.time.LocalDateTime> colCreado;

    @FXML
    private TableColumn<Asistencia, java.time.LocalDateTime> colActualizado;

    private final ObservableList<Asistencia> data = FXCollections.observableArrayList();

    @FXML
    private DatePicker datePicker;

    public String date;

    @FXML
    private Button guardarButton;

    @FXML
    private Button verButton;

    @FXML
    private TextField idInscripcionTextField;

    @FXML
    private void initialize() {
        colIdAsistencia.setCellValueFactory(c -> c.getValue().idAsistenciaProperty());
        colIdInscripcion.setCellValueFactory(c -> c.getValue().idInscripcionProperty());
        colFecha.setCellValueFactory(c -> c.getValue().fechaProperty());
        colCreado.setCellValueFactory(c -> c.getValue().createdAtProperty());
        colActualizado.setCellValueFactory(c -> c.getValue().updatedAtProperty());

        tabla.setItems(data);

        // ordenar ascendente por id_asistencia
        colIdAsistencia.setSortType(TableColumn.SortType.ASCENDING);
        tabla.getSortOrder().add(colIdAsistencia);
    }

    @FXML
    public void obtenerFecha(){
        LocalDate fechaSeleccionada = datePicker.getValue();

        if(fechaSeleccionada != null){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            date = fechaSeleccionada.format(formatter);
            System.out.println("Fecha seleccionada (formato YYYY/MM/DD): " + date);
        } else{
            date = null;
            System.out.println("No se ha seleccionado una fecha.");
        }
    }

    @FXML
    void guardarButtonPressed(ActionEvent event) {
        logicAsistencias();
    }

    @FXML
    void verButtonPressed(ActionEvent event) throws Exception {
        refrescarTabla();
    }

    @FXML
    void menuButtonPressed(ActionEvent event) {
        cambiarVentana(event, "/Menu.fxml");
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

    private void refrescarTabla() throws Exception {
        MainSSH.ejecutarConexion();

        // se ejecuta el comando select
        ObservableList<Asistencia> nuevas = MainSSH.ejecutarComandoSelect();

        // Limpia y carga de nuevo
        data.setAll(nuevas);

        // Reaplica orden
        tabla.sort();

        MainSSH.desconectar();
    }

    private void logicAsistencias(){
        obtenerFecha();
        if (idInscripcionTextField.getText().isEmpty() || date == null) {
            // alert porque JOptionPane peta la GUI
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Advertencia");
                alert.setHeaderText(null);
                alert.setContentText("Por favor, ingrese todos los datos.");
                alert.showAndWait();
            });
            idInscripcionTextField.setText("");
            datePicker.setValue(null);
            return;
        }

        try {
            MainSSH.ejecutarConexion();

            id_inscripcion = idInscripcionTextField.getText();
            fecha = date;

            MainSSH.ejecutarComandoUpdate();

            datePicker.setValue(null);
            idInscripcionTextField.setText("");

            refrescarTabla();

            MainSSH.desconectar();
        } catch (Exception e){
            e.printStackTrace();
            // alert porque JOptionPane peta la GUI
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("ERROR");
                alert.setHeaderText(null);
                alert.setContentText("Hubo un error al realizar la operación.");
                alert.showAndWait();
            });
        }
    }

}
