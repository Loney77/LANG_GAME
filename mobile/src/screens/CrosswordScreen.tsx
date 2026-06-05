import React, { useCallback, useEffect, useMemo, useState } from 'react';
import {
  ActivityIndicator, Dimensions, ScrollView, Text, TextInput, TouchableOpacity, View,
} from 'react-native';
import { api } from '../api/client';
import { colors, styles } from '../theme';
import type { CrosswordPuzzleDto } from '../api/types';

const dirLabel = (d: string) => (d === 'ACROSS' ? 'по горизонтали' : 'по вертикали');
const keyOf = (n: number, d: string) => `${n}:${d}`;

interface Cell {
  letter: string;
  number?: number;
}

export default function CrosswordScreen() {
  const [puzzle, setPuzzle] = useState<CrosswordPuzzleDto | null>(null);
  const [answers, setAnswers] = useState<Record<string, string>>({});
  const [result, setResult] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const load = useCallback(async () => {
    setLoading(true);
    setAnswers({});
    setResult(null);
    setError(null);
    try {
      setPuzzle(await api.crossword());
    } catch {
      setError('Не удалось сгенерировать кроссворд');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { load(); }, [load]);

  // Построение сетки из подсказок + введённых ответов.
  const grid = useMemo(() => {
    if (!puzzle) return null;
    const map = new Map<string, Cell>();
    for (const c of puzzle.clues) {
      // символы (диграфы = 2 клетки), синхронно с сервером
      const letters = Array.from(answers[keyOf(c.number, c.direction)] ?? '');
      for (let k = 0; k < c.length; k++) {
        const r = c.direction === 'ACROSS' ? c.row : c.row + k;
        const col = c.direction === 'ACROSS' ? c.col + k : c.col;
        const id = `${r},${col}`;
        const existing = map.get(id);
        const letter = letters[k] ?? existing?.letter ?? '';
        map.set(id, { letter, number: k === 0 ? c.number : existing?.number });
      }
    }
    return map;
  }, [puzzle, answers]);

  if (loading) return <View style={styles.center}><ActivityIndicator color={colors.primary} size="large" /></View>;
  if (error || !puzzle || !grid) {
    return (
      <View style={styles.center}>
        <Text style={styles.error}>{error}</Text>
        <TouchableOpacity style={styles.button} onPress={load}><Text style={styles.buttonText}>Повторить</Text></TouchableOpacity>
      </View>
    );
  }

  const cell = Math.max(20, Math.floor((Dimensions.get('window').width - 24) / puzzle.cols));

  const submit = async () => {
    const payload = puzzle.clues.map((c) => ({
      number: c.number,
      direction: c.direction,
      answer: answers[keyOf(c.number, c.direction)] ?? '',
    }));
    try {
      const res = await api.crosswordAnswer(puzzle.puzzleId, payload);
      setResult(res.allCorrect
        ? `🎉 Всё верно! ${res.correct}/${res.total}, +${res.score}`
        : `Угадано ${res.correct} из ${res.total}`);
    } catch {
      setResult('Ошибка сети');
    }
  };

  return (
    <ScrollView style={styles.screen}>
      <Text style={[styles.subtitle, { textAlign: 'center' }]}>Сетка {puzzle.rows}×{puzzle.cols}</Text>

      {/* Сетка кроссворда */}
      <View style={{ alignSelf: 'center', marginBottom: 16 }}>
        {Array.from({ length: puzzle.rows }, (_, r) => (
          <View key={r} style={{ flexDirection: 'row' }}>
            {Array.from({ length: puzzle.cols }, (_, c) => {
              const data = grid.get(`${r},${c}`);
              if (!data) {
                return <View key={c} style={{ width: cell, height: cell }} />;
              }
              return (
                <View key={c} style={{
                  width: cell, height: cell, borderWidth: 1, borderColor: colors.primaryDark,
                  backgroundColor: '#fff', alignItems: 'center', justifyContent: 'center',
                }}>
                  {data.number != null && (
                    <Text style={{ position: 'absolute', top: 1, left: 2, fontSize: 8, color: colors.muted }}>
                      {data.number}
                    </Text>
                  )}
                  <Text style={{ fontSize: cell > 26 ? 16 : 11, fontWeight: '700', color: colors.text }}>
                    {data.letter}
                  </Text>
                </View>
              );
            })}
          </View>
        ))}
      </View>

      {/* Подсказки + ввод ответов (заполняют сетку выше) */}
      {puzzle.clues.map((c) => (
        <View key={keyOf(c.number, c.direction)} style={styles.card}>
          <Text style={{ color: colors.muted, marginBottom: 6 }}>
            №{c.number} {dirLabel(c.direction)} · {c.length} букв
          </Text>
          <Text style={{ fontSize: 15, color: colors.text, marginBottom: 8 }}>{c.clue}</Text>
          <TextInput
            style={[styles.input, { marginBottom: 0 }]}
            placeholder="ответ"
            autoCapitalize="none"
            autoCorrect={false}
            value={answers[keyOf(c.number, c.direction)] ?? ''}
            onChangeText={(t) => setAnswers((p) => ({ ...p, [keyOf(c.number, c.direction)]: t }))}
            editable={!result}
          />
        </View>
      ))}

      {result && <Text style={[styles.subtitle, { fontWeight: '600', textAlign: 'center' }]}>{result}</Text>}

      <TouchableOpacity style={[styles.button, { width: '100%' }]} onPress={result ? load : submit}>
        <Text style={styles.buttonText}>{result ? 'Новый кроссворд' : 'Проверить'}</Text>
      </TouchableOpacity>
    </ScrollView>
  );
}
