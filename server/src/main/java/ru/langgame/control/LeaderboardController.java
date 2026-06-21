package ru.langgame.control;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.langgame.mediator.ILeaderboardService;
import ru.langgame.mediator.dto.LeaderboardEntry;

import java.util.List;

/** Лидерборд. */
@RestController
@RequestMapping("/api/leaderboard")
@RequiredArgsConstructor
@Tag(name = "Лидерборд")
public class LeaderboardController {

    private final ILeaderboardService leaderboardService;

    @GetMapping
    @Operation(summary = "Топ игроков за период")
    public List<LeaderboardEntry> top(
            @RequestParam(required = false) String gameType,
            @RequestParam(defaultValue = "30") int days,
            @RequestParam(defaultValue = "20") int limit) {
        return leaderboardService.top(gameType, days, limit);
    }
}
