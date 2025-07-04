package dev.dhbwloerrach.kamiio.kniffel.game;

/**
 * Enum für die Kniffel-Kategorien.
 * Definiert alle möglichen Kategorien im Kniffel-Spiel mit ihren Namen
 * und zugehörigen Beschreibungen.
 */
public enum Category {
    // Oberer Bereich (Zahlen)
    ONES("Einser", "Summe aller Einsen"),
    TWOS("Zweier", "Summe aller Zweien"),
    THREES("Dreier", "Summe aller Dreien"),
    FOURS("Vierer", "Summe aller Vieren"),
    FIVES("Fünfer", "Summe aller Fünfen"),
    SIXES("Sechser", "Summe aller Sechsen"),

    // Unterer Bereich (Figuren)
    THREE_OF_A_KIND("Dreierpasch", "Mindestens drei gleiche Würfel, Summe aller Würfel"),
    FOUR_OF_A_KIND("Viererpasch", "Mindestens vier gleiche Würfel, Summe aller Würfel"),
    FULL_HOUSE("Full House", "Drei gleiche und zwei gleiche Würfel, 25 Punkte"),
    SMALL_STRAIGHT("Kleine Straße", "Vier aufeinanderfolgende Zahlen, 30 Punkte"),
    LARGE_STRAIGHT("Große Straße", "Fünf aufeinanderfolgende Zahlen, 40 Punkte"),
    KNIFFEL("Kniffel", "Fünf gleiche Würfel, 50 Punkte"),
    CHANCE("Chance", "Summe aller Würfel");

    private final String displayName;
    private final String description;

    /**
     * Konstruktor für eine Kategorie.
     *
     * @param displayName Der anzuzeigende Name der Kategorie
     * @param description Die Beschreibung der Kategorie
     */
    Category(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * Gibt den anzuzeigenden Namen der Kategorie zurück.
     *
     * @return Der Anzeigetext für die Kategorie
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gibt die Beschreibung der Kategorie zurück.
     *
     * @return Die Beschreibung der Kategorie
     */
    public String getDescription() {
        return description;
    }

    /**
     * Überschreibt die toString-Methode, um den Anzeigetext zurückzugeben.
     *
     * @return Der Anzeigetext für die Kategorie
     */
    @Override
    public String toString() {
        return displayName;
    }
}
