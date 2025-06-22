package dev.dhbwloerrach.kamiio.kniffel.game;

import dev.dhbwloerrach.kamiio.kniffel.Dice;
import java.util.ArrayList;
import java.util.List;

/**
 * Zentrale Spiellogik für Kniffel.
 */
public class Game implements GameInterface {
    private List<Player> players;
    private int currentPlayerIndex;
    private boolean gameOver;
    private List<Dice> diceList;
    private int rollsLeft;
    public static final int MAX_ROLLS = 3;
    public static final int DICE_COUNT = 5;

    public Game(List<Player> players) {
        this.players = players;
        this.currentPlayerIndex = 0;
        this.gameOver = false;
        this.diceList = new ArrayList<>();
        for (int i = 0; i < DICE_COUNT; i++) {
            diceList.add(new Dice());
        }
        this.rollsLeft = MAX_ROLLS;
    }

    @Override
    public void startGame() {
        // Startlogik, z.B. Spielerreihenfolge festlegen
        currentPlayerIndex = 0;
        gameOver = false;
        for (Player p : players) p.reset();
        resetDice();
    }

    @Override
    public void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        resetDice();
        if (players.stream().allMatch(Player::allCategoriesUsed)) {
            gameOver = true;
        }
    }

    @Override
    public boolean isGameOver() {
        return gameOver;
    }

    @Override
    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public List<Dice> getDiceList() {
        return diceList;
    }

    public int getRollsLeft() {
        return rollsLeft;
    }

    public void rollDice(boolean[] held) {
        if (rollsLeft > 0) {
            for (int i = 0; i < DICE_COUNT; i++) {
                diceList.get(i).setHeld(held[i]);
                diceList.get(i).roll();
            }
            rollsLeft--;
        }
    }

    public void resetDice() {
        for (Dice d : diceList) d.setHeld(false);
        rollsLeft = MAX_ROLLS;
        for (Dice d : diceList) d.roll();
    }
}
