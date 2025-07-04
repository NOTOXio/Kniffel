package dev.dhbwloerrach.kamiio.kniffel;

import java.util.Random;

import dev.dhbwloerrach.kamiio.kniffel.utils.InputException;

/**
 * Repräsentiert einen einzelnen Würfel im Kniffel-Spiel.
 * Ein Würfel hat einen Wert (1-6) und kann gehalten oder neu gewürfelt werden.
 */
public class Dice {
    public static final int MIN_VALUE = 1;
    public static final int MAX_VALUE = 6;

    private int value;
    private boolean held;
    private static final Random random = new Random();

    /**
     * Erstellt einen neuen Würfel mit einem zufälligen Wert.
     */
    public Dice() {
        // Direkte Zuweisung statt roll() im Konstruktor verwenden
        this.value = random.nextInt(MAX_VALUE) + MIN_VALUE;
        this.held = false;
    }

    /**
     * Erstellt einen neuen Würfel mit einem spezifizierten Wert (für Tests).
     *
     * @param initialValue Der initiale Würfelwert
     * @throws InputException wenn der Wert außerhalb des gültigen Bereichs liegt
     */
    public Dice(int initialValue) throws InputException {
        if (initialValue < MIN_VALUE || initialValue > MAX_VALUE) {
            throw new InputException("Würfelwert muss zwischen " + MIN_VALUE + " und " + MAX_VALUE + " liegen!");
        }
        this.value = initialValue;
        this.held = false;
    }

    /**
     * Würfelt den Würfel, wenn er nicht gehalten wird.
     * Generiert einen zufälligen Wert zwischen 1 und 6.
     *
     * @return true wenn gewürfelt wurde, false wenn der Würfel gehalten wird
     */
    public boolean roll() {
        if (!held) {
            value = random.nextInt(MAX_VALUE) + MIN_VALUE;
            return true;
        }
        return false;
    }

    /**
     * Gibt den aktuellen Wert des Würfels zurück.
     *
     * @return Der Wert des Würfels (1-6)
     */
    public int getValue() {
        return value;
    }

    /**
     * Prüft, ob der Würfel gehalten wird.
     *
     * @return true wenn der Würfel gehalten wird, sonst false
     */
    public boolean isHeld() {
        return held;
    }

    /**
     * Setzt den Haltestatus des Würfels.
     *
     * @param held true um den Würfel zu halten, false um ihn freizugeben
     */
    public void setHeld(boolean held) {
        this.held = held;
    }

    /**
     * Gibt eine String-Repräsentation des Würfels zurück.
     *
     * @return Eine String-Repräsentation des Würfels
     */
    @Override
    public String toString() {
        return "Dice[value=" + value + ", held=" + held + "]";
    }
}
