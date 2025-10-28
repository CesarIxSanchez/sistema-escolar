package mx.uacam.fi.its;

import javafx.beans.property.*;
import java.time.LocalDateTime;

public class Inscripcion {
    private final IntegerProperty idInscripcion = new SimpleIntegerProperty();
    private final IntegerProperty idMateria = new SimpleIntegerProperty();
    private final IntegerProperty idEstudiante = new SimpleIntegerProperty();
    private final ObjectProperty<Integer> calificacion = new SimpleObjectProperty<>(); // permite null
    private final ObjectProperty<LocalDateTime> createdAt = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDateTime> updatedAt = new SimpleObjectProperty<>();

    public Inscripcion(int idInscripcion, int idMateria, int idEstudiante, Integer calificacion,
                       LocalDateTime createdAt, LocalDateTime updatedAt) {
        setIdInscripcion(idInscripcion);
        setIdMateria(idMateria);
        setIdEstudiante(idEstudiante);
        setCalificacion(calificacion);
        setCreatedAt(createdAt);
        setUpdatedAt(updatedAt);
    }

    public Inscripcion() { /* constructor vacío requerido por algunos frameworks */ }

    // idInscripcion
    public int getIdInscripcion() { return idInscripcion.get(); }
    public void setIdInscripcion(int value) { idInscripcion.set(value); }
    public IntegerProperty idInscripcionProperty() { return idInscripcion; }

    // idMateria
    public int getIdMateria() { return idMateria.get(); }
    public void setIdMateria(int value) { idMateria.set(value); }
    public IntegerProperty idMateriaProperty() { return idMateria; }

    // idEstudiante
    public int getIdEstudiante() { return idEstudiante.get(); }
    public void setIdEstudiante(int value) { idEstudiante.set(value); }
    public IntegerProperty idEstudianteProperty() { return idEstudiante; }

    // calificacion (nullable)
    public Integer getCalificacion() { return calificacion.get(); }
    public void setCalificacion(Integer value) { calificacion.set(value); }
    public ObjectProperty<Integer> calificacionProperty() { return calificacion; }

    // createdAt
    public LocalDateTime getCreatedAt() { return createdAt.get(); }
    public void setCreatedAt(LocalDateTime value) { createdAt.set(value); }
    public ObjectProperty<LocalDateTime> createdAtProperty() { return createdAt; }

    // updatedAt
    public LocalDateTime getUpdatedAt() { return updatedAt.get(); }
    public void setUpdatedAt(LocalDateTime value) { updatedAt.set(value); }
    public ObjectProperty<LocalDateTime> updatedAtProperty() { return updatedAt; }

    @Override
    public String toString() {
        return "Inscripcion{" +
                "idInscripcion=" + getIdInscripcion() +
                ", idMateria=" + getIdMateria() +
                ", idEstudiante=" + getIdEstudiante() +
                ", calificacion=" + getCalificacion() +
                ", createdAt=" + getCreatedAt() +
                ", updatedAt=" + getUpdatedAt() +
                '}';
    }
}





