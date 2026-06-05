# Диаграмма пакетов (PCMEF)

Архитектура построена на паттерне PCMEF. Зависимости направлены **строго сверху
вниз**: Presentation → Control → Mediator → Entity → Foundation. Связь между
слоями — через интерфейсы (`IService`, `IRepository`).

## Распределение слоёв между клиентом и сервером

Presentation реализован на клиенте (React Native); Control/Mediator/Entity/Foundation —
на сервере (Spring Boot). Граница P↔C — REST/JSON.

```plantuml
@startuml
allowmixing
title Архитектура PCMEF — «Сёз оюн»

package "МОБИЛЬНЫЙ КЛИЕНТ (Presentation)" #E8F4FF {
  rectangle "screens/\n(экраны, компоненты)" as Screens
  rectangle "state/\n(Context/Redux)" as State
  rectangle "api/\n(Axios + интерфейсы)" as ApiClient
  rectangle "cache/\n(AsyncStorage)" as Cache
  Screens --> State
  State --> ApiClient
  ApiClient --> Cache
}

cloud "REST / JSON\n(JWT)" as REST

package "СЕРВЕР (Spring Boot)" {
  package "control" #FFF2E8 {
    rectangle "AuthController" as AC
    rectangle "WordController" as WC
    rectangle "GameController" as GC
    rectangle "PuzzleController" as PC
    rectangle "LeaderboardController" as LC
  }
  interface "IService\n(IWordService,\nISozdlService, ...)" as ISVC
  package "mediator" #EAFBEA {
    rectangle "WordService" as WS
    rectangle "SozdlService" as WLS
    rectangle "QuizService" as QS
    rectangle "ScoringService" as SS
    rectangle "LeaderboardService" as LS
  }
  interface "IRepository\n(IWordRepository, ...)" as IREP
  package "entity" #F3EAFB {
    rectangle "User / Word / Theme" as E1
    rectangle "Puzzle / QuizQuestion" as E2
    rectangle "GameSession" as E3
  }
  package "foundation" #FBEAEA {
    rectangle "WordRepository" as WR
    rectangle "PuzzleRepository" as PR
    rectangle "SessionRepository" as SR
    rectangle "AlphabetTokenizer\n(Data Mapper)" as MAP
  }
  database "PostgreSQL" as DB
}

Screens ..> REST
REST ..> AC
REST ..> WC
REST ..> GC

AC --> ISVC
WC --> ISVC
GC --> ISVC
PC --> ISVC
LC --> ISVC

WS ..|> ISVC
WLS ..|> ISVC
QS ..|> ISVC
LS ..|> ISVC

WS --> IREP
WLS --> IREP
QS --> IREP
LS --> IREP

WS ..> E1
WLS ..> E2
WLS ..> E3

WR ..|> IREP
PR ..|> IREP
SR ..|> IREP

WR --> DB
PR --> DB
SR --> DB
@enduml
```

## Соответствие пакетам Java

```
ru.skfu.langgame
├── control      # @RestController + DTO + обработка ошибок
├── mediator     # @Service (бизнес-логика, @Transactional) + интерфейсы IService
├── entity       # @Entity (доменные объекты с методами) + enum
├── foundation   # Spring Data JPA репозитории + мапперы (Data Mapper)
└── config       # SecurityConfig, OpenAPI, JWT-фильтр, инициализация seed
```

> PNG-экспорт: `diagrams/package-diagram-pcmef.png`.
