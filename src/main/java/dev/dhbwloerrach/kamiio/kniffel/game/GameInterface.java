package dev.dhbwloerrach.kamiio.kniffel.game;

/**
 * Interface für die Spiellogik von Kniffel.
 * Definiert die grundlegenden Methoden, die jede Implementierung eines Kniffel-Spiels
 * bereitstellen muss, um den Spielablauf zu steuern.
 */
public interface GameInterface {

    /**
     * Startet ein neues Spiel.
     * Setzt alle Spieler zurück, initialisiert die Würfel und bereitet die erste Spielrunde vor.
     */
    void startGame();

    /**
     * Wechselt zum nächsten Spieler und bereitet dessen Zug vor.
     * Prüft außerdem, ob das Spiel beendet ist.
     */
    void nextTurn();

    /**
     * Prüft, ob das Spiel beendet ist.
     *
     * @return true wenn das Spiel beendet ist, sonst false
     */
    boolean isGameOver();

    /**
     * Gibt den aktuellen Spieler zurück.
     *
     * @return Der aktuell aktive Spieler
     */
    Player getCurrentPlayer();
}
