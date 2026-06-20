package ru.langgame.mediator.dto;

/**
 * Результат проверки кроссворда.
 *
 * @param allCorrect все ли слова верны
 * @param correct    сколько слов угадано
 * @param total      всего слов
 * @param score      начисленные очки
 */
public record CrosswordResult(boolean allCorrect, int correct, int total, int score) {
}
