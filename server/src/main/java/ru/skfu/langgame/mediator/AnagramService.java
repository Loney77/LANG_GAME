package ru.skfu.langgame.mediator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skfu.langgame.entity.GameType;
import ru.skfu.langgame.entity.Puzzle;
import ru.skfu.langgame.entity.TranslationText;
import ru.skfu.langgame.entity.Word;
import ru.skfu.langgame.foundation.IGameTypeRepository;
import ru.skfu.langgame.foundation.IPuzzleRepository;
import ru.skfu.langgame.foundation.IWordRepository;
import ru.skfu.langgame.mediator.dto.AnagramPuzzleDto;
import ru.skfu.langgame.mediator.dto.AnswerResult;
import ru.skfu.langgame.mediator.exception.NotFoundException;

import java.util.Random;

/** Игра «Анаграммы»: собрать слово из перемешанных букв. */
@Service
@RequiredArgsConstructor
public class AnagramService implements IAnagramService {

    private static final String ANAGRAM = "ANAGRAM";
    private static final int MIN_LEN = 4;
    private static final int MAX_LEN = 8;

    private final IWordRepository words;
    private final IPuzzleRepository puzzles;
    private final IGameTypeRepository gameTypes;
    private final GameSessionRecorder recorder;
    private final Random random = new Random();

    @Override
    @Transactional
    public AnagramPuzzleDto newPuzzle(int length) {
        int len = Math.min(MAX_LEN, Math.max(MIN_LEN, length));
        Word target = pickSuitable(len);
        GameType type = gameTypes.findByCode(ANAGRAM)
                .orElseThrow(() -> new NotFoundException("Тип игры ANAGRAM не найден"));

        Puzzle puzzle = new Puzzle();
        puzzle.setGameType(type);
        puzzle.setWord(target);
        puzzle = puzzles.save(puzzle);

        var letters = AnagramShuffler.shuffle(target.getText(), random);
        String hint = TranslationText.clean(target.getTranslation());
        return new AnagramPuzzleDto(puzzle.getId(), letters, letters.size(), hint);
    }

    /** Слово заданной длины с осмысленным («годным») переводом. */
    private Word pickSuitable(int len) {
        Word fallback = null;
        for (int i = 0; i < 12; i++) {
            Word w = words.findRandomByLetterCount(len).orElse(null);
            if (w == null) {
                break;
            }
            fallback = w;
            if (TranslationText.isQuizSuitable(w.getText(), w.getTranslation())) {
                return w;
            }
        }
        if (fallback == null) {
            throw new NotFoundException("Нет слов длины " + len);
        }
        return fallback;
    }

    @Override
    @Transactional
    public AnswerResult answer(Long puzzleId, String answer, Long userId) {
        Puzzle puzzle = puzzles.findById(puzzleId)
                .orElseThrow(() -> new NotFoundException("Задание не найдено: " + puzzleId));
        Word target = puzzle.getWord();
        boolean correct = target != null
                && target.getText().equalsIgnoreCase(answer == null ? "" : answer.strip());
        int score = recorder.record(userId, puzzle, correct, 1, 0);
        return new AnswerResult(correct, score, target != null ? target.getText() : null);
    }
}
