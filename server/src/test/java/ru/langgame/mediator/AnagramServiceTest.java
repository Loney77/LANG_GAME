package ru.langgame.mediator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.langgame.entity.Puzzle;
import ru.langgame.entity.Word;
import ru.langgame.foundation.IGameTypeRepository;
import ru.langgame.foundation.IPuzzleRepository;
import ru.langgame.foundation.IWordRepository;
import ru.langgame.mediator.dto.AnswerResult;
import ru.langgame.mediator.exception.NotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnagramServiceTest {

    @Mock private IWordRepository words;
    @Mock private IPuzzleRepository puzzles;
    @Mock private IGameTypeRepository gameTypes;
    @Mock private GameSessionRecorder recorder;

    private AnagramService service;

    private static final Long PUZZLE_ID = 1L;
    private static final Long USER_ID = 9L;

    @BeforeEach
    void setUp() {
        service = new AnagramService(words, puzzles, gameTypes, recorder);
    }

    private Puzzle puzzleWith(String text) {
        Word w = new Word();
        w.setText(text);
        Puzzle p = new Puzzle();
        p.setWord(w);
        return p;
    }

    @Test
    void answerIsCorrectIgnoringCaseAndSpaces() {
        Puzzle p = puzzleWith("къол");
        when(puzzles.findById(PUZZLE_ID)).thenReturn(Optional.of(p));
        when(recorder.record(eq(USER_ID), eq(p), eq(true), anyInt(), anyLong())).thenReturn(50);

        AnswerResult result = service.answer(PUZZLE_ID, "  Къол ", USER_ID);

        assertThat(result.correct()).isTrue();
        assertThat(result.score()).isEqualTo(50);
    }

    @Test
    void answerIsWrongForOtherWord() {
        Puzzle p = puzzleWith("къол");
        when(puzzles.findById(PUZZLE_ID)).thenReturn(Optional.of(p));
        when(recorder.record(eq(USER_ID), eq(p), eq(false), anyInt(), anyLong())).thenReturn(0);

        AnswerResult result = service.answer(PUZZLE_ID, "тил", USER_ID);

        assertThat(result.correct()).isFalse();
        assertThat(result.score()).isZero();
    }

    @Test
    void answerThrowsWhenPuzzleNotFound() {
        when(puzzles.findById(2L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.answer(2L, "къол", USER_ID))
                .isInstanceOf(NotFoundException.class);
    }
}
