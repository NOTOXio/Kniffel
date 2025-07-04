package dev.dhbwloerrach.kamiio.kniffel.utils;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import dev.dhbwloerrach.kamiio.kniffel.Dice;
import dev.dhbwloerrach.kamiio.kniffel.game.Category;

/**
 * Utility-Klasse zur Berechnung der Punktzahlen für die verschiedenen Kniffel-Kategorien.
 * Diese Klasse enthält statische Methoden, um die Punkte basierend auf den aktuellen Würfelwerten zu berechnen.
 */
public final class KniffelScorer {
    // Konstanten für Punktwerte
    private static final int FULL_HOUSE_SCORE = 25;
    private static final int SMALL_STRAIGHT_SCORE = 30;
    private static final int LARGE_STRAIGHT_SCORE = 40;
    private static final int KNIFFEL_SCORE = 50;

    // Private Konstruktor verhindert Instanziierung
    private KniffelScorer() {
        throw new AssertionError("Utility-Klasse sollte nicht instanziiert werden!");
    }

    /**
     * Berechnet die Punktzahl für eine bestimmte Kategorie basierend auf den aktuellen Würfelwerten.
     *
     * @param category Die Kategorie, für die die Punkte berechnet werden sollen
     * @param diceList Die Liste der Würfel mit ihren aktuellen Werten
     * @return Die berechnete Punktzahl für die angegebene Kategorie
     * @throws IllegalArgumentException wenn die Würfelliste null ist oder nicht genau 5 Würfel enthält
     */
    public static int calculateScore(Category category, List<Dice> diceList) {
        // Fehlerprüfung
        if (diceList == null) {
            throw new IllegalArgumentException("Würfelliste darf nicht null sein!");
        }

        if (diceList.size() != 5) {
            throw new IllegalArgumentException("Würfelliste muss genau 5 Würfel enthalten!");
        }

        // Zähle die Häufigkeiten der Würfelwerte
        int[] counts = countDiceValues(diceList);
        int sum = calculateSum(diceList);

        // Berechne Punktzahl basierend auf der Kategorie
        switch (category) {
            case ONES:   return calculateNumberScore(counts, 1);
            case TWOS:   return calculateNumberScore(counts, 2);
            case THREES: return calculateNumberScore(counts, 3);
            case FOURS:  return calculateNumberScore(counts, 4);
            case FIVES:  return calculateNumberScore(counts, 5);
            case SIXES:  return calculateNumberScore(counts, 6);

            case THREE_OF_A_KIND: return hasNOfAKind(counts, 3) ? sum : 0;
            case FOUR_OF_A_KIND:  return hasNOfAKind(counts, 4) ? sum : 0;

            case FULL_HOUSE:      return isFullHouse(counts) ? FULL_HOUSE_SCORE : 0;
            case SMALL_STRAIGHT:  return isSmallStraight(counts) ? SMALL_STRAIGHT_SCORE : 0;
            case LARGE_STRAIGHT:  return isLargeStraight(counts) ? LARGE_STRAIGHT_SCORE : 0;
            case KNIFFEL:         return hasNOfAKind(counts, 5) ? KNIFFEL_SCORE : 0;

            case CHANCE:          return sum;
            default:              return 0;
        }
    }

    /**
     * Zählt die Häufigkeit der verschiedenen Würfelwerte.
     *
     * @param diceList Die Liste der zu zählenden Würfel
     * @return Ein Array mit der Häufigkeit der Würfelwerte (Index = Würfelwert)
     */
    private static int[] countDiceValues(List<Dice> diceList) {
        int[] counts = new int[7]; // Index 0 wird nicht verwendet
        for (Dice dice : diceList) {
            counts[dice.getValue()]++;
        }
        return counts;
    }

    /**
     * Berechnet die Summe aller Würfelwerte.
     *
     * @param diceList Die Liste der Würfel
     * @return Die Summe aller Würfelwerte
     */
    private static int calculateSum(List<Dice> diceList) {
        return diceList.stream().mapToInt(Dice::getValue).sum();
    }

    /**
     * Berechnet die Punktzahl für eine Zahlenkategorie (ONES bis SIXES).
     *
     * @param counts Array mit Würfelhäufigkeiten
     * @param number Die zu zählende Zahl (1-6)
     * @return Die berechnete Punktzahl
     */
    private static int calculateNumberScore(int[] counts, int number) {
        return counts[number] * number;
    }

    /**
     * Prüft, ob die Würfel mindestens n gleiche Werte enthalten.
     *
     * @param counts Array mit Würfelhäufigkeiten
     * @param n Die Anzahl gleicher Würfel, die gesucht werden
     * @return true, wenn mindestens n gleiche Würfel vorhanden sind, sonst false
     */
    private static boolean hasNOfAKind(int[] counts, int n) {
        for (int i = 1; i <= 6; i++) {
            if (counts[i] >= n) {
                return true;
            }
        }
        return false;
    }

    /**
     * Prüft, ob die Würfel einen Full House bilden (3 gleiche + 2 gleiche).
     *
     * @param counts Array mit Würfelhäufigkeiten
     * @return true, wenn die Würfel einen Full House bilden, sonst false
     */
    private static boolean isFullHouse(int[] counts) {
        boolean has3 = false;
        boolean has2 = false;

        for (int i = 1; i <= 6; i++) {
            if (counts[i] == 3) has3 = true;
            if (counts[i] == 2) has2 = true;
        }

        return has3 && has2;
    }

    /**
     * Prüft, ob die Würfel eine kleine Straße bilden (4 aufeinanderfolgende Zahlen).
     *
     * @param counts Array mit Würfelhäufigkeiten
     * @return true, wenn die Würfel eine kleine Straße bilden, sonst false
     */
    private static boolean isSmallStraight(int[] counts) {
        // Prüfe die drei möglichen kleinen Straßen: 1-2-3-4, 2-3-4-5, 3-4-5-6
        return (counts[1] > 0 && counts[2] > 0 && counts[3] > 0 && counts[4] > 0) ||
               (counts[2] > 0 && counts[3] > 0 && counts[4] > 0 && counts[5] > 0) ||
               (counts[3] > 0 && counts[4] > 0 && counts[5] > 0 && counts[6] > 0);
    }

    /**
     * Prüft, ob die Würfel eine große Straße bilden (5 aufeinanderfolgende Zahlen).
     *
     * @param counts Array mit Würfelhäufigkeiten
     * @return true, wenn die Würfel eine große Straße bilden, sonst false
     */
    private static boolean isLargeStraight(int[] counts) {
        // Prüfe die zwei möglichen großen Straßen: 1-2-3-4-5 oder 2-3-4-5-6
        return (counts[1] == 1 && counts[2] == 1 && counts[3] == 1 && counts[4] == 1 && counts[5] == 1) ||
               (counts[2] == 1 && counts[3] == 1 && counts[4] == 1 && counts[5] == 1 && counts[6] == 1);
    }

    /**
     * Berechnet die optimale Kategorie für die gegebenen Würfelwerte.
     * Diese Methode kann von der KI verwendet werden, um die beste Kategorie zu wählen.
     *
     * @param diceList Die Liste der Würfel
     * @return Eine Map mit den Kategorien und ihren Punktzahlen
     */
    public static Map<Category, Integer> calculateAllScores(List<Dice> diceList) {
        Map<Category, Integer> scores = new EnumMap<>(Category.class);

        for (Category category : Category.values()) {
            scores.put(category, calculateScore(category, diceList));
        }

        return scores;
    }
}
