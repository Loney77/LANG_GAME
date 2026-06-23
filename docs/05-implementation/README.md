# Этап 5. Реализация ядра

Артефакты этапа (Недели 11-12, вес 15%): исходный код ядра, юнит-тесты, отчёт о покрытии.

| Артефакт | Файл |
|----------|------|
| Структура кода (карта слоёв PCMEF) | [code-structure.md](code-structure.md) |
| Применённые паттерны проектирования | [design-patterns.md](design-patterns.md) |

## Что реализовано

Слои PCMEF на сервере (`server/src/main/java/ru/langgame/`):

| Слой | Пакет | Классы |
|------|-------|--------|
| **Entity** | `entity` | `User`, `Theme`, `GameType`, `Word`, `Puzzle`, `QuizQuestion`, `QuizOption`, `GameSession`; enum `Role`, `SessionStatus`, `TileStatus`; доменная утилита `AlphabetTokenizer` |
| **Foundation** | `foundation` | `IWordRepository`, `IPuzzleRepository`, `ISessionRepository`, `IUserRepository` (Spring Data JPA) |
| **Mediator** | `mediator` | `ISozdlService` + `SozdlService`, `ScoringService`, `LetterMatcher`; DTO `GuessResult`; исключения |

Не анемичная доменная модель: `Word.getLetters()`, `GameSession.finish()/registerAttempt()`,
`Puzzle.isExpired()`.

## Ключевая логика

- **AlphabetTokenizer** - разбиение слова на буквы карач. алфавита (диграфы = 1).
- **LetterMatcher** - двухпроходная подсветка букв с корректной обработкой повторов.
- **SozdlService** - серверная проверка догадки: валидация по словарю, учёт попыток,
  завершение сессии и подсчёт очков (анти-чит, см. ADR-002).
- **ScoringService** - очки за победу с учётом попыток и времени.

## Юнит-тесты

`server/src/test/java/ru/langgame/`:

| Тест | Покрывает |
|------|-----------|
| `AlphabetTokenizerTest` | диграфы, длина, регистр, пустая строка, null |
| `LetterMatcherTest` | CORRECT/PRESENT/ABSENT, повторы букв, слова с диграфами |
| `ScoringServiceTest` | проигрыш=0, база, штрафы за попытки/время, минимум, неотрицательность |
| `SozdlServiceTest` | not-found, неверная длина, нет в словаре, победа, продолжение (Mockito) |

Тесты - чистые юнит-тесты (без поднятия Spring-контекста и БД): быстрые и
детерминированные.

## Результаты покрытия (JaCoCo)

| Метрика | Покрытие |
|---------|----------|
| **LINE** | **91,3%** (95/104) - порог методички >40% пройден |
| INSTRUCTION | 90,1% |
| BRANCH | 75,0% |
| METHOD | 76,9% |

Геттеры/сеттеры/конструкторы Lombok исключены из покрытия (`@Generated`,
`lombok.config`). Отчёт: `server/build/reports/jacoco/test/html/index.html`.

## Сборка и запуск тестов

```bash
cd server
./gradlew test jacocoTestReport            # тесты + отчёт покрытия
./gradlew jacocoTestCoverageVerification    # проверка порога 40%
```

> Слои Control (REST-контроллеры) и безопасность (JWT) добавляются на этапе
> интерфейса/API (Этап 7); ядро Entity/Foundation/Mediator реализовано и
> покрыто тестами согласно требованиям Этапа 5.
