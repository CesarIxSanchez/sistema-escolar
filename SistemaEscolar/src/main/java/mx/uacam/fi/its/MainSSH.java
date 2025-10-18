package mx.uacam.fi.its;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

import javax.swing.*;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MainSSH {
    private static final String hostname = "fi.jcaguilar.dev";
    private static final String sshUser = "patito";
    private static final String sshPass = "cuack";
    private static final String dbUser = "becario";
    private static final String dbPass = "FdI-its-5a";
    private static Session sesion;
    private static int port;
    public static String sql;
    public static String texto;
    public static StringBuilder textoBuilder = new StringBuilder();

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
        return (Connection) DriverManager.getConnection(conString, dbUser, dbPass);
    }

    public static void ejecutarComandoUpdate() throws JSchException, SQLException {
        try (Connection con = obtenerConexion()) {
            Statement sentencia = con.createStatement();
            sentencia.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void ejecutarComandoSelect() throws JSchException, SQLException {
        textoBuilder.setLength(0); // Se limpia el textBuilder
        try (Connection con = obtenerConexion()) {
            Statement sentencia = con.createStatement();
            ResultSet resultado = sentencia.executeQuery(sql);

            while(resultado.next()) {
                String id_asistencia = resultado.getString(1);
                String id_inscripcion = resultado.getString(2);
                String fecha = resultado.getString(3);
                String created_at = resultado.getString(4);
                String updated_at = resultado.getString(5);
                // Usando StringBuilder
                textoBuilder.append("ID Asistencia: ").append(id_asistencia).append(", ID Inscripcion: ").append(id_inscripcion).append(", Fecha: ")
                        .append(fecha).append(", Creado: ").append(created_at).append(", Actualizado: ").append(updated_at).append("\n");

            }
            texto = textoBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
