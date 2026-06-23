# Структура кода

Карта реализации проекта: соответствие каталогов слоям архитектуры PCMEF
(см. [этап 2](../02-architecture/README.md)). Зависимости направлены строго
сверху вниз: Presentation → Control → Mediator → Entity → Foundation.

## Сервер (`server/src/main/java/ru/langgame/`)

| Каталог (пакет) | Слой PCMEF | Назначение | Ключевые классы |
|-----------------|-----------|------------|-----------------|
| `control` | Control | REST-контроллеры, приём запросов, маппинг DTO, единая обработка ошибок | `AuthController`, `WordController`, `PuzzleController`, `GameController`, `LeaderboardController`, `SessionController`, `ThemeController`, `GlobalExceptionHandler` |
| `mediator` | Mediator | Бизнес-логика игр и сценариев, транзакции, подсчёт очков | `SozdlService`, `AnagramService`, `QuizService`, `CrosswordService`, `ScoringService`, `LeaderboardService`, `AuthService`, `WordService`, `GameSessionRecorder` |
| `mediator/crossword` | Mediator | Автогенерация кроссворда (чистый алгоритм) | `CrosswordGenerator`, `CrosswordLayout`, `CrosswordEntry` |
| `mediator/dto` | Mediator | Объекты передачи данных (запросы/ответы REST) | `GuessRequest`/`GuessResult`, `WordDto`, `CrosswordPuzzleDto` и др. |
| `mediator/exception` | Mediator | Доменные исключения → HTTP-статусы | `NotFoundException`, `ConflictException`, `UnauthorizedException`, `InvalidGuessException`, `WordNotFoundException` |
| `entity` | Entity | Доменные сущности (JPA `@Entity`) с поведением + доменные утилиты | `User`, `Theme`, `Word`, `GameType`, `Puzzle`, `QuizQuestion`, `QuizOption`, `GameSession`; `AlphabetTokenizer`; enum `Role`, `SessionStatus`, `TileStatus` |
| `foundation` | Foundation | Репозитории Spring Data JPA (доступ к данным) | `IWordRepository`, `IPuzzleRepository`, `ISessionRepository`, `IUserRepository`, `IThemeRepository`, `IQuizQuestionRepository`, `IQuizOptionRepository`, `IGameTypeRepository` |
| `config` | (инфраструктура) | Безопасность, JWT, OpenAPI, загрузка словаря | `SecurityConfig`, `JwtService`, `JwtAuthFilter`, `AuthUser`, `OpenApiConfig`, `SozlukSeedLoader` |

Общение между слоями — через интерфейсы (`I*Service`, `I*Repository`):
Control зависит от `IService`, Mediator — от `IRepository`. Прямых обращений к
БД из контроллеров нет (проверено диаграммой зависимостей, [этап 2](../02-architecture/04-dependency-diagram.md)).

Не анемичная доменная модель: поведение размещено в сущностях, напр.
`Word.getLetters()` (токенизация по алфавиту), `GameSession.finish()` /
`registerAttempt()`, `Puzzle.isExpired()`.

### Миграции и ресурсы

- `server/src/main/resources/db/migration/` — миграции Flyway (схема в 3НФ +
  начальный словарь).
- `server/src/main/resources/application.yml` — конфигурация (порт, БД, JWT).

## Мобильный клиент (`mobile/src/`)

Адаптированный под React Native PCMEF (Presentation-слой системы):

| Каталог | Аналог слоя | Назначение |
|---------|-------------|-----------|
| `screens/` | Presentation | Экраны: `LoginScreen`, `HomeScreen`, `SozdlScreen`, `AnagramScreen`, `QuizScreen`, `CrosswordScreen`, `LeaderboardScreen`, `ProfileScreen` |
| `navigation/` | Presentation | Навигация (`RootNavigator`) и типы маршрутов |
| `auth/` | State | Контекст аутентификации (`AuthContext`) — хранение сессии/токена |
| `api/` | Control/доступ | Axios-клиент с JWT-интерсептором (`client.ts`) и типы API (`types.ts`) |
| `storage/` | Cache | Оффлайн-кэш (`cache.ts`) на AsyncStorage |
| `util/`, `theme.ts`, `config.ts` | — | Токенизатор алфавита на клиенте, тема оформления, конфигурация адреса сервера |

## Сборка

| Часть | Инструмент | Команда |
|-------|-----------|---------|
| Сервер | Gradle | `cd server && ./gradlew bootJar` |
| Клиент | Expo / EAS | `cd mobile && eas build -p android --profile preview` |
| Стек целиком | Docker Compose | `cd docker && docker compose up --build` |
