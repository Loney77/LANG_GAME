import React, { useState } from 'react';
import { Text, TextInput, TouchableOpacity, View, ScrollView } from 'react-native';
import { useAuth } from '../auth/AuthContext';
import { styles } from '../theme';

export default function LoginScreen() {
  const { login, register } = useAuth();
  const [isRegister, setIsRegister] = useState(false);
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);

  const submit = async () => {
    setError(null);
    setBusy(true);
    try {
      if (isRegister) {
        await register(username.trim(), email.trim(), password);
      } else {
        await login(email.trim(), password);
      }
    } catch (e: any) {
      const msg = e?.response?.data?.message ?? 'Не удалось подключиться к серверу';
      setError(msg);
    } finally {
      setBusy(false);
    }
  };

  return (
    <ScrollView contentContainerStyle={[styles.screen, { justifyContent: 'center', flexGrow: 1 }]}>
      <Text style={styles.title}>Сёз оюн</Text>
      <Text style={styles.subtitle}>
        {isRegister ? 'Регистрация нового игрока' : 'Вход в приложение'}
      </Text>

      {error && <Text style={styles.error}>{error}</Text>}

      {isRegister && (
        <TextInput
          style={styles.input}
          placeholder="Имя пользователя"
          autoCapitalize="none"
          value={username}
          onChangeText={setUsername}
        />
      )}
      <TextInput
        style={styles.input}
        placeholder="Email"
        keyboardType="email-address"
        autoCapitalize="none"
        value={email}
        onChangeText={setEmail}
      />
      <TextInput
        style={styles.input}
        placeholder="Пароль"
        secureTextEntry
        value={password}
        onChangeText={setPassword}
      />

      <TouchableOpacity style={styles.button} onPress={submit} disabled={busy}>
        <Text style={styles.buttonText}>{busy ? '...' : isRegister ? 'Зарегистрироваться' : 'Войти'}</Text>
      </TouchableOpacity>

      <TouchableOpacity onPress={() => { setIsRegister(!isRegister); setError(null); }}>
        <Text style={styles.link}>
          {isRegister ? 'Уже есть аккаунт? Войти' : 'Нет аккаунта? Зарегистрироваться'}
        </Text>
      </TouchableOpacity>
    </ScrollView>
  );
}
