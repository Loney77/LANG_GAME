package ru.langgame.mediator.exception;

/** Догадка не является словом из словаря (→ HTTP 422). */
public class WordNotFoundException extends RuntimeException {
    public WordNotFoundException(String word) {
        super("Слово не найдено в словаре: " + word);
    }
}
