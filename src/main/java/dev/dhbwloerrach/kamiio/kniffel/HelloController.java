package dev.dhbwloerrach.kamiio.kniffel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dev.dhbwloerrach.kamiio.kniffel.game.Category;
import dev.dhbwloerrach.kamiio.kniffel.utils.KniffelScorer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Hauptcontroller für das Kniffel-Spiel.
 * Verwaltet die Benutzeroberfläche und interagiert mit der Spiellogik.
 */
public class HelloController {
    @FXML private Label welcomeText;
    @FXML private Button dice1Btn, dice2Btn, dice3Btn, dice4Btn, dice5Btn;
    @FXML private Label rollsLeftLabel;
    @FXML private ComboBox<Category> categoryCombo;
    @FXML private Button submitCategoryButton;
    @FXML private Label playerLabel;

    // Neue UI-Elemente
    @FXML private Label player1NameLabel;
    @FXML private Label player2NameLabel;
    @FXML private Label player1ScoreLabel;
    @FXML private Label player2ScoreLabel;
    @FXML private VBox player1Box;
    @FXML private VBox player2Box;
    @FXML private HBox player2Box_input;
    @FXML private Label computerActionLabel;
    @FXML private TextArea gameHistoryArea;

    // Erweiterte Tabelle
    @FXML private TableView<CategoryRow> categoryTable;
    @FXML private TableColumn<CategoryRow, String> categoryCol;
    @FXML private TableColumn<CategoryRow, String> statusCol;
    @FXML private TableColumn<CategoryRow, Integer> valueCol;
    @FXML private TableColumn<CategoryRow, Integer> player1Col;
    @FXML private TableColumn<CategoryRow, Integer> player2Col;

    @FXML private VBox nameInputVBox;
    @FXML private VBox mainVBox;
    @FXML private TextField player1NameField;
    @FXML private TextField player2NameField;
    @FXML private Button startGameButton;
    @FXML private ToggleButton computerOpponentCheckBox;

    private final List<dev.dhbwloerrach.kamiio.kniffel.game.Player> players = new ArrayList<>();
    private dev.dhbwloerrach.kamiio.kniffel.game.Game game;
    private final boolean[] diceHeld = new boolean[5];
    private boolean firstRollDone = false;
    private final StringBuilder gameHistory = new StringBuilder();

    // Unicode-Zeichen für Würfel 1-6 und leeren Würfel
    private static final String[] DICE_UNICODE = {"⬜", "\u2680", "\u2681", "\u2682", "\u2683", "\u2684", "\u2685"};
    private static final int MAX_ROLLS = 3;

    /**
     * Initialisiert die Benutzeroberfläche beim Start.
     */
    @FXML
    public void initialize() {
        // Zeige Namensdialog, verstecke Haupt-UI
        nameInputVBox.setVisible(true);
        mainVBox.setVisible(false);

        // Computer-Aktions-Label anfangs ausblenden
        if (computerActionLabel != null) {
            computerActionLabel.setVisible(false);
        }

        // Event-Handler für den Computer-Gegner-Schalter
        if (computerOpponentCheckBox != null && player2Box_input != null) {
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

        // Initialisiere Kategorie-ComboBox
        if (categoryCombo != null) {
            categoryCombo.getItems().setAll(dev.dhbwloerrach.kamiio.kniffel.game.Category.values());
            // Deaktiviere ComboBox bis Würfel geworfen wurden
            categoryCombo.setDisable(true);

            // Listener für die Kategorie-Auswahl hinzufügen
            categoryCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
                updateSubmitButtonState();
            });
        }

        // Initialisiere den Submit-Button als deaktiviert
        if (submitCategoryButton != null) {
            submitCategoryButton.setDisable(true);
            // Setze inaktiven Stil
            updateSubmitButtonState();
        }

        // Initialisiere Kategorie-Tabelle
        if (categoryTable != null) {
            categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
            statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
            valueCol.setCellValueFactory(new PropertyValueFactory<>("value"));

            // Neue Spalten für Spieler-Punktestände
            player1Col.setCellValueFactory(new PropertyValueFactory<>("player1Score"));
            player2Col.setCellValueFactory(new PropertyValueFactory<>("player2Score"));

            updateCategoryTable();
        }

        // Setze leere Würfel am Anfang
        if (dice1Btn != null) dice1Btn.setText(DICE_UNICODE[0]);
        if (dice2Btn != null) dice2Btn.setText(DICE_UNICODE[0]);
        if (dice3Btn != null) dice3Btn.setText(DICE_UNICODE[0]);
        if (dice4Btn != null) dice4Btn.setText(DICE_UNICODE[0]);
        if (dice5Btn != null) dice5Btn.setText(DICE_UNICODE[0]);

        // Spielhistorie initialisieren und stylen
        if (gameHistoryArea != null) {
            gameHistoryArea.setText("Willkommen bei Kniffel! Die Spielhistorie wird hier angezeigt.\n");

            // Schöneres Styling für die Textfläche
            gameHistoryArea.setStyle("-fx-control-inner-background: #f8fafc; " + // Hintergrund
                                   "-fx-font-family: 'Consolas', 'Monaco', monospace; " + // Font
                                   "-fx-font-size: 12px; " + // Schriftgröße
                                   "-fx-border-color: #cbd5e1; " + // Rahmenfarbe
                                   "-fx-border-radius: 8; " + // Rahmenradius
                                   "-fx-background-radius: 8; " + // Hintergrundradius
                                   "-fx-border-width: 1.5; " + // Rahmenstärke
                                   "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.05), 4, 0.0, 0, 1);"); // Schatten
        }
    }

    /**
     * Wird aufgerufen, wenn der Spielstart-Button geklickt wird.
     * Initialisiert das Spiel mit den eingegebenen Spielernamen.
     */
    @FXML
    protected void onStartGameClick() {
        String name1 = player1NameField.getText().trim();
        String name2 = player2NameField.getText().trim();
        if (name1.isEmpty()) name1 = "Spieler 1";
        if (name2.isEmpty()) name2 = "Computer";

        players.clear();
        players.add(new dev.dhbwloerrach.kamiio.kniffel.game.PersonPlayer(name1));

        boolean isComputerOpponent = computerOpponentCheckBox != null && computerOpponentCheckBox.isSelected();

        // Prüfen, ob gegen den Computer gespielt werden soll
        if (isComputerOpponent) {
            players.add(new dev.dhbwloerrach.kamiio.kniffel.game.ComputerPlayer(name2));
        } else {
            players.add(new dev.dhbwloerrach.kamiio.kniffel.game.PersonPlayer(name2));
        }

        // Spiel initialisieren
        game = new dev.dhbwloerrach.kamiio.kniffel.game.Game(players);
        game.startGame();

        // UI umschalten
        nameInputVBox.setVisible(false);
        mainVBox.setVisible(true);        // Spieler-Labels setzen
        player1NameLabel.setText(name1);
        player2NameLabel.setText(name2);

        // Aktualisiere die Namen der Spieler in den Tabellenspalten
        player1Col.setText(name1);
        player2Col.setText(name2);

        // Willkommenstext auf "Kniffel" ändern (falls nicht bereits geschehen)
        if (welcomeText.getText().equals("Willkommen zu Kniffel!")) {
            welcomeText.setText("Kniffel");
        }

        // Spielstand aktualisieren
        updatePlayerScores();

        // Spieler-Boxen hervorheben - aktiver Spieler
        updateActivePlayerHighlight();

        // Setze Kategorie-ComboBox und Tabelle nach Spielstart erneut
        updateAvailableCategories();
        updateCategoryTable();

        // Spielhistorie zurücksetzen
        gameHistory.setLength(0);
        addToGameHistory("Neues Spiel gestartet: " + name1 + " vs. " + name2, true);
        if (isComputerOpponent) {
            addToGameHistory(name1 + " spielt gegen den Computer (" + name2 + ")");
        }

        // Aktualisiere die gesamte UI
        updateUI();
    }

    /**
     * Fügt einen Eintrag zur Spielhistorie hinzu.
     *
     * @param entry Der hinzuzufügende Eintrag
     * @param isImportant Ob der Eintrag hervorgehoben werden soll
     */
    private void addToGameHistory(String entry, boolean isImportant) {
        // Zeitstempel erstellen (nur Stunden:Minuten:Sekunden)
        java.time.LocalTime now = java.time.LocalTime.now();
        String timestamp = String.format("[%02d:%02d:%02d] ", now.getHour(), now.getMinute(), now.getSecond());

        // Hervorhebung für wichtige Ereignisse
        String formattedEntry;
        if (isImportant) {
            formattedEntry = "➤ " + entry;
        } else {
            formattedEntry = "• " + entry;
        }

        // Zum Verlauf hinzufügen
        gameHistory.append(timestamp).append(formattedEntry).append("\n");

        if (gameHistoryArea != null) {
            gameHistoryArea.setText(gameHistory.toString());
            gameHistoryArea.positionCaret(gameHistoryArea.getText().length());
        }
    }

    /**
     * Fügt einen normalen (nicht hervorgehobenen) Eintrag zur Spielhistorie hinzu.
     *
     * @param entry Der hinzuzufügende Eintrag
     */
    private void addToGameHistory(String entry) {
        addToGameHistory(entry, false);
    }

    /**
     * Aktualisiert die Anzeige der verfügbaren Kategorien in der ComboBox.
     * Entfernt bereits verwendete Kategorien.
     */
    private void updateAvailableCategories() {
        if (categoryCombo == null || game == null) return;

        dev.dhbwloerrach.kamiio.kniffel.game.Player current = game.getCurrentPlayer();

        // Filtere bereits verwendete Kategorien heraus
        List<Category> availableCategories = java.util.Arrays.stream(Category.values())
            .filter(cat -> !current.isCategoryUsed(cat))
            .collect(Collectors.toList());

        categoryCombo.getItems().setAll(availableCategories);
        categoryCombo.setPromptText("Kategorie wählen");

        // Aktiviere Combo-Box nur wenn mindestens einmal gewürfelt wurde
        categoryCombo.setDisable(!firstRollDone);
    }

    /**
     * Aktualisiert die Anzeige der Spielerstände.
     */
    private void updatePlayerScores() {
        if (game == null || players.isEmpty()) return;

        dev.dhbwloerrach.kamiio.kniffel.game.Player player1 = players.get(0);
        player1ScoreLabel.setText(player1.getScore() + " Punkte");

        if (players.size() > 1) {
            dev.dhbwloerrach.kamiio.kniffel.game.Player player2 = players.get(1);
            player2ScoreLabel.setText(player2.getScore() + " Punkte");
        }
    }

    /**
     * Hebt die Box des aktiven Spielers hervor.
     */
    private void updateActivePlayerHighlight() {
        if (game == null || players.isEmpty()) return;

        int currentPlayerIndex = players.indexOf(game.getCurrentPlayer());

        // Setze Styles für beide Boxen zurück
        player1Box.setStyle("-fx-background-color: white; -fx-padding: 16; -fx-background-radius: 12; " +
                           "-fx-border-color: #e2e8f0; -fx-border-radius: 12; -fx-border-width: 1.5; " +
                           "-fx-effect: dropshadow(gaussian, rgba(203, 213, 225, 0.5), 6, 0.0, 0, 2); " +
                           "-fx-background-insets: 0; -fx-border-insets: 0;");
        player2Box.setStyle("-fx-background-color: white; -fx-padding: 16; -fx-background-radius: 12; " +
                           "-fx-border-color: #e2e8f0; -fx-border-radius: 12; -fx-border-width: 1.5; " +
                           "-fx-effect: dropshadow(gaussian, rgba(203, 213, 225, 0.5), 6, 0.0, 0, 2); " +
                           "-fx-background-insets: 0; -fx-border-insets: 0;");

        // Hebe aktiven Spieler hervor
        if (currentPlayerIndex == 0) {
            player1Box.setStyle("-fx-background-color: #dcfce7; -fx-padding: 16; -fx-background-radius: 12; " +
                               "-fx-border-color: #22c55e; -fx-border-radius: 12; -fx-border-width: 2; " +
                               "-fx-effect: dropshadow(gaussian, rgba(34, 197, 94, 0.3), 8, 0.0, 0, 3); " +
                               "-fx-background-insets: 0; -fx-border-insets: 0;");
        } else if (currentPlayerIndex == 1) {
            player2Box.setStyle("-fx-background-color: #dcfce7; -fx-padding: 16; -fx-background-radius: 12; " +
                               "-fx-border-color: #22c55e; -fx-border-radius: 12; -fx-border-width: 2; " +
                               "-fx-effect: dropshadow(gaussian, rgba(34, 197, 94, 0.3), 8, 0.0, 0, 3); " +
                               "-fx-background-insets: 0; -fx-border-insets: 0;");
        }
    }

    @FXML
    protected void onDice1Click() { if (firstRollDone) { diceHeld[0] = !diceHeld[0]; updateDiceButtonStyle(0); } }
    @FXML
    protected void onDice2Click() { if (firstRollDone) { diceHeld[1] = !diceHeld[1]; updateDiceButtonStyle(1); } }
    @FXML
    protected void onDice3Click() { if (firstRollDone) { diceHeld[2] = !diceHeld[2]; updateDiceButtonStyle(2); } }
    @FXML
    protected void onDice4Click() { if (firstRollDone) { diceHeld[3] = !diceHeld[3]; updateDiceButtonStyle(3); } }
    @FXML
    protected void onDice5Click() { if (firstRollDone) { diceHeld[4] = !diceHeld[4]; updateDiceButtonStyle(4); } }

    private void updateDiceButtonStyle(int index) {
        String baseStyle = "-fx-font-size: 40px; -fx-background-radius: 12px; -fx-border-color: #cbd5e1; -fx-border-radius: 12px; -fx-border-width: 1.5; " +
                           "-fx-effect: dropshadow(gaussian, rgba(203, 213, 225, 0.6), 6, 0.0, 0, 2); -fx-background-insets: 0; -fx-padding: 0;";

        String style = diceHeld[index]
            ? "-fx-background-color: #bbf7d0; -fx-border-color: #22c55e;"
            : "-fx-background-color: white;";

        switch (index) {
            case 0: dice1Btn.setStyle(style + baseStyle); break;
            case 1: dice2Btn.setStyle(style + baseStyle); break;
            case 2: dice3Btn.setStyle(style + baseStyle); break;
            case 3: dice4Btn.setStyle(style + baseStyle); break;
            case 4: dice5Btn.setStyle(style + baseStyle); break;
        }
    }

    @FXML
    protected void onRollButtonClick() {
        firstRollDone = true;
        animateDiceRoll();
        updateSubmitButtonState();
        // Aktualisiere den Zustand des Würfel-Buttons
        updateRollButtonState();
    }

    private void animateDiceRoll() {
        Timeline timeline = new Timeline();
        for (int i = 0; i < 10; i++) {
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(50 * i), e -> {
                for (int d = 0; d < 5; d++) {
                    if (!diceHeld[d]) {
                        int random = 1 + (int)(Math.random() * 6);
                        setDiceButton(d, random);
                    }
                }
            }));
        }
        timeline.setOnFinished(e -> {
            boolean[] toRoll = new boolean[5];
            for (int i = 0; i < 5; i++) toRoll[i] = !diceHeld[i];
            game.rollDice(toRoll);
            updateUI();
            // Aktualisiere den Status des Würfel-Buttons nach dem Würfeln
            updateRollButtonState();
        });
        timeline.play();
    }

    private void setDiceButton(int index, int value) {
        switch (index) {
            case 0: dice1Btn.setText(DICE_UNICODE[value]); break;
            case 1: dice2Btn.setText(DICE_UNICODE[value]); break;
            case 2: dice3Btn.setText(DICE_UNICODE[value]); break;
            case 3: dice4Btn.setText(DICE_UNICODE[value]); break;
            case 4: dice5Btn.setText(DICE_UNICODE[value]); break;
        }
    }

    @FXML
    protected void onSubmitCategoryClick() {
        Category cat = categoryCombo.getValue();
        dev.dhbwloerrach.kamiio.kniffel.game.Player current = game.getCurrentPlayer();
        if (cat != null && !current.isCategoryUsed(cat)) {
            int points = KniffelScorer.calculateScore(cat, game.getDiceList());
            current.addScore(cat, points);

            // Spielzug protokollieren
            String diceValues = game.getDiceList().stream()
                .map(dice -> String.valueOf(dice.getValue()))
                .collect(Collectors.joining(", "));

            addToGameHistory(current.getName() + " wählt " + cat.name() +
                             " mit Würfeln [" + diceValues + "] für " + points + " Punkte");

            // Zurücksetzen der UI-Elemente
            categoryCombo.setValue(null);
            firstRollDone = false;

            game.nextTurn();
            updateUI();

            // Überprüfe, ob der nächste Spieler ein Computer ist
            if (!game.isGameOver() && game.getCurrentPlayer() instanceof dev.dhbwloerrach.kamiio.kniffel.game.ComputerPlayer) {
                // Kurze Verzögerung, damit der Spieler den Wechsel sieht
                javafx.application.Platform.runLater(() -> {
                    Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
                        handleComputerTurn();
                    }));
                    timeline.play();
                });
            }
        }
    }

    /**
     * Behandelt einen vollständigen Computer-Zug
     */
    private void handleComputerTurn() {
        if (!game.isGameOver() && game.getCurrentPlayer() instanceof dev.dhbwloerrach.kamiio.kniffel.game.ComputerPlayer) {
            dev.dhbwloerrach.kamiio.kniffel.game.ComputerPlayer computerPlayer =
                (dev.dhbwloerrach.kamiio.kniffel.game.ComputerPlayer) game.getCurrentPlayer();

            // Animiere den ersten Würfelwurf
            firstRollDone = true;
            animateComputerDiceRoll();

            // Nach einer kurzen Verzögerung, führe die Computer-Strategie aus
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
                // Entscheide, welche Würfel behalten werden sollen
                boolean[] toRoll = computerPlayer.decideDiceToRoll(game.getDiceList());

                // Markiere die Würfel, die gehalten werden sollen
                for (int i = 0; i < 5; i++) {
                    diceHeld[i] = !toRoll[i];
                    updateDiceButtonStyle(i);
                }

                // Zweiter Würfelwurf nach kurzer Verzögerung
                Timeline timeline2 = new Timeline(new KeyFrame(Duration.seconds(1), e2 -> {
                    animateComputerDiceRoll();

                    // Nach dem zweiten Wurf, entscheide erneut
                    Timeline timeline3 = new Timeline(new KeyFrame(Duration.seconds(1), e3 -> {
                        boolean[] toRoll2 = computerPlayer.decideDiceToRoll(game.getDiceList());

                        // Markiere die Würfel, die gehalten werden sollen
                        for (int i = 0; i < 5; i++) {
                            diceHeld[i] = !toRoll2[i];
                            updateDiceButtonStyle(i);
                        }

                        // Dritter Würfelwurf nach kurzer Verzögerung
                        Timeline timeline4 = new Timeline(new KeyFrame(Duration.seconds(1), e4 -> {
                            if (game.getRollsLeft() > 0) {
                                animateComputerDiceRoll();
                            }

                            // Nach dem dritten Wurf oder wenn keine Würfe mehr übrig sind, wähle Kategorie
                            Timeline timeline5 = new Timeline(new KeyFrame(Duration.seconds(1), e5 -> {
                                // Wähle beste Kategorie
                                Category bestCategory = computerPlayer.chooseBestCategory(game.getDiceList());
                                categoryCombo.setValue(bestCategory);

                                // Wende Kategorie an und beende den Zug
                                Timeline timeline6 = new Timeline(new KeyFrame(Duration.seconds(1), e6 -> {
                                    int points = KniffelScorer.calculateScore(bestCategory, game.getDiceList());
                                    computerPlayer.addScore(bestCategory, points);

                                    // Computer-Spielzug protokollieren
                                    String diceValues = game.getDiceList().stream()
                                        .map(dice -> String.valueOf(dice.getValue()))
                                        .collect(Collectors.joining(", "));

                                    addToGameHistory("Computer wählt " + bestCategory.name() +
                                                   " mit Würfeln [" + diceValues + "] für " + points + " Punkte");

                                    game.nextTurn();
                                    updateUI();

                                    // Prüfe rekursiv, ob der nächste Spieler wieder ein Computer ist
                                    if (!game.isGameOver() && game.getCurrentPlayer() instanceof dev.dhbwloerrach.kamiio.kniffel.game.ComputerPlayer) {
                                        handleComputerTurn();
                                    }
                                }));
                                timeline6.play();
                            }));
                            timeline5.play();
                        }));
                        timeline4.play();
                    }));
                    timeline3.play();
                }));
                timeline2.play();
            }));
            timeline.play();
        }
    }

    private void animateComputerDiceRoll() {
        Timeline timeline = new Timeline();
        for (int i = 0; i < 10; i++) {
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(50 * i), e -> {
                for (int d = 0; d < 5; d++) {
                    if (!diceHeld[d]) {
                        int random = 1 + (int)(Math.random() * 6);
                        setDiceButton(d, random);
                    }
                }
            }));
        }
        timeline.setOnFinished(e -> {
            boolean[] toRoll = new boolean[5];
            for (int i = 0; i < 5; i++) toRoll[i] = !diceHeld[i];
            game.rollDice(toRoll);
            updateUI();
            // Aktualisiere den Status des Würfel-Buttons nach dem Würfeln
            updateRollButtonState();
        });
        timeline.play();
    }

    /**
     * Aktualisiert die gesamte Benutzeroberfläche mit dem aktuellen Spielstand.
     */
    private void updateUI() {
        if (game == null) return;

        dev.dhbwloerrach.kamiio.kniffel.game.Player current = game.getCurrentPlayer();

        // Würfel aktualisieren
        setDiceButton(0, game.getDiceList().get(0).getValue());
        setDiceButton(1, game.getDiceList().get(1).getValue());
        setDiceButton(2, game.getDiceList().get(2).getValue());
        setDiceButton(3, game.getDiceList().get(3).getValue());
        setDiceButton(4, game.getDiceList().get(4).getValue());

        // Informationen aktualisieren
        rollsLeftLabel.setText("Würfe übrig: " + game.getRollsLeft());
        playerLabel.setText("Am Zug: " + current.getName());

        // Spielerstände aktualisieren
        updatePlayerScores();

        // Aktiven Spieler hervorheben
        updateActivePlayerHighlight();

        // Nach jedem Spielerwechsel alle Würfel auf nicht gehalten zurücksetzen und firstRollDone zurücksetzen
        for (int i = 0; i < 5; i++) {
            diceHeld[i] = false;
            updateDiceButtonStyle(i);
        }

        // Wenn noch nicht gewürfelt wurde, zeige leere Würfel
        if (!firstRollDone) {
            dice1Btn.setText(DICE_UNICODE[0]);
            dice2Btn.setText(DICE_UNICODE[0]);
            dice3Btn.setText(DICE_UNICODE[0]);
            dice4Btn.setText(DICE_UNICODE[0]);
            dice5Btn.setText(DICE_UNICODE[0]);
        }

        // Spiel-Status überprüfen und UI entsprechend aktualisieren
        if (game.isGameOver()) {
            // Spiel zu Ende
            welcomeText.setText("🏆 Spiel beendet! Gewinner: " + getWinnerName());

            // Endstand zur Spielhistorie hinzufügen
            StringBuilder sb = new StringBuilder("Endstand:\n");
            for (dev.dhbwloerrach.kamiio.kniffel.game.Player p : game.getPlayers()) {
                sb.append(p.getName()).append(": ").append(p.getScore()).append(" Punkte\n");
            }
            addToGameHistory("Spiel beendet. Gewinner: " + getWinnerName(), true);
            addToGameHistory(sb.toString());

            // Game Over Panel mit Revanche-Option anzeigen
            showGameOverPanel();

            // Kategorie-Auswahl deaktivieren
            categoryCombo.setDisable(true);

        } else if (game.getRollsLeft() == MAX_ROLLS) {
            // Neuer Zug, noch nicht gewürfelt
            firstRollDone = false;
            welcomeText.setText(current.getName() + " ist am Zug - Bitte würfeln");        // Kategorien aktualisieren
        updateAvailableCategories();

        // Button-Status aktualisieren
        updateSubmitButtonState();
        updateRollButtonState();

        // Starte Computer-Zug, wenn ein Computer am Zug ist
            if (current instanceof dev.dhbwloerrach.kamiio.kniffel.game.ComputerPlayer) {
                // Computer-Aktions-Label anzeigen
                computerActionLabel.setVisible(true);
                computerActionLabel.setText("Computer überlegt...");

                welcomeText.setText("Computer ist am Zug - Wurf " + (MAX_ROLLS - game.getRollsLeft() + 1) + " von " + MAX_ROLLS);

                // Verzögerung, damit der Spieler sehen kann, dass der Computer am Zug ist
                javafx.application.Platform.runLater(() -> {
                    Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
                        handleComputerTurn();
                    }));
                    timeline.play();
                });
            } else {
                // Computer-Aktions-Label ausblenden, wenn menschlicher Spieler am Zug ist
                computerActionLabel.setVisible(false);
            }
        } else {
            // Mitten im Zug
            int remainingRolls = game.getRollsLeft();
            welcomeText.setText(current.getName() + " - Noch " + remainingRolls + " Würfe übrig");
            updateAvailableCategories();
        }

        // Kategorie-Tabelle aktualisieren
        updateCategoryTable();
    }

    /**
     * Aktualisiert die Kategorie-Tabelle mit den aktuellen Spielinformationen.
     */
    private void updateCategoryTable() {
        if (categoryTable == null) return;

        ObservableList<CategoryRow> data = FXCollections.observableArrayList();

        if (game == null) {
            // Zeige alle Kategorien als offen und leer, solange kein Spiel läuft
            for (dev.dhbwloerrach.kamiio.kniffel.game.Category cat : dev.dhbwloerrach.kamiio.kniffel.game.Category.values()) {
                data.add(new CategoryRow(cat.name(), "Offen", null, null, null));
            }
            categoryTable.setItems(data);
            return;
        }

        dev.dhbwloerrach.kamiio.kniffel.game.Player current = game.getCurrentPlayer();
        List<Dice> dice = game.getDiceList();
        boolean noRoll = !firstRollDone;

        // Hole beide Spieler für die Anzeige der Punkte
        dev.dhbwloerrach.kamiio.kniffel.game.Player player1 = players.get(0);
        dev.dhbwloerrach.kamiio.kniffel.game.Player player2 = players.size() > 1 ? players.get(1) : null;

        for (dev.dhbwloerrach.kamiio.kniffel.game.Category cat : dev.dhbwloerrach.kamiio.kniffel.game.Category.values()) {
            boolean used = current.isCategoryUsed(cat);
            String status = used ? "Eingelöst" : "Offen";

            // Aktueller potentieller Wert für die Kategorie
            Integer currentValue;
            if (used) {
                currentValue = current.getCategoryScore(cat);
            } else if (noRoll) {
                currentValue = null;
            } else {
                currentValue = dev.dhbwloerrach.kamiio.kniffel.utils.KniffelScorer.calculateScore(cat, dice);
            }

            // Punkte für beide Spieler
            Integer player1Score = player1.isCategoryUsed(cat) ? player1.getCategoryScore(cat) : null;
            Integer player2Score = player2 != null && player2.isCategoryUsed(cat) ? player2.getCategoryScore(cat) : null;

            data.add(new CategoryRow(cat.name(), status, currentValue, player1Score, player2Score));
        }

        categoryTable.setItems(data);
    }    /**
     * Aktualisiert den Zustand des Wählen-Buttons basierend auf dem Spielstatus.
     * Der Button ist nur aktiviert, wenn bereits gewürfelt wurde und eine Kategorie ausgewählt ist.
     */
    private void updateSubmitButtonState() {
        if (submitCategoryButton == null) return;

        boolean shouldEnable = firstRollDone && categoryCombo.getValue() != null;
        submitCategoryButton.setDisable(!shouldEnable);

        // Style anpassen je nach Status
        if (shouldEnable) {
            // Aktiver Zustand (grün)
            submitCategoryButton.setStyle("-fx-font-size: 16px; -fx-background-color: #22c55e; -fx-text-fill: white; " +
                                         "-fx-background-radius: 12px; -fx-padding: 10 24; -fx-font-weight: bold; " +
                                         "-fx-effect: dropshadow(gaussian, rgba(34, 197, 94, 0.25), 6, 0.0, 0, 2); " +
                                         "-fx-background-insets: 0;");
        } else {
            // Deaktivierter Zustand (grau)
            submitCategoryButton.setStyle("-fx-font-size: 16px; -fx-background-color: #cbd5e1; -fx-text-fill: #94a3b8; " +
                                         "-fx-background-radius: 12px; -fx-padding: 10 24; -fx-font-weight: bold; " +
                                         "-fx-effect: none; -fx-background-insets: 0;");
        }
    }

    /**
     * Aktualisiert den Zustand des Würfel-Buttons basierend auf der Anzahl der verbleibenden Würfe.
     */
    private void updateRollButtonState() {
        if (game == null) return;

        Button rollButton = null;
        for (javafx.scene.Node node : mainVBox.lookupAll("Button")) {
            if (node instanceof Button && ((Button) node).getText() != null && ((Button) node).getText().equals("Würfeln")) {
                rollButton = (Button) node;
                break;
            }
        }

        if (rollButton != null) {
            boolean canRoll = game.getRollsLeft() > 0;
            rollButton.setDisable(!canRoll);

            if (canRoll) {
                // Aktiver Zustand (blau)
                rollButton.setStyle("-fx-font-size: 18px; -fx-background-color: #38bdf8; -fx-text-fill: white; " +
                                  "-fx-background-radius: 12px; -fx-padding: 10 28; -fx-font-weight: bold; " +
                                  "-fx-effect: dropshadow(gaussian, rgba(56, 189, 248, 0.25), 6, 0.0, 0, 2); " +
                                  "-fx-background-insets: 0;");
            } else {
                // Deaktivierter Zustand (grau)
                rollButton.setStyle("-fx-font-size: 18px; -fx-background-color: #cbd5e1; -fx-text-fill: #94a3b8; " +
                                  "-fx-background-radius: 12px; -fx-padding: 10 28; -fx-font-weight: bold; " +
                                  "-fx-effect: none; -fx-background-insets: 0;");
            }
        }
    }

    private String getWinnerName() {
        dev.dhbwloerrach.kamiio.kniffel.game.Player winner = game.getWinner();
        return winner != null ? winner.getName() : "Kein Gewinner";
    }

    /**
     * Startet eine neue Runde mit den gleichen Spielern (Revanche).
     */
    private void startRematch() {
        // Spiel neu starten mit den gleichen Spielern
        game.startGame();

        // UI zurücksetzen
        firstRollDone = false;
        for (int i = 0; i < 5; i++) {
            diceHeld[i] = false;
            updateDiceButtonStyle(i);
        }

        // Kategorie-ComboBox zurücksetzen und aktivieren
        categoryCombo.getItems().setAll(dev.dhbwloerrach.kamiio.kniffel.game.Category.values());
        categoryCombo.setDisable(true);
        categoryCombo.setValue(null);

        // Spielhistorie-Eintrag
        addToGameHistory("Neue Runde gestartet (Revanche)", true);

        // UI aktualisieren
        updateUI();

        // Animation für den Neustart zeigen
        showRevancheAnimation();
    }

    /**
     * Kehrt zum Hauptmenü zurück und setzt das Spiel zurück.
     */
    private void returnToMainMenu() {
        // Spielhistorie-Eintrag
        addToGameHistory("Zurück zum Hauptmenü", true);

        // UI umschalten
        nameInputVBox.setVisible(true);
        mainVBox.setVisible(false);

        // Spieler-Namen aus dem vorherigen Spiel übernehmen
        if (players.size() >= 2) {
            player1NameField.setText(players.get(0).getName());
            player2NameField.setText(players.get(1).getName());

            // Computer-Spieler-Checkbox richtig setzen
            boolean wasComputer = players.get(1) instanceof dev.dhbwloerrach.kamiio.kniffel.game.ComputerPlayer;
            if (computerOpponentCheckBox != null) {
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
            }

            // Spieler-Input entsprechend anpassen
            if (player2Box_input != null) {
                player2Box_input.setVisible(!wasComputer);
                player2Box_input.setManaged(!wasComputer);
            }
        }

        // Würfel zurücksetzen
        dice1Btn.setText(DICE_UNICODE[0]);
        dice2Btn.setText(DICE_UNICODE[0]);
        dice3Btn.setText(DICE_UNICODE[0]);
        dice4Btn.setText(DICE_UNICODE[0]);
        dice5Btn.setText(DICE_UNICODE[0]);

        // Welcome-Text zurücksetzen
        welcomeText.setText("Willkommen zu Kniffel!");
    }

    /**
     * Zeigt eine Animation für den Start einer Revanche an.
     */
    private void showRevancheAnimation() {
        // Halbdurchsichtiger Hintergrund für Popup-Effekt
        javafx.scene.layout.StackPane overlay = new javafx.scene.layout.StackPane();
        overlay.setPrefSize(mainVBox.getWidth(), mainVBox.getHeight());
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);"); // Halbdurchsichtiger schwarzer Hintergrund

        // Revanche-Panel erstellen
        VBox revanchePanel = new VBox(15);
        revanchePanel.setAlignment(javafx.geometry.Pos.CENTER);
        revanchePanel.setPrefWidth(400);
        revanchePanel.setPrefHeight(200);
        revanchePanel.setStyle("-fx-background-color: white; -fx-background-radius: 20; " +
                           "-fx-border-color: #22c55e; -fx-border-width: 2; -fx-border-radius: 20; " +
                           "-fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.4), 15, 0.0, 0, 10);");
        revanchePanel.setMaxWidth(400);
        revanchePanel.setMaxHeight(200);

        // Würfel-Icon hinzufügen
        Label diceLabel = new Label("🎲");
        diceLabel.setStyle("-fx-font-size: 40px;");

        // Neue Runde Text
        Label revancheLabel = new Label("Neue Runde!");
        revancheLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #16a34a;");

        // Viel Glück Text
        Label goodLuckLabel = new Label("Viel Glück!");
        goodLuckLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #334155;");

        // Alles zum Panel hinzufügen
        revanchePanel.getChildren().addAll(diceLabel, revancheLabel, goodLuckLabel);

        // Das Panel zum Overlay hinzufügen (zentriert)
        overlay.getChildren().add(revanchePanel);

        // Das Overlay zum Haupt-Layout hinzufügen
        javafx.scene.layout.Pane rootPane = (javafx.scene.layout.Pane) mainVBox.getScene().getRoot();
        rootPane.getChildren().add(overlay);

        // Overlay an die Größe des Fensters anpassen
        overlay.prefWidthProperty().bind(rootPane.widthProperty());
        overlay.prefHeightProperty().bind(rootPane.heightProperty());

        // Animation für das Einblenden
        revanchePanel.setScaleX(0.5);
        revanchePanel.setScaleY(0.5);
        revanchePanel.setOpacity(0);

        // Overlay zunächst unsichtbar
        overlay.setOpacity(0);

        // Animationen
        javafx.animation.Timeline popupTimeline = new javafx.animation.Timeline();
        popupTimeline.getKeyFrames().addAll(
            new KeyFrame(Duration.ZERO,
                new javafx.animation.KeyValue(overlay.opacityProperty(), 0),
                new javafx.animation.KeyValue(revanchePanel.scaleXProperty(), 0.5),
                new javafx.animation.KeyValue(revanchePanel.scaleYProperty(), 0.5),
                new javafx.animation.KeyValue(revanchePanel.opacityProperty(), 0)),
            new KeyFrame(Duration.seconds(0.15),
                new javafx.animation.KeyValue(overlay.opacityProperty(), 1)),
            new KeyFrame(Duration.seconds(0.3),
                new javafx.animation.KeyValue(revanchePanel.scaleXProperty(), 1),
                new javafx.animation.KeyValue(revanchePanel.scaleYProperty(), 1),
                new javafx.animation.KeyValue(revanchePanel.opacityProperty(), 1)),
            new KeyFrame(Duration.seconds(1.5),
                new javafx.animation.KeyValue(overlay.opacityProperty(), 1)),
            new KeyFrame(Duration.seconds(1.8),
                new javafx.animation.KeyValue(overlay.opacityProperty(), 0),
                new javafx.animation.KeyValue(revanchePanel.opacityProperty(), 0))
        );

        // Entferne das Overlay wenn die Animation fertig ist
        popupTimeline.setOnFinished(e -> rootPane.getChildren().remove(overlay));

        // Animation starten
        popupTimeline.play();
    }

    /**
     * Zeigt das Spielende-Panel mit Optionen für Revanche oder Spiel beenden an.
     * Dieses Panel wird angezeigt, nachdem ein Spiel abgeschlossen ist.
     */
    private void showGameOverPanel() {
        // Halbdurchsichtiger Hintergrund für Popup-Effekt
        javafx.scene.layout.StackPane overlay = new javafx.scene.layout.StackPane();
        overlay.setPrefSize(mainVBox.getWidth(), mainVBox.getHeight());
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);"); // Halbdurchsichtiger schwarzer Hintergrund

        // Game-Over-Panel erstellen
        VBox gameOverPanel = new VBox(15);
        gameOverPanel.setAlignment(javafx.geometry.Pos.CENTER);
        gameOverPanel.setPrefWidth(500);
        gameOverPanel.setPrefHeight(250);
        gameOverPanel.setStyle("-fx-background-color: white; -fx-background-radius: 20; " +
                             "-fx-border-color: #22c55e; -fx-border-width: 2; -fx-border-radius: 20; " +
                             "-fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.4), 15, 0.0, 0, 10);");
        gameOverPanel.setMaxWidth(500);
        gameOverPanel.setMaxHeight(250);

        // Sieger ermitteln
        dev.dhbwloerrach.kamiio.kniffel.game.Player winner = game.getWinner();
        String winnerName = winner != null ? winner.getName() : "Kein Gewinner";
        boolean isComputerWinner = winner instanceof dev.dhbwloerrach.kamiio.kniffel.game.ComputerPlayer;

        // Icon je nach Gewinner wählen
        Label trophyLabel = new Label(isComputerWinner ? "🤖" : "🏆");
        trophyLabel.setStyle("-fx-font-size: 40px;");

        // Text je nach Gewinner anpassen
        Label winnerLabel;
        if (isComputerWinner) {
            winnerLabel = new Label("Schade! Der Computer hat gewonnen.");
            winnerLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #ef4444;"); // Rote Färbung für Niederlage
        } else {
            winnerLabel = new Label("Glückwunsch " + winnerName + "!");
            winnerLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #16a34a;"); // Grüne Färbung für Sieg
        }

        // Punkte
        int winnerScore = winner.getScore();
        Label scoreLabel = new Label("Punktzahl: " + winnerScore);
        scoreLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #334155;");

        // Buttons für die Aktionen
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER);
        buttonBox.setPadding(new javafx.geometry.Insets(10, 0, 0, 0));

        // Revanche-Button
        Button rematchButton = new Button("Revanche");
        rematchButton.setStyle("-fx-font-size: 16px; -fx-background-color: #22c55e; -fx-text-fill: white; " +
                             "-fx-background-radius: 8; -fx-padding: 10 25; -fx-font-weight: bold; " +
                             "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.1), 4, 0, 0, 1);");
        rematchButton.setOnAction(e -> {
            // Entferne das Overlay
            javafx.scene.layout.Pane rootPane = (javafx.scene.layout.Pane) mainVBox.getScene().getRoot();
            rootPane.getChildren().remove(overlay);

            // Starte Revanche
            startRematch();
        });

        // Beenden-Button
        Button quitButton = new Button("Zurück zum Menü");
        quitButton.setStyle("-fx-font-size: 16px; -fx-background-color: #f1f5f9; -fx-text-fill: #334155; " +
                          "-fx-background-radius: 8; -fx-padding: 10 25; -fx-font-weight: bold; " +
                          "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.1), 4, 0, 0, 1);");
        quitButton.setOnAction(e -> {
            // Entferne das Overlay
            javafx.scene.layout.Pane rootPane = (javafx.scene.layout.Pane) mainVBox.getScene().getRoot();
            rootPane.getChildren().remove(overlay);

            // Zurück zum Hauptmenü
            returnToMainMenu();
        });

        // Buttons hinzufügen
        buttonBox.getChildren().addAll(rematchButton, quitButton);

        // Alles zum Panel hinzufügen
        gameOverPanel.getChildren().addAll(trophyLabel, winnerLabel, scoreLabel, buttonBox);

        // Das Panel zum Overlay hinzufügen (zentriert)
        overlay.getChildren().add(gameOverPanel);

        // Das Overlay zum Haupt-Layout hinzufügen
        javafx.scene.layout.Pane rootPane = (javafx.scene.layout.Pane) mainVBox.getScene().getRoot();
        rootPane.getChildren().add(overlay);

        // Overlay an die Größe des Fensters anpassen
        overlay.prefWidthProperty().bind(rootPane.widthProperty());
        overlay.prefHeightProperty().bind(rootPane.heightProperty());

        // Animation für das Einblenden
        gameOverPanel.setScaleX(0.5);
        gameOverPanel.setScaleY(0.5);
        gameOverPanel.setOpacity(0);

        // Overlay zunächst unsichtbar
        overlay.setOpacity(0);

        // Animationen
        javafx.animation.Timeline popupTimeline = new javafx.animation.Timeline();
        popupTimeline.getKeyFrames().addAll(
            new KeyFrame(Duration.ZERO,
                new javafx.animation.KeyValue(overlay.opacityProperty(), 0),
                new javafx.animation.KeyValue(gameOverPanel.scaleXProperty(), 0.5),
                new javafx.animation.KeyValue(gameOverPanel.scaleYProperty(), 0.5),
                new javafx.animation.KeyValue(gameOverPanel.opacityProperty(), 0)),
            new KeyFrame(Duration.seconds(0.3),
                new javafx.animation.KeyValue(overlay.opacityProperty(), 1)),
            new KeyFrame(Duration.seconds(0.5),
                new javafx.animation.KeyValue(gameOverPanel.scaleXProperty(), 1),
                new javafx.animation.KeyValue(gameOverPanel.scaleYProperty(), 1),
                new javafx.animation.KeyValue(gameOverPanel.opacityProperty(), 1))
        );

        popupTimeline.play();
    }

    /**
     * Datenklasse für eine Zeile in der Kategorie-Tabelle.
     * Enthält Informationen über eine Kategorie und deren Werte für beide Spieler.
     */
    public static class CategoryRow {
        private final String category;
        private final String status;
        private final Integer value;
        private final Integer player1Score;
        private final Integer player2Score;

        /**
         * Erstellt eine neue CategoryRow mit den angegebenen Werten.
         *
         * @param category Die Kategorie
         * @param status Der Status (z.B. "Offen", "Eingelöst")
         * @param value Der aktuelle berechnete Wert
         * @param player1Score Der Punktestand von Spieler 1 für diese Kategorie
         * @param player2Score Der Punktestand von Spieler 2 für diese Kategorie
         */
        public CategoryRow(String category, String status, Integer value, Integer player1Score, Integer player2Score) {
            this.category = category;
            this.status = status;
            this.value = value;
            this.player1Score = player1Score;
            this.player2Score = player2Score;
        }

        public String getCategory() { return category; }
        public String getStatus() { return status; }
        public Integer getValue() { return value; }
        public Integer getPlayer1Score() { return player1Score; }
        public Integer getPlayer2Score() { return player2Score; }
    }
}