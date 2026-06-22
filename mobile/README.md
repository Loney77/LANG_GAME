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

## Настройка адреса сервера

Адрес сервера задаётся переменной `EXPO_PUBLIC_API_URL` в файле `.env`
(шаблон - `.env.example`); код адрес не содержит.

```bash
cp .env.example .env        # Windows PowerShell: Copy-Item .env.example .env
```

Типичные значения: адрес развёрнутого сервера (`https://<домен>`), адрес ПК в
локальной сети (`http://<IP-ПК>:8137`) или `http://10.0.2.2:8137` для Android-эмулятора.

## Запуск в режиме разработки (Expo)

```bash
npm install
npm run android        # либо npm start и сканировать QR в приложении Expo Go
```

## Сборка APK

```bash
eas build -p android --profile preview
```

Команда собирает устанавливаемый APK в облаке Expo и возвращает ссылку на файл.
Адрес сервера берётся из `.env` на момент сборки.

## Проверка типов

```bash
npm run typecheck
```
