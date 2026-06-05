package ru.skfu.langgame.mediator.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Запрос ответа на анаграмму. */
public record AnagramAnswerRequest(@NotNull Long puzzleId, @NotBlank String answer) {
}
