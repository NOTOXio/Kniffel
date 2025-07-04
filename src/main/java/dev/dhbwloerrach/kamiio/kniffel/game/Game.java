package dev.dhbwloerrach.kamiio.kniffel.game;

import java.util.ArrayList;
import java.util.List;

import dev.dhbwloerrach.kamiio.kniffel.Dice;
import dev.dhbwloerrach.kamiio.kniffel.utils.KniffelScorer;

/**
 * Zentrale Spiellogik für Kniffel.
 * Diese Klasse verwaltet den gesamten Spielablauf, einschließlich Spielerwechsel,
 * Würfelvorgänge und die Verwaltung des Spielzustands.
 */
public class Game implements GameInterface {
    private final List<Player> players;
    private int currentPlayerIndex;
    private boolean gameOver;
    private final List<Dice> diceList;
    private int rollsLeft;
    public static final int MAX_ROLLS = 3;
    public static final int DICE_COUNT = 5;

    /**
     * Erstellt ein neues Kniffel-Spiel mit den angegebenen Spielern.
     * Initialisiert alle nötigen Spielkomponenten wie Würfel und Spielzustand.
     *
     * @param players Liste der teilnehmenden Spieler
     */
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

    /**
     * Startet ein neues Spiel.
     * Setzt alle Spieler zurück, initialisiert die Würfel
     * und bereitet die erste Spielrunde vor.
     */
    @Override
    public void startGame() {
        // Startlogik, z.B. Spielerreihenfolge festlegen
        currentPlayerIndex = 0;
        gameOver = false;
        for (Player p : players) p.reset();
        resetDice();
    }

    /**
     * Wechselt zum nächsten Spieler und bereitet dessen Zug vor.
     * Prüft, ob das Spiel beendet ist und führt bei Computer-Spielern
     * automatisch den Zug aus.
     */
    @Override
    public void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        resetDice();

        // Prüfe, ob das Spiel beendet ist
        if (players.stream().allMatch(Player::allCategoriesUsed)) {
            gameOver = true;
        }

        // Falls der nächste Spieler ein ComputerPlayer ist und das Spiel noch nicht beendet ist,
        // führe den Computer-Zug automatisch aus
        if (!gameOver && getCurrentPlayer() instanceof ComputerPlayer) {
            executeComputerTurn();
        }
    }

    /**
     * Führt einen vollständigen Zug für den Computer-Spieler aus.
     */
    private void executeComputerTurn() {
        ComputerPlayer computerPlayer = (ComputerPlayer) getCurrentPlayer();

        // 1. Erster Würfelwurf
        boolean[] allDice = new boolean[DICE_COUNT];
        for (int i = 0; i < DICE_COUNT; i++) allDice[i] = true;
        rollDice(allDice);

        // 2. Entscheide, welche Würfel behalten werden sollen (für bis zu 2 weitere Würfe)
        for (int i = 0; i < 2; i++) {
            if (rollsLeft > 0) {
                boolean[] toRoll = computerPlayer.decideDiceToRoll(diceList);
                rollDice(toRoll);
            }
        }

        // 3. Wähle die beste Kategorie und trage Punkte ein
        Category bestCategory = computerPlayer.chooseBestCategory(diceList);
        int points = KniffelScorer.calculateScore(bestCategory, diceList);
        computerPlayer.addScore(bestCategory, points);

        // 4. Zug zum nächsten Spieler wechseln ohne rekursiven Aufruf
        // Wir wechseln nur den Spieler und setzen die Würfel zurück, ohne nextTurn() zu verwenden
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        resetDice();

        // Prüfe, ob das Spiel beendet ist
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

    /**
     * Gibt den Spieler zurück, der die meisten Punkte hat (Gewinner).
     *
     * @return Der Spieler mit den meisten Punkten
     */
    public Player getWinner() {
        return players.stream()
            .max((a, b) -> Integer.compare(a.getScore(), b.getScore()))
            .orElse(null);
    }

    public List<Dice> getDiceList() {
        return diceList;
    }

    public int getRollsLeft() {
        return rollsLeft;
    }

    /**
     * Führt einen Würfelvorgang mit den angegebenen Würfeln durch.
     * Nur nicht festgehaltene Würfel werden neu gewürfelt.
     *
     * @param toRoll Array, das angibt, welche Würfel neu gewürfelt werden sollen (true = würfeln)
     */
    public void rollDice(boolean[] toRoll) {
        if (rollsLeft > 0) {
            for (int i = 0; i < DICE_COUNT; i++) {
                if (toRoll[i]) {
                    diceList.get(i).roll();
                }
            }
            rollsLeft--;
        }
    }

    /**
     * Setzt alle Würfel zurück und stellt die maximale Anzahl an Würfen wieder her.
     * Wird beim Spielerwechsel und zu Beginn eines neuen Spiels aufgerufen.
     */
    public void resetDice() {
        for (Dice d : diceList) d.setHeld(false);
        rollsLeft = MAX_ROLLS;
    }

    /**
     * Fügt einen neuen Spieler zum Spiel hinzu.
     *
     * @param player Der hinzuzufügende Spieler
     */
    public void addPlayer(Player player) {
        players.add(player);
    }

    /**
     * Gibt die Liste aller Spieler zurück.
     *
     * @return Liste aller Spieler
     */
    public List<Player> getPlayers() {
        return players;
    }
}
