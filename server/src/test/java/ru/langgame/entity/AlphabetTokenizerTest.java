package ru.langgame.entity;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AlphabetTokenizerTest {

    @Test
    void splitsSimpleWordIntoLetters() {
        assertThat(AlphabetTokenizer.tokenize("абрек"))
                .containsExactly("а", "б", "р", "е", "к");
    }

    @Test
    void treatsDigraphGъAsSingleLetter() {
        assertThat(AlphabetTokenizer.tokenize("азгъан"))
                .containsExactly("а", "з", "гъ", "а", "н");
        assertThat(AlphabetTokenizer.letterCount("азгъан")).isEqualTo(5);
    }

    @Test
    void treatsAllDigraphsAsSingleLetters() {
        assertThat(AlphabetTokenizer.tokenize("азлыкъ"))
                .containsExactly("а", "з", "л", "ы", "къ");
        assertThat(AlphabetTokenizer.tokenize("айюнъе"))
                .containsExactly("а", "й", "ю", "нъ", "е");
        assertThat(AlphabetTokenizer.tokenize("акъджал"))   // къ + дж
                .containsExactly("а", "къ", "дж", "а", "л");
    }

    @Test
    void isCaseInsensitiveAndTrims() {
        assertThat(AlphabetTokenizer.tokenize("  АзГъАн  "))
                .containsExactly("а", "з", "гъ", "а", "н");
    }

    @Test
    void emptyStringYieldsEmptyList() {
        assertThat(AlphabetTokenizer.tokenize("")).isEmpty();
        assertThat(AlphabetTokenizer.letterCount("")).isZero();
    }

    @Test
    void nullThrows() {
        assertThatThrownBy(() -> AlphabetTokenizer.tokenize(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void trailingSingleCharIsNotMistakenForDigraph() {
        // 'н' в конце не образует диграф без следующей буквы
        List<String> letters = AlphabetTokenizer.tokenize("сан");
        assertThat(letters).containsExactly("с", "а", "н");
    }
}
