package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    public void start(Stage primaryStage) {
        try {
            //carico la GUI
            Parent root = FXMLLoader.load(getClass().getResource("../view/scenaPrincipale.fxml"));

            //carico l'icona dell'applicazione
            Image anotherIcon = new Image("file:icon.png");
            primaryStage.getIcons().add(anotherIcon);

            //carico la scena principale
            primaryStage.setTitle("Sistema con equazioni di primo grado");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    //avvio l'applicazione
    public static void main(String[] args) {
        launch(args);
    }
}