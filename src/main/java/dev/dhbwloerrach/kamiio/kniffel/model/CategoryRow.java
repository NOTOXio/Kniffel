package dev.dhbwloerrach.kamiio.kniffel.model;

/**
 * Datenklasse für eine Zeile in der Kategorie-Tabelle.
 * Enthält Informationen über eine Kategorie und deren Werte für beide Spieler.
 */
public class CategoryRow {
    private final String category;
    private final String status;
    private final Integer value;
    private final Integer player1Score;
    private final Integer player2Score;

    /**
     * Erstellt eine neue CategoryRow mit den angegebenen Werten.
     *
     * @param category     Die Kategorie
     * @param status       Der Status (z.B. "Offen", "Eingelöst")
     * @param value        Der aktuelle berechnete Wert
     * @param player1Score Der Punktestand von Spieler 1 für diese Kategorie
     * @param player2Score Der Punktestand von Spieler 2 für diese Kategorie
     */
    public CategoryRow(String category, String status, Integer value, Integer player1Score, Integer player2Score) {
        this.category = category;
        this.status = status;
        this.value = value;
        this.player1Score = player1Score;
        this.player2Score = player2Score;
    }

    /**
     * Gibt den Kategorienamen zurück.
     *
     * @return Der Kategoriename
     */
    public String getCategory() {
        return category;
    }

    /**
     * Gibt den Status der Kategorie zurück.
     *
     * @return Der Status (z.B. "Offen", "Eingelöst")
     */
    public String getStatus() {
        return status;
    }

    /**
     * Gibt den aktuellen berechneten Wert zurück.
     *
     * @return Der berechnete Wert oder null, wenn kein Wert verfügbar ist
     */
    public Integer getValue() {
        return value;
    }

    /**
     * Gibt die Punktzahl von Spieler 1 für diese Kategorie zurück.
     *
     * @return Die Punktzahl oder null, wenn die Kategorie nicht verwendet wurde
     */
    public Integer getPlayer1Score() {
        return player1Score;
    }

    /**
     * Gibt die Punktzahl von Spieler 2 für diese Kategorie zurück.
     *
     * @return Die Punktzahl oder null, wenn die Kategorie nicht verwendet wurde
     */
    public Integer getPlayer2Score() {
        return player2Score;
    }
}
