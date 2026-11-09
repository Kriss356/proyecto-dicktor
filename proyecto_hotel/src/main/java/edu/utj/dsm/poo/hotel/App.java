package edu.utj.dsm.poo.hotel;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        // 1. Iniciamos en la vista de Login como pide la instrucción
        scene = new Scene(loadFXML("Login"));
        stage.setTitle("Sistema de Gestión de Hotel");
        stage.setScene(scene);
        stage.show();
    }

    // Cambia la pantalla COMPLETA (para ir de Login -> PanelAdmin o viceversa)
    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    // Cambiado a public static para poder usarlo desde el controlador al cambiar solo el centro (Alternativa B)
    public static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}