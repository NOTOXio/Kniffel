package dev.dhbwloerrach.kamiio.kniffel.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dev.dhbwloerrach.kamiio.kniffel.Dice;
import dev.dhbwloerrach.kamiio.kniffel.game.Category;

/**
 * Testet die Punkteberechnungen in der KniffelScorer-Klasse.
 */
class KniffelScorerTest {

    private final List<Dice> diceList = new ArrayList<>();

    // Hilfsmethode zum Erstellen von Würfeln mit bestimmten Werten
    private void createDice(int... values) throws InputException {
        diceList.clear();
        for (int value : values) {
            diceList.add(new Dice(value));
        }
    }

    @Test
    @DisplayName("Berechnung für Einser sollte korrekt sein")
    void testOnesScore() throws InputException {
        // Testfall 1: 1, 2, 3, 4, 5 - ein Einser = 1 Punkt
        createDice(1, 2, 3, 4, 5);
        assertEquals(1, KniffelScorer.calculateScore(Category.ONES, diceList),
                "Einser-Punktzahl sollte 1 sein");

        // Testfall 2: 1, 1, 3, 4, 5 - zwei Einser = 2 Punkte
        createDice(1, 1, 3, 4, 5);
        assertEquals(2, KniffelScorer.calculateScore(Category.ONES, diceList),
                "Einser-Punktzahl sollte 2 sein");

        // Testfall 3: 2, 3, 4, 5, 6 - kein Einser = 0 Punkte
        createDice(2, 3, 4, 5, 6);
        assertEquals(0, KniffelScorer.calculateScore(Category.ONES, diceList),
                "Einser-Punktzahl sollte 0 sein");
    }

    @Test
    @DisplayName("Berechnung für Dreierpasch sollte korrekt sein")
    void testThreeOfAKindScore() throws InputException {
        // Testfall 1: 3, 3, 3, 4, 5 - Summe = 18
        createDice(3, 3, 3, 4, 5);
        assertEquals(18, KniffelScorer.calculateScore(Category.THREE_OF_A_KIND, diceList),
                "Dreierpasch-Punktzahl sollte 18 sein");

        // Testfall 2: 2, 2, 2, 2, 5 - Summe = 13 (vier Gleiche zählen auch als Dreierpasch)
        createDice(2, 2, 2, 2, 5);
        assertEquals(13, KniffelScorer.calculateScore(Category.THREE_OF_A_KIND, diceList),
                "Dreierpasch-Punktzahl sollte 13 sein");

        // Testfall 3: 1, 2, 3, 4, 5 - kein Dreierpasch = 0 Punkte
        createDice(1, 2, 3, 4, 5);
        assertEquals(0, KniffelScorer.calculateScore(Category.THREE_OF_A_KIND, diceList),
                "Dreierpasch-Punktzahl sollte 0 sein");
    }

    @Test
    @DisplayName("Berechnung für Viererpasch sollte korrekt sein")
    void testFourOfAKindScore() throws InputException {
        // Testfall 1: 4, 4, 4, 4, 5 - Summe = 21
        createDice(4, 4, 4, 4, 5);
        assertEquals(21, KniffelScorer.calculateScore(Category.FOUR_OF_A_KIND, diceList),
                "Viererpasch-Punktzahl sollte 21 sein");

        // Testfall 2: 3, 3, 3, 3, 3 - Summe = 15 (fünf Gleiche zählen auch als Viererpasch)
        createDice(3, 3, 3, 3, 3);
        assertEquals(15, KniffelScorer.calculateScore(Category.FOUR_OF_A_KIND, diceList),
                "Viererpasch-Punktzahl sollte 15 sein");

        // Testfall 3: 2, 2, 2, 3, 4 - kein Viererpasch = 0 Punkte
        createDice(2, 2, 2, 3, 4);
        assertEquals(0, KniffelScorer.calculateScore(Category.FOUR_OF_A_KIND, diceList),
                "Viererpasch-Punktzahl sollte 0 sein");
    }

    @Test
    @DisplayName("Berechnung für Full House sollte korrekt sein")
    void testFullHouseScore() throws InputException {
        // Testfall 1: 2, 2, 2, 5, 5 - Full House = 25 Punkte
        createDice(2, 2, 2, 5, 5);
        assertEquals(25, KniffelScorer.calculateScore(Category.FULL_HOUSE, diceList),
                "Full House-Punktzahl sollte 25 sein");

        // Testfall 2: 3, 3, 3, 3, 3 - kein Full House = 0 Punkte
        createDice(3, 3, 3, 3, 3);
        assertEquals(0, KniffelScorer.calculateScore(Category.FULL_HOUSE, diceList),
                "Full House-Punktzahl sollte 0 sein");

        // Testfall 3: 1, 2, 3, 4, 5 - kein Full House = 0 Punkte
        createDice(1, 2, 3, 4, 5);
        assertEquals(0, KniffelScorer.calculateScore(Category.FULL_HOUSE, diceList),
                "Full House-Punktzahl sollte 0 sein");
    }

    @Test
    @DisplayName("Berechnung für Kleine Straße sollte korrekt sein")
    void testSmallStraightScore() throws InputException {
        // Testfall 1: 1, 2, 3, 4, 6 - kleine Straße = 30 Punkte
        createDice(1, 2, 3, 4, 6);
        assertEquals(30, KniffelScorer.calculateScore(Category.SMALL_STRAIGHT, diceList),
                "Kleine Straße-Punktzahl sollte 30 sein");

        // Testfall 2: 2, 3, 4, 5, 5 - kleine Straße = 30 Punkte
        createDice(2, 3, 4, 5, 5);
        assertEquals(30, KniffelScorer.calculateScore(Category.SMALL_STRAIGHT, diceList),
                "Kleine Straße-Punktzahl sollte 30 sein");

        // Testfall 3: 3, 4, 5, 6, 6 - kleine Straße = 30 Punkte
        createDice(3, 4, 5, 6, 6);
        assertEquals(30, KniffelScorer.calculateScore(Category.SMALL_STRAIGHT, diceList),
                "Kleine Straße-Punktzahl sollte 30 sein");

        // Testfall 4: 1, 2, 5, 6, 6 - keine kleine Straße = 0 Punkte
        createDice(1, 2, 5, 6, 6);
        assertEquals(0, KniffelScorer.calculateScore(Category.SMALL_STRAIGHT, diceList),
                "Kleine Straße-Punktzahl sollte 0 sein");
    }

    @Test
    @DisplayName("Berechnung für Große Straße sollte korrekt sein")
    void testLargeStraightScore() throws InputException {
        // Testfall 1: 1, 2, 3, 4, 5 - große Straße = 40 Punkte
        createDice(1, 2, 3, 4, 5);
        assertEquals(40, KniffelScorer.calculateScore(Category.LARGE_STRAIGHT, diceList),
                "Große Straße-Punktzahl sollte 40 sein");

        // Testfall 2: 2, 3, 4, 5, 6 - große Straße = 40 Punkte
        createDice(2, 3, 4, 5, 6);
        assertEquals(40, KniffelScorer.calculateScore(Category.LARGE_STRAIGHT, diceList),
                "Große Straße-Punktzahl sollte 40 sein");

        // Testfall 3: 1, 3, 4, 5, 6 - keine große Straße = 0 Punkte
        createDice(1, 3, 4, 5, 6);
        assertEquals(0, KniffelScorer.calculateScore(Category.LARGE_STRAIGHT, diceList),
                "Große Straße-Punktzahl sollte 0 sein");
    }

    @Test
    @DisplayName("Berechnung für Kniffel sollte korrekt sein")
    void testKniffelScore() throws InputException {
        // Testfall 1: 6, 6, 6, 6, 6 - Kniffel = 50 Punkte
        createDice(6, 6, 6, 6, 6);
        assertEquals(50, KniffelScorer.calculateScore(Category.KNIFFEL, diceList),
                "Kniffel-Punktzahl sollte 50 sein");

        // Testfall 2: 1, 1, 1, 1, 1 - Kniffel = 50 Punkte
        createDice(1, 1, 1, 1, 1);
        assertEquals(50, KniffelScorer.calculateScore(Category.KNIFFEL, diceList),
                "Kniffel-Punktzahl sollte 50 sein");

        // Testfall 3: 1, 1, 1, 1, 2 - kein Kniffel = 0 Punkte
        createDice(1, 1, 1, 1, 2);
        assertEquals(0, KniffelScorer.calculateScore(Category.KNIFFEL, diceList),
                "Kniffel-Punktzahl sollte 0 sein");
    }

    @Test
    @DisplayName("Berechnung für Chance sollte korrekt sein")
    void testChanceScore() throws InputException {
        // Testfall 1: 1, 2, 3, 4, 5 - Summe = 15
        createDice(1, 2, 3, 4, 5);
        assertEquals(15, KniffelScorer.calculateScore(Category.CHANCE, diceList),
                "Chance-Punktzahl sollte 15 sein");

        // Testfall 2: 6, 6, 6, 6, 6 - Summe = 30
        createDice(6, 6, 6, 6, 6);
        assertEquals(30, KniffelScorer.calculateScore(Category.CHANCE, diceList),
                "Chance-Punktzahl sollte 30 sein");
    }

    @Test
    @DisplayName("calculateScore sollte eine IllegalArgumentException werfen, wenn diceList null ist")
    void testCalculateScoreWithNullDiceList() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> KniffelScorer.calculateScore(Category.ONES, null),
                "calculateScore sollte eine IllegalArgumentException werfen, wenn diceList null ist");
        assertTrue(exception.getMessage().contains("null"));
    }

    @Test
    @DisplayName("calculateScore sollte eine IllegalArgumentException werfen, wenn diceList nicht 5 Würfel enthält")
    void testCalculateScoreWithInvalidDiceListSize() throws InputException {
        // Zu wenige Würfel
        createDice(1, 2, 3, 4);
        IllegalArgumentException exTooFew = assertThrows(IllegalArgumentException.class,
                () -> KniffelScorer.calculateScore(Category.ONES, diceList),
                "calculateScore sollte eine IllegalArgumentException werfen, wenn diceList nicht 5 Würfel enthält");
        assertTrue(exTooFew.getMessage().contains("5 Würfel"));

        // Zu viele Würfel
        createDice(1, 2, 3, 4, 5, 6);
        IllegalArgumentException exTooMany = assertThrows(IllegalArgumentException.class,
                () -> KniffelScorer.calculateScore(Category.ONES, diceList),
                "calculateScore sollte eine IllegalArgumentException werfen, wenn diceList nicht 5 Würfel enthält");
        assertTrue(exTooMany.getMessage().contains("5 Würfel"));
    }

    @Test
    @DisplayName("calculateAllScores sollte Punktzahlen für alle Kategorien berechnen")
    void testCalculateAllScores() throws InputException {
        createDice(2, 2, 2, 3, 3);
        Map<Category, Integer> scores = KniffelScorer.calculateAllScores(diceList);

        assertEquals(0, scores.get(Category.ONES), "Ones sollte 0 sein");
        assertEquals(6, scores.get(Category.TWOS), "Twos sollte 6 sein");
        assertEquals(6, scores.get(Category.THREES), "Threes sollte 6 sein");
        assertEquals(0, scores.get(Category.FOURS), "Fours sollte 0 sein");
        assertEquals(0, scores.get(Category.FIVES), "Fives sollte 0 sein");
        assertEquals(0, scores.get(Category.SIXES), "Sixes sollte 0 sein");
        assertEquals(12, scores.get(Category.THREE_OF_A_KIND), "THREE_OF_A_KIND sollte 12 sein");
        assertEquals(0, scores.get(Category.FOUR_OF_A_KIND), "FOUR_OF_A_KIND sollte 0 sein");
        assertEquals(25, scores.get(Category.FULL_HOUSE), "FULL_HOUSE sollte 25 sein");
        assertEquals(0, scores.get(Category.SMALL_STRAIGHT), "SMALL_STRAIGHT sollte 0 sein");
        assertEquals(0, scores.get(Category.LARGE_STRAIGHT), "LARGE_STRAIGHT sollte 0 sein");
        assertEquals(0, scores.get(Category.KNIFFEL), "KNIFFEL sollte 0 sein");
        assertEquals(12, scores.get(Category.CHANCE), "CHANCE sollte 12 sein");
    }
}
