# Матрица трассировки

Связь бизнес-прецедентов (Этап 0), системных Use Case (Этап 1), сущностей и
REST-эндпоинтов. Обеспечивает прослеживаемость требований до реализации.

## BUC → UC → Сущности → Эндпоинты

| Бизнес-прецедент (BUC) | Системный UC | Задействованные сущности | Эндпоинт(ы) |
|------------------------|--------------|--------------------------|-------------|
| BUC-1 Изучать лексику через игры | UC3, UC4, UC5, UC6, UC7 | Word, Puzzle, GameSession | `/api/puzzles*`, `/api/games/*` |
| BUC-2 Ежедневное задание | UC3 | Puzzle, Word | `GET /api/puzzles/daily` |
| BUC-3 Соревноваться | UC9 | GameSession, User | `GET /api/leaderboard` |
| BUC-4 Отслеживать прогресс | UC10 | GameSession | `GET /api/sessions/me` |
| BUC-5 Управлять словарём | UC11, UC12, UC14 | Word, Theme | `/api/words*`, `/api/themes*` |
| BUC-6 Готовить контент | UC13 | QuizQuestion, Word | `/api/quiz-questions*` |
| BUC-7 Проверять переводы | UC11 (правка) | Word | `PUT /api/words/{id}` |
| (служебное) Регистрация/вход | UC1, UC2 | User | `/api/auth/*` |

## UC → Слои PCMEF (предварительно)

| UC | Control | Mediator | Entity | Foundation |
|----|---------|----------|--------|------------|
| UC2 Вход | `AuthController` | `AuthService` | User | `UserRepository` |
| UC4 Догадка Сёздл | `GameController` | `SozdlService` | Word, Puzzle, GameSession | `WordRepository`, `PuzzleRepository`, `SessionRepository` |
| UC9 Лидерборд | `LeaderboardController` | `LeaderboardService` | GameSession | `SessionRepository` (агрегат) |
| UC11 CRUD словаря | `WordController` | `WordService` | Word | `WordRepository` |

## Покрытие функциональных требований

Каждый системный UC имеет хотя бы один эндпоинт и хотя бы одну сущность —
«висячих» требований нет. Эндпоинтов уровня MVP: **14** (> требуемых 8).
