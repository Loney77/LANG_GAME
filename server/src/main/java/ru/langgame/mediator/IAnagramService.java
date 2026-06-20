package ru.langgame.mediator;

import ru.langgame.mediator.dto.AnagramPuzzleDto;
import ru.langgame.mediator.dto.AnswerResult;

/** Контракт игры «Анаграммы» (Control → Mediator). */
public interface IAnagramService {

    /** Создать новое задание-анаграмму заданной длины. */
    AnagramPuzzleDto newPuzzle(int length);

    /** Проверить ответ игрока. */
    AnswerResult answer(Long puzzleId, String answer, Long userId);
}
