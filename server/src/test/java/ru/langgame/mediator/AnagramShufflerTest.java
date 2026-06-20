package ru.langgame.mediator;

import org.junit.jupiter.api.Test;
import ru.langgame.entity.AlphabetTokenizer;

import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class AnagramShufflerTest {

    @Test
    void keepsSameLettersMultiset() {
        List<String> shuffled = AnagramShuffler.shuffle("аслан", new Random(1));
        assertThat(shuffled).containsExactlyInAnyOrderElementsOf(
                AlphabetTokenizer.tokenize("аслан"));
    }

    @Test
    void differsFromOriginalForVariedWord() {
        // у слова из разных букв перемешанный вариант не должен совпасть с исходным
        List<String> original = AlphabetTokenizer.tokenize("батыр");
        List<String> shuffled = AnagramShuffler.shuffle("батыр", new Random(7));
        assertThat(shuffled).isNotEqualTo(original);
    }

    @Test
    void preservesDigraphAsSingleToken() {
        // 'азгъан' → диграф 'гъ' остаётся одной плиткой
        List<String> shuffled = AnagramShuffler.shuffle("азгъан", new Random(3));
        assertThat(shuffled).contains("гъ");
        assertThat(shuffled).hasSize(5);
    }
}
