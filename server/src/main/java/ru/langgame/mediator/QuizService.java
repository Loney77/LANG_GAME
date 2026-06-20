package ru.langgame.mediator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.langgame.entity.*;
import ru.langgame.foundation.*;
import ru.langgame.mediator.dto.QuizAnswerResult;
import ru.langgame.mediator.dto.QuizQuestionDto;
import ru.langgame.mediator.exception.NotFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Игра «Викторина»: выбрать правильный перевод слова из 4 вариантов. */
@Service
@RequiredArgsConstructor
public class QuizService implements IQuizService {

    private static final String QUIZ = "QUIZ";
    private static final int OPTIONS = 4;

    private final IWordRepository words;
    private final IPuzzleRepository puzzles;
    private final IGameTypeRepository gameTypes;
    private final IQuizQuestionRepository questions;
    private final IQuizOptionRepository options;
    private final GameSessionRecorder recorder;

    @Override
    @Transactional
    public QuizQuestionDto newQuestion() {
        List<Word> picked = pickDistinct(OPTIONS);
        Word correct = picked.get(0);

        GameType type = gameTypes.findByCode(QUIZ)
                .orElseThrow(() -> new NotFoundException("Тип игры QUIZ не найден"));
        Puzzle puzzle = new Puzzle();
        puzzle.setGameType(type);
        puzzle.setWord(correct);
        puzzle = puzzles.save(puzzle);

        QuizQuestion question = new QuizQuestion();
        question.setPuzzle(puzzle);
        question.setQuestionText("Что означает слово «" + correct.getText() + "»?");
        question.setCorrectWord(correct);
        question = questions.save(question);

        List<QuizOption> opts = new ArrayList<>();
        for (Word w : picked) {
            QuizOption opt = new QuizOption();
            opt.setQuestion(question);
            opt.setOptionText(TranslationText.clean(w.getTranslation()));
            opt.setCorrect(w.getId().equals(correct.getId()));
            opts.add(opt);
        }
        Collections.shuffle(opts);
        options.saveAll(opts);

        List<QuizQuestionDto.Option> dto = opts.stream()
                .map(o -> new QuizQuestionDto.Option(o.getId(), o.getOptionText()))
                .toList();
        return new QuizQuestionDto(question.getId(), question.getQuestionText(), dto);
    }

    @Override
    @Transactional
    public QuizAnswerResult answer(Long questionId, Long optionId, Long userId) {
        QuizQuestion question = questions.findById(questionId)
                .orElseThrow(() -> new NotFoundException("Вопрос не найден: " + questionId));
        List<QuizOption> opts = options.findByQuestion_Id(questionId);
        Long correctId = opts.stream().filter(QuizOption::isCorrect)
                .map(QuizOption::getId).findFirst().orElse(null);
        boolean correct = optionId != null && optionId.equals(correctId);

        int score = recorder.record(userId, question.getPuzzle(), correct, 1, 0);
        return new QuizAnswerResult(correct, correctId, score);
    }

    /** Выбирает n «чистых» слов с различными переводами (correct = первый). */
    private List<Word> pickDistinct(int n) {
        Map<String, Word> byTranslation = new LinkedHashMap<>();
        for (int attempt = 0; attempt < 8 && byTranslation.size() < n; attempt++) {
            for (Word w : words.findRandom(n * 8)) {
                if (!TranslationText.isQuizSuitable(w.getText(), w.getTranslation())) {
                    continue;   // пропускаем перекрёстные ссылки и заимствования
                }
                String key = TranslationText.clean(w.getTranslation()).toLowerCase();
                byTranslation.putIfAbsent(key, w);
            }
        }
        List<Word> result = new ArrayList<>(byTranslation.values());
        if (result.size() < n) {
            throw new NotFoundException("Недостаточно слов для викторины");
        }
        return result.subList(0, n);
    }
}
