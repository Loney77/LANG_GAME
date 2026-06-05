# Этап 3. Проектирование базы данных

Артефакты этапа (Недели 7-8, вес 10%):

| Артефакт | Файл |
|----------|------|
| ER-диаграмма (логическая модель) | [01-er-diagram.md](01-er-diagram.md) |
| DDL, ограничения, миграции | [02-ddl-and-constraints.md](02-ddl-and-constraints.md) |
| Стратегия ORM (Entity → таблицы) | [03-orm-strategy.md](03-orm-strategy.md) |

## Реализация (Flyway)

`server/src/main/resources/db/migration/`:

- `V1__init_schema.sql` — 8 таблиц (3НФ), ключи, ограничения, индексы
- `V2__reference_data.sql` — типы игр (4) и темы (6)
- `V3__seed_words.sql` — словарь (246 слов), генерируется `tools/gen_seed_sql.py`

> 8 таблиц > требуемых 5-7 бизнес-сущностей.
