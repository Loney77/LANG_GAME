package ru.skfu.langgame.mediator.exception;

/** Ошибка аутентификации (→ HTTP 401). */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
