package ru.skfu.langgame.entity;

import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Доработка русских переводов словаря для показа в играх.
 *
 * <p>Исходный словарь содержит технические пометы, нумерацию смыслов и
 * перекрёстные ссылки («страд. к…», «то же, что…», «отвлеч. к инициатор»),
 * а также заимствования с аффиксом, чей перевод совпадает с корнем. Утилита
 * чистит текст для показа и отбраковывает непригодные для викторины статьи.</p>
 *
 * <p>Важно: проверки построены на {@code contains}, а не на {@code \b} —
 * в Java граница слова не работает с кириллицей (ASCII-класс {@code \w}).</p>
 */
public final class TranslationText {

    /** Ведущая нумерация смыслов: «1.», «2)», римские «I.». */
    private static final Pattern LEADING_ENUM =
            Pattern.compile("^\\s*(?:[IVX]+\\.?|\\d+[.)])\\s*");
    /** Ведущая помета-сокращение, напр. «отвлеч.», «спорт.», «и.». */
    private static final Pattern LEADING_ABBR = Pattern.compile("^[а-яёa-z]{1,9}\\.\\s*");
    /** Хвостовой грамматический маркер рода: «… ж», «… м.». */
    private static final Pattern TRAILING_GENDER = Pattern.compile("\\s+(ж|м|с|мн)\\.?$");

    /** Подстроки-маркеры перекрёстных ссылок и грамматических дериватов. */
    private static final List<String> CROSS_REF = List.of(
            "см.", "то же", "отвлеч", "понуд", "страд", "взаимн", "возвр",
            "однокр", "многокр", "уменьш", "ласк", "собир", "превосх", "сравн", "действ");

    private TranslationText() {
    }

    /** Чистый перевод для показа (без нумерации, ведущих помет, маркеров рода). */
    public static String clean(String raw) {
        if (raw == null) {
            return "";
        }
        String s = raw.strip();
        s = s.split(";")[0].strip();                 // только первый смысл
        for (int i = 0; i < 5; i++) {                // снять ведущую нумерацию и пометы
            String next = LEADING_ENUM.matcher(s).replaceFirst("");
            next = LEADING_ABBR.matcher(next).replaceFirst("");
            if (next.equals(s)) {
                break;
            }
            s = next;
        }
        s = TRAILING_GENDER.matcher(s).replaceAll("");
        s = s.replace("||", "/");                    // вариант: «бисер / бисерный»
        return s.strip().replaceAll("^[,–\\-:)]+", "").strip();
    }

    /**
     * Пригоден ли перевод как ответ в викторине: не перекрёстная ссылка,
     * не тривиальное заимствование (перевод ≈ само слово), достаточной длины.
     */
    public static boolean isQuizSuitable(String word, String rawTranslation) {
        if (word == null || rawTranslation == null) {
            return false;
        }
        String raw = rawTranslation.toLowerCase(Locale.ROOT);
        for (String marker : CROSS_REF) {
            if (raw.contains(marker)) {
                return false;
            }
        }
        if (raw.contains(" к ") || raw.startsWith("к ")) {   // «… к слову»
            return false;
        }
        String t = clean(rawTranslation).toLowerCase(Locale.ROOT);
        if (t.length() < 3 || t.startsWith("к ") || t.contains(" к ")) {
            return false;
        }
        // заимствование/дериват: слово начинается с русского корня
        String wl = word.toLowerCase(Locale.ROOT);
        String tl = t.replaceAll("[^а-яёa-z]", "");
        if (tl.length() >= 4 && (wl.equals(tl) || wl.startsWith(tl))) {
            return false;
        }
        return true;
    }
}
