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
}
