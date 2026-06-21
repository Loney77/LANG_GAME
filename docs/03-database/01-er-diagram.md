# ER-диаграмма (логическая модель данных)

Схема нормализована до **3НФ**: нет повторяющихся групп, все неключевые атрибуты
зависят от полного первичного ключа, транзитивные зависимости устранены
(в частности, тип игры у сессии не дублируется, а выводится через `puzzle`).

```plantuml
@startuml
hide circle
skinparam linetype ortho

entity users {
  * id : bigint <<PK>>
  --
  * username : varchar(50) <<U>>
  * email : varchar(255) <<U>>
  * password_hash : varchar(100)
  * role : varchar(10)
  * created_at : timestamptz
}

entity theme {
  * id : bigint <<PK>>
  --
  * name : varchar(80) <<U>>
  description : varchar(255)
}

entity game_type {
  * id : bigint <<PK>>
  --
  * code : varchar(20) <<U>>
  * title : varchar(80)
}

entity word {
  * id : bigint <<PK>>
  --
  * text : varchar(64)
  * translation : varchar(255)
  full_definition : text
  * letter_count : int
  theme_id : bigint <<FK>>
}

entity puzzle {
  * id : bigint <<PK>>
  --
  * game_type_id : bigint <<FK>>
  puzzle_date : date
  word_id : bigint <<FK>>
  payload : jsonb
  created_by : bigint <<FK>>
  * created_at : timestamptz
}

entity quiz_question {
  * id : bigint <<PK>>
  --
  * puzzle_id : bigint <<FK>>
  * question_text : varchar(500)
  * correct_word_id : bigint <<FK>>
}

entity quiz_option {
  * id : bigint <<PK>>
  --
  * question_id : bigint <<FK>>
  * option_text : varchar(255)
  * is_correct : boolean
}

entity game_session {
  * id : bigint <<PK>>
  --
  * user_id : bigint <<FK>>
  * puzzle_id : bigint <<FK>>
  * score : int
  * attempts : int
  duration_ms : bigint
  * status : varchar(12)
  * started_at : timestamptz
  finished_at : timestamptz
}

theme       ||--o{ word          : "классифицирует"
game_type   ||--o{ puzzle        : "тип"
word        ||--o{ puzzle        : "целевое слово"
users       ||--o{ puzzle        : "создал (админ)"
puzzle      ||--o{ quiz_question : "содержит"
word        ||--o{ quiz_question : "верный ответ"
quiz_question ||--o{ quiz_option : "варианты"
users       ||--o{ game_session  : "играет"
puzzle      ||--o{ game_session  : "порождает"
@enduml
```

## Таблицы

| Таблица | Назначение | Кол-во записей (seed) |
|---------|-----------|----------------------|
| `users` | Игроки и администраторы | — (создаются при регистрации) |
| `theme` | Темы слов | 6 (V2) |
| `game_type` | Справочник типов игр | 4 (V2) |
| `word` | Словарь | 246 (V3) |
| `puzzle` | Задания | — (генерируются) |
| `quiz_question` | Вопросы викторины | — |
| `quiz_option` | Варианты ответа | — |
| `game_session` | Результаты игр | — |

![ER-диаграмма](images/er-diagram.png)
