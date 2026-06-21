package ru.langgame.mediator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.langgame.foundation.ISessionRepository;
import ru.langgame.mediator.dto.LeaderboardEntry;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LeaderboardServiceTest {

    @Mock private ISessionRepository sessions;

    private LeaderboardService service;

    @BeforeEach
    void setUp() {
        service = new LeaderboardService(sessions);
    }

    private static ISessionRepository.LeaderboardRow row(String username, long total) {
        return new ISessionRepository.LeaderboardRow() {
            @Override
            public String getUsername() {
                return username;
            }

            @Override
            public long getTotal() {
                return total;
            }
        };
    }

    @Test
    void aggregatesAcrossAllGamesWhenGameTypeBlank() {
        when(sessions.aggregateTopAll(any(Instant.class), any()))
                .thenReturn(List.of(row("aslan", 100), row("kazim", 50)));

        List<LeaderboardEntry> top = service.top(null, 7, 10);

        assertThat(top).hasSize(2);
        assertThat(top.get(0).rank()).isEqualTo(1);
        assertThat(top.get(0).username()).isEqualTo("aslan");
        assertThat(top.get(0).totalScore()).isEqualTo(100);
        assertThat(top.get(1).rank()).isEqualTo(2);
        assertThat(top.get(1).username()).isEqualTo("kazim");
    }

    @Test
    void filtersByGameTypeWhenProvided() {
        when(sessions.aggregateTop(eq("SOZDL"), any(Instant.class), any()))
                .thenReturn(List.of(row("aslan", 30)));

        List<LeaderboardEntry> top = service.top("SOZDL", 7, 10);

        assertThat(top).hasSize(1);
        assertThat(top.get(0).username()).isEqualTo("aslan");
        assertThat(top.get(0).totalScore()).isEqualTo(30);
    }
}
