# Запуск в Docker

## Локальная разработка

PostgreSQL и сервер поднимаются одной командой:

```bash
docker compose up --build
```

| Сервис | Назначение | Порт |
|--------|-----------|------|
| `db` | PostgreSQL | `localhost:5433` |
| `server` | REST API (Spring Boot) | `localhost:8137` |

Документация API — `http://localhost:8137/swagger-ui.html`. Остановка — `docker compose down`
(с `-v` — с удалением данных базы). Переменные при необходимости задаются в `docker/.env`
(шаблон — `.env.example`).

## Продакшн (сервер с публичным HTTPS)

Конфигурация `docker-compose.prod.yml` поднимает PostgreSQL, сервер и обратный
прокси Caddy с автоматическим сертификатом Let's Encrypt — сервер становится
доступен по HTTPS из любой сети.

```bash
cp .env.prod.example .env     # заполнить SITE_ADDRESS, DB_PASSWORD, JWT_SECRET
docker compose -f docker-compose.prod.yml up -d --build
```

`SITE_ADDRESS` — доменное имя, публично указывающее на IP сервера (Caddy получит для
него сертификат). При отсутствии собственного домена подойдёт `<IP-сервера>.nip.io`.
Наружу открыты только порты 80 и 443 (Caddy); сервер и база доступны лишь внутри сети
Docker.
