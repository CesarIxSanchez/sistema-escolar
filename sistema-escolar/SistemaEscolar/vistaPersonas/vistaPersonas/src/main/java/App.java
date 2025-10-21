

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        try {

            //  Carga el FXML desde la carpeta resources
            System.out.println(getClass().getResource("/vistaPersonas.fxml"));
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistaPersonas.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            stage.setTitle("Registro de Personas");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

        }catch (IOException e){
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        launch(args); // Inicia la aplicación JavaFX
    }
}
