## Programmentwurf Gruppe B

### 📄 Projektidee

**Kniffel – ein Java-gestütztes Desktop-Würfelspiel**

In diesem Projekt wird das Würfelspiel **Kniffel** als Java-Desktop-Anwendung umgesetzt.
Ziel des Spiels ist es, durch geschicktes Würfeln und gezielte Auswahl von Kategorien die höchste Punktzahl zu erreichen.

Geplant ist eine **grafische Oberfläche** (z. B. mit JavaFX oder Swing) und die Möglichkeit, sowohl **im Multiplayer** als auch **im Einzelspieler gegen den Computer** zu spielen.

Kniffel eignet sich hervorragend, um objektorientierte Programmierung praxisnah umzusetzen und gleichzeitig eine interaktive, unterhaltsame Anwendung zu entwickeln.

---

### ✅ Anforderungen & Umsetzung

- **UML-Klassendiagramm:** Modellierung der verwendeten Klassen.
- **Namenskonventionen:** CamelCase für Variablen, PascalCase für Klassen.
- **Schleifen:** Für Würfelrunden, Spielerwechsel usw.
- **Bedingte Anweisungen:** Regelprüfung, Gewinnerermittlung usw.
- **Komplexe Datenstrukturen:** Werden umgesetzt.
- **Klassen:** `Player`, `Game`, …
- **Zugriffskontrolle:** Wird bei den Klassen verwendet.
- **Vererbung:**
  - `Player` (abstract) → `PersonPlayer`, `ComputerPlayer`
- **Abstrakte Klassen / Interfaces:** Werden umgesetzt.
- **Paketstruktur:** Aufteilung in `Utils`, `UI`, `Game` usw.
- **Exception Handling:** Für Eingabefehler, ungültige Zustände usw.
- **JavaDoc:** Wird verwendet.
- **Unit-Tests:** Für Würfellogik, Computerspieler usw.
- **Build-Tools:** Gradle.
- **Externe Java Libraries:** Auf jeden Fall für die UI.

---
