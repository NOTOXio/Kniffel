# Änderungen am Kniffel-Projekt

## Korrigierte UI-Elemente

### 1. Shadow-Effekte angepasst

- Alle `dropshadow(three-pass-box, ...)` wurden zu `dropshadow(gaussian, ...)` geändert
- Parameter wurden angepasst: `(gaussian, rgba(color, opacity), size, 0.0, 0, offset)`
- Sorgt für korrekte Darstellung der Schatten ohne Abschneidungen

### 2. Würfel-Button-Styling

- Konsistentes Styling für alle fünf Würfel-Buttons
- Verbesserte Hervorhebung der gehaltenen Würfel (hellgrün mit grünem Rand)
- Basis-Style und dynamisches Styling für bessere Wartbarkeit

### 3. Spieler-Box-Styling

- Weiße Hintergrundfarbe für besseren Kontrast
- Konsistente Rundungen (border-radius und background-radius)
- Verbesserte Hervorhebung des aktiven Spielers mit grünem Hintergrund und Rand

### 4. Button-Styling

- Konsistente Schatten für alle Buttons
- Angepasste Parameter für bessere visuelle Wirkung
- Reduzierte Opazität der Schatten für subtileren Effekt

### 5. Eingabe-Dialog

- Schatten-Effekt korrigiert für bessere Darstellung

## Code-Verbesserungen

### 1. Controller-Methoden

- `updateActivePlayerHighlight()` für konsistentes Styling angepasst
- `updateDiceButtonStyle()` mit verbesserten visuellen Effekten

### 2. Styling-Konsistenz

- Einheitliche Farbpalette in allen UI-Elementen
- Konsistente Verwendung von Radius und Padding
- Verbesserte visuelle Hierarchie durch differenzierte Schatten

Diese Änderungen verbessern die visuelle Konsistenz und Benutzerfreundlichkeit des Spiels erheblich. Alle Elemente folgen nun einer einheitlichen Design-Sprache und sorgen für ein professionelleres Erscheinungsbild.
