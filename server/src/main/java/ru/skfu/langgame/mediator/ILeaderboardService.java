package ru.skfu.langgame.mediator;

import ru.skfu.langgame.mediator.dto.LeaderboardEntry;

import java.util.List;

/** Контракт лидерборда (Control → Mediator). */
public interface ILeaderboardService {

    /** Топ игроков по сумме очков за период (в днях) для типа игры. */
    List<LeaderboardEntry> top(String gameTypeCode, int days, int limit);
}
