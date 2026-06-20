package ru.langgame.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/** Словарная статья: карачаевское слово и его перевод. */
@Entity
@Table(name = "word")
@Getter
@Setter
@NoArgsConstructor
public class Word {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Карачаевское слово. */
    @Column(nullable = false, length = 64)
    private String text;

    /** Краткий русский перевод. */
    @Column(nullable = false, length = 255)
    private String translation;

    /** Полное определение из словаря. */
    @Column(name = "full_definition", columnDefinition = "text")
    private String fullDefinition;

    /** Длина в буквах карачаевского алфавита (с учётом диграфов). */
    @Column(name = "letter_count", nullable = false)
    private int letterCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_id")
    private Theme theme;

    /**
     * Доменный метод (не анемичная модель): буквы слова по карач. алфавиту.
     * Делегирует доменной утилите того же слоя.
     */
    @Transient
    public List<String> getLetters() {
        return AlphabetTokenizer.tokenize(text);
    }
}
