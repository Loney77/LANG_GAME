package ru.skfu.langgame.mediator;

import ru.skfu.langgame.mediator.dto.CrosswordAnswerRequest;
import ru.skfu.langgame.mediator.dto.CrosswordPuzzleDto;
import ru.skfu.langgame.mediator.dto.CrosswordResult;

/** Контракт игры «Кроссворд» (Control → Mediator). */
public interface ICrosswordService {

    /** Сгенерировать новый кроссворд. */
    CrosswordPuzzleDto newPuzzle();

    /** Проверить заполнение. */
    CrosswordResult check(CrosswordAnswerRequest request, Long userId);
}
