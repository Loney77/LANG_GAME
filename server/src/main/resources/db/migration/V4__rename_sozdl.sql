-- =====================================================================
-- V4: Переименование типа игры WORDLE → SOZDL («Сёздл»).
-- «Wordle» - товарный знак; используем собственное имя.
-- =====================================================================

ALTER TABLE game_type DROP CONSTRAINT game_type_code_check;

UPDATE game_type SET code = 'SOZDL', title = 'Сёздл' WHERE code = 'WORDLE';

ALTER TABLE game_type ADD CONSTRAINT game_type_code_check
    CHECK (code IN ('SOZDL', 'ANAGRAM', 'QUIZ', 'CROSSWORD'));
