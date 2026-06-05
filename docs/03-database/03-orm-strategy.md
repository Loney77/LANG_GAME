# Стратегия ORM (маппинг Entity → таблицы)

Используется **Spring Data JPA / Hibernate**. Доменные классы слоя Entity
отображаются на таблицы; доступ — через репозитории Foundation.

## Соответствие сущностей и таблиц

| JPA Entity | Таблица | Примечания |
|------------|---------|-----------|
| `User` | `users` | `role` → `@Enumerated(EnumType.STRING)` |
| `Theme` | `theme` | |
| `GameType` | `game_type` | справочник; `code` → enum-обёртка |
| `Word` | `word` | доменный метод `getLetters()` (токенизатор) — `@Transient` |
| `Puzzle` | `puzzle` | `payload` (JSONB) → `@JdbcTypeCode(SqlTypes.JSON)` |
| `QuizQuestion` | `quiz_question` | `@ManyToOne` к `Puzzle`, `Word` |
| `QuizOption` | `quiz_option` | `@ManyToOne` к `QuizQuestion` |
| `GameSession` | `game_session` | `status` → enum string |

## Ключевые решения маппинга

- **Идентификаторы:** `@Id @GeneratedValue(strategy = IDENTITY)` — согласовано с
  `GENERATED ALWAYS AS IDENTITY` в PostgreSQL.
- **Связи и Lazy Load:** все `@ManyToOne` — `fetch = FetchType.LAZY` (паттерн
  **Lazy Load**, Этап 6), чтобы не тянуть граф объектов без необходимости.
  `@OneToMany` используются ограниченно и тоже ленивы.
- **Enum:** хранятся строками (`EnumType.STRING`) — читаемо в БД, устойчиво к
  изменению порядка значений.
- **Identity Map:** обеспечивается контекстом персистентности Hibernate (L1-кэш):
  в пределах одной транзакции один экземпляр на строку (Этап 6).
- **Data Mapper:** Entity не покидают сервер — наружу отдаются DTO; преобразование
  Entity ↔ DTO выполняют мапперы слоя Foundation (Этап 6).
- **Транзакции:** управляются в слое Mediator аннотацией `@Transactional`.
- **Валидация схемы:** `spring.jpa.hibernate.ddl-auto=validate` — Hibernate только
  сверяет маппинг со схемой Flyway, но не изменяет её.

## Тестовая БД

Для slice/unit-тестов используется **H2** в режиме совместимости PostgreSQL; на
проде/деве — PostgreSQL. Flyway-миграции прогоняются в обоих окружениях.
