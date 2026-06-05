import React from 'react';
import { Text, TouchableOpacity, View, ScrollView } from 'react-native';
import { NativeStackScreenProps } from '@react-navigation/native-stack';
import { useAuth } from '../auth/AuthContext';
import { colors, styles } from '../theme';
import type { GamesStackParamList } from '../navigation/types';

type Props = NativeStackScreenProps<GamesStackParamList, 'Home'>;

const GAMES: { route: keyof GamesStackParamList; title: string; desc: string }[] = [
  { route: 'Sozdl', title: '🟩 Сёздл', desc: 'Угадай слово дня за 6 попыток' },
  { route: 'Anagram', title: '🔤 Анаграмма', desc: 'Собери слово из перемешанных букв' },
  { route: 'Quiz', title: '❓ Викторина', desc: 'Выбери правильный перевод' },
  { route: 'Crossword', title: '🧩 Кроссворд', desc: 'Заполни сетку словами' },
];

export default function HomeScreen({ navigation }: Props) {
  const { user } = useAuth();
  return (
    <ScrollView style={styles.screen}>
      <Text style={styles.title}>Салам, {user?.username}!</Text>
      <Text style={styles.subtitle}>Выбери игру и тренируй карачаевский</Text>
      {GAMES.map((g) => (
        <TouchableOpacity key={g.route} style={styles.card} onPress={() => navigation.navigate(g.route)}>
          <Text style={{ fontSize: 18, fontWeight: '600', color: colors.text }}>{g.title}</Text>
          <Text style={{ color: colors.muted, marginTop: 4 }}>{g.desc}</Text>
        </TouchableOpacity>
      ))}
    </ScrollView>
  );
}
