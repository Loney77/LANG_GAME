package ru.langgame.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GameSessionTest {

    @Test
    void newSessionIsInProgress() {
        GameSession session = new GameSession();
        assertThat(session.getStatus()).isEqualTo(SessionStatus.IN_PROGRESS);
        assertThat(session.isFinished()).isFalse();
        assertThat(session.getAttempts()).isZero();
    }

    @Test
    void registerAttemptIncrementsCounter() {
        GameSession session = new GameSession();
        session.registerAttempt();
        session.registerAttempt();
        assertThat(session.getAttempts()).isEqualTo(2);
    }

    @Test
    void finishSetsStatusScoreAndTimestamp() {
        GameSession session = new GameSession();
        session.finish(SessionStatus.WIN, 50);
        assertThat(session.getStatus()).isEqualTo(SessionStatus.WIN);
        assertThat(session.getScore()).isEqualTo(50);
        assertThat(session.getFinishedAt()).isNotNull();
        assertThat(session.isFinished()).isTrue();
    }
}
