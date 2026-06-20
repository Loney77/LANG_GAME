# Запуск в Docker

Полный стек (PostgreSQL, сервер и публичный туннель ngrok) поднимается одной командой.
Это позволяет запускать проект на любом устройстве без установки Java и ручной настройки —
достаточно Docker.

## 1. Настройка

Скопируйте шаблон переменных окружения и заполните его:

```bash
cp .env.example .env        # Windows PowerShell: Copy-Item .env.example .env
```

| Переменная | Назначение |
|------------|-----------|
| `NGROK_AUTHTOKEN` | Токен из личного кабинета ngrok |
| `NGROK_DOMAIN` | Постоянный домен ngrok (полный URL со схемой) |
| `JWT_SECRET` | Ключ подписи JWT |

## 2. Запуск

```bash
docker compose up --build
```

Поднимаются три сервиса:

| Сервис | Назначение | Порт |
|--------|-----------|------|
| `db` | PostgreSQL | `localhost:5433` |
| `server` | REST API (Spring Boot) | `localhost:8137` |
| `ngrok` | Публичный туннель к серверу | домен из `NGROK_DOMAIN` |

При первом старте сервер применяет миграции Flyway (схема, справочники, словарь).
Документация API доступна по адресу `http://localhost:8137/swagger-ui.html`, а извне —
по адресу `NGROK_DOMAIN`. Веб-инспектор ngrok — `http://localhost:4040`.

Адрес `NGROK_DOMAIN` указывается в мобильном клиенте через `mobile/.env`
(`EXPO_PUBLIC_API_URL`), после чего приложение работает из любой сети.

## 3. Остановка

```bash
docker compose down        # с сохранением данных
docker compose down -v     # с удалением данных базы
```
