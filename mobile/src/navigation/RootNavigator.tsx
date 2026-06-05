import React from 'react';
import { ActivityIndicator, View } from 'react-native';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { useAuth } from '../auth/AuthContext';
import { colors, styles } from '../theme';
import type { GamesStackParamList, MainTabsParamList } from './types';

import LoginScreen from '../screens/LoginScreen';
import HomeScreen from '../screens/HomeScreen';
import SozdlScreen from '../screens/SozdlScreen';
import AnagramScreen from '../screens/AnagramScreen';
import QuizScreen from '../screens/QuizScreen';
import CrosswordScreen from '../screens/CrosswordScreen';
import LeaderboardScreen from '../screens/LeaderboardScreen';
import ProfileScreen from '../screens/ProfileScreen';

const GamesStack = createNativeStackNavigator<GamesStackParamList>();
const Tabs = createBottomTabNavigator<MainTabsParamList>();

const screenHeader = { headerStyle: { backgroundColor: colors.primary }, headerTintColor: '#fff' };

function GamesNavigator() {
  return (
    <GamesStack.Navigator screenOptions={screenHeader}>
      <GamesStack.Screen name="Home" component={HomeScreen} options={{ title: 'Сёз оюн' }} />
      <GamesStack.Screen name="Sozdl" component={SozdlScreen} options={{ title: 'Сёздл' }} />
      <GamesStack.Screen name="Anagram" component={AnagramScreen} options={{ title: 'Анаграмма' }} />
      <GamesStack.Screen name="Quiz" component={QuizScreen} options={{ title: 'Викторина' }} />
      <GamesStack.Screen name="Crossword" component={CrosswordScreen} options={{ title: 'Кроссворд' }} />
    </GamesStack.Navigator>
  );
}

function MainTabs() {
  return (
    <Tabs.Navigator screenOptions={{ ...screenHeader, tabBarActiveTintColor: colors.primary }}>
      <Tabs.Screen name="Игры" component={GamesNavigator} options={{ headerShown: false }} />
      <Tabs.Screen name="Рейтинг" component={LeaderboardScreen} />
      <Tabs.Screen name="Профиль" component={ProfileScreen} />
    </Tabs.Navigator>
  );
}

export default function RootNavigator() {
  const { user, loading } = useAuth();

  if (loading) {
    return (
      <View style={styles.center}>
        <ActivityIndicator size="large" color={colors.primary} />
      </View>
    );
  }

  return (
    <NavigationContainer>
      {user ? <MainTabs /> : <LoginScreen />}
    </NavigationContainer>
  );
}
