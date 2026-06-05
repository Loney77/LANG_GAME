# Концептуальная модель предметной области (Domain Model)

Уточнение бизнес-классов Этапа 0 до уровня системных сущностей с атрибутами и
связями. Реализационные детали (типы БД, индексы) добавляются на Этапе 3.

```plantuml
@startuml
title Domain Model: «Сёз оюн»
skinparam classAttributeIconSize 0

enum Role { USER \n ADMIN }
enum GameType { SOZDL \n ANAGRAM \n QUIZ \n CROSSWORD }
enum SessionStatus { IN_PROGRESS \n WIN \n LOSS }

class User {
  +id : Long
  +username : String
  +email : String
  +passwordHash : String
  +role : Role
  +createdAt : Instant
}

class Theme {
  +id : Long
  +name : String
  +description : String
}

class Word {
  +id : Long
  +text : String        ' карачаевское слово
  +translation : String ' русский перевод
  +letterCount : int    ' длина в буквах алфавита (с диграфами)
  --
  +getLetters() : List<String>  ' токенизация по карач. алфавиту
}

class Puzzle {
  +id : Long
  +gameType : GameType
  +puzzleDate : LocalDate  ' для daily-заданий
  +payload : String        ' JSON: набор букв / сетка кроссворда
  --
  +isExpired() : boolean
}

class QuizQuestion {
  +id : Long
  +questionText : String
  +options : List<String>
}

class GameSession {
  +id : Long
  +gameType : GameType
  +score : int
  +attempts : int
  +durationMs : long
  +status : SessionStatus
  +startedAt : Instant
  +finishedAt : Instant
  --
  +calculateScore() : int
}

User "1" --> "*" GameSession : играет
Theme "1" --> "*" Word : содержит
Word "1" --> "*" Puzzle : целевое слово
GameType "1" --> "*" Puzzle
Puzzle "1" --> "*" QuizQuestion : включает
QuizQuestion "*" --> "1" Word : верный ответ
Puzzle "1" --> "*" GameSession : порождает
@enduml
```

## Описание сущностей, атрибутов и бизнес-правил

| Сущность | Ключевые атрибуты | Бизнес-правила |
|----------|-------------------|----------------|
| **User** | username (уник.), email (уник.), passwordHash, role | Пароль хранится только в виде BCrypt-хэша; роль по умолчанию `USER`. |
| **Theme** | name (уник.) | Удаление темы не должно осиротить слова (ограничение FK / nullable). |
| **Word** | text, translation, letterCount, theme | `letterCount` вычисляется токенизатором (диграфы = 1 буква); для Сёздл используются слова с `letterCount = 5`. |
| **Puzzle** | gameType, puzzleDate, payload, word | Для типа `SOZDL` daily-задание на дату уникально; `payload` хранит данные конкретной игры. |
| **QuizQuestion** | questionText, options, correctWord | Должен быть ровно один верный вариант, соответствующий `correctWord`. |
| **GameSession** | gameType, score, attempts, durationMs, status | `score` вычисляется на сервере по `attempts`/`durationMs`; не принимается от клиента (анти-чит). |

## Связь с реализацией PCMEF

Эти сущности станут JPA `@Entity` (слой **Entity**) с **не анемичной** моделью:
методы `Word.getLetters()`, `Puzzle.isExpired()`, `GameSession.calculateScore()`
содержат доменную логику, а не только геттеры.
