package ru.skfu.langgame.mediator.dto;

import java.util.List;

/**
 * DTO кроссворда для клиента (ответы НЕ передаются — только подсказки и позиции).
 *
 * @param puzzleId идентификатор задания
 * @param rows     число строк сетки
 * @param cols     число столбцов сетки
 * @param clues    список записей (подсказки + координаты)
 */
public record CrosswordPuzzleDto(Long puzzleId, int rows, int cols, List<Clue> clues) {

    /** Подсказка к слову (без самого слова). */
    public record Clue(int number, String direction, int row, int col, int length, String clue) {
    }
}
