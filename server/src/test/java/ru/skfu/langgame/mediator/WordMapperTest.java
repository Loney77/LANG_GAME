package ru.skfu.langgame.mediator;

import org.junit.jupiter.api.Test;
import ru.skfu.langgame.entity.Theme;
import ru.skfu.langgame.entity.Word;
import ru.skfu.langgame.mediator.dto.WordDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class WordMapperTest {

    private final WordMapper mapper = new WordMapper();

    @Test
    void mapsAllFieldsIncludingTheme() {
        Theme theme = new Theme();
        theme.setName("Животные");
        Word word = new Word();
        word.setText("аслан");
        word.setTranslation("лев");
        word.setLetterCount(5);
        word.setTheme(theme);

        WordDto dto = mapper.toDto(word);

        assertThat(dto.text()).isEqualTo("аслан");
        assertThat(dto.translation()).isEqualTo("лев");
        assertThat(dto.letterCount()).isEqualTo(5);
        assertThat(dto.theme()).isEqualTo("Животные");
    }

    @Test
    void themeIsNullWhenAbsent() {
        Word word = new Word();
        word.setText("бурун");
        word.setTranslation("нос");
        word.setLetterCount(5);

        assertThat(mapper.toDto(word).theme()).isNull();
    }

    @Test
    void nullEntityMapsToNull() {
        assertThat(mapper.toDto(null)).isNull();
    }

    @Test
    void mapsList() {
        Word w1 = new Word();
        w1.setText("аслан");
        Word w2 = new Word();
        w2.setText("бёрю");

        List<WordDto> dtos = mapper.toDtoList(List.of(w1, w2));

        assertThat(dtos).hasSize(2);
        assertThat(dtos).extracting(WordDto::text).containsExactly("аслан", "бёрю");
    }
}
