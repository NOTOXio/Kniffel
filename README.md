# Kniffel Spiel

Eine JavaFX-Implementierung des beliebten Würfelspiels Kniffel (auch bekannt als Yahtzee).

## Projektstruktur

Das Projekt ist nach dem Model-View-Controller (MVC) Muster strukturiert und verwendet Java 9 Module:

### Pakete

- `dev.dhbwloerrach.kamiio.kniffel`
  - **Hauptpaket**
    - `App.java` - Startet die JavaFX-Anwendung
    - `Dice.java` - Würfel-Klasse mit 5 Würfeln
    - `HelloController.java` - Controller für die Benutzeroberfläche
  - `.controller` - Controller für die UI
    - `GameController.java` - Hauptcontroller für das Spiel
    - `GameSetupController.java` - Controller für die Spieleinrichtung
  - `.model` - Datenmodelle
    - `CategoryRow.java` - Modell für eine Kategorie-Zeile in der Tabelle
  - `.ui` - UI-spezifische Klassen
    - `DiceButton.java` - Komponente für einen Würfel-Button
    - `GameOverPanel.java` - Panel für Spielende
    - `AnimationHelper.java` - Helper für UI-Animationen
  - `.utils` - Utilities
    - `InputException.java` - Benutzerdefinierte Exception-Klasse
    - `KniffelScorer.java` - Punkteberechnung für alle Kategorien
  - `.game` - Spiellogik
    - `Category.java` - Enum für alle Kniffel-Kategorien
    - `ComputerPlayer.java` - KI-Spieler Implementierung
    - `Game.java` - Hauptspiel-Klasse (Singleton Pattern)
    - `GameInterface.java` - Interface für Spiellogik
    - `PersonPlayer.java` - Menschlicher Spieler
    - `Player.java` - Abstrakte Basisklasse für Spieler

### Ressourcen

- `views/game-view.fxml` - FXML-Datei für die Benutzeroberfläche

## Technische Anforderungen

- **Java**: JDK 17 oder höher (projekt konfiguriert für Java 17 LTS)
- **Maven**: 3.6+ für Build-Management
- **JavaFX**: Automatisch über Maven verwaltet (Version 21.0.2 und 17.0.6)

### Zusätzliche Abhängigkeiten

- **ControlsFX**: Erweiterte UI-Komponenten
- **ValidatorFX**: Eingabevalidierung
- **Ikonli**: Icon-Framework für JavaFX
- **BootstrapFX**: CSS-Framework
- **JUnit 5**: Testing Framework (40 Unit Tests)

## Ausführen des Spiels

### Voraussetzungen

1. Stellen Sie sicher, dass Java JDK 17+ installiert ist
2. Maven muss verfügbar sein
3. `JAVA_HOME` sollte korrekt gesetzt sein

### Maven-Befehle

```bash
# Projekt kompilieren und JavaFX-Anwendung starten
mvn clean compile javafx:run

# Nur kompilieren
mvn clean compile

# Tests ausführen
mvn test
```

## Spielablauf

1. Geben Sie die Namen der Spieler ein
2. Wählen Sie, ob Sie gegen einen Computergegner spielen möchten
3. Würfeln Sie bis zu dreimal pro Runde
4. Wählen Sie eine Kategorie, um Punkte zu erhalten
5. Gewinner ist, wer am Ende die meisten Punkte hat

## Entwicklung

Das Projekt verwendet Maven als Build-Tool und folgende Technologien:

- **JavaFX**: Moderne UI-Framework für Java Desktop-Anwendungen
- **Java 9 Module System**: Modularität mit module-info.java
- **Singleton Pattern**: Für die Game-Klasse zur zentralen Spielverwaltung
- **MVC Pattern**: Saubere Trennung von Darstellung, Steuerung und Datenmodell
- **Maven**: Build-Management mit javafx-maven-plugin
- **JUnit 5**: Umfassende Testabdeckung mit 40 Unit Tests

### Build-Konfiguration

- **Java Release**: 17 LTS (compiler.release in pom.xml)
- **Maven Compiler Plugin**: 3.13.0
- **JavaFX Maven Plugin**: 0.0.8
- **Surefire Plugin**: 3.2.5 für Testausführung
- **Javadoc Plugin**: 3.6.3 für API-Dokumentation
