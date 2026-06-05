# Паттерн Data Mapper

**Назначение (методичка):** отделение бизнес-логики/домена от доступа к данным.

В проекте Data Mapper применён на двух уровнях.

## 1. Персистентность: репозитории Spring Data JPA

Слой Foundation (`IWordRepository`, `IPuzzleRepository`, `ISessionRepository`,
`IUserRepository`) — это реализация Data Mapper: он отображает строки таблиц на
доменные сущности и обратно, а домен (Entity) не содержит SQL и не знает о БД.

```java
public interface IWordRepository extends JpaRepository<Word, Long> {
    boolean existsByTextIgnoreCase(String text);
}
```

## 2. Транспорт: мапперы Entity ↔ DTO

Чтобы наружу (в Presentation) не утекали JPA-сущности с ленивыми связями,
введён явный маппер `WordMapper` (слой Mediator), преобразующий `Word` → `WordDto`:

```java
@Component
public class WordMapper {
    public WordDto toDto(Word word) {
        return new WordDto(word.getId(), word.getText(), word.getTranslation(),
                word.getLetterCount(),
                word.getTheme() != null ? word.getTheme().getName() : null);
    }
}
```

**Эффект рефакторинга:**
- сущности перестали быть частью внешнего контракта;
- разорвана связь между схемой БД и форматом ответа API;
- упростилось тестирование представления (мокать DTO, а не граф сущностей).

Покрыто тестом `WordMapperTest` (поля, отсутствие темы, null, список).

> Файлы: `mediator/WordMapper.java`, `mediator/dto/WordDto.java`,
> `foundation/I*Repository.java`.
