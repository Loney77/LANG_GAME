package ru.langgame.mediator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.langgame.entity.AlphabetTokenizer;
import ru.langgame.entity.Theme;
import ru.langgame.entity.Word;
import ru.langgame.foundation.IThemeRepository;
import ru.langgame.foundation.IWordRepository;
import ru.langgame.mediator.dto.CreateWordRequest;
import ru.langgame.mediator.dto.UpdateWordRequest;
import ru.langgame.mediator.dto.WordDto;
import ru.langgame.mediator.exception.ConflictException;
import ru.langgame.mediator.exception.NotFoundException;

import java.util.List;

/** Управление словарём: поиск (для игроков) и CRUD (для администратора). */
@Service
@RequiredArgsConstructor
public class WordService implements IWordService {

    private final IWordRepository words;
    private final IThemeRepository themes;
    private final WordMapper mapper;

    @Override
    public WordDto getById(Long id) {
        return mapper.toDto(words.findById(id)
                .orElseThrow(() -> new NotFoundException("Слово не найдено: " + id)));
    }

    @Override
    public List<WordDto> search(Integer length) {
        List<Word> result = (length != null)
                ? words.findByLetterCount(length)
                : words.findAll();
        return mapper.toDtoList(result);
    }

    @Override
    @Transactional
    public WordDto create(CreateWordRequest request) {
        if (words.existsByTextIgnoreCase(request.text())) {
            throw new ConflictException("Такое слово уже есть: " + request.text());
        }
        Word word = new Word();
        applyFields(word, request.text(), request.translation(), request.themeId());
        return mapper.toDto(words.save(word));
    }

    @Override
    @Transactional
    public WordDto update(Long id, UpdateWordRequest request) {
        Word word = words.findById(id)
                .orElseThrow(() -> new NotFoundException("Слово не найдено: " + id));
        applyFields(word, request.text(), request.translation(), request.themeId());
        return mapper.toDto(words.save(word));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!words.existsById(id)) {
            throw new NotFoundException("Слово не найдено: " + id);
        }
        words.deleteById(id);
    }

    private void applyFields(Word word, String text, String translation, Long themeId) {
        word.setText(text);
        word.setTranslation(translation);
        word.setLetterCount(AlphabetTokenizer.letterCount(text));
        if (themeId != null) {
            Theme theme = themes.findById(themeId)
                    .orElseThrow(() -> new NotFoundException("Тема не найдена: " + themeId));
            word.setTheme(theme);
        } else {
            word.setTheme(null);
        }
    }
}
