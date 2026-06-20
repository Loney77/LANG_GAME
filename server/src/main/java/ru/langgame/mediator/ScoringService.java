package ru.langgame.mediator;

import org.springframework.stereotype.Service;

/**
 * Подсчёт очков за игру (бизнес-логика Mediator).
 *
 * <p>Очки начисляются только за победу; чем меньше попыток и быстрее — тем больше.
 * Подсчёт выполняется на сервере (анти-чит): клиент не присылает очки.</p>
 */
@Service
public class ScoringService {

    static final int BASE = 100;
    static final int ATTEMPT_PENALTY = 10;
    static final int MIN_WIN = 10;
    static final int MAX_TIME_PENALTY = 50;
    /** 1 балл штрафа за каждые 10 секунд. */
    static final long MS_PER_TIME_PENALTY = 10_000L;

    /**
     * @param attempts   число использованных попыток (≥ 1 при победе)
     * @param durationMs длительность партии в миллисекундах
     * @param win        победа или нет
     * @return неотрицательное число очков
     */
    public int score(int attempts, long durationMs, boolean win) {
        if (!win) {
            return 0;
        }
        long timePenalty = Math.min(MAX_TIME_PENALTY, Math.max(0, durationMs) / MS_PER_TIME_PENALTY);
        int raw = BASE - (Math.max(1, attempts) - 1) * ATTEMPT_PENALTY - (int) timePenalty;
        return Math.max(raw, MIN_WIN);
    }
}
