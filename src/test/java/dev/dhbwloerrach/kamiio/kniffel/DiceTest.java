package dev.dhbwloerrach.kamiio.kniffel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dev.dhbwloerrach.kamiio.kniffel.utils.InputException;

/**
 * Testet die Funktionalität der Dice-Klasse.
 */
class DiceTest {

    @Test
    @DisplayName("Würfel sollte einen gültigen Wert nach dem Würfeln haben")
    void testInitialRoll() {
        Dice dice = new Dice();
        int value = dice.getValue();
        assertTrue(value >= Dice.MIN_VALUE && value <= Dice.MAX_VALUE,
                "Würfelwert sollte zwischen " + Dice.MIN_VALUE + " und " + Dice.MAX_VALUE + " liegen");
    }

    @Test
    @DisplayName("Würfel sollte mit spezifischem Wert erzeugt werden können")
    void testCreateDiceWithValue() throws InputException {
        int testValue = 3; // Wir verwenden einen Beispielwert
        Dice dice = new Dice(testValue);
        assertEquals(testValue, dice.getValue(), "Würfel sollte mit dem angegebenen Wert initialisiert werden");
    }

    @Test
    @DisplayName("Würfel sollte bei ungültigem Initialwert eine Exception werfen")
    void testInvalidValue() {
        InputException exLow = assertThrows(InputException.class, () -> new Dice(0),
                "Bei Wert unter MIN_VALUE sollte eine InputException geworfen werden");
        assertTrue(exLow.getMessage().contains("Würfelwert muss zwischen"));

        InputException exHigh = assertThrows(InputException.class, () -> new Dice(7),
                "Bei Wert über MAX_VALUE sollte eine InputException geworfen werden");
        assertTrue(exHigh.getMessage().contains("Würfelwert muss zwischen"));
    }

    @Test
    @DisplayName("Würfeln sollte den Wert ändern wenn der Würfel nicht gehalten wird")
    void testRoll() {
        try {
            // Würfel mit bekanntem Initialwert erstellen
            Dice dice = new Dice(1);

            // Wir können nicht direkt das Ergebnis des Würfelns testen (Zufall),
            // aber wir können mehrfach würfeln und prüfen, ob sich der Wert mindestens einmal ändert
            boolean valueChanged = false;
            for (int i = 0; i < 100; i++) {
                int oldValue = dice.getValue();
                dice.roll();
                if (oldValue != dice.getValue()) {
                    valueChanged = true;
                    break;
                }
            }

            assertTrue(valueChanged, "Nach mehrmaligem Würfeln sollte sich der Wert mindestens einmal ändern");
        } catch (InputException e) {
            fail("Unerwartete Exception beim Erzeugen des Würfels: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Würfeln sollte den Wert nicht ändern wenn der Würfel gehalten wird")
    void testRollWhenHeld() {
        try {
            Dice dice = new Dice(3);

            // Würfel halten
            dice.setHeld(true);

            // Versuche zu würfeln
            boolean result = dice.roll();

            assertFalse(result, "Würfeln sollte false zurückgeben, wenn der Würfel gehalten wird");
            assertEquals(3, dice.getValue(), "Würfelwert sollte sich nicht ändern, wenn er gehalten wird");

            // Würfel freigeben und erneut würfeln
            dice.setHeld(false);

            // Wir können nicht den genauen Wert testen, aber der roll()-Aufruf sollte true zurückgeben
            assertTrue(dice.roll(), "Würfeln sollte true zurückgeben, wenn der Würfel nicht gehalten wird");

        } catch (InputException e) {
            fail("Unerwartete Exception beim Erzeugen des Würfels: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Held-Status sollte korrekt gesetzt und abgerufen werden können")
    void testHeldStatus() {
        Dice dice = new Dice();

        // Standardmäßig nicht gehalten
        assertFalse(dice.isHeld(), "Würfel sollte standardmäßig nicht gehalten sein");

        // Status setzen und überprüfen
        dice.setHeld(true);
        assertTrue(dice.isHeld(), "Würfel sollte als gehalten markiert sein");

        dice.setHeld(false);
        assertFalse(dice.isHeld(), "Würfel sollte als nicht gehalten markiert sein");
    }

    @Test
    @DisplayName("toString sollte korrekte Repräsentation liefern")
    void testToString() {
        try {
            Dice dice = new Dice(4);
            String expected = "Dice[value=4, held=false]";
            assertEquals(expected, dice.toString(), "toString sollte korrekte Repräsentation liefern");

            dice.setHeld(true);
            expected = "Dice[value=4, held=true]";
            assertEquals(expected, dice.toString(), "toString sollte korrekte Repräsentation nach Änderung liefern");
        } catch (InputException e) {
            fail("Unerwartete Exception beim Erzeugen des Würfels: " + e.getMessage());
        }
    }
}
