package ru.skfu.langgame.mediator;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.skfu.langgame.foundation.ISessionRepository;
import ru.skfu.langgame.mediator.dto.LeaderboardEntry;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/** Формирование лидерборда (агрегат над сессиями игр). */
@Service
@RequiredArgsConstructor
public class LeaderboardService implements ILeaderboardService {

    private final ISessionRepository sessions;

    @Override
    public List<LeaderboardEntry> top(String gameTypeCode, int days, int limit) {
        Instant since = Instant.now().minus(Duration.ofDays(Math.max(1, days)));
        var rows = sessions.aggregateTop(gameTypeCode, since, PageRequest.of(0, Math.max(1, limit)));

        List<LeaderboardEntry> result = new ArrayList<>();
        int rank = 1;
        for (var row : rows) {
            result.add(new LeaderboardEntry(rank++, row.getUsername(), row.getTotal()));
        }
        return result;
    }
}
