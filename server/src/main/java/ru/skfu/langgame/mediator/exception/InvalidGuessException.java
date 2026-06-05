package ru.skfu.langgame.mediator.exception;

/** Догадка некорректного формата (например, неверная длина) (→ HTTP 400). */
public class InvalidGuessException extends RuntimeException {
    public InvalidGuessException(String message) {
        super(message);
    }
}
