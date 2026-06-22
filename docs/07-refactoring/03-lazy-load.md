# Паттерн Lazy Load

**Назначение (методичка, рекомендуется):** отложенная загрузка связанных
объектов - данные подгружаются только при первом обращении.

## Реализация

Все ассоциации `@ManyToOne` объявлены ленивыми (`fetch = FetchType.LAZY`):

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "theme_id")
private Theme theme;            // Word → Theme

@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "word_id")
private Word word;              // Puzzle → Word
```

Применено в `Word`, `Puzzle`, `QuizQuestion`, `QuizOption`, `GameSession`.

## Эффект

- При загрузке `Puzzle` не тянется весь граф (`Word`, `GameType`, `User`), пока к
  ним не обратятся;
- меньше лишних JOIN и объёма выборки;
- `open-in-view: false` (в `application.yml`) - ленивые связи не подгружаются вне
  транзакции, что заставляет осознанно отдавать DTO (см. [Data Mapper](01-data-mapper.md)).

## Риск и контроль

Ленивые связи требуют аккуратности с N+1 и `LazyInitializationException`.
Контроль: преобразование в DTO внутри транзакции (Mediator) до выхода в Control.
