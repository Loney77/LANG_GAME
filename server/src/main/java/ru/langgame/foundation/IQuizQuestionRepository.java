package ru.langgame.foundation;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.langgame.entity.QuizQuestion;

/** Репозиторий вопросов викторины (Foundation). */
public interface IQuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {
}
