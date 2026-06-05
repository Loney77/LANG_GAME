package ru.skfu.langgame.mediator.exception;

/** Запрошенный ресурс не найден (→ HTTP 404). */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
