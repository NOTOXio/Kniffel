package dev.dhbwloerrach.kamiio.kniffel.game;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Testet die Spiellogik der Game-Klasse.
 */
class GameTest {

    private Game game;
    private PersonPlayer player1;
    private PersonPlayer player2;

    @BeforeEach
    void setUp() {
        player1 = new PersonPlayer("Spieler1");
        player2 = new PersonPlayer("Spieler2");
        List<Player> players = Arrays.asList(player1, player2);
        game = Game.getInstance(players);
    }

    @Test
    @DisplayName("Game sollte korrekt initialisiert werden")
    void testGameInitialization() {
        // Prüfe, ob die Spieler korrekt gesetzt wurden
        assertEquals(2, game.getPlayers().size(), "Sollte 2 Spieler haben");
        assertEquals(player1, game.getPlayers().get(0), "Erster Spieler sollte player1 sein");
        assertEquals(player2, game.getPlayers().get(1), "Zweiter Spieler sollte player2 sein");

        // Prüfe Initialisierung der Würfel
        assertEquals(5, game.getDiceList().size(), "Sollte 5 Würfel haben");
    }

    @Test
    @DisplayName("startGame sollte das Spiel korrekt initialisieren")
    void testStartGame() {
        game.startGame();

        // Prüfe, ob alle Spieler zurückgesetzt wurden
        assertEquals(0, player1.getScore(), "Spieler1 sollte 0 Punkte haben");
        assertEquals(0, player2.getScore(), "Spieler2 sollte 0 Punkte haben");

        // Prüfe, ob Würfelzähler zurückgesetzt wurde
        assertEquals(3, game.getRollsLeft(), "Sollte 3 Würfelversuche haben");

        // Prüfe, ob das Spiel nicht beendet ist
        assertFalse(game.isGameOver(), "Spiel sollte nicht beendet sein");

        // Prüfe, ob der erste Spieler dran ist
        assertEquals(player1, game.getCurrentPlayer(), "Spieler1 sollte als erster dran sein");
    }

    @Test
    @DisplayName("rollDice sollte die Würfel korrekt würfeln")
    void testRollDice() {
        game.startGame();

        // Merke die aktuellen Würfelwerte
        List<Integer> valuesBefore = game.getDiceList().stream()
                .map(dice -> dice.getValue())
                .collect(Collectors.toList());

        // Alle Würfel neu würfeln
        boolean[] toRoll = {true, true, true, true, true};
        game.rollDice(toRoll);

        // Prüfe, ob die Anzahl der verbleibenden Würfelversuche reduziert wurde
        assertEquals(2, game.getRollsLeft(), "Sollte 2 Würfelversuche übrig haben");

        // Würfeln sollte mindestens einen Würfelwert geändert haben
        // (theoretisch könnten alle gleich bleiben, aber unwahrscheinlich)
        List<Integer> valuesAfter = game.getDiceList().stream()
                .map(dice -> dice.getValue())
                .collect(Collectors.toList());

        boolean atLeastOneChanged = false;
        for (int i = 0; i < valuesBefore.size(); i++) {
            if (!valuesBefore.get(i).equals(valuesAfter.get(i))) {
                atLeastOneChanged = true;
                break;
            }
        }

        assertTrue(atLeastOneChanged, "Mindestens ein Würfel sollte einen anderen Wert haben");
    }

    @Test
    @DisplayName("nextTurn sollte korrekt zum nächsten Spieler wechseln")
    void testNextTurn() {
        game.startGame();

        // Erster Spieler ist player1
        assertEquals(player1, game.getCurrentPlayer(), "Erster Spieler sollte player1 sein");

        // Zum nächsten Spieler wechseln
        game.nextTurn();

        // Zweiter Spieler sollte player2 sein
        assertEquals(player2, game.getCurrentPlayer(), "Zweiter Spieler sollte player2 sein");

        // Nochmal wechseln sollte zurück zu player1 gehen
        game.nextTurn();
        assertEquals(player1, game.getCurrentPlayer(), "Dritter Spieler sollte wieder player1 sein");
    }

    @Test
    @DisplayName("isGameOver sollte korrekt erkennen, wenn das Spiel beendet ist")
    void testIsGameOver() {
        game.startGame();

        // Am Anfang ist das Spiel nicht beendet
        assertFalse(game.isGameOver(), "Spiel sollte nicht beendet sein");

        // Alle Kategorien für beide Spieler verwenden
        for (Category cat : Category.values()) {
            player1.addScore(cat, 1);
            player2.addScore(cat, 1);
        }

        // Nexturn aufrufen, damit die gameOver-Flagge aktualisiert wird
        game.nextTurn();

        // Jetzt sollte das Spiel beendet sein
        assertTrue(game.isGameOver(), "Spiel sollte beendet sein");
    }

    @Test
    @DisplayName("getWinner sollte den Spieler mit der höchsten Punktzahl zurückgeben")
    void testGetWinner() {
        game.startGame();

        // Spieler 1 erhält mehr Punkte
        player1.addScore(Category.SIXES, 30);
        player2.addScore(Category.ONES, 5);

        assertEquals(player1, game.getWinner(), "Spieler1 sollte Gewinner sein");

        // Zurücksetzen
        setUp();
        game.startGame();

        // Spieler 2 erhält mehr Punkte
        player1.addScore(Category.TWOS, 8);
        player2.addScore(Category.KNIFFEL, 50);

        assertEquals(player2, game.getWinner(), "Spieler2 sollte Gewinner sein");

        // Test bei Gleichstand (der erste Spieler sollte gewinnen)
        setUp();
        game.startGame();

        player1.addScore(Category.THREES, 12);
        player2.addScore(Category.THREES, 12);

        assertEquals(player1, game.getWinner(), "Bei Gleichstand sollte der erste Spieler gewinnen");
    }
}
