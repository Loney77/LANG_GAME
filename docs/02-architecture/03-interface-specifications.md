# Спецификация интерфейсов между слоями

Контракты определяют связь Control → Mediator (`IService`) и Mediator → Foundation
(`IRepository`). Реализуются как Java-интерфейсы в Этапе 5.

## Control → Mediator (IService)

```java
public interface IWordService {
    WordDto getById(Long id);
    Page<WordDto> search(String theme, Integer length, Pageable pageable);
    WordDto create(CreateWordRequest req);   // ADMIN
    WordDto update(Long id, UpdateWordRequest req); // ADMIN
    void delete(Long id);                     // ADMIN
}

public interface ISozdlService {
    /** Возвращает активное ежедневное задание Сёздл. */
    PuzzleDto getDailyPuzzle(LocalDate date);

    /**
     * Проверяет догадку: валидирует слово по словарю, вычисляет статусы плиток,
     * учитывает попытку и при завершении создаёт сессию.
     */
    GuessResult evaluateGuess(Long puzzleId, String guess, Long userId);
}

public interface IQuizService {
    PuzzleDto getQuizPuzzle();
    AnswerResult checkAnswer(Long questionId, Long chosenWordId, Long userId);
}

public interface ILeaderboardService {
    List<LeaderboardEntry> top(GameType type, Period period, int limit);
}

public interface IAuthService {
    AuthResponse register(RegisterRequest req);
    AuthResponse login(LoginRequest req);   // -> JWT
}
```

## Mediator → Foundation (IRepository)

Расширяют `JpaRepository`, добавляя доменные запросы.

```java
public interface IWordRepository extends JpaRepository<Word, Long> {
    boolean existsByTextIgnoreCase(String text);
    Page<Word> findByThemeNameAndLetterCount(String theme, int letterCount, Pageable p);
    List<Word> findByLetterCount(int letterCount);
}

public interface IPuzzleRepository extends JpaRepository<Puzzle, Long> {
    Optional<Puzzle> findByGameTypeAndPuzzleDate(GameType type, LocalDate date);
}

public interface ISessionRepository extends JpaRepository<GameSession, Long> {
    List<GameSession> findByUserIdOrderByFinishedAtDesc(Long userId);

    @Query("""
        select s.user.username as username, sum(s.score) as total
        from GameSession s
        where s.gameType = :type and s.finishedAt >= :since
        group by s.user.username order by total desc
    """)
    List<LeaderboardRow> aggregateTop(GameType type, Instant since, Pageable p);
}
```

## Вспомогательные типы (DTO / результаты)

| Тип | Назначение |
|-----|-----------|
| `WordDto`, `PuzzleDto` | Передача данных в Presentation (без внутренних полей) |
| `GuessResult` | `{ tiles: [CORRECT/PRESENT/ABSENT], attempt, finished, win }` |
| `AnswerResult` | `{ correct: boolean, correctWordId, scoreDelta }` |
| `LeaderboardEntry` | `{ username, totalScore, rank }` |
| `AuthResponse` | `{ token, username, role }` |

## Принципы

- Интерфейсы **узкие** и ориентированы на сценарии (Interface Segregation).
- Control не знает о реализациях Mediator; внедрение через конструктор (DI).
- DTO отделены от `@Entity` — это вход в паттерн **Data Mapper** (Этап 6).
