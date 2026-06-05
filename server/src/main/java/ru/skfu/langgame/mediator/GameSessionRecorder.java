package ru.skfu.langgame.mediator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.skfu.langgame.entity.GameSession;
import ru.skfu.langgame.entity.Puzzle;
import ru.skfu.langgame.entity.SessionStatus;
import ru.skfu.langgame.foundation.ISessionRepository;
import ru.skfu.langgame.foundation.IUserRepository;

/**
 * Запись результата одноходовых игр (анаграмма, викторина, кроссворд).
 *
 * <p>Очки считаются на сервере через {@link ScoringService}; клиент очки не присылает.</p>
 */
@Component
@RequiredArgsConstructor
public class GameSessionRecorder {

    private final ISessionRepository sessions;
    private final IUserRepository users;
    private final ScoringService scoring;

    /**
     * Создаёт завершённую сессию.
     *
     * @return начисленные очки
     */
    public int record(Long userId, Puzzle puzzle, boolean win, int attempts, long durationMs) {
        int points = scoring.score(attempts, durationMs, win);
        GameSession session = new GameSession();
        session.setUser(users.getReferenceById(userId));
        session.setPuzzle(puzzle);
        session.setAttempts(Math.max(1, attempts));
        session.setDurationMs(durationMs);
        session.finish(win ? SessionStatus.WIN : SessionStatus.LOSS, points);
        sessions.save(session);
        return points;
    }
}
