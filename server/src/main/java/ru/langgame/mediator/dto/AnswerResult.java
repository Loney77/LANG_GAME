package ru.langgame.mediator.dto;

/**
 * Результат проверки одноходового ответа (анаграмма/викторина).
 *
 * @param correct       верен ли ответ
 * @param score         начисленные очки
 * @param correctAnswer правильный ответ (раскрывается после ответа)
 */
public record AnswerResult(boolean correct, int score, String correctAnswer) {
}
