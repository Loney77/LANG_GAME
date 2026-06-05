# Диаграмма классов проектирования

Детальная структура классов сервера по слоям PCMEF (фрагмент ключевого среза —
игра Сёздл).

```plantuml
@startuml
skinparam classAttributeIconSize 0

package control {
  class GameController {
    - sozdlService : ISozdlService
    + guess(req : GuessRequest) : GuessResult
  }
}

package mediator {
  interface ISozdlService {
    + getDailyPuzzle(date) : PuzzleDto
    + evaluateGuess(puzzleId, guess, userId) : GuessResult
  }
  class SozdlService {
    - words : IWordRepository
    - puzzles : IPuzzleRepository
    - sessions : ISessionRepository
    - tokenizer : AlphabetTokenizer
    - scoring : ScoringService
    + evaluateGuess(...) : GuessResult
    - computeTiles(guess, target) : TileStatus[]
  }
  class ScoringService {
    + score(attempts : int, durationMs : long, win : boolean) : int
  }
  ISozdlService <|.. SozdlService
}

package entity {
  class Word {
    - text : String
    - translation : String
    - letterCount : int
    + getLetters(tok) : List<String>
  }
  class GameSession {
    - score : int
    - attempts : int
    - status : SessionStatus
    + calculateScore(scoring) : void
  }
  enum TileStatus { CORRECT \n PRESENT \n ABSENT }
}

package foundation {
  class AlphabetTokenizer {
    + {static} tokenize(word : String) : List<String>
    + {static} letterCount(word : String) : int
  }
  interface IWordRepository
  interface IPuzzleRepository
  interface ISessionRepository
}

GameController --> ISozdlService
SozdlService --> IWordRepository
SozdlService --> IPuzzleRepository
SozdlService --> ISessionRepository
SozdlService --> AlphabetTokenizer
SozdlService --> ScoringService
SozdlService ..> GameSession
SozdlService ..> Word
@enduml
```

> Полная диаграмма классов (все сущности и сервисы) экспортируется в
> `images/design-class-diagram.png`.
