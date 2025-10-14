package mx.uacam.fi.its;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.mariadb.jdbc.Connection;
import org.mariadb.jdbc.Statement;
import org.mariadb.jdbc.client.result.Result;

import java.sql.DriverManager;
import java.sql.SQLException;

public class MainSSH {
    public static String sql;

    public static void main(String[] args) throws JSchException, SQLException {
        String hostname = "fi.jcaguilar.dev";
        String sshUser = "patito";
        String sshPass = "cuack";

        String dbUser = "becario";
        String dbPass = "FdI-its-5a";

        JSch jsch = new JSch();

        // ssh patito@fi.jcaguilar.dev
        Session sesion = jsch.getSession(sshUser, hostname);

        // introducir la contraseña
        sesion.setPassword(sshPass);

        // Deshabilita los mensajes de error
        sesion.setConfig("StrictHostKeyChecking", "no");

        // Obtenemos un puerto redireccionado
        sesion.connect();
        int port = sesion.setPortForwardingL(0, "localhost", 3306);

        String conString = "jdbc:mariadb://localhost:" + port + "/its5a";
        System.out.println(conString);

        try (Connection con = (Connection) DriverManager.getConnection(conString, dbUser, dbPass)) {
            // Juega con la db normalmente
            Statement sentencia = con.createStatement();
            // Sentencia a modificar
            Result resultado = (Result) sentencia.executeQuery(sql);

            while(resultado.next()) {
                // Modificar
                String id = resultado.getString(1);
                String nombre = resultado.getString(2);
                String apellido = resultado.getString(3);
                System.out.println("Persona: ID: " + id + ", Nombre: " + nombre + ", Apellido: " + apellido);
            }
        }

        // Exit
        sesion.disconnect();
    }
}
