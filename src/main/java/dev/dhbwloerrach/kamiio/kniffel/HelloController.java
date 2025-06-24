package dev.dhbwloerrach.kamiio.kniffel;

import dev.dhbwloerrach.kamiio.kniffel.game.*;
import dev.dhbwloerrach.kamiio.kniffel.utils.KniffelScorer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;

import java.util.ArrayList;
import java.util.List;

public class HelloController {
    @FXML private Label welcomeText;
    @FXML private Button dice1Btn, dice2Btn, dice3Btn, dice4Btn, dice5Btn;
    @FXML private Label rollsLeftLabel;
    @FXML private ComboBox<Category> categoryCombo;
    @FXML private Button submitCategoryButton;
    @FXML private Label playerLabel;
    @FXML private Label scoreLabel;
    @FXML private TableView<Player> scoreTable;
    @FXML private TableView<CategoryRow> categoryTable;
    @FXML private TableColumn<CategoryRow, String> categoryCol;
    @FXML private TableColumn<CategoryRow, String> statusCol;
    @FXML private TableColumn<CategoryRow, Integer> valueCol;
    @FXML private VBox nameInputVBox;
    @FXML private VBox mainVBox;
    @FXML private TextField player1NameField;
    @FXML private TextField player2NameField;
    @FXML private Button startGameButton;

    private List<dev.dhbwloerrach.kamiio.kniffel.game.Player> players = new ArrayList<>();
    private dev.dhbwloerrach.kamiio.kniffel.game.Game game;
    @FXML private boolean[] diceToRoll = new boolean[5];
    @FXML private boolean[] diceHeld = new boolean[5];
    private boolean firstRollDone = false;

    // Unicode-Zeichen für Würfel 1-6
    private static final String[] DICE_UNICODE = {"", "\u2680", "\u2681", "\u2682", "\u2683", "\u2684", "\u2685"};
    private static final int MAX_ROLLS = 3;

    @FXML
    public void initialize() {
        // Zeige Namensdialog, verstecke Haupt-UI
        nameInputVBox.setVisible(true);
        mainVBox.setVisible(false);
        // Initialisiere Kategorie-ComboBox und Tabelle direkt beim Start
        if (categoryCombo != null) {
            categoryCombo.getItems().setAll(dev.dhbwloerrach.kamiio.kniffel.game.Category.values());
        }
        if (categoryTable != null) {
            if (categoryCol != null) categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
            if (statusCol != null) statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
            if (valueCol != null) valueCol.setCellValueFactory(new PropertyValueFactory<>("value"));
            updateCategoryTable();
        }
    }

    @FXML
    protected void onStartGameClick() {
        String name1 = player1NameField.getText().trim();
        String name2 = player2NameField.getText().trim();
        if (name1.isEmpty()) name1 = "Spieler 1";
        if (name2.isEmpty()) name2 = "Spieler 2";
        players.clear();
        players.add(new dev.dhbwloerrach.kamiio.kniffel.game.PersonPlayer(name1));
        players.add(new dev.dhbwloerrach.kamiio.kniffel.game.PersonPlayer(name2));
        game = new dev.dhbwloerrach.kamiio.kniffel.game.Game(players);
        game.startGame();
        nameInputVBox.setVisible(false);
        mainVBox.setVisible(true);
        // Setze Kategorie-ComboBox und Tabelle nach Spielstart erneut
        categoryCombo.getItems().setAll(dev.dhbwloerrach.kamiio.kniffel.game.Category.values());
        updateCategoryTable();
        updateUI();
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
        String style = diceHeld[index] ? "-fx-background-color: #90ee90;" : "";
        switch (index) {
            case 0: dice1Btn.setStyle("-fx-font-size: 36px;" + style); break;
            case 1: dice2Btn.setStyle("-fx-font-size: 36px;" + style); break;
            case 2: dice3Btn.setStyle("-fx-font-size: 36px;" + style); break;
            case 3: dice4Btn.setStyle("-fx-font-size: 36px;" + style); break;
            case 4: dice5Btn.setStyle("-fx-font-size: 36px;" + style); break;
        }
    }

    @FXML
    protected void onRollButtonClick() {
        firstRollDone = true;
        animateDiceRoll();
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
            game.nextTurn();
            updateUI();
        }
    }

    private void updateUI() {
        dev.dhbwloerrach.kamiio.kniffel.game.Player current = game.getCurrentPlayer();
        setDiceButton(0, game.getDiceList().get(0).getValue());
        setDiceButton(1, game.getDiceList().get(1).getValue());
        setDiceButton(2, game.getDiceList().get(2).getValue());
        setDiceButton(3, game.getDiceList().get(3).getValue());
        setDiceButton(4, game.getDiceList().get(4).getValue());
        rollsLeftLabel.setText("Würfe übrig: " + game.getRollsLeft());
        playerLabel.setText("Am Zug: " + current.getName());
        scoreLabel.setText("Punkte: " + current.getScore());
        // Nach jedem Spielerwechsel alle Würfel auf nicht gehalten zurücksetzen und firstRollDone zurücksetzen
        for (int i = 0; i < 5; i++) {
            diceHeld[i] = false;
            updateDiceButtonStyle(i);
        }
        if (game.getRollsLeft() == MAX_ROLLS) {
            firstRollDone = false;
            welcomeText.setText("Bitte zuerst würfeln!");
        } else if (game.isGameOver()) {
            welcomeText.setText("Spiel beendet! Gewinner: " + getWinnerName());
        } else {
            welcomeText.setText("Willkommen zu Kniffel!");
        }
        updateCategoryTable();
    }

    private void updateCategoryTable() {
        if (categoryTable == null) return;
        if (game == null) {
            // Zeige alle Kategorien als offen und leer, solange kein Spiel läuft
            ObservableList<CategoryRow> data = FXCollections.observableArrayList();
            for (dev.dhbwloerrach.kamiio.kniffel.game.Category cat : dev.dhbwloerrach.kamiio.kniffel.game.Category.values()) {
                data.add(new CategoryRow(cat.name(), "Offen", null));
            }
            categoryTable.setItems(data);
            return;
        }
        dev.dhbwloerrach.kamiio.kniffel.game.Player current = game.getCurrentPlayer();
        ObservableList<CategoryRow> data = FXCollections.observableArrayList();
        List<Dice> dice = game.getDiceList();
        boolean noRoll = !firstRollDone;
        for (dev.dhbwloerrach.kamiio.kniffel.game.Category cat : dev.dhbwloerrach.kamiio.kniffel.game.Category.values()) {
            boolean used = current.isCategoryUsed(cat);
            int value = current.getCategoryScore(cat);
            String status = used ? "Eingelöst" : "Offen";
            Integer displayValue;
            if (used) {
                displayValue = value;
            } else if (noRoll) {
                displayValue = null;
            } else {
                displayValue = dev.dhbwloerrach.kamiio.kniffel.utils.KniffelScorer.calculateScore(cat, dice);
            }
            data.add(new CategoryRow(cat.name(), status, displayValue));
        }
        categoryTable.setItems(data);
        // Markiere bereits eingelöste Kategorien in der Tabelle
        categoryTable.setRowFactory(tv -> new TableRow<CategoryRow>() {
            @Override
            protected void updateItem(CategoryRow item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else if ("Eingelöst".equals(item.getStatus())) {
                    setStyle("-fx-background-color: #e0e0e0; -fx-font-weight: bold;");
                } else {
                    setStyle("");
                }
            }
        });
    }

    private String getWinnerName() {
        return players.stream().max((a, b) -> Integer.compare(a.getScore(), b.getScore())).get().getName();
    }

    public static class CategoryRow {
        private final String category;
        private final String status;
        private final Integer value;
        public CategoryRow(String category, String status, Integer value) {
            this.category = category;
            this.status = status;
            this.value = value;
        }
        public String getCategory() { return category; }
        public String getStatus() { return status; }
        public Integer getValue() { return value; }
    }
}