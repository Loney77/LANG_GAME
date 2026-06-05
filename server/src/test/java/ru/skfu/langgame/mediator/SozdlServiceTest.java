package ru.skfu.langgame.mediator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.skfu.langgame.entity.*;
import ru.skfu.langgame.foundation.IPuzzleRepository;
import ru.skfu.langgame.foundation.ISessionRepository;
import ru.skfu.langgame.foundation.IUserRepository;
import ru.skfu.langgame.foundation.IWordRepository;
import ru.skfu.langgame.mediator.dto.GuessResult;
import ru.skfu.langgame.mediator.exception.InvalidGuessException;
import ru.skfu.langgame.mediator.exception.NotFoundException;
import ru.skfu.langgame.mediator.exception.WordNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SozdlServiceTest {

    @Mock private IWordRepository words;
    @Mock private IPuzzleRepository puzzles;
    @Mock private ISessionRepository sessions;
    @Mock private IUserRepository users;

    private SozdlService service;

    private static final Long PUZZLE_ID = 1L;
    private static final Long USER_ID = 7L;

    private Puzzle puzzle;

    @BeforeEach
    void setUp() {
        service = new SozdlService(words, puzzles, sessions, users, new ScoringService());
        Word target = new Word();
        target.setText("абрек");
        puzzle = new Puzzle();
        puzzle.setWord(target);
    }

    @Test
    void throwsWhenPuzzleNotFound() {
        when(puzzles.findById(PUZZLE_ID)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.evaluateGuess(PUZZLE_ID, "абрек", USER_ID))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void throwsWhenGuessHasWrongLength() {
        when(puzzles.findById(PUZZLE_ID)).thenReturn(Optional.of(puzzle));
        assertThatThrownBy(() -> service.evaluateGuess(PUZZLE_ID, "аб", USER_ID))
                .isInstanceOf(InvalidGuessException.class);
    }

    @Test
    void throwsWhenGuessNotInDictionary() {
        when(puzzles.findById(PUZZLE_ID)).thenReturn(Optional.of(puzzle));
        when(words.existsByTextIgnoreCase("ккккк")).thenReturn(false);
        assertThatThrownBy(() -> service.evaluateGuess(PUZZLE_ID, "ккккк", USER_ID))
                .isInstanceOf(WordNotFoundException.class);
    }

    @Test
    void correctGuessWinsAndPersistsWinningSession() {
        when(puzzles.findById(PUZZLE_ID)).thenReturn(Optional.of(puzzle));
        when(words.existsByTextIgnoreCase("абрек")).thenReturn(true);
        when(sessions.findByUser_IdAndPuzzle_IdAndStatus(USER_ID, PUZZLE_ID, SessionStatus.IN_PROGRESS))
                .thenReturn(Optional.empty());
        when(users.getReferenceById(USER_ID)).thenReturn(new User());
        when(sessions.save(any(GameSession.class))).thenAnswer(inv -> inv.getArgument(0));

        GuessResult result = service.evaluateGuess(PUZZLE_ID, "абрек", USER_ID);

        assertThat(result.win()).isTrue();
        assertThat(result.finished()).isTrue();
        assertThat(result.attempt()).isEqualTo(1);
        assertThat(result.tiles()).containsExactly(
                TileStatus.CORRECT, TileStatus.CORRECT, TileStatus.CORRECT,
                TileStatus.CORRECT, TileStatus.CORRECT);
    }

    @Test
    void wrongButValidGuessContinuesGame() {
        when(puzzles.findById(PUZZLE_ID)).thenReturn(Optional.of(puzzle));
        when(words.existsByTextIgnoreCase("сахан")).thenReturn(true);
        when(sessions.findByUser_IdAndPuzzle_IdAndStatus(USER_ID, PUZZLE_ID, SessionStatus.IN_PROGRESS))
                .thenReturn(Optional.empty());
        lenient().when(users.getReferenceById(USER_ID)).thenReturn(new User());
        when(sessions.save(any(GameSession.class))).thenAnswer(inv -> inv.getArgument(0));

        GuessResult result = service.evaluateGuess(PUZZLE_ID, "сахан", USER_ID);

        assertThat(result.win()).isFalse();
        assertThat(result.finished()).isFalse();
        assertThat(result.attempt()).isEqualTo(1);
    }
}
