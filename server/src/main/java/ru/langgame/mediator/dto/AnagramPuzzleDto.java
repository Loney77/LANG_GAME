package ru.langgame.mediator.dto;

import java.util.List;

/**
 * DTO задания-анаграммы. Исходное слово скрыто - отдаются только перемешанные буквы.
 *
 * @param puzzleId   идентификатор задания (для проверки ответа)
 * @param letters    перемешанные буквы алфавита
 * @param length     число букв
 * @param hint       перевод (подсказка)
 */
public record AnagramPuzzleDto(Long puzzleId, List<String> letters, int length, String hint) {
}
