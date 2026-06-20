package ru.langgame.mediator.crossword;

import org.junit.jupiter.api.Test;
import ru.langgame.entity.AlphabetTokenizer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class CrosswordGeneratorTest {

    private static CrosswordGenerator.Candidate cand(String text) {
        return new CrosswordGenerator.Candidate(text, AlphabetTokenizer.tokenize(text), "пер: " + text);
    }

    @Test
    void generatesConnectedGridWithAtLeastTwoWords() {
        // слова имеют общие буквы → должны переплестись
        List<CrosswordGenerator.Candidate> pool = List.of(
                cand("салам"), cand("арба"), cand("мал"), cand("аскер"), cand("балта"));

        CrosswordLayout layout = CrosswordGenerator.generate(pool, 6);

        assertThat(layout.entries().size()).isGreaterThanOrEqualTo(2);
        assertThat(layout.rows()).isPositive();
        assertThat(layout.cols()).isPositive();
        assertValidGrid(layout);
    }

    @Test
    void everyEntryAnswerComesFromPool() {
        List<CrosswordGenerator.Candidate> pool = List.of(cand("салам"), cand("мал"), cand("арба"));
        CrosswordLayout layout = CrosswordGenerator.generate(pool, 6);
        List<String> texts = pool.stream().map(CrosswordGenerator.Candidate::text).toList();
        assertThat(layout.entries()).allSatisfy(e -> assertThat(texts).contains(e.answer()));
    }

    /** Раскладываем слова на сетку и проверяем: нет конфликтов и есть пересечение. */
    private void assertValidGrid(CrosswordLayout layout) {
        Map<String, String> grid = new HashMap<>();
        int intersections = 0;
        for (CrosswordEntry e : layout.entries()) {
            List<String> letters = AlphabetTokenizer.tokenize(e.answer());
            assertThat(letters).hasSize(e.length());
            for (int k = 0; k < letters.size(); k++) {
                int r = "ACROSS".equals(e.direction()) ? e.row() : e.row() + k;
                int c = "ACROSS".equals(e.direction()) ? e.col() + k : e.col();
                String key = r + "," + c;
                String existing = grid.get(key);
                if (existing != null) {
                    assertThat(existing).isEqualTo(letters.get(k));   // пересечение согласовано
                    intersections++;
                } else {
                    grid.put(key, letters.get(k));
                }
            }
        }
        assertThat(intersections).isGreaterThanOrEqualTo(1);
    }
}
