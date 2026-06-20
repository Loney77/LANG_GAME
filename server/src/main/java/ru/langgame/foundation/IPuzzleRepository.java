package ru.langgame.foundation;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.langgame.entity.Puzzle;

import java.time.LocalDate;
import java.util.Optional;

/** Репозиторий заданий (Foundation). */
public interface IPuzzleRepository extends JpaRepository<Puzzle, Long> {

    Optional<Puzzle> findByGameType_CodeAndPuzzleDate(String gameTypeCode, LocalDate puzzleDate);
}
