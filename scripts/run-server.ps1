# Запуск всего серверного стека в Docker: PostgreSQL, сервер и туннель ngrok.
# Перед первым запуском заполните docker/.env (см. docker/.env.example).
$ErrorActionPreference = 'Stop'
$root = Split-Path $PSScriptRoot -Parent

# Docker Desktop (запуск при необходимости)
docker info *> $null
if (-not $?) {
    Write-Host 'Starting Docker Desktop...' -ForegroundColor Yellow
    Start-Process "$env:ProgramFiles\Docker\Docker\Docker Desktop.exe"
    foreach ($i in 1..40) { Start-Sleep 5; docker info *> $null; if ($?) { break } }
}

Set-Location "$root\docker"
if (-not (Test-Path "$root\docker\.env")) {
    Write-Host 'Файл docker/.env не найден. Создайте его из docker/.env.example (NGROK_AUTHTOKEN, NGROK_DOMAIN).' -ForegroundColor Yellow
}

docker compose up --build
