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

import java.util.ArrayList;
import java.util.List;

public class HelloController {
    @FXML private Label welcomeText;
    @FXML private Button dice1Btn, dice2Btn, dice3Btn, dice4Btn, dice5Btn;
    @FXML private CheckBox hold1Check, hold2Check, hold3Check, hold4Check, hold5Check;
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

    private dev.dhbwloerrach.kamiio.kniffel.game.Game game;
    private List<dev.dhbwloerrach.kamiio.kniffel.game.Player> players = new ArrayList<>();

    // Unicode-Zeichen für Würfel 1-6
    private static final String[] DICE_UNICODE = {"", "\u2680", "\u2681", "\u2682", "\u2683", "\u2684", "\u2685"};

    @FXML
    public void initialize() {
        players.add(new dev.dhbwloerrach.kamiio.kniffel.game.PersonPlayer("Spieler 1"));
        players.add(new dev.dhbwloerrach.kamiio.kniffel.game.PersonPlayer("Spieler 2"));
        game = new dev.dhbwloerrach.kamiio.kniffel.game.Game(players);
        game.startGame();
        categoryCombo.getItems().setAll(Category.values());
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        valueCol.setCellValueFactory(new PropertyValueFactory<>("value"));
        updateUI();
    }

    @FXML
    protected void onRollButtonClick() {
        animateDiceRoll();
    }

    private void animateDiceRoll() {
        Timeline timeline = new Timeline();
        for (int i = 0; i < 10; i++) {
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(50 * i), e -> {
                for (int d = 0; d < 5; d++) {
                    if (!getHoldCheck(d).isSelected()) {
                        int random = 1 + (int)(Math.random() * 6);
                        setDiceButton(d, random);
                    }
                }
            }));
        }
        timeline.setOnFinished(e -> {
            boolean[] held = new boolean[5];
            held[0] = hold1Check.isSelected();
            held[1] = hold2Check.isSelected();
            held[2] = hold3Check.isSelected();
            held[3] = hold4Check.isSelected();
            held[4] = hold5Check.isSelected();
            game.rollDice(held);
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

    private CheckBox getHoldCheck(int index) {
        switch (index) {
            case 0: return hold1Check;
            case 1: return hold2Check;
            case 2: return hold3Check;
            case 3: return hold4Check;
            case 4: return hold5Check;
        }
        return null;
    }

    @FXML
    protected void onDice1Click() { hold1Check.setSelected(!hold1Check.isSelected()); }
    @FXML
    protected void onDice2Click() { hold2Check.setSelected(!hold2Check.isSelected()); }
    @FXML
    protected void onDice3Click() { hold3Check.setSelected(!hold3Check.isSelected()); }
    @FXML
    protected void onDice4Click() { hold4Check.setSelected(!hold4Check.isSelected()); }
    @FXML
    protected void onDice5Click() { hold5Check.setSelected(!hold5Check.isSelected()); }

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
        // Häkchen für gehaltene Würfel nach jedem Spielerwechsel zurücksetzen
        hold1Check.setSelected(false);
        hold2Check.setSelected(false);
        hold3Check.setSelected(false);
        hold4Check.setSelected(false);
        hold5Check.setSelected(false);
        updateCategoryTable();
        if (game.isGameOver()) {
            welcomeText.setText("Spiel beendet! Gewinner: " + getWinnerName());
        }
    }

    private void updateCategoryTable() {
        dev.dhbwloerrach.kamiio.kniffel.game.Player current = game.getCurrentPlayer();
        ObservableList<CategoryRow> data = FXCollections.observableArrayList();
        for (Category cat : Category.values()) {
            boolean used = current.isCategoryUsed(cat);
            int value = current.getCategoryScore(cat);
            String status = used ? "Eingelöst" : "Offen";
            data.add(new CategoryRow(cat.name(), status, used ? value : null));
        }
        categoryTable.setItems(data);
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