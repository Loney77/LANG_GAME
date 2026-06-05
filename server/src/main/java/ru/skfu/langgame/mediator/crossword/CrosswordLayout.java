package ru.skfu.langgame.mediator.crossword;

import java.util.List;

/**
 * Раскладка кроссворда (внутренняя модель, сериализуется в Puzzle.payload).
 *
 * @param rows    число строк сетки
 * @param cols    число столбцов сетки
 * @param entries слова с координатами и ответами
 */
public record CrosswordLayout(int rows, int cols, List<CrosswordEntry> entries) {
}
