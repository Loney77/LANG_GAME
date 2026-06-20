package ru.langgame.entity;

/** Статус буквы (плитки) в Sozdl. */
public enum TileStatus {
    /** Буква на своём месте (🟩). */
    CORRECT,
    /** Буква есть в слове, но на другом месте (🟨). */
    PRESENT,
    /** Буквы нет в слове (⬜). */
    ABSENT
}
