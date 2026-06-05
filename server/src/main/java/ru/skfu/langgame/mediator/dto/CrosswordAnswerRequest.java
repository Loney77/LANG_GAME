package ru.skfu.langgame.mediator.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

/** Запрос проверки заполнения кроссворда. */
public record CrosswordAnswerRequest(@NotNull Long puzzleId, List<EntryAnswer> answers) {

    /** Ответ по одной записи. */
    public record EntryAnswer(int number, String direction, String answer) {
    }
}
