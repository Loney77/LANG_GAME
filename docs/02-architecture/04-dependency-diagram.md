# Диаграмма зависимостей пакетов

Показывает направленность зависимостей между Java-пакетами сервера. Граф должен
быть **ацикличным** (требование PCMEF).

```plantuml
@startuml
title Зависимости пакетов (ацикличный граф)

package "control" as C
package "mediator" as M
package "entity" as E
package "foundation" as F
package "config" as CFG

C --> M : использует IService
M --> F : использует IRepository
M --> E : оперирует сущностями
F --> E : загружает/сохраняет
C ..> E : через DTO (только чтение)
CFG ..> C : настройка Security/OpenAPI

note bottom of E
  entity не зависит ни от
  одного слоя выше - листовой узел
end note
@enduml
```

![Диаграмма зависимостей PCMEF](diagrams/dependency-diagram.png)

## Матрица зависимостей

| Из \ В | control | mediator | entity | foundation | config |
|--------|:-------:|:--------:|:------:|:----------:|:------:|
| **control** | - | ✅ (IService) | ✅ (DTO) | ❌ | ❌ |
| **mediator** | ❌ | - | ✅ | ✅ (IRepository) | ❌ |
| **entity** | ❌ | ❌ | - | ❌ | ❌ |
| **foundation** | ❌ | ❌ | ✅ | - | ❌ |
| **config** | ✅ | ✅ | ❌ | ✅ | - |

✅ - допустимая зависимость, ❌ - запрещена (нарушила бы направленность PCMEF).

## Контроль

- Циклы отсутствуют: `entity` - листовой узел, `control` - корневой (на сервере).
- На Этапе 6 проверяется статическим анализом (зависимости пакетов) и код-ревью.
- Нарушение направленности = штраф −20% по критериям методички.
