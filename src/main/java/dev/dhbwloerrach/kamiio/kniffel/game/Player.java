package dev.dhbwloerrach.kamiio.kniffel.game;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 * Abstrakte Spieler-Klasse für Kniffel.
 * Diese Klasse repräsentiert einen Spieler im Kniffel-Spiel und verwaltet seinen Namen,
 * Punktestand und die genutzten Kategorien. Sie wird von konkreten Spielertypen erweitert.
 */
public abstract class Player {
    // Felder mit angemessener Zugriffssteuerung
    private final String name;
    private int score;
    private final Map<Category, Integer> categoryScores;
    private final Map<Category, Boolean> usedCategories;
    private boolean isUpperSectionBonusAdded;

    // Konstanten für die Verwaltung des oberen und unteren Bereichs
    public static final int UPPER_SECTION_BONUS_THRESHOLD = 63;
    public static final int UPPER_SECTION_BONUS = 35;

    /**
     * Erstellt einen neuen Spieler mit dem angegebenen Namen.
     *
     * @param name Der Name des Spielers
     * @throws IllegalArgumentException wenn der Name null oder leer ist
     */
    public Player(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Spielername darf nicht leer sein!");
        }

        this.name = name;
        this.score = 0;
        this.categoryScores = new EnumMap<>(Category.class);
        this.usedCategories = new EnumMap<>(Category.class);

        // Initialisiere alle Kategorien
        for (Category cat : Category.values()) {
            usedCategories.put(cat, false);
            categoryScores.put(cat, 0);
        }
    }

    /**
     * Gibt den Namen des Spielers zurück.
     *
     * @return Der Name des Spielers
     */
    public String getName() {
        return name;
    }

    /**
     * Gibt die Gesamtpunktzahl des Spielers zurück.
     *
     * @return Die Gesamtpunktzahl
     */
    public int getScore() {
        return score;
    }

    /**
     * Fügt Punkte für eine bestimmte Kategorie hinzu und markiert sie als verwendet.
     * Aktualisiert den Gesamtscore des Spielers.
     *
     * @param category Die Kategorie, für die Punkte hinzugefügt werden sollen
     * @param points Die Anzahl der hinzuzufügenden Punkte
     * @throws IllegalArgumentException wenn die Kategorie bereits verwendet wurde
     */
    public void addScore(Category category, int points) {
        if (points < 0) {
            throw new IllegalArgumentException("Punktzahl darf nicht negativ sein!");
        }

        if (isCategoryUsed(category)) {
            throw new IllegalArgumentException("Kategorie " + category.getDisplayName() + " wurde bereits verwendet!");
        }

        this.score += points;
        categoryScores.put(category, points);
        usedCategories.put(category, true);

        // Prüfe auf Bonus im oberen Bereich
        checkAndAddUpperSectionBonus();
    }

    /**
     * Prüft, ob eine Kategorie bereits verwendet wurde.
     *
     * @param category Die zu prüfende Kategorie
     * @return true wenn die Kategorie bereits verwendet wurde, sonst false
     */
    public boolean isCategoryUsed(Category category) {
        return usedCategories.getOrDefault(category, false);
    }

    /**
     * Gibt die Punktzahl für eine bestimmte Kategorie zurück.
     *
     * @param category Die Kategorie, deren Punktzahl zurückgegeben werden soll
     * @return Die Punktzahl für die angegebene Kategorie
     */
    public int getCategoryScore(Category category) {
        return categoryScores.getOrDefault(category, 0);
    }

    /**
     * Gibt eine unveränderliche Kopie der Kategorie-Punkte-Map zurück.
     *
     * @return Eine unveränderliche Map der Kategorie-Punkte
     */
    public Map<Category, Integer> getCategoryScores() {
        return Collections.unmodifiableMap(categoryScores);
    }

    /**
     * Gibt eine unveränderliche Kopie der verwendeten Kategorien-Map zurück.
     *
     * @return Eine unveränderliche Map der verwendeten Kategorien
     */
    public Map<Category, Boolean> getUsedCategories() {
        return Collections.unmodifiableMap(usedCategories);
    }

    /**
     * Prüft, ob alle Kategorien verwendet wurden.
     *
     * @return true wenn alle Kategorien verwendet wurden, sonst false
     */
    public boolean allCategoriesUsed() {
        return usedCategories.values().stream().allMatch(Boolean::booleanValue);
    }

    /**
     * Setzt den Spieler zurück (alle Punkte und verwendeten Kategorien).
     */
    public void reset() {
        score = 0;
        for (Category cat : Category.values()) {
            usedCategories.put(cat, false);
            categoryScores.put(cat, 0);
        }
    }

    /**
     * Berechnet die Summe der Punkte im oberen Bereich (ONES bis SIXES).
     *
     * @return Die Summe der Punkte im oberen Bereich
     */
    public int getUpperSectionScore() {
        int sum = 0;
        sum += getCategoryScore(Category.ONES);
        sum += getCategoryScore(Category.TWOS);
        sum += getCategoryScore(Category.THREES);
        sum += getCategoryScore(Category.FOURS);
        sum += getCategoryScore(Category.FIVES);
        sum += getCategoryScore(Category.SIXES);
        return sum;
    }

    /**
     * Prüft, ob der Spieler den Bonus für den oberen Bereich erhalten hat.
     *
     * @return true wenn der Spieler den Bonus erhalten hat, sonst false
     */
    public boolean hasUpperSectionBonus() {
        return getUpperSectionScore() >= UPPER_SECTION_BONUS_THRESHOLD;
    }

    /**
     * Prüft und fügt bei Bedarf den Bonus für den oberen Bereich hinzu.
     * Dies geschieht automatisch, wenn die Summe des oberen Bereichs den Schwellenwert überschreitet.
     */
    private void checkAndAddUpperSectionBonus() {
        if (hasUpperSectionBonus()) {
            // Da wir keinen expliziten Bonus-Eintrag haben, wird der Bonus
            // automatisch zur Gesamtpunktzahl addiert, wenn der Schwellenwert
            // überschritten wird
            if (!isUpperSectionBonusAdded) {
                score += UPPER_SECTION_BONUS;
                isUpperSectionBonusAdded = true;
            }
        }
    }

    /**
     * Spieler führt seinen Zug aus.
     * Diese abstrakte Methode muss von konkreten Unterklassen implementiert werden.
     */
    public abstract void takeTurn();

    /**
     * Gibt eine String-Repräsentation des Spielers zurück.
     *
     * @return Eine String-Repräsentation des Spielers
     */
    @Override
    public String toString() {
        return "Player[name=" + name + ", score=" + score + "]";
    }
}
