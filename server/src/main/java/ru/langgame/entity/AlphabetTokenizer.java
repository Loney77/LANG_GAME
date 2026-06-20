package ru.langgame.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Токенизатор карачаевского алфавита.
 *
 * <p>Диграфы <b>гъ, къ, нъ, нг, дж</b> — это сочетания двух кириллических
 * символов, обозначающие один звук и считающиеся одной буквой алфавита.
 * Токенизатор разбивает слово на «буквы» с учётом этого правила, что критично
 * для подсчёта длины слова и подсветки плиток в Sozdl.</p>
 *
 * <p>Доменная утилита слоя Entity (без внешних зависимостей).</p>
 */
public final class AlphabetTokenizer {

    /** Диграфы алфавита (две буквы = одна). */
    private static final Set<String> DIGRAPHS = Set.of("гъ", "къ", "нъ", "нг", "дж");

    private AlphabetTokenizer() {
    }

    /**
     * Разбивает слово на буквы карачаевского алфавита.
     *
     * @param word слово (любой регистр); не {@code null}
     * @return список букв; диграфы склеены в один токен
     */
    public static List<String> tokenize(String word) {
        if (word == null) {
            throw new IllegalArgumentException("word must not be null");
        }
        String w = word.strip().toLowerCase(Locale.ROOT);
        List<String> letters = new ArrayList<>();
        int i = 0;
        while (i < w.length()) {
            if (i + 1 < w.length() && DIGRAPHS.contains(w.substring(i, i + 2))) {
                letters.add(w.substring(i, i + 2));
                i += 2;
            } else {
                letters.add(String.valueOf(w.charAt(i)));
                i++;
            }
        }
        return letters;
    }

    /** Длина слова в буквах карачаевского алфавита. */
    public static int letterCount(String word) {
        return tokenize(word).size();
    }
}
