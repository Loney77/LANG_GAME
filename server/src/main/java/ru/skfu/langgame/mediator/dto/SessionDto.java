package ru.skfu.langgame.mediator.dto;

import java.time.Instant;

/** DTO сессии игры (для истории игрока). */
public record SessionDto(
        Long id,
        String gameType,
        int score,
        int attempts,
        String status,
        Instant finishedAt
) {
}
