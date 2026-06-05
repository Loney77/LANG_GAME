# -*- coding: utf-8 -*-
"""
Генерирует Flyway-миграцию V3__seed_words.sql из data/words.seed.json.

Запускать после parse_vocab.py. Воспроизводимо: при изменении словаря —
перегенерировать (или создать новую версию миграции, если V3 уже применена).
"""

import json
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
SRC = ROOT / "data" / "words.seed.json"
OUT = ROOT / "server" / "src" / "main" / "resources" / "db" / "migration" / "V3__seed_words.sql"


def sql_str(value: str) -> str:
    """Безопасно оформляет строку как SQL-литерал (экранирование кавычек)."""
    return "'" + value.replace("'", "''") + "'"


def main():
    words = json.loads(SRC.read_text(encoding="utf-8"))
    lines = [
        "-- =====================================================================",
        f"-- V3: Загрузка словаря ({len(words)} слов) из vocab.txt.",
        "-- Сгенерировано tools/gen_seed_sql.py — не редактировать вручную.",
        "-- theme_id = NULL: тему присваивает администратор через CRUD.",
        "-- =====================================================================",
        "",
        "INSERT INTO word (text, translation, full_definition, letter_count, theme_id) VALUES",
    ]
    rows = []
    for w in words:
        rows.append(
            f"    ({sql_str(w['text'])}, {sql_str(w['translation'])}, "
            f"{sql_str(w['fullDefinition'])}, {w['letterCount']}, NULL)"
        )
    lines.append(",\n".join(rows) + ";")
    lines.append("")

    OUT.write_text("\n".join(lines), encoding="utf-8")
    print(f"Записано {len(words)} слов в {OUT}")


if __name__ == "__main__":
    main()
