package ru.langgame.mediator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.langgame.entity.AlphabetTokenizer;
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

/** Формирование игровых заданий. */
@Service
@RequiredArgsConstructor
public class PuzzleService implements IPuzzleService {

    private static final String SOZDL = "SOZDL";
    private static final int SOZDL_LENGTH = 5;

    private final IPuzzleRepository puzzles;
    private final IWordRepository words;
    private final IGameTypeRepository gameTypes;

    @Override
    @Transactional
    public PuzzleDto getDailySozdl(LocalDate date) {
        Puzzle puzzle = puzzles.findByGameType_CodeAndPuzzleDate(SOZDL, date)
                .orElseGet(() -> createDailySozdl(date));
        return new PuzzleDto(
                puzzle.getId(),
                SOZDL,
                AlphabetTokenizer.letterCount(puzzle.getWord().getText()),
                puzzle.getPuzzleDate());
    }

    private Puzzle createDailySozdl(LocalDate date) {
        List<Word> candidates = words.findByLetterCount(SOZDL_LENGTH);
        if (candidates.isEmpty()) {
            throw new NotFoundException("Нет слов из " + SOZDL_LENGTH + " букв для Sozdl");
        }
        // Детерминированный выбор слова по дате: одинаков для всех игроков.
        Word target = candidates.get(Math.floorMod(date.hashCode(), candidates.size()));
        GameType gameType = gameTypes.findByCode(SOZDL)
                .orElseThrow(() -> new NotFoundException("Тип игры SOZDL не найден"));

        Puzzle puzzle = new Puzzle();
        puzzle.setGameType(gameType);
        puzzle.setWord(target);
        puzzle.setPuzzleDate(date);
        return puzzles.save(puzzle);
    }
}
