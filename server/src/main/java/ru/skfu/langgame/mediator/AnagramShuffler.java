package ru.skfu.langgame.mediator;

import ru.skfu.langgame.entity.AlphabetTokenizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/** Перемешивание букв слова для анаграммы (буквы алфавита, диграфы целы). */
public final class AnagramShuffler {

    private AnagramShuffler() {
    }

    /**
     * Перемешивает буквы слова. Гарантирует, что результат отличается от исходного
     * (если в слове больше одной различной буквы).
     */
    public static List<String> shuffle(String word, Random random) {
        List<String> letters = AlphabetTokenizer.tokenize(word);
        if (letters.size() < 2 || letters.stream().distinct().count() < 2) {
            return letters;
        }
        List<String> shuffled = new ArrayList<>(letters);
        for (int attempt = 0; attempt < 10; attempt++) {
            Collections.shuffle(shuffled, random);
            if (!shuffled.equals(letters)) {
                return shuffled;
            }
        }
        return shuffled;
    }
}
