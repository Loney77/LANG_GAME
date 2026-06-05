package ru.skfu.langgame.mediator.exception;

/** Конфликт состояния (например, дубликат) (→ HTTP 409). */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
