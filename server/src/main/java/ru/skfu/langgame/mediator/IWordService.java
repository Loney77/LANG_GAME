package ru.skfu.langgame.mediator;

import ru.skfu.langgame.mediator.dto.CreateWordRequest;
import ru.skfu.langgame.mediator.dto.UpdateWordRequest;
import ru.skfu.langgame.mediator.dto.WordDto;

import java.util.List;

/** Контракт управления словарём (Control → Mediator). */
public interface IWordService {
    WordDto getById(Long id);

    List<WordDto> search(Integer length);

    WordDto create(CreateWordRequest request);

    WordDto update(Long id, UpdateWordRequest request);

    void delete(Long id);
}
