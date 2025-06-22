package dev.dhbwloerrach.kamiio.kniffel.utils;

import dev.dhbwloerrach.kamiio.kniffel.Dice;
import dev.dhbwloerrach.kamiio.kniffel.game.Category;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class KniffelScorer {
    public static int calculateScore(Category category, List<Dice> diceList) {
        int[] counts = new int[7];
        for (Dice d : diceList) counts[d.getValue()]++;
        int sum = diceList.stream().mapToInt(Dice::getValue).sum();
        switch (category) {
            case ONES: return counts[1] * 1;
            case TWOS: return counts[2] * 2;
            case THREES: return counts[3] * 3;
            case FOURS: return counts[4] * 4;
            case FIVES: return counts[5] * 5;
            case SIXES: return counts[6] * 6;
            case THREE_OF_A_KIND:
                for (int i = 1; i <= 6; i++) if (counts[i] >= 3) return sum;
                return 0;
            case FOUR_OF_A_KIND:
                for (int i = 1; i <= 6; i++) if (counts[i] >= 4) return sum;
                return 0;
            case FULL_HOUSE:
                boolean has3 = false, has2 = false;
                for (int i = 1; i <= 6; i++) {
                    if (counts[i] == 3) has3 = true;
                    if (counts[i] == 2) has2 = true;
                }
                return (has3 && has2) ? 25 : 0;
            case SMALL_STRAIGHT:
                if ((counts[1] > 0 && counts[2] > 0 && counts[3] > 0 && counts[4] > 0) ||
                    (counts[2] > 0 && counts[3] > 0 && counts[4] > 0 && counts[5] > 0) ||
                    (counts[3] > 0 && counts[4] > 0 && counts[5] > 0 && counts[6] > 0))
                    return 30;
                return 0;
            case LARGE_STRAIGHT:
                if ((counts[1] == 1 && counts[2] == 1 && counts[3] == 1 && counts[4] == 1 && counts[5] == 1) ||
                    (counts[2] == 1 && counts[3] == 1 && counts[4] == 1 && counts[5] == 1 && counts[6] == 1))
                    return 40;
                return 0;
            case KNIFFEL:
                for (int i = 1; i <= 6; i++) if (counts[i] == 5) return 50;
                return 0;
            case CHANCE:
                return sum;
            default: return 0;
        }
    }
}
