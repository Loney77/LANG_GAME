package ru.langgame.mediator.dto;

import ru.langgame.entity.TileStatus;

import java.util.List;

/**
 * Результат проверки одной догадки Sozdl.
 *
 * @param tiles    статус каждой буквы догадки
 * @param attempt  номер использованной попытки
 * @param finished завершена ли игра (победа или исчерпан лимит)
 * @param win      является ли догадка победной
 */
public record GuessResult(
        List<TileStatus> tiles,
        int attempt,
        boolean finished,
        boolean win
) {
}
