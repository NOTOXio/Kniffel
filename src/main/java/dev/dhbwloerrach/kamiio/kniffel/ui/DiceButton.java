package dev.dhbwloerrach.kamiio.kniffel.ui;

import javafx.scene.control.Button;

/**
 * Spezialisierte Button-Klasse für die Würfel im Kniffel-Spiel.
 * Verwaltet das Erscheinungsbild und den Status des Würfels.
 */
public class DiceButton extends Button {

    // Unicode-Zeichen für Würfel 1-6 und leeren Würfel
    public static final String[] DICE_UNICODE = {"⬜", "\u2680", "\u2681", "\u2682", "\u2683", "\u2684", "\u2685"};
    private static final String BASE_STYLE = "-fx-font-size: 40px; -fx-background-radius: 12px; -fx-border-color: #cbd5e1; " +
                                           "-fx-border-radius: 12px; -fx-border-width: 1.5; " +
                                           "-fx-effect: dropshadow(gaussian, rgba(203, 213, 225, 0.6), 6, 0.0, 0, 2); " +
                                           "-fx-background-insets: 0; -fx-padding: 0;";

    private boolean isHeld = false;
    private int value = 0;

    /**
     * Erstellt einen neuen DiceButton mit Standardeigenschaften.
     */
    public DiceButton() {
        super();
        setMinWidth(75);
        setMinHeight(75);
        setText(DICE_UNICODE[0]);
        updateStyle();
    }

    /**
     * Gibt den aktuellen Würfelwert zurück.
     *
     * @return Der aktuelle Würfelwert (0-6)
     */
    public int getValue() {
        return value;
    }

    /**
     * Setzt den Würfelwert und aktualisiert das UI entsprechend.
     *
     * @param value Der neue Würfelwert (0-6)
     */
    public void setValue(int value) {
        if (value >= 0 && value <= 6) {
            this.value = value;
            setText(DICE_UNICODE[value]);
        }
    }

    /**
     * Gibt zurück, ob der Würfel aktuell gehalten wird.
     *
     * @return true, wenn der Würfel gehalten wird, sonst false
     */
    public boolean isHeld() {
        return isHeld;
    }

    /**
     * Ändert den Halte-Status des Würfels und aktualisiert das UI.
     *
     * @param held Der neue Halte-Status
     */
    public void setHeld(boolean held) {
        isHeld = held;
        updateStyle();
    }

    /**
     * Wechselt den Halte-Status des Würfels.
     */
    public void toggleHeld() {
        isHeld = !isHeld;
        updateStyle();
    }

    /**
     * Aktualisiert den visuellen Stil des Würfels basierend auf seinem Halte-Status.
     */
    private void updateStyle() {
        String style = isHeld
            ? "-fx-background-color: #bbf7d0; -fx-border-color: #22c55e;"
            : "-fx-background-color: white;";
        setStyle(style + BASE_STYLE);
    }
}
