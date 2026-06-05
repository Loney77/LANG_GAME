package ru.skfu.langgame.mediator;

import ru.skfu.langgame.mediator.dto.QuizAnswerResult;
import ru.skfu.langgame.mediator.dto.QuizQuestionDto;

/** Контракт игры «Викторина» (Control → Mediator). */
public interface IQuizService {

    /** Сгенерировать новый вопрос (слово + 4 варианта перевода). */
    QuizQuestionDto newQuestion();

    /** Проверить выбранный вариант. */
    QuizAnswerResult answer(Long questionId, Long optionId, Long userId);
}
