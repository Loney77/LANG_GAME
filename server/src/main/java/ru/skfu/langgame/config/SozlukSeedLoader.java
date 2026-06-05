package ru.skfu.langgame.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import ru.skfu.langgame.entity.AlphabetTokenizer;
import ru.skfu.langgame.entity.Word;
import ru.skfu.langgame.foundation.IWordRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Dev-загрузчик расширенного словаря (Сёзлюк) из локального JSON.
 *
 * <p>Данные источника НЕ хранятся в репозитории. Если файл существует рядом и в БД
 * только базовый seed — слова догружаются (идемпотентно, без дублей по написанию).
 * На машинах без файла загрузчик ничего не делает.</p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SozlukSeedLoader implements ApplicationRunner {

    private static final long BASELINE_THRESHOLD = 1000;   // выше — считаем, что уже загружено
    private static final int BATCH = 1000;
    private static final int MAX_TRANSLATION = 255;

    private final IWordRepository words;
    private final ObjectMapper objectMapper;

    @Value("${app.seed.sozluk-file:../data/sozluk.words.json}")
    private String sozlukFile;

    @Value("${app.seed.enabled:true}")
    private boolean enabled;

    @JsonIgnoreProperties(ignoreUnknown = true)
    record SozlukWord(String text, String translation) {
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!enabled) {
            return;
        }
        if (words.count() > BASELINE_THRESHOLD) {
            log.info("Словарь уже наполнен ({} слов) — пропускаю загрузку Сёзлюка", words.count());
            return;
        }
        Path path = Path.of(sozlukFile);
        if (!Files.exists(path)) {
            log.info("Файл {} не найден — расширенный словарь не загружается (ок для CI/чужих машин)", path);
            return;
        }

        SozlukWord[] all = objectMapper.readValue(path.toFile(), SozlukWord[].class);
        Set<String> existing = words.findAll().stream()
                .map(w -> w.getText().toLowerCase(Locale.ROOT))
                .collect(Collectors.toSet());

        List<Word> batch = new ArrayList<>(BATCH);
        int inserted = 0;
        for (SozlukWord rec : all) {
            if (rec.text() == null || rec.translation() == null) {
                continue;
            }
            String key = rec.text().toLowerCase(Locale.ROOT);
            if (!existing.add(key)) {     // уже есть (baseline или дубль) — пропускаем
                continue;
            }
            Word word = new Word();
            word.setText(rec.text());
            word.setTranslation(truncate(rec.translation()));
            word.setLetterCount(AlphabetTokenizer.letterCount(rec.text()));
            batch.add(word);
            if (batch.size() >= BATCH) {
                words.saveAll(batch);
                inserted += batch.size();
                batch.clear();
            }
        }
        if (!batch.isEmpty()) {
            words.saveAll(batch);
            inserted += batch.size();
        }
        log.info("Загрузка Сёзлюка завершена: добавлено {} слов (всего в БД: {})", inserted, words.count());
    }

    private static String truncate(String s) {
        return s.length() > MAX_TRANSLATION ? s.substring(0, MAX_TRANSLATION) : s;
    }
}
