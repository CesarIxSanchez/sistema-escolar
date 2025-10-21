package mx.uacam.fi.its;
import javafx.application.Platform;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class materias {

    // TableView y columnas
    @FXML private TableView<Materia> tabla;
    @FXML private TableColumn<Materia, String>  TCdescripcion;
    @FXML private TableColumn<Materia, String>  TCsemestre;
    @FXML private TableColumn<Materia, Number>  TCcreditos;
    @FXML private TableColumn<Materia, String>  TCcreado;
    @FXML private TableColumn<Materia, String>  TCactualizado;

    // Inputs
    @FXML private TextField descripcion;
    @FXML private ComboBox<String> semestre; // tipado a String
    @FXML private TextField creditos;

    // Botones (si luego les das acción, añade onAction en el FXML)
    @FXML private Button guardar;
    @FXML private Button inscripcion;
    @FXML private Button personas;
    @FXML private Button asistencias;

    private final ObservableList<Materia> data = FXCollections.observableArrayList();
    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    private void initialize() {
        // Opciones del combo semestre
        semestre.setItems(FXCollections.observableArrayList("1","2","3","4","5","6","7","8"));

        // Enlaces de columnas
        TCdescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        TCsemestre.setCellValueFactory(new PropertyValueFactory<>("semestre"));
        TCcreditos.setCellValueFactory(new PropertyValueFactory<>("creditos"));

        // Fechas formateadas a String
        TCcreado.setCellValueFactory(c -> new SimpleStringProperty(formatFecha(c.getValue().getCreatedAt())));
        TCactualizado.setCellValueFactory(c -> new SimpleStringProperty(formatFecha(c.getValue().getUpdatedAt())));

        tabla.setItems(data);

        // Carga inicial
        refrescarTabla();
    }

    private String formatFecha(LocalDateTime ldt) {
        return ldt == null ? "" : DF.format(ldt);
    }

    // Metodo getter para descripcion
    public String getDescripcion() {
        return descripcion.getText(); // Devolvemos el texto del campo descripcion
    }

    // Métodos getter para semestre y creditos (si los necesitas)
    public String getSemestre() {
        return semestre.getValue();
    }

    public String getCreditos() {
        return creditos.getText();
    }

    // Guardar materia
    @FXML
    private void onGuardar(ActionEvent e) {
        String desc = getDescripcion().trim(); // Usando getter
        String sem  = getSemestre();  // Usando getter
        String cred = getCreditos().trim();  // Usando getter

        if (desc.isEmpty() || sem == null || sem.isEmpty() || cred.isEmpty()) {
            alertWarn("Por favor, completa descripción, semestre y créditos.");
            return;
        }

        int credInt;
        try {
            credInt = Integer.parseInt(cred);
        } catch (NumberFormatException ex) {
            alertWarn("Créditos debe ser un número entero.");
            return;
        }

        try {
            // Capturamos la JSchException aquí
            try {
                MainSSH.ejecutarConexion();  // Esto puede lanzar JSchException
            } catch (com.jcraft.jsch.JSchException ex) {
                ex.printStackTrace();
                alertWarn("Error al intentar conectarse a la base de datos.");
                return;  // Salir del método si no se puede conectar
            }

            String sql = "INSERT INTO materias(descripcion, semestre, creditos) VALUES (?, ?, ?);";

            // Usar PreparedStatement
            try (PreparedStatement ps = MainSSH.obtenerConexion().prepareStatement(sql)) {
                ps.setString(1, desc);  // Establecer valor de descripcion
                ps.setString(2, sem);   // Establecer valor de semestre
                ps.setInt(3, credInt);  // Establecer valor de creditos

                int resultado = ps.executeUpdate();  // Ejecutar la inserción

                // Alertas para mostrar el resultado
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    if (resultado > 0) {
                        alert.setTitle("Éxito");
                        alert.setHeaderText(null);
                        alert.setContentText(String.format("Se ha(n) insertado %d fila(s).", resultado));
                    } else {
                        alert.setTitle("Error");
                        alert.setHeaderText(null);
                        alert.setContentText("Error al insertar datos");
                    }
                    alert.showAndWait();
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            alertWarn("Error al guardar la materia.");
        } finally {
            MainSSH.desconectar();
        }

        // Limpiar y recargar
        descripcion.clear();
        semestre.getSelectionModel().clearSelection();
        creditos.clear();
        refrescarTabla();
    }


    // Útil si agregas un botón "Ver/Actualizar" con onAction="#onVer"
    @FXML
    private void onVer(ActionEvent e) {
        refrescarTabla();
    }

    private void refrescarTabla() {
        try {
            MainSSH.ejecutarConexion();
            String selectSQL = "SELECT id_materia, descripcion, semestre, creditos, created_at, updated_at " +
                    "FROM materias ORDER BY id_materia ASC;";
            ObservableList<Materia> nuevas = MainSSH.ejecutarComandoSelectMaterias(selectSQL);  // Pasamos el SQL directamente
            data.setAll(nuevas);
            tabla.sort();
        } catch (Exception ex) {
            ex.printStackTrace();
            alertWarn("Error al cargar las materias.");
        } finally {
            MainSSH.desconectar();
        }
    }

    private void alertWarn(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }
}
