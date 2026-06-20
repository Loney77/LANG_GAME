package ru.langgame.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.time.LocalDate;

/** Конкретное игровое задание (в т.ч. ежедневное). */
@Entity
@Table(name = "puzzle")
@Getter
@Setter
@NoArgsConstructor
public class Puzzle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "game_type_id", nullable = false)
    private GameType gameType;

    /** Дата ежедневного задания (null — не ежедневное). */
    @Column(name = "puzzle_date")
    private LocalDate puzzleDate;

    /** Целевое слово (для Sozdl/анаграммы). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id")
    private Word word;

    /** Данные игры (буквы анаграммы, сетка кроссворда) в формате JSON. */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String payload;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    /** Является ли задание ежедневным. */
    public boolean isDaily() {
        return puzzleDate != null;
    }

    /** Просрочено ли ежедневное задание относительно указанной даты. */
    public boolean isExpired(LocalDate today) {
        return puzzleDate != null && puzzleDate.isBefore(today);
    }
}
