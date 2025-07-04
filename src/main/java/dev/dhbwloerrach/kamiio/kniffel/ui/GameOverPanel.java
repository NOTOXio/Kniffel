package dev.dhbwloerrach.kamiio.kniffel.ui;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Panel, das am Ende eines Spiels angezeigt wird.
 * Zeigt den Gewinner und Optionen für eine Revanche oder Rückkehr zum Hauptmenü an.
 */
public class GameOverPanel {

    /**
     * Interface zur Behandlung von Panel-Aktionen.
     */
    public interface GameOverPanelHandler {
        /**
         * Wird aufgerufen, wenn der Benutzer eine Revanche spielen möchte.
         */
        void onRematchRequested();

        /**
         * Wird aufgerufen, wenn der Benutzer zum Hauptmenü zurückkehren möchte.
         */
        void onReturnToMainMenuRequested();
    }

    private final StackPane overlay;
    private final VBox panel;
    private final GameOverPanelHandler handler;
    private final Pane rootPane;

    /**
     * Erstellt ein neues GameOverPanel.
     *
     * @param rootPane Das Root-Panel der Anwendung
     * @param winnerName Der Name des Gewinners
     * @param isComputer true, wenn der Computer gewonnen hat, sonst false
     * @param score Die Punktzahl des Gewinners
     * @param handler Der Handler für Panel-Aktionen
     */
    public GameOverPanel(Pane rootPane, String winnerName, boolean isComputer, int score, GameOverPanelHandler handler) {
        this.rootPane = rootPane;
        this.handler = handler;

        // Halbdurchsichtiger Hintergrund
        overlay = new StackPane();
        overlay.setPrefSize(rootPane.getWidth(), rootPane.getHeight());
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");

        // Game-Over-Panel erstellen
        panel = new VBox(15);
        panel.setAlignment(Pos.CENTER);
        panel.setPrefWidth(500);
        panel.setPrefHeight(250);
        panel.setStyle("-fx-background-color: white; -fx-background-radius: 20; " +
                      "-fx-border-color: #22c55e; -fx-border-width: 2; -fx-border-radius: 20; " +
                      "-fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.4), 15, 0.0, 0, 10);");
        panel.setMaxWidth(500);
        panel.setMaxHeight(250);

        // Icon je nach Gewinner wählen
        Label trophyLabel = new Label(isComputer ? "🤖" : "🏆");
        trophyLabel.setStyle("-fx-font-size: 40px;");

        // Text je nach Gewinner anpassen
        Label winnerLabel;
        if (isComputer) {
            winnerLabel = new Label("Schade! Der Computer hat gewonnen.");
            winnerLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #ef4444;");
        } else {
            winnerLabel = new Label("Glückwunsch " + winnerName + "!");
            winnerLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #16a34a;");
        }

        // Punkte
        Label scoreLabel = new Label("Punktzahl: " + score);
        scoreLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #334155;");

        // Buttons für die Aktionen
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        // Revanche-Button
        Button rematchButton = new Button("Revanche");
        rematchButton.setStyle("-fx-font-size: 16px; -fx-background-color: #22c55e; -fx-text-fill: white; " +
                             "-fx-background-radius: 8; -fx-padding: 10 25; -fx-font-weight: bold; " +
                             "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.1), 4, 0, 0, 1);");
        rematchButton.setOnAction(e -> {
            hide();
            if (handler != null) {
                handler.onRematchRequested();
            }
        });

        // Beenden-Button
        Button quitButton = new Button("Zurück zum Menü");
        quitButton.setStyle("-fx-font-size: 16px; -fx-background-color: #f1f5f9; -fx-text-fill: #334155; " +
                          "-fx-background-radius: 8; -fx-padding: 10 25; -fx-font-weight: bold; " +
                          "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.1), 4, 0, 0, 1);");
        quitButton.setOnAction(e -> {
            hide();
            if (handler != null) {
                handler.onReturnToMainMenuRequested();
            }
        });

        // Buttons hinzufügen
        buttonBox.getChildren().addAll(rematchButton, quitButton);

        // Alles zum Panel hinzufügen
        panel.getChildren().addAll(trophyLabel, winnerLabel, scoreLabel, buttonBox);

        // Das Panel zum Overlay hinzufügen
        overlay.getChildren().add(panel);
    }

    /**
     * Zeigt das Panel mit einer Animation an.
     */
    public void show() {
        // Das Overlay zum Haupt-Layout hinzufügen
        rootPane.getChildren().add(overlay);

        // Overlay an die Größe des Fensters anpassen
        overlay.prefWidthProperty().bind(rootPane.widthProperty());
        overlay.prefHeightProperty().bind(rootPane.heightProperty());

        // Animation für das Einblenden vorbereiten
        panel.setScaleX(0.5);
        panel.setScaleY(0.5);
        panel.setOpacity(0);
        overlay.setOpacity(0);

        // Animationen starten
        Timeline popupTimeline = new Timeline();
        popupTimeline.getKeyFrames().addAll(
            new KeyFrame(Duration.ZERO,
                new KeyValue(overlay.opacityProperty(), 0),
                new KeyValue(panel.scaleXProperty(), 0.5),
                new KeyValue(panel.scaleYProperty(), 0.5),
                new KeyValue(panel.opacityProperty(), 0)),
            new KeyFrame(Duration.seconds(0.3),
                new KeyValue(overlay.opacityProperty(), 1)),
            new KeyFrame(Duration.seconds(0.5),
                new KeyValue(panel.scaleXProperty(), 1),
                new KeyValue(panel.scaleYProperty(), 1),
                new KeyValue(panel.opacityProperty(), 1))
        );

        popupTimeline.play();
    }

    /**
     * Entfernt das Panel aus dem UI.
     */
    public void hide() {
        rootPane.getChildren().remove(overlay);
    }
}
