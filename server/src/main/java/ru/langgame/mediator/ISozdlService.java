package ru.langgame.mediator;

import ru.langgame.mediator.dto.GuessResult;

/** Контракт игровой логики «Сёздл» (Control → Mediator). */
public interface ISozdlService {

    /**
     * Проверяет догадку игрока в рамках задания.
     *
     * @param puzzleId идентификатор задания
     * @param guess    слово-догадка
     * @param userId   идентификатор игрока
     * @return результат с подсветкой букв и состоянием игры
     */
    GuessResult evaluateGuess(Long puzzleId, String guess, Long userId);
}
