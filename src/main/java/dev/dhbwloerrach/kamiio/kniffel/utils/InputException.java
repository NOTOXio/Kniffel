package dev.dhbwloerrach.kamiio.kniffel.utils;

/**
 * Exception für fehlerhafte Benutzereingaben oder ungültige Zustände im Spiel.
 * Diese Exception wird geworfen, wenn eine Eingabe nicht den erwarteten Regeln entspricht
 * oder wenn ein ungültiger Spielzustand erreicht wird.
 */
public class InputException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Erstellt eine neue InputException mit der angegebenen Fehlermeldung.
     *
     * @param message Die Fehlermeldung
     */
    public InputException(String message) {
        super(message);
    }

    /**
     * Erstellt eine neue InputException mit der angegebenen Fehlermeldung und Ursache.
     *
     * @param message Die Fehlermeldung
     * @param cause Die Ursache der Exception
     */
    public InputException(String message, Throwable cause) {
        super(message, cause);
    }
}
