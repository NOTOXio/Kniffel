# Kniffel Spiel

Eine JavaFX-Implementierung des beliebten Würfelspiels Kniffel (auch bekannt als Yahtzee).

## Projektstruktur

Das Projekt ist nach dem Model-View-Controller (MVC) Muster strukturiert:

### Pakete

- `dev.dhbwloerrach.kamiio.kniffel`
  - `.app` - Hauptanwendung
    - `App.java` - Startet die JavaFX-Anwendung
  - `.controller` - Controller für die UI
    - `GameController.java` - Hauptcontroller für das Spiel
    - `GameSetupController.java` - Controller für die Spieleinrichtung
    - `GameHistoryManager.java` - Verwaltet die Spielhistorie
  - `.model` - Datenmodelle
    - `CategoryRow.java` - Modell für eine Kategorie-Zeile in der Tabelle
  - `.ui` - UI-spezifische Klassen
    - `DiceButton.java` - Komponente für einen Würfel-Button
    - `GameOverPanel.java` - Panel für Spielende
    - `AnimationHelper.java` - Helper für UI-Animationen
  - `.utils` - Utilities
    - `InputException.java`
  - `.game` - Spiellogik
    - `Category.java`
    - `ComputerPlayer.java`
    - `Game.java` (Singleton)
    - `GameInterface.java`
    - `PersonPlayer.java`
    - `Player.java`

### Ressourcen

- `views/game-view.fxml` - FXML-Datei für die Benutzeroberfläche

## Ausführen des Spiels

1. Stellen Sie sicher, dass Java JDK installiert ist und `JAVA_HOME` gesetzt ist.
2. Führen Sie die Datei `start.bat` aus oder verwenden Sie den Maven-Befehl:
   ```
   mvnw clean javafx:run
   ```

## Spielablauf

1. Geben Sie die Namen der Spieler ein
2. Wählen Sie, ob Sie gegen einen Computergegner spielen möchten
3. Würfeln Sie bis zu dreimal pro Runde
4. Wählen Sie eine Kategorie, um Punkte zu erhalten
5. Gewinner ist, wer am Ende die meisten Punkte hat

## Entwicklung

Das Projekt verwendet Maven als Build-Tool und folgende Technologien:
- JavaFX für die Benutzeroberfläche
- Das Singleton-Pattern für die Game-Klasse
- Verschiedene Design-Patterns für die Spiellogik
