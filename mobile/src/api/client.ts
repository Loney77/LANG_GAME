import axios from 'axios';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { API_BASE_URL, STORAGE_KEYS } from '../config';
import type {
  AnagramPuzzleDto,
  AnswerResult,
  AuthResponse,
  CrosswordPuzzleDto,
  CrosswordResult,
  GuessResult,
  LeaderboardEntry,
  PuzzleDto,
  QuizAnswerResult,
  QuizQuestionDto,
  SessionDto,
  WordDto,
} from './types';

/** Единый HTTP-клиент с JWT-интерсептором (слой api клиента / Presentation). */
export const http = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
});

// Подставляем Bearer-токен в каждый запрос.
http.interceptors.request.use(async (config) => {
  const token = await AsyncStorage.getItem(STORAGE_KEYS.token);
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export const api = {
  // --- auth ---
  register: (username: string, email: string, password: string) =>
    http.post<AuthResponse>('/api/auth/register', { username, email, password }).then((r) => r.data),
  login: (email: string, password: string) =>
    http.post<AuthResponse>('/api/auth/login', { email, password }).then((r) => r.data),

  // --- sozdl ---
  dailySozdl: () => http.get<PuzzleDto>('/api/puzzles/daily').then((r) => r.data),
  sozdlGuess: (puzzleId: number, guess: string) =>
    http.post<GuessResult>('/api/games/sozdl/guess', { puzzleId, guess }).then((r) => r.data),

  // --- anagram ---
  anagram: (length: number) =>
    http.get<AnagramPuzzleDto>(`/api/puzzles/anagram?length=${length}`).then((r) => r.data),
  anagramAnswer: (puzzleId: number, answer: string) =>
    http.post<AnswerResult>('/api/games/anagram/answer', { puzzleId, answer }).then((r) => r.data),

  // --- quiz ---
  quiz: () => http.get<QuizQuestionDto>('/api/puzzles/quiz').then((r) => r.data),
  quizAnswer: (questionId: number, optionId: number) =>
    http.post<QuizAnswerResult>('/api/games/quiz/answer', { questionId, optionId }).then((r) => r.data),

  // --- crossword ---
  crossword: () => http.get<CrosswordPuzzleDto>('/api/puzzles/crossword').then((r) => r.data),
  crosswordAnswer: (
    puzzleId: number,
    answers: { number: number; direction: string; answer: string }[],
  ) =>
    http
      .post<CrosswordResult>('/api/games/crossword/answer', { puzzleId, answers })
      .then((r) => r.data),

  // --- misc ---
  leaderboard: (days = 30) =>
    http.get<LeaderboardEntry[]>(`/api/leaderboard?days=${days}`).then((r) => r.data),
  mySessions: () => http.get<SessionDto[]>('/api/sessions/me').then((r) => r.data),
  words: (length?: number) =>
    http
      .get<WordDto[]>(length ? `/api/words?length=${length}` : '/api/words')
      .then((r) => r.data),
};
