/**
 * Базовый адрес REST API.
 *
 * Берётся из переменной окружения EXPO_PUBLIC_API_URL (файл mobile/.env,
 * см. mobile/.env.example); при отсутствии — локальный дефолт на порту 8137.
 * Вынесение в окружение позволяет запускать клиент и сервер на разных
 * устройствах и в разных сетях без изменения исходного кода.
 */
const rawApiUrl = process.env.EXPO_PUBLIC_API_URL?.trim();
export const API_BASE_URL = rawApiUrl
  ? (/^https?:\/\//.test(rawApiUrl) ? rawApiUrl : `https://${rawApiUrl}`).replace(/\/+$/, '')
  : 'http://localhost:8137';

/** Ключи AsyncStorage. */
export const STORAGE_KEYS = {
  token: 'auth.token',
  user: 'auth.user',
  cachePrefix: 'cache.',
};
