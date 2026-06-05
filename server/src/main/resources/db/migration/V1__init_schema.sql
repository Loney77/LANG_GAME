-- =====================================================================
-- V1: Начальная схема БД «Сёз оюн» (PostgreSQL, нормализация до 3НФ)
-- =====================================================================

-- ---- Пользователи ----------------------------------------------------
CREATE TABLE users (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    role          VARCHAR(10)  NOT NULL DEFAULT 'USER'
                  CHECK (role IN ('USER', 'ADMIN')),
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now()
);

-- ---- Темы (категории слов) ------------------------------------------
CREATE TABLE theme (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name        VARCHAR(80) NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- ---- Справочник типов игр -------------------------------------------
CREATE TABLE game_type (
    id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE
         CHECK (code IN ('WORDLE', 'ANAGRAM', 'QUIZ', 'CROSSWORD')),
    title VARCHAR(80) NOT NULL
);

-- ---- Слова (словарные статьи) ---------------------------------------
CREATE TABLE word (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    text            VARCHAR(64)  NOT NULL,             -- карачаевское слово
    translation     VARCHAR(255) NOT NULL,             -- русский перевод
    full_definition TEXT,                              -- полное определение
    letter_count    INT          NOT NULL CHECK (letter_count > 0),
    theme_id        BIGINT REFERENCES theme(id) ON DELETE SET NULL,
    -- омонимы (одно написание, разные значения) допускаются → уникальна пара
    CONSTRAINT uq_word_text_translation UNIQUE (text, translation)
);
CREATE INDEX idx_word_letter_count ON word(letter_count);
CREATE INDEX idx_word_theme        ON word(theme_id);

-- ---- Задания (конкретные экземпляры игр) ----------------------------
CREATE TABLE puzzle (
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    game_type_id BIGINT NOT NULL REFERENCES game_type(id),
    puzzle_date  DATE,                                  -- для ежедневных заданий
    word_id      BIGINT REFERENCES word(id),            -- целевое слово (Wordle/анаграмма)
    payload      JSONB,                                 -- данные игры (буквы, сетка)
    created_by   BIGINT REFERENCES users(id),
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now()
);
-- одно ежедневное задание на тип игры и дату
CREATE UNIQUE INDEX uq_puzzle_daily ON puzzle(game_type_id, puzzle_date)
    WHERE puzzle_date IS NOT NULL;

-- ---- Вопросы викторины ----------------------------------------------
CREATE TABLE quiz_question (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    puzzle_id       BIGINT NOT NULL REFERENCES puzzle(id) ON DELETE CASCADE,
    question_text   VARCHAR(500) NOT NULL,
    correct_word_id BIGINT NOT NULL REFERENCES word(id)
);

-- ---- Варианты ответа викторины (нормализация вместо JSON-массива) ----
CREATE TABLE quiz_option (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    question_id BIGINT  NOT NULL REFERENCES quiz_question(id) ON DELETE CASCADE,
    option_text VARCHAR(255) NOT NULL,
    is_correct  BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE INDEX idx_quiz_option_question ON quiz_option(question_id);

-- ---- Сессии игр (результаты) ----------------------------------------
-- gameType НЕ дублируется: он выводится через puzzle → game_type (3НФ).
CREATE TABLE game_session (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id     BIGINT NOT NULL REFERENCES users(id),
    puzzle_id   BIGINT NOT NULL REFERENCES puzzle(id),
    score       INT    NOT NULL DEFAULT 0 CHECK (score >= 0),
    attempts    INT    NOT NULL DEFAULT 0 CHECK (attempts >= 0),
    duration_ms BIGINT CHECK (duration_ms >= 0),
    status      VARCHAR(12) NOT NULL DEFAULT 'IN_PROGRESS'
                CHECK (status IN ('IN_PROGRESS', 'WIN', 'LOSS')),
    started_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    finished_at TIMESTAMPTZ
);
CREATE INDEX idx_session_user   ON game_session(user_id);
CREATE INDEX idx_session_puzzle ON game_session(puzzle_id);
