package ru.langgame.mediator;

import org.springframework.stereotype.Component;
import ru.langgame.entity.Word;
import ru.langgame.mediator.dto.WordDto;

import java.util.List;

/**
 * Маппер словарных статей (паттерн Data Mapper на уровне транспорта).
 *
 * <p>Преобразует доменную сущность {@link Word} в {@link WordDto} и обратно,
 * изолируя слой представления от деталей персистентности.</p>
 */
@Component
public class WordMapper {

    /** Сущность → DTO. */
    public WordDto toDto(Word word) {
        if (word == null) {
            return null;
        }
        return new WordDto(
                word.getId(),
                word.getText(),
                word.getTranslation(),
                word.getLetterCount(),
                word.getTheme() != null ? word.getTheme().getName() : null
        );
    }

    /** Список сущностей → список DTO. */
    public List<WordDto> toDtoList(List<Word> words) {
        return words.stream().map(this::toDto).toList();
    }
}
