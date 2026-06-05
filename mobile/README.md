# Мобильный клиент «Сёз оюн» (React Native + Expo, TypeScript)

Presentation-слой PCMEF: экраны, состояние (Context), API-клиент (Axios + JWT),
оффлайн-кэш (AsyncStorage).

## Экраны (8)

| Экран | Назначение |
|-------|-----------|
| Login/Register | Вход и регистрация (JWT) |
| Home | Меню выбора игры |
| Сёздл | Угадывание слова с подсветкой букв |
| Anagram | Сбор слова из перемешанных букв |
| Quiz | Викторина (4 варианта) |
| Crossword | Заполнение кроссворда по подсказкам |
| Leaderboard | Рейтинг игроков |
| Profile | Профиль, история игр, выход |

## Структура

```
mobile/
├── App.tsx
└── src/
    ├── config.ts            # базовый URL сервера (важно для эмулятора!)
    ├── api/                 # axios-клиент + JWT-интерсептор, типы DTO
    ├── auth/AuthContext.tsx # регистрация/вход/выход, токен в AsyncStorage
    ├── storage/cache.ts     # оффлайн-кэш (withCache)
    ├── util/alphabet.ts     # токенизатор алфавита (диграфы) для плиток
    ├── navigation/          # стек игр + нижние табы
    └── screens/             # 8 экранов
```

## Запуск

1. Установить зависимости:
   ```bash
   cd mobile
   npm install
   # при несовпадении версий под твой Expo:  npx expo install --fix
   ```
2. **Запустить сервер** (см. `../docker/README.md`) — клиент ходит на него по HTTP.
3. Указать адрес сервера в `src/config.ts` (`API_BASE_URL`) под свой эмулятор:
   - Android Studio AVD: `http://10.0.2.2:8137`
   - Genymotion: `http://10.0.3.2:8137`
   - BlueStacks / реальное устройство: `http://<IP-ПК>:8137`
4. Запустить приложение:
   ```bash
   npx expo start
   # затем нажать 'a' — откроется на Android-эмуляторе (нужен запущенный AVD
   # или установленный Expo Go)
   ```

## Проверка типов без запуска

```bash
npm run typecheck
```

> Это React Native через фреймворк Expo (native-часть собирается автоматически,
> отдельный Android-проект писать не нужно). AsyncStorage, Axios и React Navigation
> поддерживаются полностью.
