package ru.langgame.mediator;

import ru.langgame.mediator.dto.CreateWordRequest;
import ru.langgame.mediator.dto.UpdateWordRequest;
import ru.langgame.mediator.dto.WordDto;

import java.util.List;

/** Контракт управления словарём (Control → Mediator). */
public interface IWordService {
    WordDto getById(Long id);

    List<WordDto> search(Integer length);

    WordDto create(CreateWordRequest request);

    WordDto update(Long id, UpdateWordRequest request);

    void delete(Long id);
}
