# Диаграмма системных прецедентов (Use Case)

Системные Use Case описывают функциональность системы (в отличие от бизнес-уровня
Этапа 0). Акторы - пользователи системы и внешние компоненты.

```plantuml
@startuml
left to right direction
skinparam packageStyle rectangle
title Системные прецеденты: «Сёз оюн»

actor "Игрок" as Player
actor "Администратор" as Admin

rectangle "Система «Сёз оюн»" {
  ' --- Аутентификация ---
  usecase "UC1 Зарегистрироваться" as UC1
  usecase "UC2 Войти (JWT)" as UC2

  ' --- Игры ---
  usecase "UC3 Получить ежедневное\nзадание Сёздл" as UC3
  usecase "UC4 Отправить догадку\n(Сёздл)" as UC4
  usecase "UC5 Играть в анаграмму" as UC5
  usecase "UC6 Пройти викторину" as UC6
  usecase "UC7 Играть в кроссворд" as UC7

  ' --- Результаты ---
  usecase "UC8 Записать результат\nсессии" as UC8
  usecase "UC9 Смотреть лидерборд" as UC9
  usecase "UC10 Смотреть свою\nисторию/очки" as UC10

  ' --- Администрирование ---
  usecase "UC11 Управлять словарём\n(CRUD)" as UC11
  usecase "UC12 Управлять темами" as UC12
  usecase "UC13 Управлять вопросами\nвикторины" as UC13
  usecase "UC14 Искать слова\n(фильтр)" as UC14
}

Player --> UC1
Player --> UC2
Player --> UC3
Player --> UC4
Player --> UC5
Player --> UC6
Player --> UC7
Player --> UC9
Player --> UC10

UC4 ..> UC8 : <<include>>
UC5 ..> UC8 : <<include>>
UC6 ..> UC8 : <<include>>
UC7 ..> UC8 : <<include>>
UC8 ..> UC9 : <<extend>>

Admin --> UC2
Admin --> UC11
Admin --> UC12
Admin --> UC13
Admin --> UC14
UC11 ..> UC14 : <<extend>>
@enduml
```

![Диаграмма прецедентов](images/use-case-diagram.png)

## Реестр прецедентов

| ID | Прецедент | Актор | Приоритет | Эндпоинт(ы) |
|----|-----------|-------|:---------:|-------------|
| UC1 | Регистрация | Игрок | High | `POST /api/auth/register` |
| UC2 | Вход (JWT) | Игрок, Админ | High | `POST /api/auth/login` |
| UC3 | Ежедневное задание Сёздл | Игрок | High | `GET /api/puzzles/daily?type=SOZDL` |
| UC4 | Отправить догадку Сёздл | Игрок | High | `POST /api/games/sozdl/guess` |
| UC5 | Анаграмма | Игрок | Medium | `GET /api/puzzles?type=ANAGRAM` |
| UC6 | Викторина | Игрок | Medium | `POST /api/games/quiz/answer` |
| UC7 | Кроссворд | Игрок | Low (stretch) | `GET /api/puzzles?type=CROSSWORD` |
| UC8 | Записать результат сессии | Игрок | High | `POST /api/sessions` |
| UC9 | Лидерборд | Игрок | High | `GET /api/leaderboard` |
| UC10 | История/очки игрока | Игрок | Medium | `GET /api/sessions/me` |
| UC11 | CRUD словаря | Админ | High | `GET/POST/PUT/DELETE /api/words` |
| UC12 | Управление темами | Админ | Medium | `GET /api/themes`, `POST/PUT/DELETE` |
| UC13 | Управление вопросами викторины | Админ | Medium | `/api/quiz-questions` |
| UC14 | Поиск слов с фильтром | Админ | Medium | `GET /api/words?theme=&length=` |
