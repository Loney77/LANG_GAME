import AsyncStorage from '@react-native-async-storage/async-storage';
import { STORAGE_KEYS } from '../config';

/**
 * Простой оффлайн-кэш поверх AsyncStorage.
 *
 * Шаблон использования: попытаться получить данные с сервера, при ошибке сети —
 * вернуть последнее закэшированное значение (оффлайн-режим).
 */
export const cache = {
  async set<T>(key: string, value: T): Promise<void> {
    await AsyncStorage.setItem(STORAGE_KEYS.cachePrefix + key, JSON.stringify(value));
  },

  async get<T>(key: string): Promise<T | null> {
    const raw = await AsyncStorage.getItem(STORAGE_KEYS.cachePrefix + key);
    return raw ? (JSON.parse(raw) as T) : null;
  },
};

/**
 * Возвращает данные с сервера; при сетевой ошибке — из кэша (если есть).
 */
export async function withCache<T>(key: string, fetcher: () => Promise<T>): Promise<{ data: T; offline: boolean }> {
  try {
    const data = await fetcher();
    await cache.set(key, data);
    return { data, offline: false };
  } catch (e) {
    const cached = await cache.get<T>(key);
    if (cached !== null) {
      return { data: cached, offline: true };
    }
    throw e;
  }
}
