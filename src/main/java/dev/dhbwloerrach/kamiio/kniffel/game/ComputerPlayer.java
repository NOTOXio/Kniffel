package dev.dhbwloerrach.kamiio.kniffel.game;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import dev.dhbwloerrach.kamiio.kniffel.Dice;
import dev.dhbwloerrach.kamiio.kniffel.utils.KniffelScorer;

/**
 * Spieler, der vom Computer gesteuert wird.
 * Implementiert eine einfache Strategie für den Computer:
 * 1. Würfelt bis zu 3 mal und versucht dabei, hohe Punktzahlen zu erreichen
 * 2. Wählt die Kategorie mit der höchsten Punktzahl
 */
public class ComputerPlayer extends Player {
    private Random random = new Random();

    public ComputerPlayer(String name) {
        super(name);
    }

    @Override
    public void takeTurn() {
        // Diese Methode wird aufgerufen, wenn der Computer am Zug ist
        // Die eigentliche Implementierung würde mit der Game-Klasse interagieren
    }

    /**
     * Entscheidet, welche Würfel der Computer halten möchte.
     *
     * @param diceList Die aktuelle Liste der Würfel
     * @return Ein Boolean-Array, das angibt, welche Würfel neu gewürfelt werden sollen
     */
    public boolean[] decideDiceToRoll(List<Dice> diceList) {
        boolean[] toRoll = new boolean[diceList.size()];
        int[] values = new int[7]; // Zählt die Häufigkeit jeder Würfelzahl (1-6)

        // Zähle die Würfelwerte
        for (Dice dice : diceList) {
            values[dice.getValue()]++;
        }

        // Strategie: Behalte gleiche Zahlen und versuche, Straßen zu vervollständigen
        int maxCount = 0;
        int maxValue = 0;

        // Finde die häufigste Zahl
        for (int i = 1; i <= 6; i++) {
            if (values[i] > maxCount) {
                maxCount = values[i];
                maxValue = i;
            }
        }

        // Entscheidungslogik
        for (int i = 0; i < diceList.size(); i++) {
            int value = diceList.get(i).getValue();

            // Wenn wir bereits 3 oder mehr gleiche Zahlen haben, behalte diese
            if (maxCount >= 3 && value == maxValue) {
                toRoll[i] = false; // Nicht neu würfeln
            }
            // Wenn wir 2 gleiche Zahlen haben und eine gute Chance auf 3 gleiche, behalte diese
            else if (maxCount == 2 && value == maxValue) {
                toRoll[i] = false;
            }
            // Für eine potenzielle Straße behalte die Zahlen in der Mitte (3,4)
            else if ((value == 3 || value == 4) && (values[3] > 0 && values[4] > 0)) {
                toRoll[i] = false;
            }
            // Ansonsten würfle neu
            else {
                toRoll[i] = true;
            }
        }

        return toRoll;
    }

    /**
     * Wählt die beste verfügbare Kategorie basierend auf den aktuellen Würfeln.
     *
     * @param diceList Die aktuelle Liste der Würfel
     * @return Die gewählte Kategorie
     */
    public Category chooseBestCategory(List<Dice> diceList) {
        Map<Category, Integer> potentialScores =
            java.util.Arrays.stream(Category.values())
                .filter(cat -> !isCategoryUsed(cat))
                .collect(Collectors.toMap(
                    cat -> cat,
                    cat -> KniffelScorer.calculateScore(cat, diceList)
                ));

        // Finde die Kategorie mit der höchsten Punktzahl
        return potentialScores.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(Category.CHANCE); // Fallback auf CHANCE, sollte aber nie vorkommen
    }
}
