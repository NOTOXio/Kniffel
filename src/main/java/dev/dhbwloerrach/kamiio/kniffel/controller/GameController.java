package dev.dhbwloerrach.kamiio.kniffel.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import dev.dhbwloerrach.kamiio.kniffel.game.Category;
import dev.dhbwloerrach.kamiio.kniffel.game.ComputerPlayer;
import dev.dhbwloerrach.kamiio.kniffel.game.Game;
import dev.dhbwloerrach.kamiio.kniffel.game.Player;
import dev.dhbwloerrach.kamiio.kniffel.model.CategoryRow;
import dev.dhbwloerrach.kamiio.kniffel.ui.AnimationHelper;
import dev.dhbwloerrach.kamiio.kniffel.ui.DiceButton;
import dev.dhbwloerrach.kamiio.kniffel.ui.GameOverPanel;
import dev.dhbwloerrach.kamiio.kniffel.utils.KniffelScorer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
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
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Hauptcontroller für das Kniffel-Spiel.
 * Verwaltet die Benutzeroberfläche und interagiert mit der Spiellogik.
 */
public class GameController implements GameOverPanel.GameOverPanelHandler {
    @FXML private Label welcomeText;
    @FXML private Button dice1Btn, dice2Btn, dice3Btn, dice4Btn, dice5Btn;
    @FXML private Label rollsLeftLabel;
    @FXML private ComboBox<Category> categoryCombo;
    @FXML private Button submitCategoryButton;
    @FXML private Label playerLabel;

    // UI-Elemente
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
    @FXML private ToggleButton computerOpponentCheckBox;

    private final List<Player> players = new ArrayList<>();
    private Game game;
    private final boolean[] diceHeld = new boolean[5];
    private boolean firstRollDone = false;

    // Hilfsobjekte
    private GameHistoryManager historyManager;
    private GameSetupController setupController;

    // Konstanten
    private static final int MAX_ROLLS = 3;

    /**
     * Initialisiert die Benutzeroberfläche beim Start.
     */
    @FXML
    public void initialize() {
        // Hilfscontroller initialisieren
        historyManager = new GameHistoryManager(gameHistoryArea);
        setupController = new GameSetupController(
                player1NameField,
                player2NameField,
                computerOpponentCheckBox,
                player2Box_input,
                nameInputVBox,
                mainVBox);

        // Würfel-Buttons initialisieren
        initializeDiceButtons();

        // UI-Elemente initialisieren
        initializeUI();
    }

    /**
     * Initialisiert die Würfel-Buttons.
     */
    private void initializeDiceButtons() {
        Button[] buttons = {dice1Btn, dice2Btn, dice3Btn, dice4Btn, dice5Btn};

        for (int i = 0; i < buttons.length; i++) {
            final int index = i;
            buttons[i].setText(DiceButton.DICE_UNICODE[0]);
            buttons[i].setOnAction(e -> {
                if (firstRollDone) {
                    diceHeld[index] = !diceHeld[index];
                    updateDiceButtonStyle(index);
                }
            });
        }
    }

    /**
     * Initialisiert die UI-Elemente des Spiels.
     */
    private void initializeUI() {
        // Zeige Namensdialog, verstecke Haupt-UI
        nameInputVBox.setVisible(true);
        mainVBox.setVisible(false);

        // Computer-Aktions-Label anfangs ausblenden
        if (computerActionLabel != null) {
            computerActionLabel.setVisible(false);
        }

        // Initialisiere Kategorie-ComboBox
        if (categoryCombo != null) {
            categoryCombo.getItems().setAll(Category.values());
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
            updateSubmitButtonState();
        }

        // Initialisiere Kategorie-Tabelle
        if (categoryTable != null) {
            categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
            statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
            valueCol.setCellValueFactory(new PropertyValueFactory<>("value"));

            // Spalten für Spieler-Punktestände
            player1Col.setCellValueFactory(new PropertyValueFactory<>("player1Score"));
            player2Col.setCellValueFactory(new PropertyValueFactory<>("player2Score"));

            // Tabelle nicht anklickbar machen und Focus-Shadow entfernen
            categoryTable.setMouseTransparent(true); // Macht die Tabelle nicht anklickbar
            categoryTable.setFocusTraversable(false); // Verhindert, dass die Tabelle den Fokus erhält

            // Entferne den Focus-Style
            categoryTable.setStyle(categoryTable.getStyle() +
                "-fx-faint-focus-color: transparent; " +
                "-fx-focus-color: transparent; " +
                "-fx-effect: dropshadow(gaussian, rgba(203, 213, 225, 0.5), 8, 0.0, 0, 2);");

            updateCategoryTable();
        }

        // Setze leere Würfel am Anfang
        if (dice1Btn != null) dice1Btn.setText(DiceButton.DICE_UNICODE[0]);
        if (dice2Btn != null) dice2Btn.setText(DiceButton.DICE_UNICODE[0]);
        if (dice3Btn != null) dice3Btn.setText(DiceButton.DICE_UNICODE[0]);
        if (dice4Btn != null) dice4Btn.setText(DiceButton.DICE_UNICODE[0]);
        if (dice5Btn != null) dice5Btn.setText(DiceButton.DICE_UNICODE[0]);
    }

    /**
     * Wird aufgerufen, wenn der Spielstart-Button geklickt wird.
     * Initialisiert das Spiel mit den eingegebenen Spielernamen.
     */
    @FXML
    protected void onStartGameClick() {
        // Spieler erstellen
        players.clear();
        players.addAll(setupController.createPlayers());

        // Spiel initialisieren (Singleton-Pattern)
        game = Game.getInstance(players);
        game.startGame();

        // UI umschalten
        setupController.showGameView();

        // Spieler-Labels setzen
        player1NameLabel.setText(players.get(0).getName());
        player2NameLabel.setText(players.get(1).getName());

        // Aktualisiere die Namen der Spieler in den Tabellenspalten
        player1Col.setText(players.get(0).getName());
        player2Col.setText(players.get(1).getName());

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
        boolean isComputerOpponent = players.get(1) instanceof ComputerPlayer;
        historyManager.reset("Neues Spiel gestartet: " + players.get(0).getName() + " vs. " + players.get(1).getName());

        if (isComputerOpponent) {
            historyManager.addEntry(players.get(0).getName() + " spielt gegen den Computer (" + players.get(1).getName() + ")");
        }

        // Aktualisiere die gesamte UI und setze die Würfel zurück (neuer Spielbeginn)
        updateUI(true);
    }

    /**
     * Aktualisiert die Anzeige der verfügbaren Kategorien in der ComboBox.
     * Entfernt bereits verwendete Kategorien.
     */
    private void updateAvailableCategories() {
        if (categoryCombo == null || game == null) return;

        Player current = game.getCurrentPlayer();

        // Filtere bereits verwendete Kategorien heraus
        List<Category> availableCategories = Arrays.stream(Category.values())
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

        Player player1 = players.get(0);
        player1ScoreLabel.setText(player1.getScore() + " Punkte");

        if (players.size() > 1) {
            Player player2 = players.get(1);
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
        String inactiveStyle = "-fx-background-color: white; -fx-padding: 16; -fx-background-radius: 12; " +
                               "-fx-border-color: #e2e8f0; -fx-border-radius: 12; -fx-border-width: 1.5; " +
                               "-fx-effect: dropshadow(gaussian, rgba(203, 213, 225, 0.5), 6, 0.0, 0, 2); " +
                               "-fx-background-insets: 0; -fx-border-insets: 0;";

        String activeStyle = "-fx-background-color: #dcfce7; -fx-padding: 16; -fx-background-radius: 12; " +
                            "-fx-border-color: #22c55e; -fx-border-radius: 12; -fx-border-width: 2; " +
                            "-fx-effect: dropshadow(gaussian, rgba(34, 197, 94, 0.3), 8, 0.0, 0, 3); " +
                            "-fx-background-insets: 0; -fx-border-insets: 0;";

        player1Box.setStyle(inactiveStyle);
        player2Box.setStyle(inactiveStyle);

        // Hebe aktiven Spieler hervor
        if (currentPlayerIndex == 0) {
            player1Box.setStyle(activeStyle);
        } else if (currentPlayerIndex == 1) {
            player2Box.setStyle(activeStyle);
        }
    }

    // Die Klick-Handler für die Würfel werden in initializeDiceButtons() dynamisch erstellt

    /**
     * Aktualisiert das Aussehen eines Würfel-Buttons basierend auf seinem Halte-Status.
     *
     * @param index Der Index des zu aktualisierenden Würfels (0-4)
     */
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

    /**
     * Ereignishandler für den Klick auf den Würfel-Button.
     */
    @FXML
    protected void onRollButtonClick() {
        firstRollDone = true;
        animateDiceRoll();
        updateSubmitButtonState();
        // Aktualisiere den Zustand des Würfel-Buttons
        updateRollButtonState();
    }

    /**
     * Animiert das Würfeln der nicht gehaltenen Würfel.
     */
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
            // Würfel beim Würfeln nicht zurücksetzen (false)
            updateUI(false);
            // Aktualisiere den Status des Würfel-Buttons nach dem Würfeln
            updateRollButtonState();
        });
        timeline.play();
    }

    /**
     * Setzt den Wert eines Würfel-Buttons.
     *
     * @param index Der Index des Würfels (0-4)
     * @param value Der neue Würfelwert (1-6)
     */
    private void setDiceButton(int index, int value) {
        switch (index) {
            case 0: dice1Btn.setText(DiceButton.DICE_UNICODE[value]); break;
            case 1: dice2Btn.setText(DiceButton.DICE_UNICODE[value]); break;
            case 2: dice3Btn.setText(DiceButton.DICE_UNICODE[value]); break;
            case 3: dice4Btn.setText(DiceButton.DICE_UNICODE[value]); break;
            case 4: dice5Btn.setText(DiceButton.DICE_UNICODE[value]); break;
        }
    }

    /**
     * Ereignishandler für den Klick auf den Kategorie-Auswahl-Button.
     */
    @FXML
    protected void onSubmitCategoryClick() {
        Category cat = categoryCombo.getValue();
        Player current = game.getCurrentPlayer();
        if (cat != null && !current.isCategoryUsed(cat)) {
            int points = KniffelScorer.calculateScore(cat, game.getDiceList());
            current.addScore(cat, points);

            // Spielzug protokollieren
            String diceValues = game.getDiceList().stream()
                .map(dice -> String.valueOf(dice.getValue()))
                .collect(Collectors.joining(", "));

            historyManager.addEntry(current.getName() + " wählt " + cat.getDisplayName() +
                             " mit Würfeln [" + diceValues + "] für " + points + " Punkte");

            // Zurücksetzen der UI-Elemente
            categoryCombo.setValue(null);
            firstRollDone = false;

            game.nextTurn();
            // Nach Kategorie-Auswahl die Würfel zurücksetzen (true)
            updateUI(true);

            // Überprüfe, ob der nächste Spieler ein Computer ist
            if (!game.isGameOver() && game.getCurrentPlayer() instanceof ComputerPlayer) {
                // Kurze Verzögerung, damit der Spieler den Wechsel sieht
                Platform.runLater(() -> {
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
        if (!game.isGameOver() && game.getCurrentPlayer() instanceof ComputerPlayer) {
            ComputerPlayer computerPlayer = (ComputerPlayer) game.getCurrentPlayer();

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

                                    historyManager.addEntry("Computer wählt " + bestCategory.getDisplayName() +
                                                   " mit Würfeln [" + diceValues + "] für " + points + " Punkte");

                                    game.nextTurn();
                                    // Nach Kategorie-Auswahl die Würfel zurücksetzen (true)
                                    updateUI(true);

                                    // Prüfe rekursiv, ob der nächste Spieler wieder ein Computer ist
                                    if (!game.isGameOver() && game.getCurrentPlayer() instanceof ComputerPlayer) {
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

    /**
     * Animiert das Würfeln für den Computer-Spieler.
     */
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
            // Würfel beim Würfeln nicht zurücksetzen (false)
            updateUI(false);
            // Aktualisiere den Status des Würfel-Buttons nach dem Würfeln
            updateRollButtonState();
        });
        timeline.play();
    }

    // Diese Methode wurde entfernt, da sie nie direkt aufgerufen wird und updateUI(true) redundant ist

    /**
     * Aktualisiert die gesamte Benutzeroberfläche mit dem aktuellen Spielstand.
     *
     * @param resetDiceHeld Gibt an, ob die festgehaltenen Würfel zurückgesetzt werden sollen (true bei Spielerwechsel)
     */
    private void updateUI(boolean resetDiceHeld) {
        if (game == null) return;

        Player current = game.getCurrentPlayer();

        // Würfel aktualisieren
        for (int i = 0; i < 5; i++) {
            setDiceButton(i, game.getDiceList().get(i).getValue());
        }

        // Informationen aktualisieren
        rollsLeftLabel.setText("Würfe übrig: " + game.getRollsLeft());
        playerLabel.setText("Am Zug: " + current.getName());

        // Spielerstände aktualisieren
        updatePlayerScores();

        // Aktiven Spieler hervorheben
        updateActivePlayerHighlight();

        // Würfel zurücksetzen, aber nur bei Spielerwechsel
        if (resetDiceHeld) {
            for (int i = 0; i < 5; i++) {
                diceHeld[i] = false;
                updateDiceButtonStyle(i);
            }
        }

        // Wenn noch nicht gewürfelt wurde, zeige leere Würfel
        if (!firstRollDone) {
            for (int i = 0; i < 5; i++) {
                setDiceButton(i, 0);
            }
        }

        // Spiel-Status überprüfen und UI entsprechend aktualisieren
        if (game.isGameOver()) {
            // Spiel zu Ende
            Player winner = game.getWinner();
            welcomeText.setText("🏆 Spiel beendet! Gewinner: " + winner.getName());

            // Endstand zur Spielhistorie hinzufügen
            StringBuilder sb = new StringBuilder("Endstand:\n");
            for (Player p : game.getPlayers()) {
                sb.append(p.getName()).append(": ").append(p.getScore()).append(" Punkte\n");
            }
            historyManager.addEntry("Spiel beendet. Gewinner: " + winner.getName(), true);
            historyManager.addEntry(sb.toString());

            // Game Over Panel mit Revanche-Option anzeigen
            showGameOverPanel(winner);

            // Kategorie-Auswahl deaktivieren
            categoryCombo.setDisable(true);

        } else if (game.getRollsLeft() == MAX_ROLLS) {
            // Neuer Zug, noch nicht gewürfelt
            firstRollDone = false;
            welcomeText.setText(current.getName() + " ist am Zug - Bitte würfeln");

            // Kategorien aktualisieren
            updateAvailableCategories();

            // Button-Status aktualisieren
            updateSubmitButtonState();
            updateRollButtonState();

            // Starte Computer-Zug, wenn ein Computer am Zug ist
            if (current instanceof ComputerPlayer) {
                // Computer-Aktions-Label anzeigen
                computerActionLabel.setVisible(true);
                computerActionLabel.setText("Computer überlegt...");

                welcomeText.setText("Computer ist am Zug - Wurf " + (MAX_ROLLS - game.getRollsLeft() + 1) + " von " + MAX_ROLLS);

                // Verzögerung, damit der Spieler sehen kann, dass der Computer am Zug ist
                Platform.runLater(() -> {
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

        // Status-Spalte mit eigenem Cell Factory für farbliche Markierung
        statusCol.setCellFactory(column -> {
            return new javafx.scene.control.TableCell<CategoryRow, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);
                        if ("Eingelöst".equals(item)) {
                            setStyle("-fx-text-fill: #16a34a; -fx-font-weight: bold;"); // Grüne Textfarbe für "Eingelöst"
                        } else {
                            setStyle("-fx-text-fill: #64748b;"); // Graue Textfarbe für "Offen"
                        }
                    }
                }
            };
        });

        ObservableList<CategoryRow> data = FXCollections.observableArrayList();

        if (game == null) {
            // Zeige alle Kategorien als offen und leer, solange kein Spiel läuft
            for (Category cat : Category.values()) {
                data.add(new CategoryRow(cat.getDisplayName(), "Offen", null, null, null));
            }
            categoryTable.setItems(data);
            return;
        }

        Player current = game.getCurrentPlayer();
        List<dev.dhbwloerrach.kamiio.kniffel.Dice> dice = game.getDiceList();
        boolean noRoll = !firstRollDone;

        // Hole beide Spieler für die Anzeige der Punkte
        Player player1 = players.get(0);
        Player player2 = players.size() > 1 ? players.get(1) : null;

        for (Category cat : Category.values()) {
            boolean used = current.isCategoryUsed(cat);
            String status = used ? "Eingelöst" : "Offen";

            // Aktueller potentieller Wert für die Kategorie
            Integer currentValue;
            if (used) {
                currentValue = current.getCategoryScore(cat);
            } else if (noRoll) {
                currentValue = null;
            } else {
                currentValue = KniffelScorer.calculateScore(cat, dice);
            }

            // Punkte für beide Spieler
            Integer player1Score = player1.isCategoryUsed(cat) ? player1.getCategoryScore(cat) : null;
            Integer player2Score = player2 != null && player2.isCategoryUsed(cat) ? player2.getCategoryScore(cat) : null;

            data.add(new CategoryRow(cat.getDisplayName(), status, currentValue, player1Score, player2Score));
        }

        categoryTable.setItems(data);        // Farbliche Markierung für bereits eingelöste Kategorien hinzufügen
        categoryTable.setRowFactory(tv -> new javafx.scene.control.TableRow<CategoryRow>() {
            @Override
            protected void updateItem(CategoryRow item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setStyle("");
                    return;
                }

                // Wenn Status "Eingelöst" ist, die Zeile farblich markieren
                if (item.getStatus().equals("Eingelöst")) {
                    // Deutlichere Farbgebung für eingelöste Kategorien (kräftigeres Grün mit Border)
                    setStyle("-fx-background-color: #bbf7d0; " + // Kräftigeres Grün
                             "-fx-border-color: #86efac; " +     // Grüner Rahmen
                             "-fx-border-width: 0 0 1 0; " +     // Nur untere Kante
                             "-fx-font-weight: normal;");        // Normale Schrift
                } else {
                    // Weißer Hintergrund für offene Kategorien
                    setStyle("-fx-background-color: white;");
                }
            }
        });
    }

    /**
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

    /**
     * Zeigt das Spielende-Panel mit dem Gewinner an.
     *
     * @param winner Der Gewinner des Spiels
     */
    private void showGameOverPanel(Player winner) {
        boolean isComputer = winner instanceof ComputerPlayer;
        Pane rootPane = (Pane) mainVBox.getScene().getRoot();

        GameOverPanel panel = new GameOverPanel(
                rootPane,
                winner.getName(),
                isComputer,
                winner.getScore(),
                this);

        panel.show();
    }

    /**
     * Startet eine neue Runde mit den gleichen Spielern (Revanche).
     * Wird durch das GameOverPanel aufgerufen.
     */
    @Override
    public void onRematchRequested() {
        // Spiel neu starten mit den gleichen Spielern
        game.startGame();

        // UI zurücksetzen
        firstRollDone = false;
        for (int i = 0; i < 5; i++) {
            diceHeld[i] = false;
            updateDiceButtonStyle(i);
        }

        // Kategorie-ComboBox zurücksetzen und aktivieren
        categoryCombo.getItems().setAll(Category.values());
        categoryCombo.setDisable(true);
        categoryCombo.setValue(null);

        // Spielhistorie-Eintrag
        historyManager.addEntry("Neue Runde gestartet (Revanche)", true);

        // UI aktualisieren (und Würfel zurücksetzen)
        updateUI(true);

        // Animation für den Neustart zeigen
        AnimationHelper.showRevancheAnimation((Pane) mainVBox.getScene().getRoot());
    }

    /**
     * Kehrt zum Hauptmenü zurück und setzt das Spiel zurück.
     * Wird durch das GameOverPanel aufgerufen.
     */
    @Override
    public void onReturnToMainMenuRequested() {
        // Spielhistorie-Eintrag
        historyManager.addEntry("Zurück zum Hauptmenü", true);

        // UI auf Startbildschirm zurücksetzen
        setupController.resetWithPlayers(game);
        setupController.showSetupView();

        // Würfel zurücksetzen
        for (int i = 0; i < 5; i++) {
            setDiceButton(i, 0);
        }

        // Welcome-Text zurücksetzen
        welcomeText.setText("Willkommen zu Kniffel!");
    }
}
