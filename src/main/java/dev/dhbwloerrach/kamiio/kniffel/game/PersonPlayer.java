package dev.dhbwloerrach.kamiio.kniffel.game;

/**
 * Spieler, der von einer echten Person gesteuert wird.
 */
public class PersonPlayer extends Player {
    public PersonPlayer(String name) {
        super(name);
    }

    @Override
    public void takeTurn() {
        // Logik für menschlichen Spieler (z.B. UI-Interaktion)
    }
}
