# -*- coding: utf-8 -*-
"""
Извлечение карачаево-балкарского словаря из БД Сёзлюк (Эльбрусоид) + чистка.

ВНИМАНИЕ: исходная БД и результат - НЕ для коммита (правовые ограничения источника).

Делает (детерминированно, без LLM - ничего не выдумывает):
  * чистит русский перевод: убирает HTML, пометы, нумерацию, примеры (после ~ и :),
    скобки, берёт первый смысл;
  * отбрасывает перекрёстные ссылки («то же, что…», «страд. к…», «см. …»);
  * отбрасывает РУССКИЕ ЗАИМСТВОВАНИЯ - полные (абажур) и с карачаевским аффиксом
    (палата+лы→«палатный», инициатор+лукъ) - по списку русских слов data/russian.txt;
    настоящие карач. слова, совпадающие с рус. именем (аслан=лев), сохраняются, т.к.
    сверяем КОРЕНЬ С ПЕРЕВОДОМ, а список лишь подтверждает, что корень русский.

Запуск: python tools/extract_sozluk.py  ->  data/sozluk.words.json
"""
import html
import json
import re
import sqlite3
import sys
from collections import Counter
from pathlib import Path

sys.stdout.reconfigure(encoding="utf-8")

ROOT = Path(__file__).resolve().parent.parent
DB = r"C:\Users\Klance\Desktop\elbrusoid\assets\slovarbr"
OUT = ROOT / "data" / "sozluk.words.json"
RUS_FILE = ROOT / "data" / "russian.txt"

# --- алфавит ---
DIGRAPHS = ("гъ", "къ", "нъ", "нг", "дж")
KB_LETTERS = set("абвгдежзийклмнопрстуфхцчшщъыьэюёя")


def tokenize(word):
    w = word.strip().lower()
    out, i = [], 0
    while i < len(w):
        if w[i:i + 2] in DIGRAPHS:
            out.append(w[i:i + 2]); i += 2
        else:
            out.append(w[i]); i += 1
    return out


# --- чистка перевода ---
TAG = re.compile(r"<[^>]+>")
LEADING_ENUM = re.compile(r"^\s*(?:[IVX]+\.?|\d+[.)])\s*")
LEADING_ABBR = re.compile(r"^[а-яёa-z]{1,9}\.\s*")
PARENS = re.compile(r"\([^)]*\)")
RAZN = re.compile(r"^в\s+разн\.?\s*знач\.?\s*", re.IGNORECASE)
CROSS_REF = ("см.", "то же", "отвлеч", "понуд", "страд", "взаимн", "возвр",
             "однокр", "многокр", "уменьш", "ласк", "собир", "превосх", "сравн", "действ")
# пометы без точки, идущие в начале перевода
BARE_LABELS = {"редко", "разг", "прост", "груб", "поэт", "уст", "обл", "шутл",
               "ирон", "бран", "фольк", "миф", "собир", "ласк", "детск", "вульг",
               "карач", "балк"}


def clean(raw):
    s = html.unescape(raw or "").replace("­", "")
    s = TAG.sub("", s)
    s = s.split("~", 1)[0]                 # пример после ~ - отрезаем
    s = s.split(";", 1)[0]                 # первый смысл
    s = re.split(r"\s2[.)]", s, maxsplit=1)[0]   # до второго значения
    for _ in range(6):
        n = RAZN.sub("", s)
        n = LEADING_ENUM.sub("", n)
        n = LEADING_ABBR.sub("", n)
        tok = n.split(" ", 1)[0].strip(".,")
        if tok.lower() in BARE_LABELS:
            n = n[len(n.split(" ", 1)[0]):].strip()
        if n == s:
            break
        s = n
    s = s.split(":", 1)[0]                 # пример после : - отрезаем
    s = PARENS.sub("", s)                  # скобочные пояснения
    s = re.sub(r"\s+", " ", s).strip(" .,–-:()")
    return s


def is_cross_ref(raw, cleaned):
    low = (raw or "").lower()
    if any(m in low for m in CROSS_REF):
        return True
    c = cleaned.lower()
    return c.startswith("к ") or " к " in c or c.startswith("от ")


# --- детект заимствований ---
SUFFIXES = sorted({"лыкъ", "лукъ", "лик", "люк", "лы", "ли", "лу", "лю",
                   "чы", "чи", "сыз", "сиз", "ча", "че"}, key=len, reverse=True)


def load_rus():
    if not RUS_FILE.exists():
        print("ВНИМАНИЕ: нет data/russian.txt - фильтр заимствований выключен")
        return set()
    return set(w.lower() for w in RUS_FILE.read_text(encoding="cp1251", errors="replace").split())


def lcp(a, b):
    n = 0
    for x, y in zip(a, b):
        if x == y:
            n += 1
        else:
            break
    return n


def shares_root(stem, t0):
    return lcp(stem, t0) >= 4 and lcp(stem, t0) >= min(len(stem), len(t0)) - 2


def first_token(t):
    m = re.findall(r"[а-яё]+", t.lower())
    return m[0] if m else ""


def is_loanword(word, cleaned, rus):
    if not rus:
        return False
    w = word.lower()
    t0 = first_token(cleaned)
    if not t0:
        return False
    if w == t0 and w in rus:                       # полное заимствование
        return True
    for s in SUFFIXES:                              # дериват: корень + аффикс
        if w.endswith(s) and len(w) - len(s) >= 4:
            stem = w[:-len(s)]
            if stem in rus and shares_root(stem, t0):
                return True
    return False


def is_clean_word(slovo):
    w = slovo.strip().lower()
    return bool(w) and " " not in w and "-" not in w and all(ch in KB_LETTERS for ch in w)


def main():
    rus = load_rus()
    con = sqlite3.connect(DB)
    cur = con.cursor()

    seen = {}
    stats = Counter()
    for slovo, perevod in cur.execute("select slovo, perevod from slovarkbr"):
        stats["rows"] += 1
        if not slovo or not is_clean_word(slovo):
            stats["not_word"] += 1
            continue
        key = slovo.strip().lower()
        if key in seen:
            continue
        cleaned = clean(perevod)
        if len(cleaned) < 2:
            stats["empty"] += 1
            continue
        if is_cross_ref(perevod, cleaned):
            stats["cross_ref"] += 1
            continue
        if is_loanword(key, cleaned, rus):
            stats["loanword"] += 1
            continue
        letters = tokenize(key)
        seen[key] = {"text": key, "translation": cleaned,
                     "letterCount": len(letters),
                     "hasDigraph": any(l in DIGRAPHS for l in letters)}
    con.close()

    words = sorted(seen.values(), key=lambda x: x["text"])
    OUT.write_text(json.dumps(words, ensure_ascii=False, indent=1), encoding="utf-8")

    by_len = Counter(w["letterCount"] for w in words)
    print(f"Строк в БД: {stats['rows']}")
    print(f"  не одиночное слово: {stats['not_word']}")
    print(f"  пустой/короткий перевод: {stats['empty']}")
    print(f"  перекрёстные ссылки (откинуто): {stats['cross_ref']}")
    print(f"  заимствования (откинуто): {stats['loanword']}")
    print(f"ИТОГО чистых слов: {len(words)}")
    print(f"  5-буквенных: {by_len.get(5, 0)}")
    print(f"Записано в: {OUT}")
    print("\nПримеры:")
    for w in words[:12]:
        print(f"  {w['text']:<14} ({w['letterCount']}) - {w['translation'][:55]}")


if __name__ == "__main__":
    main()
