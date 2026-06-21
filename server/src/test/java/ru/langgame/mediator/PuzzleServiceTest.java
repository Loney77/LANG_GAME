package ru.langgame.mediator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.langgame.entity.GameType;
import ru.langgame.entity.Puzzle;
import ru.langgame.entity.Word;
import ru.langgame.foundation.IGameTypeRepository;
import ru.langgame.foundation.IPuzzleRepository;
import ru.langgame.foundation.IWordRepository;
import ru.langgame.mediator.dto.PuzzleDto;
import ru.langgame.mediator.exception.NotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PuzzleServiceTest {

    @Mock private IPuzzleRepository puzzles;
    @Mock private IWordRepository words;
    @Mock private IGameTypeRepository gameTypes;

    private PuzzleService service;
    private final LocalDate date = LocalDate.of(2026, 6, 20);

    @BeforeEach
    void setUp() {
        service = new PuzzleService(puzzles, words, gameTypes);
    }

    private static Word fiveLetter() {
        Word w = new Word();
        w.setText("абрек"); // 5 букв
        return w;
    }

    @Test
    void returnsExistingDailyPuzzle() {
        Puzzle existing = new Puzzle();
        existing.setWord(fiveLetter());
        existing.setPuzzleDate(date);
        when(puzzles.findByGameType_CodeAndPuzzleDate("SOZDL", date)).thenReturn(Optional.of(existing));

        PuzzleDto dto = service.getDailySozdl(date);

        assertThat(dto.gameType()).isEqualTo("SOZDL");
        assertThat(dto.length()).isEqualTo(5);
        assertThat(dto.date()).isEqualTo(date);
    }

    @Test
    void createsDailyPuzzleWhenAbsent() {
        when(puzzles.findByGameType_CodeAndPuzzleDate("SOZDL", date)).thenReturn(Optional.empty());
        when(words.findByLetterCount(5)).thenReturn(List.of(fiveLetter()));
        when(gameTypes.findByCode("SOZDL")).thenReturn(Optional.of(new GameType()));
        when(puzzles.save(any(Puzzle.class))).thenAnswer(inv -> inv.getArgument(0));

        PuzzleDto dto = service.getDailySozdl(date);

        assertThat(dto.length()).isEqualTo(5);
        assertThat(dto.date()).isEqualTo(date);
    }

    @Test
    void throwsWhenNoFiveLetterWords() {
        when(puzzles.findByGameType_CodeAndPuzzleDate(eq("SOZDL"), any())).thenReturn(Optional.empty());
        when(words.findByLetterCount(5)).thenReturn(List.of());
        assertThatThrownBy(() -> service.getDailySozdl(date)).isInstanceOf(NotFoundException.class);
    }
}
