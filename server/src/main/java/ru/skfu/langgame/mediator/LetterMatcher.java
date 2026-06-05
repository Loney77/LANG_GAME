package ru.skfu.langgame.mediator;

import ru.skfu.langgame.entity.TileStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Чистая логика подсветки букв в игре «Сёздл» (без побочных эффектов).
 *
 * <p>Двухпроходный алгоритм корректно обрабатывает повторяющиеся буквы:
 * сначала помечаются буквы на своих местах ({@link TileStatus#CORRECT}),
 * затем из оставшегося запаса целевого слова — присутствующие
 * ({@link TileStatus#PRESENT}); прочие — {@link TileStatus#ABSENT}.</p>
 */
public final class LetterMatcher {

    private LetterMatcher() {
    }

    /**
     * @param guess  буквы догадки
     * @param target буквы целевого слова (той же длины)
     * @return статус каждой буквы догадки
     */
    public static TileStatus[] evaluate(List<String> guess, List<String> target) {
        if (guess.size() != target.size()) {
            throw new IllegalArgumentException("guess and target length mismatch");
        }
        int n = target.size();
        TileStatus[] result = new TileStatus[n];

        // Запас букв целевого слова.
        Map<String, Integer> remaining = new HashMap<>();
        for (String letter : target) {
            remaining.merge(letter, 1, Integer::sum);
        }

        // Проход 1: буквы на своих местах.
        for (int i = 0; i < n; i++) {
            if (guess.get(i).equals(target.get(i))) {
                result[i] = TileStatus.CORRECT;
                remaining.merge(guess.get(i), -1, Integer::sum);
            }
        }

        // Проход 2: присутствующие/отсутствующие.
        for (int i = 0; i < n; i++) {
            if (result[i] != null) {
                continue;
            }
            String letter = guess.get(i);
            Integer left = remaining.get(letter);
            if (left != null && left > 0) {
                result[i] = TileStatus.PRESENT;
                remaining.merge(letter, -1, Integer::sum);
            } else {
                result[i] = TileStatus.ABSENT;
            }
        }
        return result;
    }
}
