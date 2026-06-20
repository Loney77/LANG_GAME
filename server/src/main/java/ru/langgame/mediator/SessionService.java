package ru.langgame.mediator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.langgame.entity.GameSession;
import ru.langgame.foundation.ISessionRepository;
import ru.langgame.mediator.dto.SessionDto;

import java.util.List;

/** История игровых сессий пользователя. */
@Service
@RequiredArgsConstructor
public class SessionService implements ISessionService {

    private final ISessionRepository sessions;

    @Override
    @Transactional(readOnly = true)
    public List<SessionDto> historyOf(Long userId) {
        return sessions.findByUser_IdOrderByFinishedAtDesc(userId).stream()
                .map(this::toDto)
                .toList();
    }

    private SessionDto toDto(GameSession s) {
        return new SessionDto(
                s.getId(),
                s.getPuzzle().getGameType().getCode(),
                s.getScore(),
                s.getAttempts(),
                s.getStatus().name(),
                s.getFinishedAt());
    }
}
