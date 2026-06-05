import React, { useCallback, useEffect, useState } from 'react';
import { ActivityIndicator, ScrollView, Text, TouchableOpacity, View } from 'react-native';
import { api } from '../api/client';
import { colors, styles } from '../theme';
import type { AnagramPuzzleDto } from '../api/types';

interface Tile {
  letter: string;
  id: number;
}

function LetterTile({ letter, onPress, muted }: { letter: string; onPress: () => void; muted?: boolean }) {
  return (
    <TouchableOpacity
      onPress={onPress}
      style={{
        minWidth: 48, height: 52, margin: 5, borderRadius: 8, paddingHorizontal: 6,
        alignItems: 'center', justifyContent: 'center',
        backgroundColor: muted ? colors.border : colors.primary,
      }}>
      <Text style={{ color: muted ? colors.muted : '#fff', fontSize: 22, fontWeight: '700' }}>{letter}</Text>
    </TouchableOpacity>
  );
}

export default function AnagramScreen() {
  const [puzzle, setPuzzle] = useState<AnagramPuzzleDto | null>(null);
  const [pool, setPool] = useState<Tile[]>([]);
  const [chosen, setChosen] = useState<Tile[]>([]);
  const [result, setResult] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const load = useCallback(async () => {
    setLoading(true);
    setResult(null);
    setChosen([]);
    setError(null);
    try {
      const p = await api.anagram(5);
      setPuzzle(p);
      setPool(p.letters.map((letter, id) => ({ letter, id })));
    } catch {
      setError('Не удалось загрузить анаграмму');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { load(); }, [load]);

  const pick = (tile: Tile) => {
    if (result) return;
    setPool((p) => p.filter((t) => t.id !== tile.id));
    setChosen((c) => [...c, tile]);
  };

  const unpick = (tile: Tile) => {
    if (result) return;
    setChosen((c) => c.filter((t) => t.id !== tile.id));
    setPool((p) => [...p, tile].sort((a, b) => a.id - b.id));
  };

  const submit = async () => {
    if (!puzzle) return;
    const answer = chosen.map((t) => t.letter).join('');
    try {
      const res = await api.anagramAnswer(puzzle.puzzleId, answer);
      setResult(res.correct ? `✅ Верно! +${res.score} очков` : `❌ Неверно. Ответ: ${res.correctAnswer}`);
    } catch {
      setResult('Ошибка сети');
    }
  };

  if (loading) return <View style={styles.center}><ActivityIndicator color={colors.primary} size="large" /></View>;
  if (error) return <View style={styles.center}><Text style={styles.error}>{error}</Text></View>;

  return (
    <ScrollView style={styles.screen} contentContainerStyle={{ alignItems: 'center' }}>
      <Text style={[styles.subtitle, { textAlign: 'center' }]}>Подсказка: {puzzle?.hint}</Text>

      {/* Собираемое слово */}
      <View style={{
        flexDirection: 'row', flexWrap: 'wrap', justifyContent: 'center', minHeight: 64,
        borderBottomWidth: 2, borderColor: colors.border, marginBottom: 20, paddingBottom: 6,
      }}>
        {chosen.length === 0
          ? <Text style={{ color: colors.muted, alignSelf: 'center' }}>нажимайте буквы ниже</Text>
          : chosen.map((t) => <LetterTile key={t.id} letter={t.letter} onPress={() => unpick(t)} />)}
      </View>

      {/* Доступные буквы */}
      <View style={{ flexDirection: 'row', flexWrap: 'wrap', justifyContent: 'center', marginBottom: 16 }}>
        {pool.map((t) => <LetterTile key={t.id} letter={t.letter} onPress={() => pick(t)} />)}
      </View>

      {!result ? (
        <TouchableOpacity
          style={[styles.button, { width: '100%', opacity: chosen.length === puzzle?.length ? 1 : 0.5 }]}
          disabled={chosen.length !== puzzle?.length}
          onPress={submit}>
          <Text style={styles.buttonText}>Проверить</Text>
        </TouchableOpacity>
      ) : (
        <>
          <Text style={[styles.subtitle, { fontWeight: '600', textAlign: 'center' }]}>{result}</Text>
          <TouchableOpacity style={[styles.button, { width: '100%' }]} onPress={load}>
            <Text style={styles.buttonText}>Новое слово</Text>
          </TouchableOpacity>
        </>
      )}
    </ScrollView>
  );
}
