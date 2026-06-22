import React, { useEffect, useState } from 'react';
import { ActivityIndicator, FlatList, Text, TouchableOpacity, View } from 'react-native';
import { api } from '../api/client';
import { useAuth } from '../auth/AuthContext';
import { colors, styles } from '../theme';
import type { SessionDto } from '../api/types';

export default function ProfileScreen() {
  const { user, logout } = useAuth();
  const [sessions, setSessions] = useState<SessionDto[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    (async () => {
      try {
        setSessions(await api.mySessions());
      } catch {
        // оффлайн / ошибка - оставляем пустой список
      } finally {
        setLoading(false);
      }
    })();
  }, []);

  const total = sessions.reduce((s, x) => s + x.score, 0);

  return (
    <View style={styles.screen}>
      <Text style={styles.title}>{user?.username}</Text>
      <Text style={styles.subtitle}>Всего очков: {total}</Text>

      <Text style={[styles.title, { fontSize: 18 }]}>История игр</Text>
      {loading ? (
        <ActivityIndicator color={colors.primary} />
      ) : (
        <FlatList
          data={sessions}
          keyExtractor={(s) => String(s.id)}
          ListEmptyComponent={<Text style={styles.subtitle}>Сыграй первую партию!</Text>}
          renderItem={({ item }) => (
            <View style={[styles.card, { flexDirection: 'row', justifyContent: 'space-between' }]}>
              <Text style={{ color: colors.text }}>{item.gameType} · {item.status}</Text>
              <Text style={{ fontWeight: '700', color: colors.primary }}>{item.score}</Text>
            </View>
          )}
        />
      )}

      <TouchableOpacity style={[styles.button, { backgroundColor: colors.danger }]} onPress={logout}>
        <Text style={styles.buttonText}>Выйти</Text>
      </TouchableOpacity>
    </View>
  );
}
