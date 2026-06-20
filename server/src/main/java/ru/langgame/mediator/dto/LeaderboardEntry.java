package ru.langgame.mediator.dto;

/** Строка лидерборда. */
public record LeaderboardEntry(int rank, String username, long totalScore) {
}
