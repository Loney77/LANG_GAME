package ru.skfu.langgame.mediator;

import ru.skfu.langgame.mediator.dto.SessionDto;

import java.util.List;

/** Контракт истории сессий (Control → Mediator). */
public interface ISessionService {

    /** История сессий игрока (свежие сверху). */
    List<SessionDto> historyOf(Long userId);
}
