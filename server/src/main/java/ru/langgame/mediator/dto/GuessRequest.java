package ru.langgame.mediator.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Запрос проверки догадки Sozdl. */
public record GuessRequest(
        @NotNull Long puzzleId,
        @NotBlank String guess
) {
}
