package ru.langgame.mediator.crossword;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Автогенератор кроссворда из набора слов.
 *
 * <p>Жадное размещение с пересечениями: первое слово кладётся по горизонтали,
 * каждое следующее - перпендикулярно через общую букву. Проверяются конфликты
 * клеток и смежность (слова не должны «слипаться» боками), что даёт корректную
 * связную сетку. Работает в «буквах» алфавита (диграфы - одна клетка).</p>
 */
public final class CrosswordGenerator {

    /** Слово-кандидат: текст, буквы алфавита, подсказка. */
    public record Candidate(String text, List<String> letters, String clue) {
    }

    private record Point(int r, int c) {
    }

    private record Placement(int row, int col, boolean horizontal) {
    }

    private record Placed(Candidate word, int row, int col, boolean horizontal) {
    }

    private CrosswordGenerator() {
    }

    /**
     * @param candidates пул слов (будет отсортирован по длине убыв.)
     * @param maxWords   желаемое число слов в сетке
     * @return раскладка (с ответами) либо с минимум 2 словами; иначе бросает
     */
    public static CrosswordLayout generate(List<Candidate> candidates, int maxWords) {
        List<Candidate> pool = new ArrayList<>(candidates);
        // перемешиваем для разнообразия длин; длинное слово берём затравкой
        Collections.shuffle(pool);
        pool.stream().max(Comparator.comparingInt(c -> c.letters().size()))
                .ifPresent(longest -> {
                    pool.remove(longest);
                    pool.add(0, longest);
                });

        Map<Point, String> grid = new HashMap<>();
        List<Placed> placed = new ArrayList<>();

        Candidate first = pool.get(0);
        put(grid, first, 0, 0, true);
        placed.add(new Placed(first, 0, 0, true));

        for (int i = 1; i < pool.size() && placed.size() < maxWords; i++) {
            Candidate cand = pool.get(i);
            Placement p = findPlacement(grid, cand);
            if (p != null) {
                put(grid, cand, p.row(), p.col(), p.horizontal());
                placed.add(new Placed(cand, p.row(), p.col(), p.horizontal()));
            }
        }

        if (placed.size() < 2) {
            throw new IllegalStateException("Не удалось собрать кроссворд из данного набора");
        }
        return toLayout(placed);
    }

    private static void put(Map<Point, String> grid, Candidate w, int r, int c, boolean horiz) {
        List<String> letters = w.letters();
        for (int k = 0; k < letters.size(); k++) {
            int pr = horiz ? r : r + k;
            int pc = horiz ? c + k : c;
            grid.put(new Point(pr, pc), letters.get(k));
        }
    }

    /** Лучшее размещение слова - с максимумом пересечений (плотная сетка). */
    private static Placement findPlacement(Map<Point, String> grid, Candidate cand) {
        List<String> letters = cand.letters();
        Placement best = null;
        int bestScore = 0;
        for (int i = 0; i < letters.size(); i++) {
            String letter = letters.get(i);
            for (Map.Entry<Point, String> cell : grid.entrySet()) {
                if (!cell.getValue().equals(letter)) {
                    continue;
                }
                Point hit = cell.getKey();
                for (Placement p : List.of(
                        new Placement(hit.r(), hit.c() - i, true),
                        new Placement(hit.r() - i, hit.c(), false))) {
                    int score = intersectionScore(grid, letters, p);
                    if (score > bestScore) {
                        bestScore = score;
                        best = p;
                    }
                }
            }
        }
        return best;
    }

    /** Число пересечений при размещении, либо -1 если размещение недопустимо. */
    private static int intersectionScore(Map<Point, String> grid, List<String> letters, Placement p) {
        int len = letters.size();
        int intersections = 0;

        // клетки перед началом и после конца должны быть пусты
        Point before = p.horizontal()
                ? new Point(p.row(), p.col() - 1) : new Point(p.row() - 1, p.col());
        Point after = p.horizontal()
                ? new Point(p.row(), p.col() + len) : new Point(p.row() + len, p.col());
        if (grid.containsKey(before) || grid.containsKey(after)) {
            return -1;
        }

        for (int k = 0; k < len; k++) {
            int pr = p.horizontal() ? p.row() : p.row() + k;
            int pc = p.horizontal() ? p.col() + k : p.col();
            Point cur = new Point(pr, pc);
            String existing = grid.get(cur);
            if (existing != null) {
                if (!existing.equals(letters.get(k))) {
                    return -1;
                }
                intersections++;
            } else {
                // соседи перпендикулярно направлению слова должны быть пусты
                Point side1 = p.horizontal() ? new Point(pr - 1, pc) : new Point(pr, pc - 1);
                Point side2 = p.horizontal() ? new Point(pr + 1, pc) : new Point(pr, pc + 1);
                if (grid.containsKey(side1) || grid.containsKey(side2)) {
                    return -1;
                }
            }
        }
        return (intersections >= 1 && intersections < len) ? intersections : -1;
    }

    private static CrosswordLayout toLayout(List<Placed> placed) {
        int minR = placed.stream().flatMap(CrosswordGenerator::cells).mapToInt(Point::r).min().orElse(0);
        int minC = placed.stream().flatMap(CrosswordGenerator::cells).mapToInt(Point::c).min().orElse(0);
        int maxR = placed.stream().flatMap(CrosswordGenerator::cells).mapToInt(Point::r).max().orElse(0);
        int maxC = placed.stream().flatMap(CrosswordGenerator::cells).mapToInt(Point::c).max().orElse(0);

        // нумерация: общие номера для записей с одной стартовой клеткой
        List<Placed> sorted = new ArrayList<>(placed);
        sorted.sort(Comparator.comparingInt((Placed w) -> w.row() - minR)
                .thenComparingInt(w -> w.col() - minC));
        Map<Point, Integer> numberByStart = new LinkedHashMap<>();
        int next = 1;
        List<CrosswordEntry> entries = new ArrayList<>();
        for (Placed w : sorted) {
            Point start = new Point(w.row() - minR, w.col() - minC);
            int number = numberByStart.computeIfAbsent(start, k -> 0);
            if (number == 0) {
                number = next++;
                numberByStart.put(start, number);
            }
            entries.add(new CrosswordEntry(
                    number,
                    w.horizontal() ? "ACROSS" : "DOWN",
                    start.r(), start.c(),
                    w.word().letters().size(),
                    w.word().text(),
                    w.word().clue()));
        }
        return new CrosswordLayout(maxR - minR + 1, maxC - minC + 1, entries);
    }

    private static java.util.stream.Stream<Point> cells(Placed w) {
        List<Point> pts = new ArrayList<>();
        int len = w.word().letters().size();
        for (int k = 0; k < len; k++) {
            pts.add(w.horizontal()
                    ? new Point(w.row(), w.col() + k) : new Point(w.row() + k, w.col()));
        }
        return pts.stream();
    }
}
