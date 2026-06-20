package ru.langgame.mediator.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** Запрос входа. */
public record LoginRequest(
        @NotBlank @Email String email,
        @NotBlank String password
) {
}
