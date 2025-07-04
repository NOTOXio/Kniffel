package dev.dhbwloerrach.kamiio.kniffel.ui;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Hilfsklasse für UI-Animationen im Kniffel-Spiel.
 */
public class AnimationHelper {

    /**
     * Zeigt eine Revanche-Animation an.
     *
     * @param rootPane Das Root-Panel der Anwendung
     */
    public static void showRevancheAnimation(Pane rootPane) {
        // Halbdurchsichtiger Hintergrund für Popup-Effekt
        StackPane overlay = new StackPane();
        overlay.setPrefSize(rootPane.getWidth(), rootPane.getHeight());
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");

        // Revanche-Panel erstellen
        VBox revanchePanel = new VBox(15);
        revanchePanel.setAlignment(javafx.geometry.Pos.CENTER);
        revanchePanel.setPrefWidth(400);
        revanchePanel.setPrefHeight(200);
        revanchePanel.setStyle("-fx-background-color: white; -fx-background-radius: 20; " +
                            "-fx-border-color: #22c55e; -fx-border-width: 2; -fx-border-radius: 20; " +
                            "-fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.4), 15, 0.0, 0, 10);");
        revanchePanel.setMaxWidth(400);
        revanchePanel.setMaxHeight(200);

        // Würfel-Icon hinzufügen
        javafx.scene.control.Label diceLabel = new javafx.scene.control.Label("🎲");
        diceLabel.setStyle("-fx-font-size: 40px;");

        // Neue Runde Text
        javafx.scene.control.Label revancheLabel = new javafx.scene.control.Label("Neue Runde!");
        revancheLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #16a34a;");

        // Viel Glück Text
        javafx.scene.control.Label goodLuckLabel = new javafx.scene.control.Label("Viel Glück!");
        goodLuckLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #334155;");

        // Alles zum Panel hinzufügen
        revanchePanel.getChildren().addAll(diceLabel, revancheLabel, goodLuckLabel);

        // Das Panel zum Overlay hinzufügen (zentriert)
        overlay.getChildren().add(revanchePanel);

        // Das Overlay zum Haupt-Layout hinzufügen
        rootPane.getChildren().add(overlay);

        // Overlay an die Größe des Fensters anpassen
        overlay.prefWidthProperty().bind(rootPane.widthProperty());
        overlay.prefHeightProperty().bind(rootPane.heightProperty());

        // Animation für das Einblenden
        revanchePanel.setScaleX(0.5);
        revanchePanel.setScaleY(0.5);
        revanchePanel.setOpacity(0);

        // Overlay zunächst unsichtbar
        overlay.setOpacity(0);

        // Animationen
        Timeline popupTimeline = new Timeline();
        popupTimeline.getKeyFrames().addAll(
            new KeyFrame(Duration.ZERO,
                new KeyValue(overlay.opacityProperty(), 0),
                new KeyValue(revanchePanel.scaleXProperty(), 0.5),
                new KeyValue(revanchePanel.scaleYProperty(), 0.5),
                new KeyValue(revanchePanel.opacityProperty(), 0)),
            new KeyFrame(Duration.seconds(0.15),
                new KeyValue(overlay.opacityProperty(), 1)),
            new KeyFrame(Duration.seconds(0.3),
                new KeyValue(revanchePanel.scaleXProperty(), 1),
                new KeyValue(revanchePanel.scaleYProperty(), 1),
                new KeyValue(revanchePanel.opacityProperty(), 1)),
            new KeyFrame(Duration.seconds(1.5),
                new KeyValue(overlay.opacityProperty(), 1)),
            new KeyFrame(Duration.seconds(1.8),
                new KeyValue(overlay.opacityProperty(), 0),
                new KeyValue(revanchePanel.opacityProperty(), 0))
        );

        // Entferne das Overlay wenn die Animation fertig ist
        popupTimeline.setOnFinished(e -> rootPane.getChildren().remove(overlay));

        // Animation starten
        popupTimeline.play();
    }

    /**
     * Animiert das Würfeln für mehrere Würfel.
     *
     * @param diceButtons Ein Array der DiceButton-Objekte
     * @param diceHeld Ein Array, das angibt, welche Würfel gehalten werden
     * @param onComplete Aktion, die nach Abschluss der Animation ausgeführt werden soll
     */
    public static void animateDiceRoll(DiceButton[] diceButtons, boolean[] diceHeld, Runnable onComplete) {
        Timeline timeline = new Timeline();

        // 10 schnelle Würfelwürfe animieren
        for (int i = 0; i < 10; i++) {
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(50 * i), e -> {
                for (int d = 0; d < diceButtons.length; d++) {
                    if (!diceHeld[d]) {
                        int random = 1 + (int)(Math.random() * 6);
                        diceButtons[d].setValue(random);
                    }
                }
            }));
        }

        // Nach Abschluss der Animation
        if (onComplete != null) {
            timeline.setOnFinished(e -> onComplete.run());
        }

        timeline.play();
    }

    /**
     * Animiert das Erscheinen oder Verschwinden eines Elements.
     *
     * @param node Das zu animierende UI-Element
     * @param show true für Einblenden, false für Ausblenden
     * @param duration Dauer der Animation in Sekunden
     */
    public static void fadeAnimation(Node node, boolean show, double duration) {
        Timeline timeline = new Timeline();
        double startValue = show ? 0.0 : 1.0;
        double endValue = show ? 1.0 : 0.0;

        timeline.getKeyFrames().add(
            new KeyFrame(Duration.ZERO, new KeyValue(node.opacityProperty(), startValue))
        );

        timeline.getKeyFrames().add(
            new KeyFrame(Duration.seconds(duration), new KeyValue(node.opacityProperty(), endValue))
        );

        // Bei Ausblenden das Element nach der Animation ausblenden
        if (!show) {
            timeline.setOnFinished(e -> node.setVisible(false));
        } else {
            node.setVisible(true);
        }

        timeline.play();
    }
}
