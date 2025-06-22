package dev.dhbwloerrach.kamiio.kniffel.game;

import java.util.EnumMap;
import java.util.Map;

/**
 * Abstrakte Spieler-Klasse für Kniffel.
 */
public abstract class Player {
    protected String name;
    protected int score;
    protected Map<Category, Integer> categoryScores = new EnumMap<>(Category.class);
    protected Map<Category, Boolean> usedCategories = new EnumMap<>(Category.class);

    public Player(String name) {
        this.name = name;
        this.score = 0;
        for (Category cat : Category.values()) {
            usedCategories.put(cat, false);
            categoryScores.put(cat, 0);
        }
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public void addScore(Category category, int points) {
        this.score += points;
        categoryScores.put(category, points);
        usedCategories.put(category, true);
    }

    public boolean isCategoryUsed(Category category) {
        return usedCategories.getOrDefault(category, false);
    }

    public int getCategoryScore(Category category) {
        return categoryScores.getOrDefault(category, 0);
    }

    public Map<Category, Integer> getCategoryScores() {
        return categoryScores;
    }

    public Map<Category, Boolean> getUsedCategories() {
        return usedCategories;
    }

    public boolean allCategoriesUsed() {
        return usedCategories.values().stream().allMatch(Boolean::booleanValue);
    }

    public void reset() {
        score = 0;
        for (Category cat : Category.values()) {
            usedCategories.put(cat, false);
            categoryScores.put(cat, 0);
        }
    }

    /**
     * Spieler führt seinen Zug aus.
     */
    public abstract void takeTurn();
}
