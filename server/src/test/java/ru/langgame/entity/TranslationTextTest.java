package ru.langgame.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TranslationTextTest {

    @Test
    void cleanReturnsEmptyForNull() {
        assertThat(TranslationText.clean(null)).isEmpty();
    }

    @Test
    void cleanTakesOnlyFirstSense() {
        assertThat(TranslationText.clean("бег; ход; движение")).isEqualTo("бег");
    }

    @Test
    void cleanStripsLeadingEnumeration() {
        assertThat(TranslationText.clean("1. мяч")).isEqualTo("мяч");
        assertThat(TranslationText.clean("2) дом")).isEqualTo("дом");
    }

    @Test
    void cleanStripsLeadingAbbreviation() {
        assertThat(TranslationText.clean("спорт. мяч")).isEqualTo("мяч");
    }

    @Test
    void cleanStripsTrailingGenderMarker() {
        assertThat(TranslationText.clean("стол м.")).isEqualTo("стол");
    }

    @Test
    void cleanReplacesVariantSeparator() {
        assertThat(TranslationText.clean("бисер||бисерный")).isEqualTo("бисер/бисерный");
    }

    @Test
    void quizRejectsNullArguments() {
        assertThat(TranslationText.isQuizSuitable(null, "лев")).isFalse();
        assertThat(TranslationText.isQuizSuitable("аслан", null)).isFalse();
    }

    @Test
    void quizRejectsCrossReferences() {
        assertThat(TranslationText.isQuizSuitable("сёз", "то же, что слово")).isFalse();
        assertThat(TranslationText.isQuizSuitable("сёз", "страд. от чего-либо")).isFalse();
    }

    @Test
    void quizRejectsReferenceToAnotherWord() {
        assertThat(TranslationText.isQuizSuitable("сёз", "идти к дому")).isFalse();
    }

    @Test
    void quizRejectsTooShortTranslation() {
        assertThat(TranslationText.isQuizSuitable("ат", "и")).isFalse();
    }

    @Test
    void quizRejectsLoanwordEqualToWord() {
        assertThat(TranslationText.isQuizSuitable("абажур", "абажур")).isFalse();
    }

    @Test
    void quizAcceptsRealTranslation() {
        assertThat(TranslationText.isQuizSuitable("аслан", "лев")).isTrue();
    }
}
