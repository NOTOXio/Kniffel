package dev.dhbwloerrach.kamiio.kniffel.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import dev.dhbwloerrach.kamiio.kniffel.Dice;
import dev.dhbwloerrach.kamiio.kniffel.utils.KniffelScorer;

/**
 * Spieler, der vom Computer gesteuert wird.
 * Implementiert eine intelligente Strategie für den Computer:
 * 1. Würfelt bis zu 3 mal und versucht dabei, hohe Punktzahlen zu erreichen
 * 2. Wählt die Kategorie mit der höchsten Punktzahl
 * 3. Berücksichtigt die aktuelle Spielsituation bei Entscheidungen
 */
public class ComputerPlayer extends Player {
    private final Random random;

    // Konstanten für die KI-Strategie
    private static final int KNIFFEL_SCORE = 50;

    // Verschiedene Schwierigkeitsstufen für die KI
    public enum Difficulty {
        EASY,    // Einfache KI, trifft manchmal nicht optimale Entscheidungen
        NORMAL,  // Normale KI, macht gute Züge
        HARD     // Schwere KI, optimiert für maximale Punktzahl
    }

    private final Difficulty difficulty;

    /**
     * Erstellt einen neuen Computer-Spieler mit dem angegebenen Namen und normaler Schwierigkeit.
     *
     * @param name Der Name des Computer-Spielers
     */
    public ComputerPlayer(String name) {
        this(name, Difficulty.NORMAL);
    }

    /**
     * Erstellt einen neuen Computer-Spieler mit dem angegebenen Namen und Schwierigkeitsgrad.
     *
     * @param name Der Name des Computer-Spielers
     * @param difficulty Die Schwierigkeit der KI
     */
    public ComputerPlayer(String name, Difficulty difficulty) {
        super(name);
        this.random = new Random();
        this.difficulty = difficulty;
    }

    /**
     * Führt einen KI-gesteuerten Zug aus.
     * Diese Methode würde mit der Game-Klasse interagieren, um einen vollständigen Zug durchzuführen.
     */
    @Override
    public void takeTurn() {
        // Implementierung erfolgt in der executeComputerTurn-Methode der Game-Klasse
    }

    /**
     * Entscheidet, welche Würfel der Computer halten möchte.
     * Implementiert eine intelligente Strategie basierend auf der Schwierigkeit und den aktuellen Würfeln.
     *
     * @param diceList Die aktuelle Liste der Würfel
     * @return Ein Boolean-Array, das angibt, welche Würfel neu gewürfelt werden sollen (true = würfeln)
     * @throws IllegalArgumentException wenn die Würfelliste null ist oder nicht genau 5 Würfel enthält
     */
    public boolean[] decideDiceToRoll(List<Dice> diceList) {
        if (diceList == null) {
            throw new IllegalArgumentException("Würfelliste darf nicht null sein!");
        }

        if (diceList.size() != 5) {
            throw new IllegalArgumentException("Würfelliste muss genau 5 Würfel enthalten!");
        }

        // Bei einfacher Schwierigkeit manchmal zufällige Entscheidungen treffen
        if (difficulty == Difficulty.EASY && random.nextDouble() < 0.3) {
            return getRandomDiceToRoll();
        }

        boolean[] toRoll = new boolean[diceList.size()];
        int[] values = countDiceValues(diceList);

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

        if (hasKniffelPotential(values)) {
            for (int i = 0; i < diceList.size(); i++) {
                toRoll[i] = diceList.get(i).getValue() != maxValue;
            }
        }
        else if (hasStraightPotential(values)) {
            decideForStraight(diceList, toRoll, values);
        }
        else {
            for (int i = 0; i < diceList.size(); i++) {
                int value = diceList.get(i).getValue();

                if (maxCount >= 3 && value == maxValue) {
                    toRoll[i] = false;
                }
                else if (maxCount == 2 && value == maxValue) {
                    toRoll[i] = false;
                }
                else if ((value == 3 || value == 4) && (values[3] > 0 && values[4] > 0)) {
                    toRoll[i] = false;
                }
                else if (difficulty == Difficulty.HARD && (value == 5 || value == 6)) {
                    toRoll[i] = random.nextDouble() > 0.7;
                }
                else {
                    toRoll[i] = true;
                }
            }
        }

        return toRoll;
    }

    /**
     * Erzeugt ein zufälliges Array für zu würfelnde Würfel.
     * Wird bei einfacher Schwierigkeit verwendet, um die KI weniger vorhersehbar zu machen.
     *
     * @return Ein zufälliges Boolean-Array für zu würfelnde Würfel
     */
    private boolean[] getRandomDiceToRoll() {
        boolean[] toRoll = new boolean[5];
        for (int i = 0; i < 5; i++) {
            toRoll[i] = random.nextBoolean();
        }
        return toRoll;
    }

    /**
     * Zählt die Häufigkeit der verschiedenen Würfelwerte.
     *
     * @param diceList Die Liste der Würfel
     * @return Ein Array mit den Häufigkeiten der Würfelwerte (Index = Würfelwert)
     */
    private int[] countDiceValues(List<Dice> diceList) {
        int[] values = new int[7];
        for (Dice dice : diceList) {
            values[dice.getValue()]++;
        }
        return values;
    }

    /**
     * Prüft, ob die aktuellen Würfel Potenzial für ein Kniffel haben.
     *
     * @param values Array mit Würfelhäufigkeiten
     * @return true wenn Kniffel-Potenzial vorhanden ist, sonst false
     */
    private boolean hasKniffelPotential(int[] values) {
        for (int i = 1; i <= 6; i++) {
            if (values[i] >= 3) {
                return true;
            }
        }
        return false;
    }

    /**
     * Prüft, ob die aktuellen Würfel Potenzial für eine Straße haben.
     *
     * @param values Array mit Würfelhäufigkeiten
     * @return true wenn Straßen-Potenzial vorhanden ist, sonst false
     */
    private boolean hasStraightPotential(int[] values) {
        int sequenceCount = 0;

        for (int i = 1; i <= 6; i++) {
            if (values[i] > 0) {
                sequenceCount++;
            } else {
                sequenceCount = 0;
            }

            if (sequenceCount >= 3) {
                return true;
            }
        }

        // Prüfe auch nicht-zusammenhängende Sequenzen
        int totalNumbers = 0;
        for (int i = 1; i <= 6; i++) {
            if (values[i] > 0) totalNumbers++;
        }

        return totalNumbers >= 3;
    }

    /**
     * Entscheidet, welche Würfel für eine potenzielle Straße gehalten werden sollen.
     *
     * @param diceList Die Liste der Würfel
     * @param toRoll Das zu aktualisierende Array mit den Würfelentscheidungen
     * @param values Array mit Würfelhäufigkeiten
     */
    private void decideForStraight(List<Dice> diceList, boolean[] toRoll, int[] values) {
        // Finde die längste mögliche Sequenz
        List<Integer> sequence = findLongestSequence(values);

        // Markiere Würfel für Behalten oder neu Würfeln
        for (int i = 0; i < diceList.size(); i++) {
            int value = diceList.get(i).getValue();

            // Behalte Würfel, die zur Sequenz gehören
            if (sequence.contains(value)) {
                toRoll[i] = false;

                // Entferne den Wert aus der Sequenz, um Duplikate zu vermeiden
                sequence.remove(Integer.valueOf(value));
            } else {
                toRoll[i] = true;
            }
        }
    }

    /**
     * Findet die längste Sequenz aufeinanderfolgender Zahlen.
     *
     * @param values Array mit Würfelhäufigkeiten
     * @return Eine Liste mit den Werten der längsten Sequenz
     */
    private List<Integer> findLongestSequence(int[] values) {
        List<Integer> longestSequence = new ArrayList<>();
        List<Integer> currentSequence = new ArrayList<>();

        for (int i = 1; i <= 6; i++) {
            if (values[i] > 0) {
                currentSequence.add(i);
            } else if (!currentSequence.isEmpty()) {
                if (currentSequence.size() > longestSequence.size()) {
                    longestSequence = new ArrayList<>(currentSequence);
                }
                currentSequence.clear();
            }
        }

        // Prüfe die letzte Sequenz
        if (currentSequence.size() > longestSequence.size()) {
            longestSequence = currentSequence;
        }

        // Wenn keine Sequenz gefunden wurde, nehme die vorhandenen Zahlen
        if (longestSequence.isEmpty()) {
            for (int i = 1; i <= 6; i++) {
                if (values[i] > 0) {
                    longestSequence.add(i);
                }
            }
        }

        return longestSequence;
    }

    /**
     * Wählt die beste verfügbare Kategorie basierend auf den aktuellen Würfeln.
     * Die Entscheidung berücksichtigt die aktuelle Spielsituation und die Schwierigkeit.
     *
     * @param diceList Die aktuelle Liste der Würfel
     * @return Die gewählte Kategorie
     */
    public Category chooseBestCategory(List<Dice> diceList) {
        // Bei einfacher Schwierigkeit manchmal eine zufällige Kategorie wählen
        if (difficulty == Difficulty.EASY && random.nextDouble() < 0.2) {
            List<Category> availableCategories = Arrays.stream(Category.values())
                .filter(cat -> !isCategoryUsed(cat))
                .collect(Collectors.toList());

            if (!availableCategories.isEmpty()) {
                return availableCategories.get(random.nextInt(availableCategories.size()));
            }
        }

        // Berechne potenzielle Punktzahlen für alle verfügbaren Kategorien
        Map<Category, Integer> potentialScores =
            Arrays.stream(Category.values())
                .filter(cat -> !isCategoryUsed(cat))
                .collect(Collectors.toMap(
                    cat -> cat,
                    cat -> KniffelScorer.calculateScore(cat, diceList)
                ));

        // Erweiterte Strategie: Gewichte bestimmte Kategorien je nach Situation
        if (difficulty != Difficulty.EASY) {
            // Prüfe, ob wir Kniffel haben und gib ihm höhere Priorität
            if (potentialScores.containsKey(Category.KNIFFEL) &&
                potentialScores.get(Category.KNIFFEL) == KNIFFEL_SCORE) {
                return Category.KNIFFEL;
            }

            // Wenn oberer Bereich Bonus noch nicht erreicht, priorisiere oberen Bereich
            if (!hasUpperSectionBonus() && getUpperSectionScore() < UPPER_SECTION_BONUS_THRESHOLD) {
                // Berechne, wie viele Punkte noch für den Bonus fehlen
                int missingForBonus = UPPER_SECTION_BONUS_THRESHOLD - getUpperSectionScore();

                // Wenn wir nah am Bonus sind, priorisiere oberen Bereich
                if (missingForBonus < 20) {
                    // Finde die beste obere Kategorie
                    return potentialScores.entrySet().stream()
                        .filter(entry -> isUpperSectionCategory(entry.getKey()))
                        .filter(entry -> entry.getValue() > 0)
                        .max(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey)
                        .orElseGet(() -> {
                            // Wenn keine gute obere Kategorie gefunden wurde, finde die beste Kategorie insgesamt
                            return potentialScores.entrySet().stream()
                                .max(Map.Entry.comparingByValue())
                                .map(Map.Entry::getKey)
                                .orElse(Category.CHANCE);
                        });
                }
            }
        }

        // Finde die Kategorie mit der höchsten Punktzahl
        return potentialScores.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(Category.CHANCE); // Fallback auf CHANCE, sollte aber nie vorkommen
    }

    /**
     * Prüft, ob eine Kategorie zum oberen Bereich gehört (ONES bis SIXES).
     *
     * @param category Die zu prüfende Kategorie
     * @return true wenn die Kategorie zum oberen Bereich gehört, sonst false
     */
    private boolean isUpperSectionCategory(Category category) {
        return category == Category.ONES || category == Category.TWOS ||
               category == Category.THREES || category == Category.FOURS ||
               category == Category.FIVES || category == Category.SIXES;
    }
}
