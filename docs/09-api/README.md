# Этап 7 (часть 1). REST API, безопасность, OpenAPI

Серверная обвязка слоя **Control** + безопасность. Документация API -
OpenAPI/Swagger (springdoc).

## Эндпоинты (12 операций, 9 путей)

| Метод | Путь | Доступ | Назначение |
|-------|------|--------|-----------|
| POST | `/api/auth/register` | público | Регистрация (выдаёт JWT) |
| POST | `/api/auth/login` | público | Вход (выдаёт JWT) |
| GET | `/api/words` | USER | Список/поиск слов (`?length=`) |
| GET | `/api/words/{id}` | USER | Слово по id |
| POST | `/api/words` | ADMIN | Создать слово |
| PUT | `/api/words/{id}` | ADMIN | Изменить слово |
| DELETE | `/api/words/{id}` | ADMIN | Удалить слово |
| GET | `/api/themes` | USER | Список тем |
| GET | `/api/puzzles/daily` | USER | Ежедневное задание Сёздл |
| GET | `/api/puzzles/anagram` | USER | Новая анаграмма (`?length=`) |
| GET | `/api/puzzles/quiz` | USER | Новый вопрос викторины |
| GET | `/api/puzzles/crossword` | USER | Новый кроссворд (автогенерация) |
| POST | `/api/games/sozdl/guess` | USER | Проверка догадки Сёздл |
| POST | `/api/games/anagram/answer` | USER | Ответ на анаграмму |
| POST | `/api/games/quiz/answer` | USER | Ответ на викторину |
| POST | `/api/games/crossword/answer` | USER | Проверка кроссворда |
| GET | `/api/sessions/me` | USER | История игр |
| GET | `/api/leaderboard` | USER | Топ игроков |

> Требование методички - ≥ 8 эндпоинтов. Выполнено (19).

## Игры (слой Mediator)

Все 4 игры реализованы и проверены вживую (целевые данные клиенту не передаются -
проверка на сервере):

| Игра | Сервис | Логика |
|------|--------|--------|
| **Сёздл** | `SozdlService`, `LetterMatcher` | daily-слово, подсветка букв (диграфы = 1 плитка) |
| **Анаграммы** | `AnagramService`, `AnagramShuffler` | перемешивание букв, сверка ответа |
| **Викторина** | `QuizService` | генерация вопроса (слово + 4 перевода) на лету |
| **Кроссворд** | `CrosswordService`, `CrosswordGenerator` | **автогенерация** связной сетки из словаря с пересечениями |

Очки одноходовых игр считает `GameSessionRecorder` через `ScoringService` (анти-чит).

### Кроссворд: автогенерация

`CrosswordGenerator` (чистый алгоритм, покрыт тестом): жадное размещение слов с
пересечениями по общим буквам, проверка конфликтов клеток и смежности → связная
сетка. Работает в «буквах» алфавита (диграфы - одна клетка). Решение хранится в
`puzzle.payload` (JSONB), клиенту отдаются только подсказки и координаты.

Живой прогон: сетка 17×11 из 6 слов, отправка всех ответов → `allCorrect=true, 6/6`.

## Безопасность (Spring Security + JWT)

- **Stateless**: сессии не хранятся; аутентификация по JWT (`Authorization: Bearer`).
- **Регистрация/вход**: `AuthService` хэширует пароль BCrypt, выдаёт JWT с `uid`,
  `username`, `role`.
- **Фильтр** `JwtAuthFilter` извлекает токен, наполняет `SecurityContext`
  principal'ом `AuthUser`.
- **Роли**: `USER` (игра), `ADMIN` (CRUD словаря/тем). POST/PUT/DELETE по
  `/api/words`, `/api/themes` требуют `ROLE_ADMIN`.
- Конфигурация: `config/SecurityConfig`, `config/JwtService`, `config/JwtAuthFilter`.

## OpenAPI / Swagger UI

- Спецификация: `GET /v3/api-docs`
- UI: `http://localhost:8137/swagger-ui.html` (кнопка Authorize - вставить JWT).
- Конфиг: `config/OpenApiConfig` (схема безопасности `bearerAuth`).

## Обработка ошибок

`GlobalExceptionHandler` (`@RestControllerAdvice`) → корректные статусы:
404 (NotFound), 409 (Conflict), 401 (Unauthorized), 400 (валидация/InvalidGuess),
422 (WordNotFound).

## Результаты живой проверки (на Dockerized PostgreSQL)

Полный сквозной прогон через `curl` против запущенного сервера:

| Проверка | Результат |
|----------|-----------|
| Регистрация → JWT | ✅ role=USER, токен выдан |
| `GET /api/puzzles/daily` | ✅ id, length=5 (целевое слово скрыто) |
| `POST .../sozdl/guess` (победа `ашлыкъ`) | ✅ win=true, 5×CORRECT (диграф `къ` = 1 плитка), score=100 |
| `GET /api/sessions/me` | ✅ status=WIN, score=100 |
| `GET /api/leaderboard` | ✅ rank 1, score 100 |
| `GET /api/words?length=5` | ✅ 239 |
| USER → `POST /api/words` | ✅ 403 (роль) |
| Запрос без токена | ✅ 403 |
| `GET /v3/api-docs` | ✅ 200 |

## Замечание по версиям (важно)

При апгрейде на Spring Boot 3.5 живой прогон выявил несовместимость
**springdoc 2.6.0** (рассчитан на Spring 6.1): `NoSuchMethodError
ControllerAdviceBean.<init>(Object)`. Исправлено обновлением springdoc до
**2.8.17** (линейка под Spring Boot 3.5).
