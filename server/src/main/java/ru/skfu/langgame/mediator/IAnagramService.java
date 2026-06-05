package ru.skfu.langgame.mediator;

import ru.skfu.langgame.mediator.dto.AnagramPuzzleDto;
import ru.skfu.langgame.mediator.dto.AnswerResult;

/** Контракт игры «Анаграммы» (Control → Mediator). */
public interface IAnagramService {

    /** Создать новое задание-анаграмму заданной длины. */
    AnagramPuzzleDto newPuzzle(int length);

    /** Проверить ответ игрока. */
    AnswerResult answer(Long puzzleId, String answer, Long userId);
}
