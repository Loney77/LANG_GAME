package ru.skfu.langgame.mediator.dto;

/**
 * Результат ответа на вопрос викторины.
 *
 * @param correct         верен ли выбранный вариант
 * @param correctOptionId id правильного варианта
 * @param score           начисленные очки
 */
public record QuizAnswerResult(boolean correct, Long correctOptionId, int score) {
}
