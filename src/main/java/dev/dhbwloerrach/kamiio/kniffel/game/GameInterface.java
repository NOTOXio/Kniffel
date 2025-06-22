package dev.dhbwloerrach.kamiio.kniffel.game;

/**
 * Interface für die Spiellogik von Kniffel.
 */
public interface GameInterface {
    void startGame();
    void nextTurn();
    boolean isGameOver();
    Player getCurrentPlayer();
}
