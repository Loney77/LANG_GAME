package ru.langgame.mediator.crossword;

/**
 * Слово в кроссворде (внутренняя модель — содержит ответ, в payload).
 *
 * @param number    номер записи
 * @param direction "ACROSS" (по горизонтали) или "DOWN" (по вертикали)
 * @param row       строка начала
 * @param col       столбец начала
 * @param length    длина в буквах алфавита
 * @param answer    слово-ответ (карачаевское)
 * @param clue      подсказка (русский перевод)
 */
public record CrosswordEntry(
        int number,
        String direction,
        int row,
        int col,
        int length,
        String answer,
        String clue
) {
}
