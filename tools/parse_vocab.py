# -*- coding: utf-8 -*-
"""
ETL: vocab.txt -> words.seed.json

Парсит неструктурированный словарь карачаево-балкарского языка в формат,
пригодный для seed-загрузки в БД (Flyway / CommandLineRunner) и для игр.

Формат входной строки:  "<номер>\t<слово>: <определение>"

Ключевая особенность — токенизатор карачаевского алфавита: диграфы
гъ, къ, нъ, нг, дж считаются ОДНОЙ буквой. Это критично для Сёздл
(подсветка плиток) и для подсчёта длины слова.
"""

import json
import re
import sys
from pathlib import Path

# --- Токенизатор карачаевского алфавита -------------------------------------

# Диграфы (две кириллические буквы = одна буква алфавита).
# Порядок важен только для читаемости; матчинг жадный по факту наличия пары.
DIGRAPHS = ("гъ", "къ", "нъ", "нг", "дж")


def tokenize(word: str) -> list[str]:
    """Разбивает слово на буквы карачаевского алфавита с учётом диграфов.

    Пример: 'азгъан' -> ['а', 'з', 'гъ', 'а', 'н']  (5 букв, не 6 символов)
    """
    w = word.strip().lower()
    letters: list[str] = []
    i = 0
    n = len(w)
    while i < n:
        pair = w[i : i + 2]
        if pair in DIGRAPHS:
            letters.append(pair)
            i += 2
        else:
            letters.append(w[i])
            i += 1
    return letters


def letter_count(word: str) -> int:
    return len(tokenize(word))


# --- Очистка перевода --------------------------------------------------------

# Стилистические/грамматические пометы, которые срезаем из короткого перевода.
LABELS = {
    "мус.", "ист.", "рел.", "бот.", "анат.", "грам.", "перен.", "уст.",
    "карач.", "балк.", "фольк.", "этн.", "дет.", "ласк.", "груб.", "прост.",
    "подр.", "межд.", "вет.", "геогр.", "миф.", "текст.", "ткац.", "лингв.",
    "обл.", "пренебр.", "модальное", "сл.", "частица", "утв.",
}

# Ведущая нумерация смыслов: "1.", "1)", "2)", римские "I", "II".
LEADING_ENUM = re.compile(r"^\s*(?:[IVX]+\.?|\d+[.)])\s*")


def clean_translation(definition: str) -> str:
    """Короткий перевод: первый смысл без помет и скобочных пояснений."""
    text = definition.strip()

    # Срезаем ведущую нумерацию (возможно вложенную: "1. 1)").
    prev = None
    while prev != text:
        prev = text
        text = LEADING_ENUM.sub("", text)

    # Срезаем ведущие пометы.
    changed = True
    while changed:
        changed = False
        token = text.split(" ", 1)[0] if text else ""
        if token.lower() in LABELS:
            text = text[len(token):].strip()
            changed = True

    # Берём первый смысл: до ';' или до маркера второго значения " 2)" / " 2.".
    text = re.split(r";|\s2[.)]", text, maxsplit=1)[0]

    # Убираем скобочные пояснения для короткого перевода.
    text = re.sub(r"\([^)]*\)", "", text)

    # Чистим хвостовую пунктуацию и пробелы.
    text = re.sub(r"\s+", " ", text).strip(" .,–-:")
    return text


# --- Парсер строки -----------------------------------------------------------

# Формат строки: "<слово>: <определение>" (ведущей нумерации в файле нет).
LINE_RE = re.compile(r"^\s*(.+?)\s*:+\s*(.*)$")


def parse_line(line: str):
    m = LINE_RE.match(line)
    if not m:
        return None
    word, definition = m.group(1), m.group(2)
    word = word.strip()
    if not word:
        return None
    return {
        "text": word,
        "translation": clean_translation(definition),
        "fullDefinition": definition.strip(),
        "letters": tokenize(word),
        "letterCount": letter_count(word),
        "hasDigraph": any(l in DIGRAPHS for l in tokenize(word)),
    }


def main():
    root = Path(__file__).resolve().parent.parent
    src = root / "vocab.txt"
    out = root / "data" / "words.seed.json"
    out.parent.mkdir(parents=True, exist_ok=True)

    entries = []
    skipped = []
    for raw in src.read_text(encoding="utf-8").splitlines():
        if not raw.strip():
            continue
        rec = parse_line(raw)
        if rec is None:
            skipped.append(raw)
        else:
            entries.append(rec)

    out.write_text(
        json.dumps(entries, ensure_ascii=False, indent=2), encoding="utf-8"
    )

    # --- Статистика ---
    total = len(entries)
    by_len: dict[int, int] = {}
    for e in entries:
        by_len[e["letterCount"]] = by_len.get(e["letterCount"], 0) + 1
    digraph_count = sum(1 for e in entries if e["hasDigraph"])

    print(f"Распознано слов: {total}")
    print(f"Пропущено строк: {len(skipped)}")
    if skipped:
        for s in skipped:
            print("  SKIP:", s)
    print(f"Слов с диграфами: {digraph_count}")
    print("Распределение по длине (букв -> кол-во):")
    for k in sorted(by_len):
        bar = "#" * by_len[k]
        print(f"  {k:>2}: {by_len[k]:>3} {bar}")
    print(f"\nСёздл-кандидаты (5 букв): {by_len.get(5, 0)}")
    print(f"Записано в: {out}")

    # Примеры со спорными диграфами для ручной проверки.
    print("\nПримеры токенизации (диграфы):")
    for e in entries:
        if e["hasDigraph"]:
            print(f"  {e['text']:<10} -> {e['letters']}  ({e['letterCount']})")
            if sum(1 for x in entries[:entries.index(e)+1] if x['hasDigraph']) >= 8:
                break


if __name__ == "__main__":
    sys.stdout.reconfigure(encoding="utf-8")
    main()
