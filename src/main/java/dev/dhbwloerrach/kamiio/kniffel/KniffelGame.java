package dev.dhbwloerrach.kamiio.kniffel;

import java.util.ArrayList;
import java.util.List;

public class KniffelGame {
    private List<Dice> diceList;
    private Player player;
    private int rollsLeft;
    public static final int MAX_ROLLS = 3;
    public static final int DICE_COUNT = 5;

    public KniffelGame(String playerName) {
        this.player = new Player(playerName);
        this.diceList = new ArrayList<>();
        for (int i = 0; i < DICE_COUNT; i++) {
            diceList.add(new Dice());
        }
        this.rollsLeft = MAX_ROLLS;
    }

    public void rollDice() {
        if (rollsLeft > 0) {
            for (Dice dice : diceList) {
                dice.roll();
            }
            rollsLeft--;
        }
    }

    public List<Dice> getDiceList() {
        return diceList;
    }

    public int getRollsLeft() {
        return rollsLeft;
    }

    public Player getPlayer() {
        return player;
    }

    public void resetRolls() {
        this.rollsLeft = MAX_ROLLS;
        for (Dice dice : diceList) {
            dice.setHeld(false);
        }
    }
}
