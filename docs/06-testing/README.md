# Тестирование и покрытие

Сопровождает Этап 5 (реализация ядра). Модульное тестирование на JUnit 5 + Mockito,
покрытие — JaCoCo.

## Тест-классы

| Класс | Что проверяет |
|-------|---------------|
| `AlphabetTokenizerTest` | токенизация, диграфы, длина, регистр, null |
| `LetterMatcherTest` | подсветка CORRECT/PRESENT/ABSENT, повторы, диграфы |
| `ScoringServiceTest` | подсчёт очков: проигрыш, штрафы, минимум, неотрицательность |
| `SozdlServiceTest` | сценарии Сёздл (Mockito): not-found, длина, словарь, победа |
| `WordMapperTest` | маппинг Entity → DTO |

## Запуск

```bash
cd server
./gradlew test jacocoTestReport
./gradlew jacocoTestCoverageVerification   # порог LINE > 40%
```

## Результаты покрытия (JaCoCo)

| Метрика | Значение |
|---------|----------|
| **LINE** | **91,3 %** (порог методички > 40%) |
| INSTRUCTION | 90,1 % |
| BRANCH | 75,0 % |

Отчёты: `build/reports/tests/test/index.html`, `build/reports/jacoco/test/html/index.html`.
Геттеры/сеттеры Lombok исключены из покрытия (`@Generated`).

> Скриншоты HTML-отчётов на момент сдачи складываются в `jacoco-report/`.
