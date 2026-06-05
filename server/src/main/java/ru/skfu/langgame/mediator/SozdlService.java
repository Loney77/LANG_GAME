package ru.skfu.langgame.mediator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skfu.langgame.entity.*;
import ru.skfu.langgame.foundation.IPuzzleRepository;
import ru.skfu.langgame.foundation.ISessionRepository;
import ru.skfu.langgame.foundation.IUserRepository;
import ru.skfu.langgame.foundation.IWordRepository;
import ru.skfu.langgame.mediator.dto.GuessResult;
import ru.skfu.langgame.mediator.exception.InvalidGuessException;
import ru.skfu.langgame.mediator.exception.NotFoundException;
import ru.skfu.langgame.mediator.exception.WordNotFoundException;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

/**
 * Игровая логика «Сёздл» (угадывание слова, Mediator).
 *
 * <p>Вся проверка выполняется на сервере: валидация слова по словарю, подсветка
 * букв, учёт попыток и подсчёт очков (см. ADR-002).</p>
 */
@Service
@RequiredArgsConstructor
public class SozdlService implements ISozdlService {

    static final int MAX_ATTEMPTS = 6;

    private final IWordRepository words;
    private final IPuzzleRepository puzzles;
    private final ISessionRepository sessions;
    private final IUserRepository users;
    private final ScoringService scoring;

    @Override
    @Transactional
    public GuessResult evaluateGuess(Long puzzleId, String guess, Long userId) {
        Puzzle puzzle = puzzles.findById(puzzleId)
                .orElseThrow(() -> new NotFoundException("Задание не найдено: " + puzzleId));
        Word target = puzzle.getWord();
        if (target == null) {
            throw new IllegalStateException("У задания нет целевого слова");
        }

        String normalizedGuess = guess == null ? "" : guess.strip();
        List<String> guessLetters = AlphabetTokenizer.tokenize(normalizedGuess);
        List<String> targetLetters = AlphabetTokenizer.tokenize(target.getText());

        if (guessLetters.size() != targetLetters.size()) {
            throw new InvalidGuessException(
                    "Ожидается слово из " + targetLetters.size() + " букв");
        }
        if (!words.existsByTextIgnoreCase(normalizedGuess)) {
            throw new WordNotFoundException(normalizedGuess);
        }

        TileStatus[] tiles = LetterMatcher.evaluate(guessLetters, targetLetters);
        boolean win = Arrays.stream(tiles).allMatch(s -> s == TileStatus.CORRECT);

        GameSession session = sessions
                .findByUser_IdAndPuzzle_IdAndStatus(userId, puzzleId, SessionStatus.IN_PROGRESS)
                .orElseGet(() -> newSession(userId, puzzle));
        session.registerAttempt();

        boolean finished = win || session.getAttempts() >= MAX_ATTEMPTS;
        if (finished) {
            long durationMs = Duration.between(session.getStartedAt(), Instant.now()).toMillis();
            session.setDurationMs(durationMs);
            int points = scoring.score(session.getAttempts(), durationMs, win);
            session.finish(win ? SessionStatus.WIN : SessionStatus.LOSS, points);
        }
        sessions.save(session);

        return new GuessResult(List.of(tiles), session.getAttempts(), finished, win);
    }

    private GameSession newSession(Long userId, Puzzle puzzle) {
        GameSession s = new GameSession();
        s.setUser(users.getReferenceById(userId));
        s.setPuzzle(puzzle);
        return s;
    }
}
