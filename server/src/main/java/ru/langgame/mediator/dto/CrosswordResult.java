package ru.langgame.mediator.dto;

import java.util.List;

/**
 * Результат проверки кроссворда.
 *
 * @param allCorrect все ли слова верны
 * @param correct    сколько слов угадано
 * @param total      всего слов
 * @param score      начисленные очки
 * @param solutions  правильные ответы (для показа после проверки)
 */
public record CrosswordResult(
        boolean allCorrect,
        int correct,
        int total,
        int score,
        List<Solution> solutions) {

    /**
     * Правильный ответ на слово кроссворда.
     *
     * @param number    номер слова
     * @param direction направление
     * @param answer    правильный ответ
     */
    public record Solution(int number, String direction, String answer) {
    }
}
