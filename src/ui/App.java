package ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        // Por ahora, solo un mensaje de bienvenida con estilo
        Label label = new Label("¡Bienvenido a tu Task Manager, Adriel!");
        label.setStyle("-fx-font-size: 20px; -fx-text-fill: white;");

        StackPane root = new StackPane(label);
        root.setStyle("-fx-background-color: #2b2b2b;"); // Fondo oscuro tipo macOS

        Scene scene = new Scene(root, 600, 400);

        stage.setTitle("Task Manager Pro");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}