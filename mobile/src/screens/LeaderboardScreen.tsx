import React, { useEffect, useState } from 'react';
import { ActivityIndicator, FlatList, RefreshControl, Text, View } from 'react-native';
import { api } from '../api/client';
import { colors, styles } from '../theme';
import type { LeaderboardEntry } from '../api/types';

export default function LeaderboardScreen() {
  const [entries, setEntries] = useState<LeaderboardEntry[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const load = async () => {
    try {
      setEntries(await api.leaderboard(30));
      setError(null);
    } catch {
      setError('Не удалось загрузить рейтинг');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); }, []);

  if (loading) return <View style={styles.center}><ActivityIndicator color={colors.primary} size="large" /></View>;

  return (
    <View style={styles.screen}>
      <Text style={styles.title}>Рейтинг (все игры, месяц)</Text>
      {error && <Text style={styles.error}>{error}</Text>}
      <FlatList
        data={entries}
        keyExtractor={(e) => String(e.rank)}
        refreshControl={<RefreshControl refreshing={false} onRefresh={load} />}
        ListEmptyComponent={<Text style={styles.subtitle}>Пока никто не набрал очков</Text>}
        renderItem={({ item }) => (
          <View style={[styles.card, { flexDirection: 'row', justifyContent: 'space-between' }]}>
            <Text style={{ fontSize: 16, color: colors.text }}>{item.rank}. {item.username}</Text>
            <Text style={{ fontSize: 16, fontWeight: '700', color: colors.primary }}>{item.totalScore}</Text>
          </View>
        )}
      />
    </View>
  );
}
