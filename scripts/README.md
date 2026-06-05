# Запуск проекта

## Сервер

```powershell
powershell -ExecutionPolicy Bypass -File scripts\run-server.ps1
```

Поднимает PostgreSQL в Docker и запускает сервер на `http://localhost:8137`
(Swagger UI — `/swagger-ui.html`). Остановка — `Ctrl+C`.

## Мобильный клиент

Адрес сервера задаётся переменной `EXPO_PUBLIC_API_URL` в `mobile/.env`
(шаблон — `mobile/.env.example`).

```powershell
powershell -ExecutionPolicy Bypass -File scripts\run-mobile.ps1
```

После старта Metro нажмите `a` для запуска в эмуляторе или отсканируйте QR-код
в приложении Expo Go.

## Остановка

- Сервер и Metro — `Ctrl+C` в соответствующих терминалах.
- База данных — `docker compose -f docker\docker-compose.yml down` (с `-v` — с удалением данных).

## Сборка APK

В APK код JavaScript встроен в сборку, поэтому перед сборкой в `mobile/.env`
указывается адрес сервера, доступный с устройства.

```powershell
cd mobile
eas build -p android --profile preview
```

Команда собирает APK в облаке Expo и возвращает ссылку на установочный файл.
Локальная сборка выполняется через `npx expo prebuild -p android` и Gradle
(`assembleRelease`).
