// DTO, соответствующие ответам сервера.

export interface AuthResponse {
  token: string;
  username: string;
  role: string;
}

export type TileStatus = 'CORRECT' | 'PRESENT' | 'ABSENT';

export interface PuzzleDto {
  id: number;
  gameType: string;
  length: number;
  date: string | null;
}

export interface GuessResult {
  tiles: TileStatus[];
  attempt: number;
  finished: boolean;
  win: boolean;
}

export interface AnagramPuzzleDto {
  puzzleId: number;
  letters: string[];
  length: number;
  hint: string;
}

export interface AnswerResult {
  correct: boolean;
  score: number;
  correctAnswer: string | null;
}

export interface QuizOption {
  id: number;
  text: string;
}

export interface QuizQuestionDto {
  questionId: number;
  questionText: string;
  options: QuizOption[];
}

export interface QuizAnswerResult {
  correct: boolean;
  correctOptionId: number;
  score: number;
}

export interface CrosswordClue {
  number: number;
  direction: 'ACROSS' | 'DOWN';
  row: number;
  col: number;
  length: number;
  clue: string;
}

export interface CrosswordPuzzleDto {
  puzzleId: number;
  rows: number;
  cols: number;
  clues: CrosswordClue[];
}

export interface CrosswordResult {
  allCorrect: boolean;
  correct: number;
  total: number;
  score: number;
}

export interface LeaderboardEntry {
  rank: number;
  username: string;
  totalScore: number;
}

export interface SessionDto {
  id: number;
  gameType: string;
  score: number;
  attempts: number;
  status: string;
  finishedAt: string | null;
}

export interface WordDto {
  id: number;
  text: string;
  translation: string;
  letterCount: number;
  theme: string | null;
}
