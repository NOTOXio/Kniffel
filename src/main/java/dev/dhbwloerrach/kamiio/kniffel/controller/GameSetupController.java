package dev.dhbwloerrach.kamiio.kniffel.controller;

import java.util.ArrayList;
import java.util.List;

import dev.dhbwloerrach.kamiio.kniffel.game.ComputerPlayer;
import dev.dhbwloerrach.kamiio.kniffel.game.Game;
import dev.dhbwloerrach.kamiio.kniffel.game.PersonPlayer;
import dev.dhbwloerrach.kamiio.kniffel.game.Player;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Controller für die Spieleinrichtung und Spielerauswahl.
 */
public class GameSetupController {
    private final TextField player1NameField;
    private final TextField player2NameField;
    private final ToggleButton computerOpponentCheckBox;
    private final HBox player2Box_input;
    private final VBox nameInputVBox;
    private final VBox mainVBox;

    /**
     * Erstellt einen neuen GameSetupController.
     *
     * @param player1NameField Das Textfeld für den Namen von Spieler 1
     * @param player2NameField Das Textfeld für den Namen von Spieler 2
     * @param computerOpponentCheckBox Die Checkbox für die Auswahl eines Computer-Gegners
     * @param player2Box_input Die Box für die Eingabe des Namens von Spieler 2
     * @param nameInputVBox Die Box mit den Namenseingaben
     * @param mainVBox Die Haupt-Box des Spiels
     */
    public GameSetupController(
            TextField player1NameField,
            TextField player2NameField,
            ToggleButton computerOpponentCheckBox,
            HBox player2Box_input,
            VBox nameInputVBox,
            VBox mainVBox) {
        this.player1NameField = player1NameField;
        this.player2NameField = player2NameField;
        this.computerOpponentCheckBox = computerOpponentCheckBox;
        this.player2Box_input = player2Box_input;
        this.nameInputVBox = nameInputVBox;
        this.mainVBox = mainVBox;

        initializeComputerOpponentToggle();
    }

    /**
     * Initialisiert den Toggle-Button für den Computer-Gegner.
     */
    private void initializeComputerOpponentToggle() {
        computerOpponentCheckBox.setOnAction(e -> {
            boolean isComputerOpponent = computerOpponentCheckBox.isSelected();
            player2Box_input.setVisible(!isComputerOpponent);
            player2Box_input.setManaged(!isComputerOpponent);

            // Text des Buttons aktualisieren
            if (isComputerOpponent) {
                computerOpponentCheckBox.setText("Ein");
                computerOpponentCheckBox.setStyle("-fx-font-size: 16px; -fx-background-radius: 20; -fx-min-width: 100; -fx-padding: 8 24; " +
                                                "-fx-background-color: #22c55e; -fx-text-fill: white; -fx-background-insets: 0;");
                player2NameField.setText("Computer");
            } else {
                computerOpponentCheckBox.setText("Aus");
                computerOpponentCheckBox.setStyle("-fx-font-size: 16px; -fx-background-radius: 20; -fx-min-width: 100; -fx-padding: 8 24; " +
                                                "-fx-background-color: #f1f5f9; -fx-text-fill: #334155; -fx-background-insets: 0;");
                player2NameField.setText("");
            }
        });
    }

    /**
     * Erstellt eine Liste von Spielern basierend auf den Eingabedaten.
     *
     * @return Eine Liste der erstellten Spieler
     */
    public List<Player> createPlayers() {
        List<Player> players = new ArrayList<>();

        String name1 = player1NameField.getText().trim();
        String name2 = player2NameField.getText().trim();

        if (name1.isEmpty()) {
            name1 = "Spieler 1";
        }
        if (name2.isEmpty()) {
            name2 = "Computer";
        }

        players.add(new PersonPlayer(name1));

        boolean isComputerOpponent = computerOpponentCheckBox.isSelected();
        if (isComputerOpponent) {
            players.add(new ComputerPlayer(name2));
        } else {
            players.add(new PersonPlayer(name2));
        }

        return players;
    }

    /**
     * Setzt Spieler-Details zurück auf die des übergebenen Spiels.
     *
     * @param game Das Spiel mit den vorhandenen Spielern
     */
    public void resetWithPlayers(Game game) {
        if (game == null || game.getPlayers().size() < 2) {
            return;
        }

        List<Player> players = game.getPlayers();

        player1NameField.setText(players.get(0).getName());
        player2NameField.setText(players.get(1).getName());

        // Computer-Spieler-Checkbox richtig setzen
        boolean wasComputer = players.get(1) instanceof ComputerPlayer;
        computerOpponentCheckBox.setSelected(wasComputer);

        if (wasComputer) {
            computerOpponentCheckBox.setText("Ein");
            computerOpponentCheckBox.setStyle("-fx-font-size: 16px; -fx-background-radius: 20; -fx-min-width: 100; -fx-padding: 8 24; " +
                                           "-fx-background-color: #22c55e; -fx-text-fill: white; -fx-background-insets: 0;");
        } else {
            computerOpponentCheckBox.setText("Aus");
            computerOpponentCheckBox.setStyle("-fx-font-size: 16px; -fx-background-radius: 20; -fx-min-width: 100; -fx-padding: 8 24; " +
                                           "-fx-background-color: #f1f5f9; -fx-text-fill: #334155; -fx-background-insets: 0;");
        }

        // Spieler-Input entsprechend anpassen
        player2Box_input.setVisible(!wasComputer);
        player2Box_input.setManaged(!wasComputer);
    }

    /**
     * Wechselt zur Hauptspielansicht.
     */
    public void showGameView() {
        nameInputVBox.setVisible(false);
        mainVBox.setVisible(true);
    }

    /**
     * Wechselt zurück zur Spieleinrichtungsansicht.
     */
    public void showSetupView() {
        nameInputVBox.setVisible(true);
        mainVBox.setVisible(false);
    }
}
