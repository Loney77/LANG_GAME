package ru.langgame.control;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.langgame.mediator.IWordService;
import ru.langgame.mediator.dto.CreateWordRequest;
import ru.langgame.mediator.dto.UpdateWordRequest;
import ru.langgame.mediator.dto.WordDto;

import java.util.List;

/** Словарь: поиск/чтение для игроков, CRUD для администратора. */
@RestController
@RequestMapping("/api/words")
@RequiredArgsConstructor
@Tag(name = "Словарь")
public class WordController {

    private final IWordService wordService;

    @GetMapping
    @Operation(summary = "Список/поиск слов (фильтр по длине)")
    public List<WordDto> search(@RequestParam(required = false) Integer length) {
        return wordService.search(length);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Слово по идентификатору")
    public WordDto getById(@PathVariable Long id) {
        return wordService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать слово (ADMIN)")
    public WordDto create(@Valid @RequestBody CreateWordRequest request) {
        return wordService.create(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Изменить слово (ADMIN)")
    public WordDto update(@PathVariable Long id, @Valid @RequestBody UpdateWordRequest request) {
        return wordService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить слово (ADMIN)")
    public void delete(@PathVariable Long id) {
        wordService.delete(id);
    }
}
