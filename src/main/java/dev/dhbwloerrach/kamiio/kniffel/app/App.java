package dev.dhbwloerrach.kamiio.kniffel.app;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Hauptklasse der Kniffel-Anwendung.
 * Startet die JavaFX-Anwendung und lädt die Haupt-FXML-Datei.
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/dev/dhbwloerrach/kamiio/kniffel/views/game-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1400, 800);
        stage.setTitle("Kniffel");
        stage.setScene(scene);
        stage.setMinWidth(1200);
        stage.setMinHeight(800);
        stage.show();
    }

    /**
     * Haupteinstiegspunkt der Anwendung.
     *
     * @param args Kommandozeilenargumente
     */
    public static void main(String[] args) {
        launch();
    }
}
