package ru.langgame.mediator;

import org.junit.jupiter.api.Test;
import ru.langgame.entity.AlphabetTokenizer;
import ru.langgame.entity.TileStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.langgame.entity.TileStatus.*;

class LetterMatcherTest {

    private static TileStatus[] eval(String guess, String target) {
        return LetterMatcher.evaluate(
                AlphabetTokenizer.tokenize(guess),
                AlphabetTokenizer.tokenize(target));
    }

    @Test
    void allCorrectWhenGuessEqualsTarget() {
        assertThat(eval("абрек", "абрек"))
                .containsExactly(CORRECT, CORRECT, CORRECT, CORRECT, CORRECT);
    }

    @Test
    void marksPresentAndAbsent() {
        assertThat(eval("керба", "абрек"))
                .containsExactly(PRESENT, PRESENT, CORRECT, PRESENT, PRESENT);
    }

    @Test
    void absentLetterMarkedGray() {
        assertThat(eval("бвгде", "тумак")).containsOnly(ABSENT);
    }

    @Test
    void duplicateLetterNotOverMarked() {
        assertThat(eval("аалар", "анана"))
                .containsExactly(CORRECT, PRESENT, ABSENT, PRESENT, ABSENT);
    }

    @Test
    void worksWithDigraphLetters() {
        assertThat(eval("гъазан", "азгъан"))
                .containsExactly(PRESENT, PRESENT, PRESENT, CORRECT, CORRECT);
    }
}
