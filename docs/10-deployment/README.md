# Этап. Развёртывание

Серверная часть разворачивается в Docker, мобильный клиент - через Expo (APK).

## Серверный стек (Docker)

Конфигурация - в каталоге [docker/](../../docker/).

- **Локальный запуск:** `docker-compose.yml` поднимает PostgreSQL и сервер
  (`docker compose up --build`).
- **Продакшн с HTTPS:** `docker-compose.prod.yml` добавляет обратный прокси Caddy
  с автоматическим сертификатом Let's Encrypt; наружу открыты только порты 80 и 443.
  Параметры (`SITE_ADDRESS`, `DB_PASSWORD`, `JWT_SECRET`) задаются в `docker/.env`.

```bash
cd docker
docker compose -f docker-compose.prod.yml up -d --build
```

Сервер собирается из [server/Dockerfile](../../server/Dockerfile) (многоступенчатая
сборка: Gradle → JRE). Миграции базы данных применяются автоматически (Flyway).

## Мобильный клиент

- Режим разработки: `npm run android` (Expo).
- Установочный APK: `eas build -p android --profile preview` (Expo Application Services);
  адрес сервера берётся из `mobile/.env` (`EXPO_PUBLIC_API_URL`).

Подробнее - в [docker/README.md](../../docker/README.md) и [mobile/README.md](../../mobile/README.md).

## Переменные окружения

| Переменная | Назначение |
|------------|-----------|
| `SERVER_PORT` | Порт сервера (по умолчанию 8137) |
| `DB_URL`, `DB_USER`, `DB_PASSWORD` | Подключение к PostgreSQL |
| `JWT_SECRET` | Ключ подписи JWT |
| `SITE_ADDRESS` | Доменное имя для HTTPS (Caddy, прод) |
| `EXPO_PUBLIC_API_URL` | Адрес сервера для мобильного клиента |
