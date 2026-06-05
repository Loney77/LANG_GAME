package ru.skfu.langgame.mediator.dto;

/** Ответ аутентификации: JWT и сведения о пользователе. */
public record AuthResponse(String token, String username, String role) {
}
