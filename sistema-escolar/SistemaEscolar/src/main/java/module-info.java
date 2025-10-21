module SistemaEscolar {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.graphics;
    requires java.sql;
    requires com.jcraft.jsch;
    requires org.mariadb.jdbc;

    opens mx.uacam.fi.its to javafx.fxml, javafx.graphics, javafx.controls;
}
