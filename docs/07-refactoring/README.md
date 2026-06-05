# Этап 6. Рефакторинг и качество

Артефакты этапа (Недели 13-14, вес 10%):

| Артефакт | Файл |
|----------|------|
| Data Mapper (обязательно) | [01-data-mapper.md](01-data-mapper.md) |
| Identity Map (обязательно) | [02-identity-map.md](02-identity-map.md) |
| Lazy Load (рекомендуется) | [03-lazy-load.md](03-lazy-load.md) |
| Отчёт статического анализа | [04-static-analysis.md](04-static-analysis.md) |

## Внедрённые паттерны (код)

- **Data Mapper:** `mediator/WordMapper.java` + `mediator/dto/WordDto.java`;
  репозитории Foundation.
- **Lazy Load:** `fetch = FetchType.LAZY` на всех `@ManyToOne` + `open-in-view: false`.
- **Identity Map:** контекст персистентности Hibernate в транзакциях Mediator.

## Качество

- Checkstyle: после настройки — **0 нарушений** (`build/reports/checkstyle/main.html`).
- Тесты после рефакторинга — зелёные, добавлен `WordMapperTest`.
- Покрытие JaCoCo сохраняется > 40% (новый код покрыт).
