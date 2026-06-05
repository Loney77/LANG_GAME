import React, { useCallback, useEffect, useState } from 'react';
import { ActivityIndicator, ScrollView, Text, TouchableOpacity, View } from 'react-native';
import { api } from '../api/client';
import { colors, styles } from '../theme';
import type { QuizQuestionDto } from '../api/types';

export default function QuizScreen() {
  const [q, setQ] = useState<QuizQuestionDto | null>(null);
  const [chosen, setChosen] = useState<number | null>(null);
  const [correctId, setCorrectId] = useState<number | null>(null);
  const [score, setScore] = useState<number | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const load = useCallback(async () => {
    setLoading(true);
    setChosen(null);
    setCorrectId(null);
    setScore(null);
    setError(null);
    try {
      setQ(await api.quiz());
    } catch {
      setError('Не удалось загрузить вопрос');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { load(); }, [load]);

  const answer = async (optionId: number) => {
    if (!q || chosen !== null) return;
    setChosen(optionId);
    try {
      const res = await api.quizAnswer(q.questionId, optionId);
      setCorrectId(res.correctOptionId);
      setScore(res.score);
    } catch {
      setError('Ошибка сети');
    }
  };

  const optionColor = (id: number) => {
    if (chosen === null) return colors.card;
    if (id === correctId) return '#dff3e6';
    if (id === chosen) return '#f8dada';
    return colors.card;
  };

  if (loading) return <View style={styles.center}><ActivityIndicator color={colors.primary} size="large" /></View>;
  if (error) return <View style={styles.center}><Text style={styles.error}>{error}</Text></View>;

  return (
    <ScrollView style={styles.screen}>
      <Text style={styles.title}>{q?.questionText}</Text>

      {q?.options.map((o) => (
        <TouchableOpacity
          key={o.id}
          style={[styles.card, { backgroundColor: optionColor(o.id) }]}
          onPress={() => answer(o.id)}
          disabled={chosen !== null}>
          <Text style={{ fontSize: 16, color: colors.text, textAlign: 'center' }}>{o.text}</Text>
        </TouchableOpacity>
      ))}

      {chosen !== null && (
        <>
          <Text style={[styles.subtitle, { fontWeight: '600', marginTop: 8 }]}>
            {chosen === correctId ? `✅ Верно! +${score}` : '❌ Неверно'}
          </Text>
          <TouchableOpacity style={styles.button} onPress={load}>
            <Text style={styles.buttonText}>Следующий вопрос</Text>
          </TouchableOpacity>
        </>
      )}
    </ScrollView>
  );
}
