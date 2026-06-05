package ru.skfu.langgame.foundation;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skfu.langgame.entity.QuizQuestion;

/** Репозиторий вопросов викторины (Foundation). */
public interface IQuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {
}
