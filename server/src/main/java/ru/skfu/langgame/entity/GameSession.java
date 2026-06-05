package ru.skfu.langgame.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/** Сессия игры: одно прохождение задания игроком с результатом. */
@Entity
@Table(name = "game_session")
@Getter
@Setter
@NoArgsConstructor
public class GameSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "puzzle_id", nullable = false)
    private Puzzle puzzle;

    @Column(nullable = false)
    private int score = 0;

    @Column(nullable = false)
    private int attempts = 0;

    @Column(name = "duration_ms")
    private Long durationMs;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private SessionStatus status = SessionStatus.IN_PROGRESS;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt = Instant.now();

    @Column(name = "finished_at")
    private Instant finishedAt;

    /** Учесть очередную попытку. */
    public void registerAttempt() {
        this.attempts++;
    }

    /** Завершить сессию с итоговым статусом и очками. */
    public void finish(SessionStatus finalStatus, int finalScore) {
        this.status = finalStatus;
        this.score = finalScore;
        this.finishedAt = Instant.now();
    }

    public boolean isFinished() {
        return status != SessionStatus.IN_PROGRESS;
    }
}
