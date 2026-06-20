package ru.langgame.mediator;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.langgame.entity.GameType;
import ru.langgame.entity.Puzzle;
import ru.langgame.entity.TranslationText;
import ru.langgame.foundation.IGameTypeRepository;
import ru.langgame.foundation.IPuzzleRepository;
import ru.langgame.foundation.IWordRepository;
import ru.langgame.mediator.crossword.CrosswordEntry;
import ru.langgame.mediator.crossword.CrosswordGenerator;
import ru.langgame.mediator.crossword.CrosswordLayout;
import ru.langgame.mediator.dto.CrosswordAnswerRequest;
import ru.langgame.mediator.dto.CrosswordPuzzleDto;
import ru.langgame.mediator.dto.CrosswordResult;
import ru.langgame.mediator.exception.NotFoundException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Игра «Кроссворд»: автогенерация сетки и проверка заполнения. */
@Service
@RequiredArgsConstructor
public class CrosswordService implements ICrosswordService {

    private static final String CROSSWORD = "CROSSWORD";
    private static final int MAX_WORDS = 8;
    private static final int POOL = 120;
    private static final int ATTEMPTS = 4;

    private final IWordRepository words;
    private final IPuzzleRepository puzzles;
    private final IGameTypeRepository gameTypes;
    private final GameSessionRecorder recorder;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public CrosswordPuzzleDto newPuzzle() {
        CrosswordLayout layout = buildLayout();

        GameType type = gameTypes.findByCode(CROSSWORD)
                .orElseThrow(() -> new NotFoundException("Тип игры CROSSWORD не найден"));
        Puzzle puzzle = new Puzzle();
        puzzle.setGameType(type);
        puzzle.setPayload(write(layout));
        puzzle = puzzles.save(puzzle);

        List<CrosswordPuzzleDto.Clue> clues = layout.entries().stream()
                .map(e -> new CrosswordPuzzleDto.Clue(
                        e.number(), e.direction(), e.row(), e.col(), e.length(), e.clue()))
                .toList();
        return new CrosswordPuzzleDto(puzzle.getId(), layout.rows(), layout.cols(), clues);
    }

    @Override
    @Transactional
    public CrosswordResult check(CrosswordAnswerRequest request, Long userId) {
        Puzzle puzzle = puzzles.findById(request.puzzleId())
                .orElseThrow(() -> new NotFoundException("Задание не найдено: " + request.puzzleId()));
        CrosswordLayout layout = read(puzzle.getPayload());

        Map<String, String> submitted = new java.util.HashMap<>();
        if (request.answers() != null) {
            for (var a : request.answers()) {
                submitted.put(key(a.number(), a.direction()), a.answer() == null ? "" : a.answer().strip());
            }
        }

        int correct = 0;
        for (CrosswordEntry e : layout.entries()) {
            String given = submitted.get(key(e.number(), e.direction()));
            if (given != null && given.equalsIgnoreCase(e.answer())) {
                correct++;
            }
        }
        int total = layout.entries().size();
        boolean allCorrect = correct == total;
        int score = recorder.record(userId, puzzle, allCorrect, 1, 0);
        return new CrosswordResult(allCorrect, correct, total, score);
    }

    private CrosswordLayout buildLayout() {
        IllegalStateException last = null;
        for (int attempt = 0; attempt < ATTEMPTS; attempt++) {
            List<CrosswordGenerator.Candidate> candidates = words.findRandomInLengthRange(3, 9, POOL)
                    .stream()
                    .filter(w -> TranslationText.isQuizSuitable(w.getText(), w.getTranslation()))
                    .map(w -> new CrosswordGenerator.Candidate(
                            w.getText(), chars(w.getText()),
                            TranslationText.clean(w.getTranslation())))
                    .collect(Collectors.toList());
            if (candidates.size() < 2) {
                continue;
            }
            try {
                return CrosswordGenerator.generate(candidates, MAX_WORDS);
            } catch (IllegalStateException ex) {
                last = ex;
            }
        }
        throw new NotFoundException("Не удалось сгенерировать кроссворд: "
                + (last != null ? last.getMessage() : "недостаточно слов"));
    }

    /** Разбивает слово на отдельные символы (диграфы = 2 клетки). */
    private static List<String> chars(String word) {
        return word.codePoints().mapToObj(Character::toString).collect(Collectors.toList());
    }

    private static String key(int number, String direction) {
        return number + ":" + (direction == null ? "" : direction.toUpperCase());
    }

    private String write(CrosswordLayout layout) {
        try {
            return objectMapper.writeValueAsString(layout);
        } catch (Exception e) {
            throw new IllegalStateException("Ошибка сериализации кроссворда", e);
        }
    }

    private CrosswordLayout read(String payload) {
        try {
            return objectMapper.readValue(payload, CrosswordLayout.class);
        } catch (Exception e) {
            throw new IllegalStateException("Ошибка чтения кроссворда", e);
        }
    }
}
