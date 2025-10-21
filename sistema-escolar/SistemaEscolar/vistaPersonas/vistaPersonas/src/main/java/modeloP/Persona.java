package modeloP;



import javafx.beans.property.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class Persona {

    // ---- Propiedades observables ----
    private final IntegerProperty idPersona = new SimpleIntegerProperty();
    private final StringProperty nombre = new SimpleStringProperty();
    private final StringProperty apellidoP = new SimpleStringProperty();
    private final StringProperty apellidoM = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> fNacimiento = new SimpleObjectProperty<>();
    private final StringProperty sexo = new SimpleStringProperty();
    private final StringProperty rol = new SimpleStringProperty();
    private final ObjectProperty<LocalDateTime> createdAt = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDateTime> updatedAt = new SimpleObjectProperty<>();

    // ---- Constructores ----
    public Persona() {}

    // ---- Getters/Setters/Properties ----

    public int getIdPersona() { return idPersona.get(); }
    public void setIdPersona(int value) { idPersona.set(value); }
    public IntegerProperty idPersonaProperty() { return idPersona; }

    public String getNombre() { return nombre.get(); }
    public void setNombre(String value) { nombre.set(value); }
    public StringProperty nombreProperty() { return nombre; }

    public String getApellidoP() { return apellidoP.get(); }
    public void setApellidoP(String value) { apellidoP.set(value); }
    public StringProperty apellidoPProperty() { return apellidoP; }

    public String getApellidoM() { return apellidoM.get(); }
    public void setApellidoM(String value) { apellidoM.set(value); }
    public StringProperty apellidoMProperty() { return apellidoM; }

    public LocalDate getFNacimiento() { return fNacimiento.get(); }
    public void setFNacimiento(LocalDate value) { fNacimiento.set(value); }
    public ObjectProperty<LocalDate> fNacimientoProperty() { return fNacimiento; }

    public String getSexo() { return sexo.get(); }
    public void setSexo(String value) { sexo.set(value); }
    public StringProperty sexoProperty() { return sexo; }

    public String getRol() { return rol.get(); }
    public void setRol(String value) { rol.set(value); }
    public StringProperty rolProperty() { return rol; }

    public LocalDateTime getCreatedAt() { return createdAt.get(); }
    public void setCreatedAt(LocalDateTime value) { createdAt.set(value); }
    public ObjectProperty<LocalDateTime> createdAtProperty() { return createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt.get(); }
    public void setUpdatedAt(LocalDateTime value) { updatedAt.set(value); }
    public ObjectProperty<LocalDateTime> updatedAtProperty() { return updatedAt; }


    // ---- Conveniencia ----
    @Override
    public String toString() {
        return String.format("ID: %d | Nombre: %s %s %s | Rol: %s",
                getIdPersona(), getNombre(), getApellidoP(), getApellidoM(), getRol());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Persona)) return false;
        Persona persona = (Persona) o;
        return getIdPersona() == persona.getIdPersona();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdPersona());
    }
}