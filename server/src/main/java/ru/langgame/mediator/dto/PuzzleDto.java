package ru.langgame.mediator.dto;

import java.time.LocalDate;

/**
 * DTO задания. Целевое слово НЕ передаётся клиенту (анти-чит) - только его длина.
 *
 * @param id       идентификатор задания
 * @param gameType код типа игры
 * @param length   длина целевого слова (для Sozdl)
 * @param date     дата ежедневного задания
 */
public record PuzzleDto(Long id, String gameType, int length, LocalDate date) {
}
