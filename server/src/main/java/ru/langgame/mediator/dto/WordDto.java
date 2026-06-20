package ru.langgame.mediator.dto;

/**
 * DTO словарной статьи для передачи в Presentation.
 *
 * <p>Отделяет наружный контракт от JPA-сущности {@code Word} (паттерн Data Mapper):
 * клиент не видит внутренних полей и связей сущности.</p>
 *
 * @param id          идентификатор
 * @param text        карачаевское слово
 * @param translation русский перевод
 * @param letterCount длина в буквах алфавита
 * @param theme       название темы (или {@code null})
 */
public record WordDto(
        Long id,
        String text,
        String translation,
        int letterCount,
        String theme
) {
}
