package MainSSH;


import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import modeloP.Persona;

import java.sql.*;
import java.time.LocalDateTime;

public class MainSSHPersonas {
    // ---- Credenciales y configuración de la conexión ----
    private static final String hostname = "fi.jcaguilar.dev";
    private static final String sshUser = "patito";
    private static final String sshPass = "cuack";
    private static final String dbUser = "becario";
    private static final String dbPass = "FdI-its-5a";
    private static Session sesion;
    private static int port;

    // ---- Métodos de Conexión ----

    /**
     * Abre una sesión SSH y crea un túnel (port-forward) local hacia la base de datos remota.
     * Si la sesión ya está activa, no hace nada.
     */
    public static void ejecutarConexion() throws JSchException {
        if (sesion != null && sesion.isConnected()) {
            return;
        }
        JSch jsch = new JSch();
        sesion = jsch.getSession(sshUser, hostname);
        sesion.setPassword(sshPass);
        sesion.setConfig("StrictHostKeyChecking", "no");
        sesion.connect();
        // Redirige un puerto local libre al puerto 3306 del servidor remoto (MySQL/MariaDB)
        port = sesion.setPortForwardingL(0, "localhost", 3306);
    }

    /**
     * Cierra la sesión SSH si está activa.
     */
    public static void desconectar() {
        if (sesion != null && sesion.isConnected()) {
            sesion.disconnect();
        }
    }

    /**
     * Obtiene una conexión JDBC a la base de datos a través del túnel SSH.
     * @return Una conexión activa a la base de datos.
     * @throws SQLException si ocurre un error al conectar.
     */
    public static Connection obtenerConexion() throws SQLException {
        String conString = "jdbc:mariadb://localhost:" + port + "/its5a";
        return DriverManager.getConnection(conString, dbUser, dbPass);
    }

    // ---- Métodos para PERSONAS ----

    /**
     * Inserta un nuevo registro de Persona en la base de datos.
     * @param persona El objeto Persona con los datos a insertar.
     * @return El número de filas afectadas (debería ser 1 si tuvo éxito).
     */
    public static int ejecutarComandoInsertPersona(Persona persona) {
        int filasAfectadas = 0;
        String sql = "INSERT INTO personas(nombre, apellido_p, apellido_m, f_nacimiento, sexo, rol) VALUES (?, ?, ?, ?, ?, ?);";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, persona.getNombre());
            ps.setString(2, persona.getApellidoP());
            ps.setString(3, persona.getApellidoM());
            ps.setObject(4, persona.getFNacimiento());
            ps.setString(5, persona.getSexo());
            ps.setString(6, persona.getRol());
            filasAfectadas = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error de Base de Datos");
                alert.setHeaderText("No se pudo guardar el registro.");
                alert.setContentText("Ocurrió un error al intentar insertar los datos de la persona.");
                alert.showAndWait();
            });
        }
        return filasAfectadas;
    }

    /**
     * Consulta todos los registros de la tabla 'personas' y los devuelve en una lista observable.
     * @return Una ObservableList de objetos Persona.
     */
    public static ObservableList<Persona> ejecutarComandoSelectPersonas() {
        ObservableList<Persona> listaPersonas = FXCollections.observableArrayList();
        String sql = "SELECT id_persona, nombre, apellido_p, apellido_m, f_nacimiento, sexo, rol, created_at, updated_at FROM personas ORDER BY id_persona ASC;";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Persona p = new Persona();
                p.setIdPersona(rs.getInt("id_persona"));
                p.setNombre(rs.getString("nombre"));
                p.setApellidoP(rs.getString("apellido_p"));
                p.setApellidoM(rs.getString("apellido_m"));
                Date fechaSql = rs.getDate("f_nacimiento");
                if (fechaSql != null) {
                    p.setFNacimiento(fechaSql.toLocalDate());
                }
                p.setSexo(rs.getString("sexo"));
                p.setRol(rs.getString("rol"));
                p.setCreatedAt(toLdt(rs.getTimestamp("created_at")));
                p.setUpdatedAt(toLdt(rs.getTimestamp("updated_at")));
                listaPersonas.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listaPersonas;
    }

    // ---- Método de utilidad ----
    private static LocalDateTime toLdt(java.sql.Timestamp ts) {
        if (ts != null) {
            return ts.toLocalDateTime();
        }
        return null;
    }
}