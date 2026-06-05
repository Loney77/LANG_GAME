# Карачаевский язык в игровой форме — мобильное приложение

Курсовой проект по дисциплине «Программная инженерия» (СКФУ, 09.03.04).
**Траектория В: Мобильная разработка (React Native + Java Spring Boot).**

Мобильное приложение для изучения карачаевского языка через словесные игры:
**Сёздл**, **анаграммы**, **викторина**, **кроссворд**. Серверная часть хранит
словарь, проверяет ответы игр (защита от подсказок), формирует ежедневные задания
и лидерборд.

## Архитектура

Проект построен на паттерне **PCMEF** (Presentation → Control → Mediator → Entity → Foundation)
с направленностью зависимостей строго сверху вниз.

| Слой | Реализация |
|------|-----------|
| Presentation | React Native (TypeScript) + REST-контракт сервера |
| Control | Spring `@RestController` |
| Mediator | Spring `@Service` (бизнес-логика игр, транзакции) |
| Entity | JPA `@Entity` (доменные объекты с методами) |
| Foundation | Spring Data JPA репозитории + Data Mapper |

## Технологический стек

- **Клиент:** TypeScript, React Native, React Navigation, Axios, AsyncStorage
- **Сервер:** Java 17, Spring Boot 3, Gradle, Spring Data JPA, Spring Security + JWT, springdoc-openapi
- **БД:** PostgreSQL (3НФ), Flyway
- **Тесты:** JUnit 5, Mockito, JaCoCo (покрытие >40%)

## Структура репозитория

```
.
├── server/     # Серверное приложение Spring Boot (PCMEF, REST API)
├── mobile/     # Мобильный клиент React Native
├── frontend/   # Веб-панель администрирования словаря
├── data/       # Словарь приложения
├── tools/      # Скрипты подготовки словаря
├── docker/     # Контейнеризация PostgreSQL
├── scripts/    # Скрипты запуска
└── docs/       # Проектная документация по этапам (00–12)
```

## Словарь

Сервер хранит словарь карачаевского языка с переводами на русский. Длина слова
определяется токенизатором по карачаевскому алфавиту, в котором диграфы
(гъ, къ, нъ, нг, дж) считаются одной буквой; пятибуквенные слова используются
в игре «Сёздл». Начальный набор слов загружается миграцией Flyway при инициализации
базы данных, дальнейшее пополнение выполняется через административный CRUD словаря.

## Запуск

Параметры подключения и секреты задаются переменными окружения и имеют значения
по умолчанию для локального запуска.

### Сервер

```bash
docker compose -f docker/docker-compose.yml up -d      # PostgreSQL
cd server && ./gradlew bootRun
```

Документация API (Swagger UI): `http://localhost:8137/swagger-ui.html`.
Переменные окружения: `SERVER_PORT`, `DB_URL`, `DB_USER`, `DB_PASSWORD`, `JWT_SECRET`.

### Клиент

Адрес сервера задаётся переменной `EXPO_PUBLIC_API_URL` в файле `mobile/.env`
(см. `mobile/.env.example`).

```bash
cd mobile
npm install
npm run android
```

## Статистика разработки

- Всего коммитов: _—_
- Период разработки: _—_

![Активность коммитов](docs/00-project-charter/images/git-commit-activity.png)
![Распределение по времени](docs/00-project-charter/images/git-punch-card.png)
