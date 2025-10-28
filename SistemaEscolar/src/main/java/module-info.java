module SistemaEscolar {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.graphics;
    requires java.sql;
    requires com.jcraft.jsch;
    requires org.mariadb.jdbc;

    exports mx.uacam.fi.its;
    opens mx.uacam.fi.its to javafx.fxml, javafx.base, javafx.graphics, javafx.controls;
    opens controladorP to javafx.fxml;
    opens modeloP to javafx.fxml;
}
