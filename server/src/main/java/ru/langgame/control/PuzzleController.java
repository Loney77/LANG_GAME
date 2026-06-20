package ru.langgame.control;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.langgame.mediator.IAnagramService;
import ru.langgame.mediator.ICrosswordService;
import ru.langgame.mediator.IPuzzleService;
import ru.langgame.mediator.IQuizService;
import ru.langgame.mediator.dto.AnagramPuzzleDto;
import ru.langgame.mediator.dto.CrosswordPuzzleDto;
import ru.langgame.mediator.dto.PuzzleDto;
import ru.langgame.mediator.dto.QuizQuestionDto;

import java.time.LocalDate;

/** Игровые задания (генерация). */
@RestController
@RequestMapping("/api/puzzles")
@RequiredArgsConstructor
@Tag(name = "Задания")
public class PuzzleController {

    private final IPuzzleService puzzleService;
    private final IAnagramService anagramService;
    private final IQuizService quizService;
    private final ICrosswordService crosswordService;

    @GetMapping("/daily")
    @Operation(summary = "Ежедневное задание Sozdl (целевое слово скрыто)")
    public PuzzleDto daily() {
        return puzzleService.getDailySozdl(LocalDate.now());
    }

    @GetMapping("/anagram")
    @Operation(summary = "Новое задание-анаграмма")
    public AnagramPuzzleDto anagram(@RequestParam(defaultValue = "5") int length) {
        return anagramService.newPuzzle(length);
    }

    @GetMapping("/quiz")
    @Operation(summary = "Новый вопрос викторины")
    public QuizQuestionDto quiz() {
        return quizService.newQuestion();
    }

    @GetMapping("/crossword")
    @Operation(summary = "Новый кроссворд (автогенерация)")
    public CrosswordPuzzleDto crossword() {
        return crosswordService.newPuzzle();
    }
}
