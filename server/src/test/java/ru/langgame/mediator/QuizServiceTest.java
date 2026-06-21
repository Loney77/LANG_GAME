package ru.langgame.mediator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.langgame.entity.Puzzle;
import ru.langgame.entity.QuizOption;
import ru.langgame.entity.QuizQuestion;
import ru.langgame.entity.Word;
import ru.langgame.foundation.IGameTypeRepository;
import ru.langgame.foundation.IPuzzleRepository;
import ru.langgame.foundation.IQuizOptionRepository;
import ru.langgame.foundation.IQuizQuestionRepository;
import ru.langgame.foundation.IWordRepository;
import ru.langgame.entity.GameType;
import ru.langgame.mediator.dto.QuizAnswerResult;
import ru.langgame.mediator.dto.QuizQuestionDto;
import ru.langgame.mediator.exception.NotFoundException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuizServiceTest {

    @Mock private IWordRepository words;
    @Mock private IPuzzleRepository puzzles;
    @Mock private IGameTypeRepository gameTypes;
    @Mock private IQuizQuestionRepository questions;
    @Mock private IQuizOptionRepository options;
    @Mock private GameSessionRecorder recorder;

    private QuizService service;

    private static final Long QID = 1L;
    private static final Long USER_ID = 7L;

    @BeforeEach
    void setUp() {
        service = new QuizService(words, puzzles, gameTypes, questions, options, recorder);
    }

    private static Word word(long id, String text, String translation) {
        Word w = new Word();
        w.setId(id);
        w.setText(text);
        w.setTranslation(translation);
        return w;
    }

    private static QuizOption option(long id, String text, boolean correct) {
        QuizOption o = new QuizOption();
        o.setId(id);
        o.setOptionText(text);
        o.setCorrect(correct);
        return o;
    }

    @Test
    void answerCorrectWhenOptionMatches() {
        QuizQuestion q = new QuizQuestion();
        q.setPuzzle(new Puzzle());
        when(questions.findById(QID)).thenReturn(Optional.of(q));
        when(options.findByQuestion_Id(QID)).thenReturn(List.of(
                option(10L, "лев", true), option(11L, "дом", false)));
        when(recorder.record(eq(USER_ID), any(Puzzle.class), eq(true), anyInt(), anyLong())).thenReturn(40);

        QuizAnswerResult res = service.answer(QID, 10L, USER_ID);

        assertThat(res.correct()).isTrue();
        assertThat(res.correctOptionId()).isEqualTo(10L);
        assertThat(res.score()).isEqualTo(40);
    }

    @Test
    void answerWrongWhenOptionDiffers() {
        QuizQuestion q = new QuizQuestion();
        q.setPuzzle(new Puzzle());
        when(questions.findById(QID)).thenReturn(Optional.of(q));
        when(options.findByQuestion_Id(QID)).thenReturn(List.of(
                option(10L, "лев", true), option(11L, "дом", false)));
        when(recorder.record(eq(USER_ID), any(Puzzle.class), eq(false), anyInt(), anyLong())).thenReturn(0);

        QuizAnswerResult res = service.answer(QID, 11L, USER_ID);

        assertThat(res.correct()).isFalse();
        assertThat(res.correctOptionId()).isEqualTo(10L);
    }

    @Test
    void answerThrowsWhenQuestionMissing() {
        when(questions.findById(2L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.answer(2L, 1L, USER_ID)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void newQuestionBuildsFourOptions() {
        when(words.findRandom(anyInt())).thenReturn(List.of(
                word(1, "аслан", "лев"),
                word(2, "юй", "дом"),
                word(3, "къол", "рука"),
                word(4, "аякъ", "нога")));
        when(gameTypes.findByCode("QUIZ")).thenReturn(Optional.of(new GameType()));
        when(puzzles.save(any(Puzzle.class))).thenAnswer(inv -> inv.getArgument(0));
        when(questions.save(any(QuizQuestion.class))).thenAnswer(inv -> inv.getArgument(0));
        when(options.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

        QuizQuestionDto dto = service.newQuestion();

        assertThat(dto.options()).hasSize(4);
        assertThat(dto.questionText()).contains("аслан");
    }
}
