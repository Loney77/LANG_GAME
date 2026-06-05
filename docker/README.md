# Локальный запуск и проверка бэкенда

## 1. Поднять PostgreSQL (Docker)

```bash
docker compose -f docker/docker-compose.yml up -d
```

БД доступна на `localhost:5433` (внутри контейнера 5432; 5433 выбран, чтобы не
конфликтовать с локально установленным PostgreSQL на 5432). БД/пользователь/пароль:
`langgame`/`langgame`/`langgame`.

## 2. Запустить сервер против этой БД

```bash
cd server
./gradlew bootJar
# Windows PowerShell:
$env:DB_URL="jdbc:postgresql://localhost:5433/langgame"; $env:DB_USER="langgame"; $env:DB_PASSWORD="langgame"
java -jar build/libs/lang-game-0.1.0.jar
```

При старте Flyway применит миграции V1–V3 (схема + справочники + 246 слов).
Признак успеха в логе: `Successfully applied 3 migrations` и `Started LangGameApplication`.

## 3. Проверить данные

```bash
docker exec langgame-db psql -U langgame -d langgame -c "select count(*) from word;"           # 246
docker exec langgame-db psql -U langgame -d langgame -c "select count(*) from word where letter_count=5;"  # 239
```

## 4. Остановить

```bash
# сервер — Ctrl+C; контейнер:
docker compose -f docker/docker-compose.yml down        # с сохранением данных в volume
docker compose -f docker/docker-compose.yml down -v     # удалить и данные
```

## Расширенный словарь (локально, не в репозитории)

Базовый seed (миграция V3) — 246 слов. Для полноценной игры есть dev-загрузчик
`SozlukSeedLoader`: если рядом лежит `data/sozluk.words.json` (≈28k слов, извлекается
скриптом `tools/extract_sozluk.py` из БД Сёзлюка — **в репозиторий не коммитится**),
он догружает слова при старте (идемпотентно).

```bash
# запуск с указанием файла словаря (абсолютный путь надёжнее):
java -Dapp.seed.sozluk-file=C:\Users\Klance\lang_game\data\sozluk.words.json -jar build/libs/lang-game-0.1.0.jar
# либо через gradlew bootRun из server/ — путь по умолчанию ../data/sozluk.words.json
```

Признак в логе: `Загрузка Сёзлюка завершена: добавлено N слов`. Повторные старты
пропускают загрузку (в БД уже >1000 слов). Отключить: `-Dapp.seed.enabled=false`.

> Swagger UI: http://localhost:8137/swagger-ui.html — зарегистрироваться, нажать
> Authorize, вставить JWT, дёргать эндпоинты.
