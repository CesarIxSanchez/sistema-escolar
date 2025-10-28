package mx.uacam.fi.its;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.stream.IntStream;

public class Inscripciones {

    // --- Valores estáticos que lee MainSSH (como en tu patrón base, pero sin chocar con @FXML) ---
    public static String id_inscripcion_value;
    public static String id_materia_value;
    public static String id_estudiante_value;
    public static Integer calificacion_value;

    // --- Vinculación con inscripcion.fxml ---
    @FXML private TableView<Inscripcion> Tabla; // fx:id="Tabla"
    @FXML private TableColumn<Inscripcion, Number> col_inscripcion; // fx:id="col_inscripcion"
    @FXML private TableColumn<Inscripcion, Number> col_materia;     // fx:id="col_materia"
    @FXML private TableColumn<Inscripcion, Number> col_estudiante;  // fx:id="col_estudiante"
    @FXML private TableColumn<Inscripcion, Integer> col_calificacion; // fx:id="col_calificacion"
    @FXML private TableColumn<Inscripcion, LocalDateTime> col_created_at; // fx:id="col_created_at"
    @FXML private TableColumn<Inscripcion, LocalDateTime> col_updated_at; // fx:id="col_updated_at"

    @FXML private TextField id_inscripcion; // fx:id="id_inscripcion"
    @FXML private TextField id_materia;     // fx:id="id_materia"
    @FXML private TextField id_estudiante;  // fx:id="id_estudiante"
    @FXML private ComboBox<Integer> recibe_calificacion; // fx:id="recibe_calificacion"

    @FXML private Button button_guardar; // fx:id="button_guardar"
    @FXML private Button button_menu;    // fx:id="button_menu"

    private final ObservableList<Inscripcion> data = FXCollections.observableArrayList();

    @FXML
    private void initialize(){
        // Columnas
        col_inscripcion.setCellValueFactory(c -> c.getValue().idInscripcionProperty());
        col_materia.setCellValueFactory(c -> c.getValue().idMateriaProperty());
        col_estudiante.setCellValueFactory(c -> c.getValue().idEstudianteProperty());
        col_calificacion.setCellValueFactory(c -> c.getValue().calificacionProperty());
        col_created_at.setCellValueFactory(c -> c.getValue().createdAtProperty());
        col_updated_at.setCellValueFactory(c -> c.getValue().updatedAtProperty());

        // ComboBox 0..10
        recibe_calificacion.getItems().setAll(IntStream.rangeClosed(0, 10).boxed().toList());
        recibe_calificacion.setEditable(false);

        // Tabla
        Tabla.setItems(data);
        col_inscripcion.setSortType(TableColumn.SortType.ASCENDING);
        Tabla.getSortOrder().add(col_inscripcion);

        // Asegurar acciones si el FXML no define onAction
        if (button_guardar != null) button_guardar.setOnAction(this::guardarButtonPressed);
        if (button_menu != null) button_menu.setOnAction(this::menuButtonPressed);

        // Cargar datos al iniciar (si falla no romper UI)
        try { refrescarTabla(); } catch (Exception ignored) {}
    }

    @FXML
    void guardarButtonPressed(ActionEvent event){
        if (!validarEntrada()) return;
        try {
            MainSSH.ejecutarConexion();

            id_inscripcion_value = id_inscripcion.getText().trim();
            id_materia_value = id_materia.getText().trim();
            id_estudiante_value = id_estudiante.getText().trim();
            calificacion_value = recibe_calificacion.getValue();

            // INSERT
            MainSSH.ejecutarComandoUpdateInscripciones();

            limpiarFormulario();
            refrescarTabla();
            MainSSH.desconectar();
        } catch (Exception e){
            e.printStackTrace();
            alerta(Alert.AlertType.ERROR, "ERROR", "Hubo un error al realizar la operación.");
        }
    }

    @FXML
    void menuButtonPressed(ActionEvent event){
        cambiarVentana(event, "/Menu.fxml");
    }

    private void refrescarTabla() throws Exception {
        MainSSH.ejecutarConexion();
        ObservableList<Inscripcion> nuevas = MainSSH.ejecutarComandoSelectInscripciones();
        data.setAll(nuevas);
        Tabla.sort();
        MainSSH.desconectar();
    }

    private boolean validarEntrada(){
        String idI = id_inscripcion.getText().trim();
        String idM = id_materia.getText().trim();
        String idE = id_estudiante.getText().trim();
        Integer cal = recibe_calificacion.getValue();

        if (idI.isEmpty() || idM.isEmpty() || idE.isEmpty() || cal == null){
            alerta(Alert.AlertType.WARNING, "Advertencia", "Por favor, complete todos los campos.");
            return false;
        }
        if (!idI.matches("\\d+") || !idM.matches("\\d+") || !idE.matches("\\d+")){
            alerta(Alert.AlertType.WARNING, "Advertencia", "Los IDs deben ser numéricos.");
            return false;
        }
        if (cal < 0 || cal > 10){
            alerta(Alert.AlertType.WARNING, "Advertencia", "La calificación debe estar entre 0 y 10.");
            return false;
        }
        return true;
    }

    private void limpiarFormulario(){
        id_inscripcion.setText("");
        id_materia.setText("");
        id_estudiante.setText("");
        recibe_calificacion.setValue(null);
    }

    private void cambiarVentana(ActionEvent event, String vistaFXML){
        try{
            Parent nuevaVista = FXMLLoader.load(getClass().getResource(vistaFXML));
            Scene nuevaEscena = new Scene(nuevaVista);
            Stage ventana = (Stage)((Node)event.getSource()).getScene().getWindow();
            ventana.setScene(nuevaEscena);
            ventana.show();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void alerta(Alert.AlertType tipo, String titulo, String mensaje){
        Platform.runLater(() -> {
            Alert alert = new Alert(tipo);
            alert.setTitle(titulo);
            alert.setHeaderText(null);
            alert.setContentText(mensaje);
            alert.showAndWait();
        });
    }
}


