package ru.langgame.mediator.dto;

import java.util.List;

/**
 * DTO вопроса викторины (правильный вариант не помечен).
 *
 * @param questionId   идентификатор вопроса
 * @param questionText текст вопроса
 * @param options      варианты ответа
 */
public record QuizQuestionDto(Long questionId, String questionText, List<Option> options) {

    /** Вариант ответа. */
    public record Option(Long id, String text) {
    }
}
