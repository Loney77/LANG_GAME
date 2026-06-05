# DDL-скрипты, ограничения и миграции

Схема ведётся через **Flyway** (версионные миграции в
`server/src/main/resources/db/migration/`). Hibernate работает в режиме
`ddl-auto: validate` — схему создаёт только Flyway.

## Миграции

| Версия | Файл | Содержание |
|--------|------|-----------|
| V1 | `V1__init_schema.sql` | Создание 8 таблиц, ключей, ограничений, индексов |
| V2 | `V2__reference_data.sql` | Справочные данные: 4 типа игр, 6 тем |
| V3 | `V3__seed_words.sql` | Загрузка словаря (246 слов), сгенерировано из `words.seed.json` |

## Нормализация (3НФ)

- **1НФ:** все атрибуты атомарны; варианты ответа вынесены в отдельную таблицу
  `quiz_option` (а не массив в одном поле).
- **2НФ:** все таблицы с одноколоночным суррогатным PK — частичных зависимостей нет.
- **3НФ:** транзитивные зависимости устранены. Ключевой пример: у `game_session`
  **нет** поля `game_type` — тип игры определяется через
  `game_session → puzzle → game_type`, что исключает дублирование и аномалии.

## Ограничения целостности

| Тип | Примеры |
|-----|---------|
| **PRIMARY KEY** | `GENERATED ALWAYS AS IDENTITY` во всех таблицах |
| **UNIQUE** | `users.username`, `users.email`, `theme.name`, `game_type.code`, `word(text, translation)` (омонимы допускаются), частичный `puzzle(game_type_id, puzzle_date)` для daily |
| **FOREIGN KEY** | `word.theme_id → theme` (ON DELETE SET NULL), `quiz_question.puzzle_id → puzzle` (ON DELETE CASCADE), `game_session.user_id/puzzle_id` |
| **CHECK** | `role ∈ {USER, ADMIN}`, `status ∈ {IN_PROGRESS, WIN, LOSS}`, `game_type.code ∈ {...}`, `letter_count > 0`, `score ≥ 0` |
| **NOT NULL** | На всех обязательных атрибутах |

## Индексы (под частые запросы)

| Индекс | Назначение |
|--------|-----------|
| `idx_word_letter_count` | Выбор слов для Сёздл (`letter_count = 5`) |
| `idx_word_theme` | Фильтрация слов по теме |
| `uq_puzzle_daily` | Поиск ежедневного задания по типу и дате |
| `idx_session_user`, `idx_session_puzzle` | История игрока и агрегаты лидерборда |
| `idx_quiz_option_question` | Загрузка вариантов вопроса |

## Воспроизводимость seed

`V3__seed_words.sql` генерируется скриптом `tools/gen_seed_sql.py` из
`data/words.seed.json`. При обновлении словаря добавляется **новая** миграция
(V4, V5…), а не правка применённой V3 (правило Flyway: применённые миграции
неизменяемы).
