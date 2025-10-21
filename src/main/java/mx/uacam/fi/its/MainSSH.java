package mx.uacam.fi.its;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.sql.*;
import java.time.LocalDateTime;

public class MainSSH {
    private static final String hostname = "fi.jcaguilar.dev";
    private static final String sshUser = "patito";
    private static final String sshPass = "cuack";
    private static final String dbUser = "becario";
    private static final String dbPass = "FdI-its-5a";
    private static Session sesion;
    private static int port;

    // Abre sesión SSH y crea un port-forward local hacia la BD remota.
    public static void ejecutarConexion() throws JSchException, SQLException{
        JSch jsch = new JSch();
        sesion = jsch.getSession(sshUser, hostname);
        sesion.setPassword(sshPass);
        sesion.setConfig("StrictHostKeyChecking", "no");
        sesion.connect();
        port = sesion.setPortForwardingL(0, "localhost", 3306);
    }

    // Cierra la sesión SSH si está activa.
    public static void desconectar(){
        if (sesion != null && sesion.isConnected()) {
            sesion.disconnect();
        }
    }

    // Obtiene una conexión JDBC a MariaDB a través del puerto local redirigido.
    public static Connection obtenerConexion() throws SQLException{
        String conString = "jdbc:mariadb://localhost:" + port + "/its5a";
        System.out.println(conString);
        return DriverManager.getConnection(conString, dbUser, dbPass);
    }

    // Ejecuta INSERT/UPDATE/DELETE usando PreparedStatement.
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
            final int filasInsertadas = ps.executeUpdate();
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

}

