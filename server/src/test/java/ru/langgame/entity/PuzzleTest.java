package ru.langgame.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class PuzzleTest {

    @Test
    void notDailyWhenDateIsNull() {
        Puzzle puzzle = new Puzzle();
        assertThat(puzzle.isDaily()).isFalse();
        assertThat(puzzle.isExpired(LocalDate.now())).isFalse();
    }

    @Test
    void dailyWhenDateIsSet() {
        Puzzle puzzle = new Puzzle();
        puzzle.setPuzzleDate(LocalDate.of(2026, 1, 1));
        assertThat(puzzle.isDaily()).isTrue();
    }

    @Test
    void expiredWhenDateBeforeToday() {
        Puzzle puzzle = new Puzzle();
        LocalDate today = LocalDate.of(2026, 6, 20);
        puzzle.setPuzzleDate(today.minusDays(1));
        assertThat(puzzle.isExpired(today)).isTrue();
    }

    @Test
    void notExpiredOnSameDay() {
        Puzzle puzzle = new Puzzle();
        LocalDate today = LocalDate.of(2026, 6, 20);
        puzzle.setPuzzleDate(today);
        assertThat(puzzle.isExpired(today)).isFalse();
    }
}
