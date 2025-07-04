package dev.dhbwloerrach.kamiio.kniffel.game;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dev.dhbwloerrach.kamiio.kniffel.Dice;
import dev.dhbwloerrach.kamiio.kniffel.utils.InputException;

/**
 * Testet die KI-Logik des ComputerPlayers.
 */
class ComputerPlayerTest {

    private ComputerPlayer computerPlayer;
    private List<Dice> diceList;

    @BeforeEach
    @SuppressWarnings("unused") // Von JUnit automatisch aufgerufen
    void setUp() {
        computerPlayer = new ComputerPlayer("Computer");
        diceList = new ArrayList<>();
    }

    // Hilfsmethode zum Erstellen von Würfeln mit bestimmten Werten
    private void createDice(int... values) throws InputException {
        diceList.clear();
        for (int value : values) {
            diceList.add(new Dice(value));
        }
    }

    @Test
    @DisplayName("ComputerPlayer sollte korrekt initialisiert werden")
    void testComputerPlayerInitialization() {
        assertEquals("Computer", computerPlayer.getName(), "Spielername sollte korrekt sein");
        assertEquals(0, computerPlayer.getScore(), "Anfangspunktzahl sollte 0 sein");

        // Teste Konstruktor mit Schwierigkeitsstufe
        ComputerPlayer hardComputer = new ComputerPlayer("HardComputer", ComputerPlayer.Difficulty.HARD);
        assertEquals("HardComputer", hardComputer.getName(), "Spielername sollte korrekt sein");
    }

    @Test
    @DisplayName("decideDiceToRoll sollte bei null-Liste eine Exception werfen")
    void testDecideDiceToRollWithNullList() {
        IllegalArgumentException exNull = assertThrows(IllegalArgumentException.class,
                () -> computerPlayer.decideDiceToRoll(null),
                "decideDiceToRoll sollte bei null-Liste eine Exception werfen");
        assertTrue(exNull.getMessage().contains("null"));
    }

    @Test
    @DisplayName("decideDiceToRoll sollte bei falscher Listengröße eine Exception werfen")
    void testDecideDiceToRollWithInvalidListSize() throws InputException {
        // Zu wenige Würfel
        createDice(1, 2, 3, 4);
        IllegalArgumentException exTooFew = assertThrows(IllegalArgumentException.class,
                () -> computerPlayer.decideDiceToRoll(diceList),
                "decideDiceToRoll sollte bei falscher Listengröße eine Exception werfen");
        assertTrue(exTooFew.getMessage().contains("5 Würfel"));

        // Zu viele Würfel
        createDice(1, 2, 3, 4, 5, 6);
        IllegalArgumentException exTooMany = assertThrows(IllegalArgumentException.class,
                () -> computerPlayer.decideDiceToRoll(diceList),
                "decideDiceToRoll sollte bei falscher Listengröße eine Exception werfen");
        assertTrue(exTooMany.getMessage().contains("5 Würfel"));
    }

    @Test
    @DisplayName("decideDiceToRoll sollte bei Kniffel-Potential die passenden Würfel behalten")
    void testDecideDiceToRollWithKniffelPotential() throws InputException {
        // Drei Vieren - sollte Vieren behalten
        createDice(4, 4, 4, 1, 2);
        boolean[] result = computerPlayer.decideDiceToRoll(diceList);

        // Die ersten drei Würfel (Vieren) sollten behalten werden
        assertFalse(result[0], "Erste Vier sollte behalten werden");
        assertFalse(result[1], "Zweite Vier sollte behalten werden");
        assertFalse(result[2], "Dritte Vier sollte behalten werden");
        assertTrue(result[3], "Eins sollte neu gewürfelt werden");
        assertTrue(result[4], "Zwei sollte neu gewürfelt werden");
    }

    @Test
    @DisplayName("chooseBestCategory sollte die beste Kategorie für die gegebenen Würfel wählen")
    void testChooseBestCategory() throws InputException {
        // Kniffel sollte höchste Priorität haben
        createDice(6, 6, 6, 6, 6);

        // Keine verwendeten Kategorien, sollte Kniffel wählen
        assertEquals(Category.KNIFFEL, computerPlayer.chooseBestCategory(diceList),
                "Sollte Kniffel wählen bei fünf Sechsen");

        // Wenn Kniffel bereits verwendet wurde, sollte Sechser oder Viererpasch gewählt werden
        computerPlayer.addScore(Category.KNIFFEL, 50);
        Category choice = computerPlayer.chooseBestCategory(diceList);
        assertTrue(choice == Category.SIXES || choice == Category.FOUR_OF_A_KIND,
                "Sollte Sechser oder Viererpasch wählen, wenn Kniffel bereits verwendet");

        // Full House Beispiel
        computerPlayer = new ComputerPlayer("Computer");
        createDice(3, 3, 3, 5, 5);
        assertEquals(Category.FULL_HOUSE, computerPlayer.chooseBestCategory(diceList),
                "Sollte Full House wählen");
    }

    @Test
    @DisplayName("ComputerPlayer sollte die obere Bereichs-Kategorien priorisieren, wenn nahe am Bonus")
    void testUpperSectionBonusPrioritization() throws InputException {
        // Einen Computer-Spieler erstellen, der nahe am Bonus ist
        ComputerPlayer bonusComputer = new ComputerPlayer("BonusComputer");

        // Punkte hinzufügen, so dass der Spieler 15 Punkte vom Bonus entfernt ist
        bonusComputer.addScore(Category.ONES, 3);
        bonusComputer.addScore(Category.TWOS, 8);
        bonusComputer.addScore(Category.THREES, 12);
        bonusComputer.addScore(Category.FOURS, 16);
        bonusComputer.addScore(Category.FIVES, 10);
        // Summe = 49, fehlen 14 Punkte für den Bonus

        // Würfel mit mehreren Sechsen
        createDice(6, 6, 6, 3, 4);

        // Sollte Sechser wählen, um den Bonus zu erreichen
        assertEquals(Category.SIXES, bonusComputer.chooseBestCategory(diceList),
                "Sollte Sechser wählen, wenn nahe am Bonus");
    }
}
