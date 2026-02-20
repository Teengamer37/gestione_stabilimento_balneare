package com.example.s_balneare;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public final class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/com/example/s_balneare/fxml/root.fxml")));

        Scene scene = new Scene(loader.load(), 1100, 750);
        stage.setTitle("Stabilimenti Balneari");
        stage.setScene(scene);
        stage.show();
    }
}
