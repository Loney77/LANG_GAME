package ru.langgame.foundation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.langgame.entity.Word;

import java.util.List;
import java.util.Optional;

/** Репозиторий словаря (Foundation). */
public interface IWordRepository extends JpaRepository<Word, Long> {

    boolean existsByTextIgnoreCase(String text);

    List<Word> findByLetterCount(int letterCount);

    Page<Word> findByTheme_NameAndLetterCount(String themeName, int letterCount, Pageable pageable);

    /** Случайное слово заданной длины (для анаграмм). */
    @Query(value = "SELECT * FROM word WHERE letter_count = :n ORDER BY random() LIMIT 1",
            nativeQuery = true)
    Optional<Word> findRandomByLetterCount(@Param("n") int n);

    /** N случайных слов (для викторины и кроссворда). */
    @Query(value = "SELECT * FROM word ORDER BY random() LIMIT :n", nativeQuery = true)
    List<Word> findRandom(@Param("n") int n);

    /** N случайных слов из диапазона длин (для пула кроссворда). */
    @Query(value = "SELECT * FROM word WHERE letter_count BETWEEN :min AND :max "
            + "ORDER BY random() LIMIT :n", nativeQuery = true)
    List<Word> findRandomInLengthRange(@Param("min") int min, @Param("max") int max,
                                       @Param("n") int n);
}
