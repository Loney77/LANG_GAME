package ru.langgame.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WordTest {

    @Test
    void getLettersTokenizesWithDigraphs() {
        Word word = new Word();
        word.setText("азгъан");
        assertThat(word.getLetters()).containsExactly("а", "з", "гъ", "а", "н");
    }

    @Test
    void getLettersCountsDigraphAsSingleLetter() {
        Word word = new Word();
        word.setText("джашил");
        assertThat(word.getLetters()).hasSize(5);
    }
}
