package ru.langgame.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Справочник типов игр (SOZDL, ANAGRAM, QUIZ, CROSSWORD). */
@Entity
@Table(name = "game_type")
@Getter
@Setter
@NoArgsConstructor
public class GameType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(nullable = false, length = 80)
    private String title;
}
