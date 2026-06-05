package ru.skfu.langgame.mediator.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Запрос создания словарной статьи (ADMIN). */
public record CreateWordRequest(
        @NotBlank @Size(max = 64) String text,
        @NotBlank @Size(max = 255) String translation,
        Long themeId
) {
}
