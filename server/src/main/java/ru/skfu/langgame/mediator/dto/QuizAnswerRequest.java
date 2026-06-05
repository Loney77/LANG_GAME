package ru.skfu.langgame.mediator.dto;

import jakarta.validation.constraints.NotNull;

/** Запрос ответа на вопрос викторины. */
public record QuizAnswerRequest(@NotNull Long questionId, @NotNull Long optionId) {
}
