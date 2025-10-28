package mx.uacam.fi.its;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application{
    @Override
    public void start(Stage escenario){
        try{
            System.out.println(getClass().getResource("/Menu.fxml"));
            Parent raiz = FXMLLoader.load(getClass().getResource("/Menu.fxml"));
            Scene escena = new Scene(raiz);
            escenario.setScene(escena);
            escenario.show();
        } catch (IOException e){
            System.out.println(e);
        }/*try {
    //  Carga el FXML desde la carpeta resources
                System.out.println(getClass().getResource("/materias.fxml"));
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/materias.fxml"));
                Parent root = loader.load();

            Scene scene = new Scene(root);
            escenario.setTitle("Gestión de Materias");
            escenario.setScene(scene);
            escenario.setResizable(false);
            escenario.show();

        }catch (IOException e){
            System.out.println(e);
    }*/
    }

    public static void main(String[] args) {
        launch(args);
    }
}
