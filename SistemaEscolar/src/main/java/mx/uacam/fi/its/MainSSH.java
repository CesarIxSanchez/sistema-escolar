package mx.uacam.fi.its;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.sql.*;

import javax.swing.*;
import java.sql.ResultSet;
import java.time.LocalDateTime;

public class MainSSH {
    private static final String hostname = "fi.jcaguilar.dev";
    private static final String sshUser = "patito";
    private static final String sshPass = "cuack";
    private static final String dbUser = "becario";
    private static final String dbPass = "FdI-its-5a";
    private static Session sesion;
    private static int port;

    public static void ejecutarConexion() throws JSchException, SQLException{
        JSch jsch = new JSch();

        // ssh patito@fi.jcaguilar.dev
        sesion = jsch.getSession(sshUser, hostname);

        // introducir la contraseña
        sesion.setPassword(sshPass);

        // Deshabilita los mensajes de error
        sesion.setConfig("StrictHostKeyChecking", "no");

        // Obtenemos un puerto redireccionado
        sesion.connect();
        port = sesion.setPortForwardingL(0, "localhost", 3306);
    }

    public static void desconectar(){
        if (sesion != null && sesion.isConnected()) {
            sesion.disconnect();
        }
    }

    public static Connection obtenerConexion() throws SQLException{
        String conString = "jdbc:mariadb://localhost:" + port + "/its5a";
        System.out.println(conString);
        return DriverManager.getConnection(conString, dbUser, dbPass);
    }

    // Asistencia
    public static void ejecutarComandoUpdate() throws JSchException, SQLException {
        try (Connection con = obtenerConexion()) {
            PreparedStatement sentencia = con.prepareStatement("INSERT INTO asistencias(id_inscripcion, fecha) VALUES (?, ?);");
            sentencia.setString(1, Asistencias.id_inscripcion);
            sentencia.setString(2, Asistencias.fecha);
            int resultado = sentencia.executeUpdate();

            // alert porque JOptionPane peta la GUI
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                if (resultado > 0){
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
        } catch (SQLIntegrityConstraintViolationException e) {
            e.printStackTrace();
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error de duplicado");
                alert.setHeaderText("Registro duplicado");
                alert.setContentText("Ya existe un registro con la misma combinación de ID de inscripción y fecha.");
                alert.showAndWait();
            });
        } catch (SQLException e){
            e.printStackTrace();
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Problema con la base de datos");
                alert.setContentText("Hubo un error al intentar realizar la operación.");
                alert.showAndWait();
            });
        }
    }

    public static ObservableList<Asistencia> ejecutarComandoSelect() throws JSchException, SQLException {
        ObservableList<Asistencia> list = FXCollections.observableArrayList();

        try (Connection con = obtenerConexion()) {
            PreparedStatement sentencia = con.prepareStatement("SELECT id_asistencia, id_inscripcion, fecha, created_at, updated_at FROM asistencias ORDER BY id_asistencia ASC;");
            ResultSet resultado = sentencia.executeQuery();

            while(resultado.next()) {
                int id_asistencia = resultado.getInt(1);
                int id_inscripcion = resultado.getInt(2);
                String fecha = resultado.getString(3);

                Timestamp created_at = resultado.getTimestamp(4);
                Timestamp updated_at = resultado.getTimestamp(5);
                LocalDateTime created_at_time = created_at != null ? created_at.toLocalDateTime() : null;
                LocalDateTime updated_at_time = updated_at != null ? updated_at.toLocalDateTime() : null;

                list.add(new Asistencia(id_asistencia, id_inscripcion, fecha, created_at_time, updated_at_time));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Materias
    public static int ejecutarComandoUpdate(Materia materia) {
        int filas = 0;
        try (Connection con = obtenerConexion()) {
            // Preparar la consulta SQL
            PreparedStatement ps = con.prepareStatement("INSERT INTO materias(descripcion, semestre, creditos) VALUES (?, ?, ?);");

            // Usar el objeto materia para obtener los valores
            ps.setString(1, materia.getDescripcion());
            ps.setString(2, materia.getSemestre());
            ps.setInt(3, materia.getCreditos());

            filas = ps.executeUpdate();
            final int filasInsertadas = ps.executeUpdate(); // (OJO: esto ejecuta DOS veces el insert)
            filas = filasInsertadas;

            // Mostrar alerta al usuario
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                if (filasInsertadas > 0) {
                    alert.setTitle("Éxito");
                    alert.setHeaderText(null);
                    alert.setContentText(String.format("Se ha(n) insertado %d fila(s).", filasInsertadas));
                } else {
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Error al insertar datos");
                }
                alert.showAndWait();
            });
        } catch (SQLIntegrityConstraintViolationException e) {
            e.printStackTrace();
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error de duplicado");
                alert.setHeaderText("Registro duplicado");
                alert.setContentText("Ya existe un registro con la misma combinación de ID de materia y semestre.");
                alert.showAndWait();
            });
        } catch (SQLException e) {
            e.printStackTrace();
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Problema con la base de datos");
                alert.setContentText("Hubo un error al intentar realizar la operación.");
                alert.showAndWait();
            });
        }
        return filas;
    }

    // Ejecuta un SELECT de materias usando PreparedStatement y mapea a ObservableList<Materia>.
    public static ObservableList<Materia> ejecutarComandoSelectMaterias(String sql) {
        ObservableList<Materia> list = FXCollections.observableArrayList();
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Materia m = new Materia();
                m.setIdMateria(rs.getInt("id_materia"));
                m.setDescripcion(rs.getString("descripcion"));
                m.setSemestre(rs.getString("semestre"));
                m.setCreditos(rs.getInt("creditos"));
                m.setCreatedAt(toLdt(rs.getTimestamp("created_at")));
                m.setUpdatedAt(toLdt(rs.getTimestamp("updated_at")));
                list.add(m);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    private static LocalDateTime toLdt(java.sql.Timestamp ts) {
        if (ts != null) {
            return ts.toLocalDateTime();
        }
        return null;
    }

    // Inscripcion
    public static void ejecutarComandoUpdateInscripciones() throws SQLException {
        try (Connection con = obtenerConexion()) {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO inscripciones(id_inscripcion, id_materia, id_estudiante, calificacion) VALUES (?, ?, ?, ?);"
            );
            ps.setInt(1, Integer.parseInt(Inscripciones.id_inscripcion_value));
            ps.setInt(2, Integer.parseInt(Inscripciones.id_materia_value));
            ps.setInt(3, Integer.parseInt(Inscripciones.id_estudiante_value));
            if (Inscripciones.calificacion_value == null) {
                ps.setNull(4, Types.INTEGER);
            } else {
                ps.setInt(4, Inscripciones.calificacion_value);
            }

            int resultado = ps.executeUpdate();

            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                if (resultado > 0){
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
        } catch (SQLIntegrityConstraintViolationException e) {
            e.printStackTrace();
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error de duplicado");
                alert.setHeaderText("Registro duplicado / FK");
                alert.setContentText("Ya existe un registro con ese id_inscripcion o hay violación de llave foránea.");
                alert.showAndWait();
            });
            throw e;
        } catch (SQLException e){
            e.printStackTrace();
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Problema con la base de datos");
                alert.setContentText("Hubo un error al intentar realizar la operación.");
                alert.showAndWait();
            });
            throw e;
        }
    }

    public static ObservableList<Inscripcion> ejecutarComandoSelectInscripciones() {
        ObservableList<Inscripcion> list = FXCollections.observableArrayList();
        try (Connection con = obtenerConexion()) {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT id_inscripcion, id_materia, id_estudiante, calificacion, created_at, updated_at " +
                            "FROM inscripciones ORDER BY id_inscripcion ASC;"
            );
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int idIns = rs.getInt(1);
                int idMat = rs.getInt(2);
                int idEst = rs.getInt(3);
                Integer cal = rs.getObject(4) == null ? null : rs.getObject(4, Integer.class);

                Timestamp cAt = rs.getTimestamp(5);
                Timestamp uAt = rs.getTimestamp(6);

                list.add(new Inscripcion(
                        idIns,
                        idMat,
                        idEst,
                        cal,
                        cAt != null ? cAt.toLocalDateTime() : null,
                        uAt != null ? uAt.toLocalDateTime() : null
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}

