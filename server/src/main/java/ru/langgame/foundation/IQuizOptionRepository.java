package ru.langgame.foundation;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.langgame.entity.QuizOption;

import java.util.List;

/** Репозиторий вариантов ответа викторины (Foundation). */
public interface IQuizOptionRepository extends JpaRepository<QuizOption, Long> {

    List<QuizOption> findByQuestion_Id(Long questionId);
}
