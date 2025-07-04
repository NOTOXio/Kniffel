package dev.dhbwloerrach.kamiio.kniffel.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dev.dhbwloerrach.kamiio.kniffel.Dice;
import dev.dhbwloerrach.kamiio.kniffel.utils.KniffelScorer;

/**
 * Zentrale Spiellogik für Kniffel.
 * Diese Klasse verwaltet den gesamten Spielablauf, einschließlich Spielerwechsel,
 * Würfelvorgänge und die Verwaltung des Spielzustands.
 *
 * Implementiert das Singleton-Pattern, um sicherzustellen, dass nur eine Instanz
 * des Spiels existiert und global zugänglich ist.
 */
public class Game implements GameInterface {
    // Singleton-Instanz
    private static Game instance;

    private List<Player> players;
    private int currentPlayerIndex;
    private boolean gameOver;
    private final List<Dice> diceList;
    private int rollsLeft;
    public static final int MAX_ROLLS = 3;
    public static final int DICE_COUNT = 5;

    /**
     * Private Konstruktor für Singleton-Pattern.
     * Verhindert direkte Instanziierung von außen.
     *
     * @param players Liste der teilnehmenden Spieler
     */
    private Game(List<Player> players) {
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
     * Gibt die einzige Instanz der Game-Klasse zurück oder erstellt sie,
     * falls sie noch nicht existiert.
     *
     * @param players Liste der teilnehmenden Spieler
     * @return Die einzige Instanz der Game-Klasse
     */
    public static synchronized Game getInstance(List<Player> players) {
        if (instance == null) {
            instance = new Game(players);
        } else {
            instance.players = players;
            instance.reset();
        }
        return instance;
    }

    /**
     * Setzt das Spiel zurück.
     */
    private void reset() {
        this.currentPlayerIndex = 0;
        this.gameOver = false;
        this.diceList.clear();
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
        currentPlayerIndex = 0;
        gameOver = false;
        for (Player player : players) {
            player.reset();
        }
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
            return;
        }

        // Falls der nächste Spieler ein ComputerPlayer ist und das Spiel noch nicht beendet ist,
        // führe den Computer-Zug automatisch aus
        if (getCurrentPlayer() instanceof ComputerPlayer) {
            executeComputerTurn();
        }
    }

    /**
     * Führt einen vollständigen Zug für den Computer-Spieler aus.
     * Implementiert die KI-Logik für Computer-gesteuerte Spieler.
     */
    private void executeComputerTurn() {
        if (!(getCurrentPlayer() instanceof ComputerPlayer)) {
            return;
        }

        ComputerPlayer computerPlayer = (ComputerPlayer) getCurrentPlayer();

        // 1. Erster Würfelwurf
        boolean[] allDice = new boolean[DICE_COUNT];
        int i = 0;
        do {
            allDice[i] = true;
            i++;
        } while (i < DICE_COUNT);

        rollDice(allDice);

        // 2. Entscheide, welche Würfel behalten werden sollen (für bis zu 2 weitere Würfe)
        int remainingRolls = 2;
        while (remainingRolls > 0 && rollsLeft > 0) {
            boolean[] toRoll = computerPlayer.decideDiceToRoll(diceList);
            rollDice(toRoll);
            remainingRolls--;
        }

        // 3. Wähle die beste Kategorie und trage Punkte ein
        Category bestCategory = computerPlayer.chooseBestCategory(diceList);
        int points = KniffelScorer.calculateScore(bestCategory, diceList);
        computerPlayer.addScore(bestCategory, points);

        // 4. Zug zum nächsten Spieler wechseln
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        resetDice();

        // Prüfe, ob das Spiel beendet ist
        gameOver = players.stream().allMatch(Player::allCategoriesUsed);
    }

    /**
     * Überprüft, ob das Spiel beendet ist.
     *
     * @return true, wenn das Spiel beendet ist, sonst false
     */
    @Override
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * Liefert den aktuellen Spieler zurück.
     *
     * @return Der aktuell aktive Spieler
     * @throws IndexOutOfBoundsException wenn kein gültiger Spieler vorhanden ist
     */
    @Override
    public Player getCurrentPlayer() {
        if (players.isEmpty()) {
            throw new IndexOutOfBoundsException("Keine Spieler vorhanden!");
        }
        return players.get(currentPlayerIndex);
    }

    /**
     * Gibt den Spieler zurück, der die meisten Punkte hat (Gewinner).
     *
     * @return Der Spieler mit den meisten Punkten oder null, wenn keine Spieler vorhanden sind
     */
    public Player getWinner() {
        return players.stream()
            .max((a, b) -> Integer.compare(a.getScore(), b.getScore()))
            .orElse(null);
    }

    /**
     * Gibt die Liste aller Würfel zurück.
     *
     * @return Die Liste der Würfel im aktuellen Spiel
     */
    public List<Dice> getDiceList() {
        return diceList;
    }

    /**
     * Gibt die Anzahl der verbleibenden Würfe zurück.
     *
     * @return Die Anzahl der verbleibenden Würfe im aktuellen Zug
     */
    public int getRollsLeft() {
        return rollsLeft;
    }

    /**
     * Führt einen Würfelvorgang mit den angegebenen Würfeln durch.
     * Nur nicht festgehaltene Würfel werden neu gewürfelt.
     *
     * @param toRoll Array, das angibt, welche Würfel neu gewürfelt werden sollen (true = würfeln)
     * @throws IllegalArgumentException wenn das Array die falsche Länge hat
     * @throws IllegalStateException wenn keine Würfe mehr übrig sind
     */
    public void rollDice(boolean[] toRoll) {
        if (toRoll.length != DICE_COUNT) {
            throw new IllegalArgumentException("Array muss genau " + DICE_COUNT + " Elemente haben!");
        }

        if (rollsLeft <= 0) {
            throw new IllegalStateException("Keine Würfe mehr übrig!");
        }

        for (int i = 0; i < DICE_COUNT; i++) {
            if (toRoll[i]) {
                diceList.get(i).roll();
            }
        }
        rollsLeft--;
    }

    /**
     * Setzt alle Würfel zurück und stellt die maximale Anzahl an Würfen wieder her.
     * Wird beim Spielerwechsel und zu Beginn eines neuen Spiels aufgerufen.
     */
    public void resetDice() {
        for (Dice d : diceList) {
            d.setHeld(false);
        }
        rollsLeft = MAX_ROLLS;
    }

    /**
     * Fügt einen neuen Spieler zum Spiel hinzu.
     *
     * @param player Der hinzuzufügende Spieler
     * @throws IllegalArgumentException wenn der Spieler null ist
     */
    public void addPlayer(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Spieler darf nicht null sein!");
        }
        players.add(player);
    }

    /**
     * Gibt die Liste aller Spieler zurück.
     *
     * @return Eine nicht veränderbare Kopie der Spielerliste
     */
    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }
}
