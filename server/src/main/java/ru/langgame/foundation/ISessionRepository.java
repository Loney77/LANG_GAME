package ru.langgame.foundation;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.langgame.entity.GameSession;
import ru.langgame.entity.SessionStatus;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/** Репозиторий сессий игр (Foundation). Содержит агрегат для лидерборда. */
public interface ISessionRepository extends JpaRepository<GameSession, Long> {

    Optional<GameSession> findByUser_IdAndPuzzle_IdAndStatus(Long userId, Long puzzleId, SessionStatus status);

    List<GameSession> findByUser_IdOrderByFinishedAtDesc(Long userId);

    /** Топ игроков по сумме очков для типа игры за период. */
    @Query("""
        select s.user.username as username, sum(s.score) as total
        from GameSession s
        where s.puzzle.gameType.code = :gameTypeCode
          and s.status = ru.langgame.entity.SessionStatus.WIN
          and s.finishedAt >= :since
        group by s.user.username
        order by total desc
    """)
    List<LeaderboardRow> aggregateTop(String gameTypeCode, Instant since, Pageable pageable);

    /** Проекция строки лидерборда. */
    interface LeaderboardRow {
        String getUsername();
        long getTotal();
    }
}
