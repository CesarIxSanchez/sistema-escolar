package mx.uacam.fi.its;

import javafx.beans.property.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;


public class Materia {

    // ---- Propiedades observables (para TableView / bindings) ----
    private final IntegerProperty idMateria     = new SimpleIntegerProperty();
    private final StringProperty  descripcion   = new SimpleStringProperty();
    private final StringProperty  semestre      = new SimpleStringProperty();
    private final IntegerProperty creditos      = new SimpleIntegerProperty();
    private final ObjectProperty<LocalDateTime> createdAt = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDateTime> updatedAt = new SimpleObjectProperty<>();

    // Formato de fecha opcional para mostrar en columnas tipo String
    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // ---- Constructores ----
    public Materia() {}

    public Materia(int idMateria, String descripcion, String semestre, int creditos,
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        setIdMateria(idMateria);
        setDescripcion(descripcion);
        setSemestre(semestre);
        setCreditos(creditos);
        setCreatedAt(createdAt);
        setUpdatedAt(updatedAt);
    }

    // ---- Getters/Setters/Properties ----
    public int getIdMateria() { return idMateria.get(); }
    public void setIdMateria(int value) { idMateria.set(value); }
    public IntegerProperty idMateriaProperty() { return idMateria; }

    public String getDescripcion() { return descripcion.get(); }
    public void setDescripcion(String value) { descripcion.set(value); }
    public StringProperty descripcionProperty() { return descripcion; }

    public String getSemestre() { return semestre.get(); }
    public void setSemestre(String value) { semestre.set(value); }
    public StringProperty semestreProperty() { return semestre; }

    public int getCreditos() { return creditos.get(); }
    public void setCreditos(int value) { creditos.set(value); }
    public IntegerProperty creditosProperty() { return creditos; }

    public LocalDateTime getCreatedAt() { return createdAt.get(); }
    public void setCreatedAt(LocalDateTime value) { createdAt.set(value); }
    public ObjectProperty<LocalDateTime> createdAtProperty() { return createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt.get(); }
    public void setUpdatedAt(LocalDateTime value) { updatedAt.set(value); }
    public ObjectProperty<LocalDateTime> updatedAtProperty() { return updatedAt; }

    // ---- Ayudas para mostrar fechas formateadas en columnas String ----
    public String getCreadoStr()     { return getCreatedAt()    == null ? "" : DF.format(getCreatedAt()); }
    public String getActualizadoStr(){ return getUpdatedAt()    == null ? "" : DF.format(getUpdatedAt()); }

    // ---- Conveniencia ----
    @Override public String toString() {
        return "Materia{" +
                "idMateria=" + getIdMateria() +
                ", descripcion='" + getDescripcion() + '\'' +
                ", semestre='" + getSemestre() + '\'' +
                ", creditos=" + getCreditos() +
                ", createdAt=" + getCreatedAt() +
                ", updatedAt=" + getUpdatedAt() +
                '}';
    }
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Materia)) return false;
        Materia m = (Materia) o;
        return getIdMateria() == m.getIdMateria();
    }
    @Override public int hashCode() { return Objects.hash(getIdMateria()); }
}
