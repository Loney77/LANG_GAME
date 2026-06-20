package ru.langgame.mediator;

import ru.langgame.mediator.dto.CrosswordAnswerRequest;
import ru.langgame.mediator.dto.CrosswordPuzzleDto;
import ru.langgame.mediator.dto.CrosswordResult;

/** Контракт игры «Кроссворд» (Control → Mediator). */
public interface ICrosswordService {

    /** Сгенерировать новый кроссворд. */
    CrosswordPuzzleDto newPuzzle();

    /** Проверить заполнение. */
    CrosswordResult check(CrosswordAnswerRequest request, Long userId);
}
