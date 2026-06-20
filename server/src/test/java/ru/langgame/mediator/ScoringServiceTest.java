package ru.langgame.mediator;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ScoringServiceTest {

    private final ScoringService scoring = new ScoringService();

    @Test
    void lossGivesZero() {
        assertThat(scoring.score(3, 5_000, false)).isZero();
    }

    @Test
    void winWithFirstAttemptAndNoTimeGivesBase() {
        assertThat(scoring.score(1, 0, true)).isEqualTo(ScoringService.BASE);
    }

    @Test
    void moreAttemptsReduceScore() {
        int first = scoring.score(1, 0, true);
        int third = scoring.score(3, 0, true);
        assertThat(third).isLessThan(first);
        assertThat(third).isEqualTo(ScoringService.BASE - 2 * ScoringService.ATTEMPT_PENALTY);
    }

    @Test
    void timePenaltyApplied() {
        // 30 секунд → штраф 3
        assertThat(scoring.score(1, 30_000, true)).isEqualTo(ScoringService.BASE - 3);
    }

    @Test
    void scoreNeverBelowMinWin() {
        assertThat(scoring.score(6, 600_000, true)).isEqualTo(ScoringService.MIN_WIN);
    }

    @Test
    void scoreIsNeverNegative() {
        assertThat(scoring.score(100, 10_000_000, true)).isGreaterThanOrEqualTo(0);
    }
}
