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
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}