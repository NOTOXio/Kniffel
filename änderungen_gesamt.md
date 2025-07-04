# Kniffel-Projekt: Gesamtübersicht der Änderungen

## Aktueller Stand des Projekts

Das Kniffel-Projekt ist ein JavaFX-basiertes Würfelspiel mit folgenden Hauptfunktionen:

- Spielmodus für 2 Spieler (Mensch vs. Mensch oder Mensch vs. Computer)
- Vollständige Implementierung aller Kniffel-Regeln und Kategorien
- Grafische Benutzeroberfläche mit Würfelanimationen
- Automatischer Computer-Gegner

## Verbesserungen an der Benutzeroberfläche

### 1. Fenstergröße und Layout

- Hauptfenster auf 1400px × 800px eingestellt
- Minimale Fensterbreite: 1200px, maximale Breite: 1600px
- Linke Tabellenseite: 750px breit
- Rechte Steuerungsseite: 550px breit
- Optimale Größenverhältnisse, um Abschneiden von UI-Elementen zu vermeiden

### 2. Visuelle Verbesserungen

- Leere Würfel werden mit einem Würfelsymbol (⬜) dargestellt
- Dynamische Titelleisten-Texte je nach Spielsituation:
  - Zu Beginn eines Zuges: "[Spielername] ist am Zug - Bitte würfeln"
  - Während des Zuges: "[Spielername] - Noch [X] Würfe übrig"
  - Bei Computerzügen: "Computer ist am Zug - Wurf [X] von 3"
  - Bei Spielende: "🏆 Spiel beendet! Gewinner: [Spielername]"
- Einheitliches Design mit abgerundeten Ecken und Schatten

### 3. Spielerinteraktion

- Würfel können durch Anklicken festgehalten werden (grün markiert)
- "Wählen"-Button wird nur aktiviert, wenn eine Kategorie ausgewählt ist
- "Würfeln"-Button wird deaktiviert, wenn keine Würfe mehr übrig sind
- Dynamische Aktualisierung der UI nach jedem Spielzug

### 4. Tabelle und Kategorien

- Tabellenspalten zeigen die tatsächlichen Spielernamen an
- Kategorien werden mit ihren aktuellen Werten dargestellt
- Bereits verwendete Kategorien werden grau markiert

## Technische Implementierungen

### 1. Button-Status-Management

- `updateRollButtonState()`: Aktiviert/deaktiviert den Würfelbutton
- `updateSubmitButtonState()`: Aktiviert/deaktiviert den Auswahl-Button für Kategorien
- Automatische Aktualisierung nach jedem Würfelwurf oder Kategorieauswahl

### 2. Würfelanimationen

- `animateDiceRoll()`: Animation für den menschlichen Spieler
- `animateComputerDiceRoll()`: Animation für den Computer-Spieler
- Nach dem Würfeln werden die Button-Stati entsprechend aktualisiert

### 3. KI-Implementierung

- Computer kann automatisch Würfe und Kategorien auswählen
- Animation der Computer-Züge mit kurzen Verzögerungen

## Ausblick und offene Punkte

- Weitere visuelle Verbesserungen könnten implementiert werden
- Spielstatistiken könnten hinzugefügt werden
- Lokalisierung für mehrere Sprachen wäre möglich
- Speichern und Laden von Spielständen könnte ergänzt werden

## Zuletzt implementierte Änderung

Die letzte Implementierung betraf die Deaktivierung des Würfel-Buttons, wenn keine Würfe mehr übrig sind. Dafür wurden folgende Methoden aktualisiert:

- `onRollButtonClick()`: Aufruf von updateRollButtonState() hinzugefügt
- `animateDiceRoll()`: Aufruf von updateRollButtonState() am Ende der Animation hinzugefügt
- `animateComputerDiceRoll()`: Aufruf von updateRollButtonState() am Ende der Animation hinzugefügt

Diese Änderungen stellen sicher, dass der Würfel-Button ausgegraut und deaktiviert wird, sobald ein Spieler alle drei Würfe verbraucht hat.

## Kompilierung und Ausführung

Das Projekt wird mit dem folgenden Maven-Befehl ausgeführt:

```bash
mvn clean compile javafx:run
```

## Autoren

- Ursprüngliches Projekt: Studierende der DHBW Lörrach
- UI-Verbesserungen und Bugfixes: GitHub Copilot
