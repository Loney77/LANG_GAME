# Тестирование и покрытие

Модульное тестирование на JUnit 5 + Mockito, покрытие — JaCoCo.

## Тест-классы

| Класс | Что проверяет |
|-------|---------------|
| `AlphabetTokenizerTest` | токенизация по алфавиту, диграфы, длина, регистр, null |
| `LetterMatcherTest` | подсветка CORRECT/PRESENT/ABSENT, повторы букв, диграфы |
| `ScoringServiceTest` | подсчёт очков: проигрыш, штрафы, минимум, неотрицательность |
| `SozdlServiceTest` | сценарии Сёздл (Mockito): not-found, длина, словарь, победа, продолжение |
| `AnagramServiceTest` | проверка ответа анаграммы (Mockito): верно/неверно, not-found |
| `AnagramShufflerTest` | перемешивание букв с учётом диграфов |
| `CrosswordGeneratorTest` | генерация сетки, пересечения слов |
| `LeaderboardServiceTest` | агрегация рейтинга: все игры / по типу (Mockito) |
| `WordServiceTest` | CRUD словаря, поиск, конфликты, темы (Mockito) |
| `AuthServiceTest` | регистрация, вход, занятый email/имя, неверный пароль |
| `PuzzleServiceTest` | ежедневное задание Сёздл: существующее/создание/нет слов |
| `QuizServiceTest` | формирование вопроса и проверка ответа викторины |
| `WordMapperTest` | маппинг Entity → DTO (Data Mapper) |
| `TranslationTextTest` | чистка переводов и отбор пригодных для викторины |
| `WordTest`, `GameSessionTest`, `PuzzleTest` | доменные методы сущностей |

## Запуск

```bash
cd server
./gradlew test jacocoTestReport
./gradlew jacocoTestCoverageVerification   # порог LINE > 40%
```

## Результаты покрытия (JaCoCo)

| Метрика | Покрытие |
|---------|----------|
| **LINE** | **60,8 %** (порог методички — > 40%) |
| INSTRUCTION | 62,8 % |
| BRANCH | 67,6 % |

Полный HTML-отчёт — в [jacoco-report/index.html](jacoco-report/index.html)
(машиночитаемый — `jacoco-report/jacocoTestReport.xml`). Геттеры и сеттеры Lombok
исключены из подсчёта (`@Generated`).
