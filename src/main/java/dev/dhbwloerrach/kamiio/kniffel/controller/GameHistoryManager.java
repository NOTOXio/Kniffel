package dev.dhbwloerrach.kamiio.kniffel.controller;

import javafx.scene.control.TextArea;

/**
 * Manager für die Spielverlaufsanzeige.
 * Speichert und formatiert Ereignisse des Spielverlaufs.
 */
public class GameHistoryManager {
    private final StringBuilder gameHistory = new StringBuilder();
    private final TextArea gameHistoryArea;

    /**
     * Erstellt einen neuen GameHistoryManager.
     *
     * @param gameHistoryArea Das TextArea-Element für die Anzeige des Spielverlaufs
     */
    public GameHistoryManager(TextArea gameHistoryArea) {
        this.gameHistoryArea = gameHistoryArea;
        initializeHistory();
    }

    /**
     * Initialisiert die Spielhistorie mit einer Willkommensnachricht.
     */
    private void initializeHistory() {
        gameHistory.setLength(0);
        gameHistoryArea.setText("Willkommen bei Kniffel! Die Spielhistorie wird hier angezeigt.\n");

        // Styling für die Textfläche
        gameHistoryArea.setStyle("-fx-control-inner-background: #f8fafc; " + // Hintergrund
                                "-fx-font-family: 'Consolas', 'Monaco', monospace; " + // Font
                                "-fx-font-size: 12px; " + // Schriftgröße
                                "-fx-border-color: #cbd5e1; " + // Rahmenfarbe
                                "-fx-border-radius: 8; " + // Rahmenradius
                                "-fx-background-radius: 8; " + // Hintergrundradius
                                "-fx-border-width: 1.5; " + // Rahmenstärke
                                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.05), 4, 0.0, 0, 1);"); // Schatten
    }

    /**
     * Fügt einen Eintrag zur Spielhistorie hinzu.
     *
     * @param entry Der hinzuzufügende Eintrag
     * @param isImportant Ob der Eintrag hervorgehoben werden soll
     */
    public void addEntry(String entry, boolean isImportant) {
        // Zeitstempel erstellen (nur Stunden:Minuten:Sekunden)
        java.time.LocalTime now = java.time.LocalTime.now();
        String timestamp = String.format("[%02d:%02d:%02d] ", now.getHour(), now.getMinute(), now.getSecond());

        // Hervorhebung für wichtige Ereignisse
        String formattedEntry;
        if (isImportant) {
            formattedEntry = "➤ " + entry;
        } else {
            formattedEntry = "• " + entry;
        }

        // Zum Verlauf hinzufügen
        gameHistory.append(timestamp).append(formattedEntry).append("\n");

        if (gameHistoryArea != null) {
            gameHistoryArea.setText(gameHistory.toString());
            gameHistoryArea.positionCaret(gameHistoryArea.getText().length());
        }
    }

    /**
     * Fügt einen normalen (nicht hervorgehobenen) Eintrag zur Spielhistorie hinzu.
     *
     * @param entry Der hinzuzufügende Eintrag
     */
    public void addEntry(String entry) {
        addEntry(entry, false);
    }

    /**
     * Löscht den Verlauf und setzt eine neue Startnachricht.
     *
     * @param message Die neue Startnachricht
     */
    public void reset(String message) {
        gameHistory.setLength(0);
        addEntry(message, true);
    }

    /**
     * Gibt den gesamten Spielverlauf als String zurück.
     *
     * @return Der vollständige Spielverlauf
     */
    public String getFullHistory() {
        return gameHistory.toString();
    }
}
