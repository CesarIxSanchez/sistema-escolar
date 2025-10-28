package controladorP;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import MainSSH.MainSSHPersonas; // Asegúrate de que este import sea correcto
import modeloP.Persona;  // Asegúrate de que este import sea correcto

import java.time.LocalDate;

public class Controladorpersonas {

    // ---- Componentes de la UI (Asegúrate que coincidan con fx:id en FXML) ----
    @FXML private TextField nombre;
    @FXML private TextField apellidoP;
    @FXML private TextField apellidoM;
    @FXML private DatePicker fNacimiento;
    @FXML private ComboBox<String> sexo;
    @FXML private ComboBox<String> rol;
    @FXML private TextArea baseDD; // Este es para MOSTRAR los registros existentes

    // ---- Botones ----
    @FXML private Button guardar;

    /**
     * Este método se llama automáticamente después de que se carga el archivo FXML.
     * Es el lugar perfecto para inicializar los componentes.
     */
    @FXML
    private void initialize() {
        // Llenar los ComboBox con opciones predefinidas
        rol.setItems(FXCollections.observableArrayList("Estudiante", "Docente", "Administrativo"));
        sexo.setItems(FXCollections.observableArrayList("Masculino", "Femenino", "Otro"));

        // Carga inicial de datos en el TextArea al abrir la ventana
        refrescarDatos();
    }

    /**
     * Este método se activa cuando se hace clic en el botón "Guardar".
     * @param event El evento de acción del clic.
     */
    @FXML
    private void onGuardar(ActionEvent event) {
        // 1. Recolectar datos de la interfaz
        String nombreVal = nombre.getText().trim();
        String apellidoPVal = apellidoP.getText().trim();
        String apellidoMVal = apellidoM.getText().trim();
        LocalDate fNacimientoVal = fNacimiento.getValue();
        String sexoVal = sexo.getValue();
        String rolVal = rol.getValue();

        // 2. Validar que los campos no estén vacíos
        if (nombreVal.isEmpty() || apellidoPVal.isEmpty() || fNacimientoVal == null || sexoVal == null || rolVal == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos Incompletos", "Todos los campos (excepto Apellido Materno) son obligatorios.");
            return;
        }

        // 3. Crear el objeto Persona
        Persona nuevaPersona = new Persona();
        nuevaPersona.setNombre(nombreVal);
        nuevaPersona.setApellidoP(apellidoPVal);
        nuevaPersona.setApellidoM(apellidoMVal); // Puede estar vacío
        nuevaPersona.setFNacimiento(fNacimientoVal);
        nuevaPersona.setSexo(sexoVal);
        nuevaPersona.setRol(rolVal);

        // 4. Ejecutar la inserción en un hilo separado para no bloquear la UI
        new Thread(() -> {
            try {
                MainSSHPersonas.ejecutarConexion();
                int filasAfectadas = MainSSHPersonas.ejecutarComandoInsertPersona(nuevaPersona);

                // 5. Mostrar retroalimentación y actualizar la UI en el hilo de JavaFX
                Platform.runLater(() -> {
                    if (filasAfectadas > 0) {
                        mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "La persona ha sido registrada correctamente.");
                        limpiarFormulario();
                        refrescarDatos(); // Actualizar el TextArea con el nuevo registro
                    } else {
                        mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo registrar a la persona.");
                    }
                });

            } catch (Exception ex) {
                ex.printStackTrace();
                Platform.runLater(() -> mostrarAlerta(Alert.AlertType.ERROR, "Error de Conexión", "Ocurrió un error al intentar guardar."));
            } finally {
                MainSSHPersonas.desconectar();
            }
        }).start();
    }

    /**
     * Carga o actualiza los datos de la tabla 'personas' y los muestra en el TextArea.
     */
    private void refrescarDatos() {
        // Se ejecuta en un hilo para no congelar la aplicación al iniciar
        new Thread(() -> {
            try {
                MainSSHPersonas.ejecutarConexion();
                ObservableList<Persona> listaPersonas = MainSSHPersonas.ejecutarComandoSelectPersonas();

                // Construir el texto para el TextArea
                StringBuilder sb = new StringBuilder("--- REGISTROS ACTUALES ---\n\n");
                for (Persona p : listaPersonas) {
                    sb.append(p.toString()).append("\n"); // Usa el método toString() de Persona
                }

                // Actualizar el TextArea en el hilo de JavaFX
                Platform.runLater(() -> {
                    baseDD.setText(sb.toString());
                });

            } catch (Exception ex) {
                ex.printStackTrace();
                Platform.runLater(() -> mostrarAlerta(Alert.AlertType.ERROR, "Error de Carga", "No se pudieron cargar los datos de las personas."));
            } finally {
                MainSSHPersonas.desconectar();
            }
        }).start();
    }

    /**
     * Limpia todos los campos del formulario.
     */
    private void limpiarFormulario() {
        nombre.clear();
        apellidoP.clear();
        apellidoM.clear();
        fNacimiento.setValue(null);
        sexo.getSelectionModel().clearSelection();
        rol.getSelectionModel().clearSelection();
    }

    /**
     * Muestra una alerta simple al usuario.
     * @param tipo El tipo de alerta (INFORMATION, WARNING, ERROR).
     * @param titulo El título de la ventana de alerta.
     * @param mensaje El contenido del mensaje.
     */
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}