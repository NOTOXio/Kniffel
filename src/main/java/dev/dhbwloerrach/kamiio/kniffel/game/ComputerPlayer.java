package dev.dhbwloerrach.kamiio.kniffel.game;

/**
 * Spieler, der vom Computer gesteuert wird.
 */
public class ComputerPlayer extends Player {
    public ComputerPlayer(String name) {
        super(name);
    }

    @Override
    public void takeTurn() {
        // Logik für Computerzug (z.B. zufällige Auswahl, KI)
    }
}
