import React, { useEffect, useRef, useState } from 'react';
import {
  ActivityIndicator, Pressable, ScrollView, Text, TextInput, TouchableOpacity, View,
} from 'react-native';
import { api } from '../api/client';
import { withCache } from '../storage/cache';
import { tokenize } from '../util/alphabet';
import { colors, styles } from '../theme';
import type { PuzzleDto, TileStatus } from '../api/types';

interface Row {
  tokens: string[];
  tiles: TileStatus[];
}

const tileColor = (s: TileStatus) =>
  s === 'CORRECT' ? colors.correct : s === 'PRESENT' ? colors.present : colors.absent;

/** Ряд плиток: для прошлых попыток — с цветом, для текущей — пустые/вводимые. */
function TileRow({ tokens, tiles, length }: { tokens: string[]; tiles?: TileStatus[]; length: number }) {
  const cells = Array.from({ length }, (_, i) => tokens[i] ?? '');
  return (
    <View style={{ flexDirection: 'row', justifyContent: 'center', marginBottom: 8 }}>
      {cells.map((t, i) => (
        <View
          key={i}
          style={{
            width: 52, height: 52, marginHorizontal: 4, borderRadius: 8,
            alignItems: 'center', justifyContent: 'center',
            backgroundColor: tiles ? tileColor(tiles[i]) : '#fff',
            borderWidth: tiles ? 0 : 2,
            borderColor: t ? colors.primary : colors.border,
          }}>
          <Text style={{ fontSize: 22, fontWeight: '700', color: tiles ? '#fff' : colors.text }}>
            {t}
          </Text>
        </View>
      ))}
    </View>
  );
}

export default function SozdlScreen() {
  const [puzzle, setPuzzle] = useState<PuzzleDto | null>(null);
  const [rows, setRows] = useState<Row[]>([]);
  const [guess, setGuess] = useState('');
  const [message, setMessage] = useState<string | null>(null);
  const [finished, setFinished] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const inputRef = useRef<TextInput>(null);

  useEffect(() => {
    (async () => {
      try {
        const { data } = await withCache('sozdl.daily', api.dailySozdl);
        setPuzzle(data);
      } catch {
        setError('Не удалось загрузить задание');
      } finally {
        setLoading(false);
      }
    })();
  }, []);

  const submit = async () => {
    if (!puzzle) return;
    setMessage(null);
    try {
      const res = await api.sozdlGuess(puzzle.id, guess.trim());
      setRows((prev) => [...prev, { tokens: tokenize(guess), tiles: res.tiles }]);
      setGuess('');
      if (res.finished) {
        setFinished(true);
        setMessage(res.win ? '🎉 Победа!' : 'Попытки закончились');
      }
    } catch (e: any) {
      const code = e?.response?.status;
      setMessage(
        code === 422 ? 'Такого слова нет в словаре'
        : code === 400 ? `Нужно слово из ${puzzle.length} букв`
        : 'Ошибка сети',
      );
    }
  };

  if (loading) return <View style={styles.center}><ActivityIndicator color={colors.primary} size="large" /></View>;
  if (error || !puzzle) return <View style={styles.center}><Text style={styles.error}>{error}</Text></View>;

  return (
    <ScrollView style={styles.screen} contentContainerStyle={{ alignItems: 'center' }}>
      <Text style={[styles.subtitle, { textAlign: 'center' }]}>Угадай слово из {puzzle.length} букв</Text>

      {rows.map((row, ri) => (
        <TileRow key={ri} tokens={row.tokens} tiles={row.tiles} length={puzzle.length} />
      ))}

      {!finished && (
        <Pressable onPress={() => inputRef.current?.focus()} style={{ width: '100%', alignItems: 'center' }}>
          <TileRow tokens={tokenize(guess)} length={puzzle.length} />
          {/* Скрытый ввод поверх клеток: тап по клеткам открывает клавиатуру */}
          <TextInput
            ref={inputRef}
            value={guess}
            onChangeText={setGuess}
            onSubmitEditing={submit}
            autoCapitalize="none"
            autoCorrect={false}
            maxLength={puzzle.length * 2}
            blurOnSubmit={false}
            style={{ position: 'absolute', width: '100%', height: 60, opacity: 0 }}
          />
        </Pressable>
      )}

      {message && (
        <Text style={[styles.subtitle, { marginTop: 10, fontWeight: '600', textAlign: 'center' }]}>{message}</Text>
      )}

      {!finished && (
        <TouchableOpacity style={[styles.button, { width: '100%' }]} onPress={submit}>
          <Text style={styles.buttonText}>Проверить</Text>
        </TouchableOpacity>
      )}
    </ScrollView>
  );
}
