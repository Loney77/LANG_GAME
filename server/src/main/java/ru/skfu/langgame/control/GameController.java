package ru.skfu.langgame.control;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.skfu.langgame.config.AuthUser;
import ru.skfu.langgame.mediator.IAnagramService;
import ru.skfu.langgame.mediator.ICrosswordService;
import ru.skfu.langgame.mediator.IQuizService;
import ru.skfu.langgame.mediator.ISozdlService;
import ru.skfu.langgame.mediator.dto.*;

/** Игровые действия (проверка ответов). */
@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
@Tag(name = "Игры")
public class GameController {

    private final ISozdlService sozdlService;
    private final IAnagramService anagramService;
    private final IQuizService quizService;
    private final ICrosswordService crosswordService;

    @PostMapping("/sozdl/guess")
    @Operation(summary = "Проверить догадку в Sozdl")
    public GuessResult sozdlGuess(@Valid @RequestBody GuessRequest request,
                                   @AuthenticationPrincipal AuthUser user) {
        return sozdlService.evaluateGuess(request.puzzleId(), request.guess(), user.id());
    }

    @PostMapping("/anagram/answer")
    @Operation(summary = "Проверить ответ на анаграмму")
    public AnswerResult anagramAnswer(@Valid @RequestBody AnagramAnswerRequest request,
                                      @AuthenticationPrincipal AuthUser user) {
        return anagramService.answer(request.puzzleId(), request.answer(), user.id());
    }

    @PostMapping("/quiz/answer")
    @Operation(summary = "Ответить на вопрос викторины")
    public QuizAnswerResult quizAnswer(@Valid @RequestBody QuizAnswerRequest request,
                                       @AuthenticationPrincipal AuthUser user) {
        return quizService.answer(request.questionId(), request.optionId(), user.id());
    }

    @PostMapping("/crossword/answer")
    @Operation(summary = "Проверить заполнение кроссворда")
    public CrosswordResult crosswordAnswer(@Valid @RequestBody CrosswordAnswerRequest request,
                                           @AuthenticationPrincipal AuthUser user) {
        return crosswordService.check(request, user.id());
    }
}
