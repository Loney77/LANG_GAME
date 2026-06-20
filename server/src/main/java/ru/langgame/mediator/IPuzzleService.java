package ru.langgame.mediator;

import ru.langgame.mediator.dto.PuzzleDto;

import java.time.LocalDate;

/** Контракт работы с заданиями (Control → Mediator). */
public interface IPuzzleService {

    /** Получить (или создать) ежедневное задание Sozdl на дату. */
    PuzzleDto getDailySozdl(LocalDate date);
}
