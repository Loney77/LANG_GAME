package ru.langgame.mediator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WordServiceTest {

    @Mock private IWordRepository words;
    @Mock private IThemeRepository themes;

    private WordService service;

    @BeforeEach
    void setUp() {
        service = new WordService(words, themes, new WordMapper());
    }

    private static Word word(Long id, String text, String translation) {
        Word w = new Word();
        w.setId(id);
        w.setText(text);
        w.setTranslation(translation);
        return w;
    }

    @Test
    void getByIdReturnsDto() {
        when(words.findById(1L)).thenReturn(Optional.of(word(1L, "къол", "рука")));
        WordDto dto = service.getById(1L);
        assertThat(dto.text()).isEqualTo("къол");
        assertThat(dto.translation()).isEqualTo("рука");
    }

    @Test
    void getByIdThrowsWhenMissing() {
        when(words.findById(9L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getById(9L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void searchByLengthUsesLetterCountQuery() {
        when(words.findByLetterCount(5)).thenReturn(List.of(word(1L, "джашил", "зелёный")));
        assertThat(service.search(5)).hasSize(1);
        verify(words).findByLetterCount(5);
    }

    @Test
    void searchWithoutLengthReturnsAll() {
        when(words.findAll()).thenReturn(List.of(word(1L, "ат", "лошадь"), word(2L, "юй", "дом")));
        assertThat(service.search(null)).hasSize(2);
    }

    @Test
    void createSavesNewWordAndComputesLetterCount() {
        when(words.existsByTextIgnoreCase("къол")).thenReturn(false);
        when(words.save(any(Word.class))).thenAnswer(inv -> inv.getArgument(0));
        WordDto dto = service.create(new CreateWordRequest("къол", "рука", null));
        assertThat(dto.text()).isEqualTo("къол");
        assertThat(dto.letterCount()).isEqualTo(3); // к, ъ -> «къ» диграф: к,ъ,о,л = 4? нет: къ,о,л = 3
    }

    @Test
    void createRejectsDuplicate() {
        when(words.existsByTextIgnoreCase("къол")).thenReturn(true);
        assertThatThrownBy(() -> service.create(new CreateWordRequest("къол", "рука", null)))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void createWithThemeLooksUpTheme() {
        Theme theme = new Theme();
        theme.setName("Тело");
        when(words.existsByTextIgnoreCase("къол")).thenReturn(false);
        when(themes.findById(3L)).thenReturn(Optional.of(theme));
        when(words.save(any(Word.class))).thenAnswer(inv -> inv.getArgument(0));
        WordDto dto = service.create(new CreateWordRequest("къол", "рука", 3L));
        assertThat(dto.theme()).isEqualTo("Тело");
    }

    @Test
    void updateChangesExistingWord() {
        when(words.findById(1L)).thenReturn(Optional.of(word(1L, "къол", "рука")));
        when(words.save(any(Word.class))).thenAnswer(inv -> inv.getArgument(0));
        WordDto dto = service.update(1L, new UpdateWordRequest("аякъ", "нога", null));
        assertThat(dto.text()).isEqualTo("аякъ");
        assertThat(dto.translation()).isEqualTo("нога");
    }

    @Test
    void updateThrowsWhenMissing() {
        when(words.findById(9L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.update(9L, new UpdateWordRequest("х", "y", null)))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void deleteRemovesExisting() {
        when(words.existsById(1L)).thenReturn(true);
        service.delete(1L);
        verify(words).deleteById(1L);
    }

    @Test
    void deleteThrowsWhenMissing() {
        when(words.existsById(9L)).thenReturn(false);
        assertThatThrownBy(() -> service.delete(9L)).isInstanceOf(NotFoundException.class);
    }
}
