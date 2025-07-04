package dev.dhbwloerrach.kamiio.kniffel.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Testet die Funktionalität der abstrakten Player-Klasse mit einer konkreten Unterklasse.
 */
class PlayerTest {

    private Player player;

    @BeforeEach
    @SuppressWarnings("unused") // Von JUnit automatisch aufgerufen
    void setUp() {
        player = new PersonPlayer("TestSpieler");
    }

    @Test
    @DisplayName("Player sollte korrekt initialisiert werden")
    void testPlayerInitialization() {
        assertEquals("TestSpieler", player.getName(), "Spielername sollte korrekt sein");
        assertEquals(0, player.getScore(), "Anfangspunktzahl sollte 0 sein");

        // Alle Kategorien sollten als unbenutzt markiert sein
        for (Category cat : Category.values()) {
            assertFalse(player.isCategoryUsed(cat), "Kategorie " + cat + " sollte nicht verwendet sein");
        }
    }

    @Test
    @DisplayName("Konstruktor sollte bei leerem Namen eine Exception werfen")
    void testConstructorWithEmptyName() {
        IllegalArgumentException exEmpty = assertThrows(IllegalArgumentException.class, () -> new PersonPlayer(""),
                "Konstruktor sollte bei leerem Namen eine Exception werfen");
        assertTrue(exEmpty.getMessage().contains("leer"));

        IllegalArgumentException exNull = assertThrows(IllegalArgumentException.class, () -> new PersonPlayer(null),
                "Konstruktor sollte bei null-Namen eine Exception werfen");
        assertTrue(exNull.getMessage().contains("leer"));
    }

    @Test
    @DisplayName("addScore sollte Punkte korrekt hinzufügen und Kategorie als verwendet markieren")
    void testAddScore() {
        player.addScore(Category.ONES, 3);
        assertEquals(3, player.getScore(), "Gesamtpunktzahl sollte 3 sein");
        assertEquals(3, player.getCategoryScore(Category.ONES), "Punktzahl für ONES sollte 3 sein");
        assertTrue(player.isCategoryUsed(Category.ONES), "ONES sollte als verwendet markiert sein");

        // Weitere Punkte für eine andere Kategorie hinzufügen
        player.addScore(Category.TWOS, 6);
        assertEquals(9, player.getScore(), "Gesamtpunktzahl sollte 9 sein");
        assertEquals(6, player.getCategoryScore(Category.TWOS), "Punktzahl für TWOS sollte 6 sein");
        assertTrue(player.isCategoryUsed(Category.TWOS), "TWOS sollte als verwendet markiert sein");
    }

    @Test
    @DisplayName("addScore sollte bei negativen Punkten eine Exception werfen")
    void testAddScoreWithNegativePoints() {
        IllegalArgumentException exNegative = assertThrows(IllegalArgumentException.class, () -> player.addScore(Category.ONES, -1),
                "addScore sollte bei negativen Punkten eine Exception werfen");
        assertTrue(exNegative.getMessage().contains("negativ"));
    }

    @Test
    @DisplayName("addScore sollte bei bereits verwendeter Kategorie eine Exception werfen")
    void testAddScoreWithUsedCategory() {
        player.addScore(Category.ONES, 3);

        IllegalArgumentException exUsed = assertThrows(IllegalArgumentException.class, () -> player.addScore(Category.ONES, 5),
                "addScore sollte bei bereits verwendeter Kategorie eine Exception werfen");
        assertTrue(exUsed.getMessage().contains("verwendet"));
    }

    @Test
    @DisplayName("getUpperSectionScore sollte die Summe des oberen Bereichs korrekt berechnen")
    void testGetUpperSectionScore() {
        player.addScore(Category.ONES, 3);
        player.addScore(Category.TWOS, 6);
        player.addScore(Category.THREES, 9);

        assertEquals(18, player.getUpperSectionScore(), "Obere Bereichssumme sollte 18 sein");

        // Weitere Kategorien hinzufügen
        player.addScore(Category.FOURS, 8);
        player.addScore(Category.FIVES, 15);
        player.addScore(Category.SIXES, 24);

        assertEquals(65, player.getUpperSectionScore(), "Obere Bereichssumme sollte 65 sein");
    }

    @Test
    @DisplayName("hasUpperSectionBonus sollte den Bonus korrekt prüfen")
    void testHasUpperSectionBonus() {
        // Weniger als Schwellenwert
        player.addScore(Category.ONES, 3);
        player.addScore(Category.TWOS, 6);
        player.addScore(Category.THREES, 9);
        player.addScore(Category.FOURS, 8);
        player.addScore(Category.FIVES, 15);
        player.addScore(Category.SIXES, 18);

        assertEquals(59, player.getUpperSectionScore(), "Obere Bereichssumme sollte 59 sein");
        assertFalse(player.hasUpperSectionBonus(), "Bonus sollte nicht gewährt werden bei < 63 Punkten");

        // Zurücksetzen und Punktzahl über dem Schwellenwert testen
        player = new PersonPlayer("TestSpieler");
        player.addScore(Category.ONES, 3);
        player.addScore(Category.TWOS, 8);
        player.addScore(Category.THREES, 12);
        player.addScore(Category.FOURS, 16);
        player.addScore(Category.FIVES, 20);
        player.addScore(Category.SIXES, 12);

        assertEquals(71, player.getUpperSectionScore(), "Obere Bereichssumme sollte 71 sein");
        assertTrue(player.hasUpperSectionBonus(), "Bonus sollte gewährt werden bei >= 63 Punkten");
    }

    @Test
    @DisplayName("reset sollte alle Werte zurücksetzen")
    void testReset() {
        player.addScore(Category.ONES, 3);
        player.addScore(Category.TWOS, 6);
        player.addScore(Category.THREES, 9);

        // Reset aufrufen
        player.reset();

        assertEquals(0, player.getScore(), "Gesamtpunktzahl sollte 0 sein");

        // Alle Kategorien sollten als unbenutzt markiert sein
        for (Category cat : Category.values()) {
            assertFalse(player.isCategoryUsed(cat), "Kategorie " + cat + " sollte nicht verwendet sein");
            assertEquals(0, player.getCategoryScore(cat), "Punktzahl für " + cat + " sollte 0 sein");
        }
    }

    @Test
    @DisplayName("allCategoriesUsed sollte korrekt prüfen, ob alle Kategorien verwendet wurden")
    void testAllCategoriesUsed() {
        // Anfangs sollten keine Kategorien verwendet sein
        assertFalse(player.allCategoriesUsed(), "Keine Kategorien sollten verwendet sein");

        // Eine Kategorie verwenden
        player.addScore(Category.ONES, 3);
        assertFalse(player.allCategoriesUsed(), "Nicht alle Kategorien sollten verwendet sein");

        // Alle Kategorien außer einer verwenden
        for (Category cat : Category.values()) {
            if (cat != Category.ONES && cat != Category.CHANCE) {
                player.addScore(cat, 1);
            }
        }
        assertFalse(player.allCategoriesUsed(), "Nicht alle Kategorien sollten verwendet sein");

        // Letzte Kategorie verwenden
        player.addScore(Category.CHANCE, 15);
        assertTrue(player.allCategoriesUsed(), "Alle Kategorien sollten verwendet sein");
    }

    @Test
    @DisplayName("toString sollte eine sinnvolle Repräsentation liefern")
    void testToString() {
        String expected = "Player[name=TestSpieler, score=0]";
        assertEquals(expected, player.toString(), "toString sollte eine sinnvolle Repräsentation liefern");

        player.addScore(Category.ONES, 3);
        expected = "Player[name=TestSpieler, score=3]";
        assertEquals(expected, player.toString(), "toString sollte eine sinnvolle Repräsentation liefern");
    }
}
