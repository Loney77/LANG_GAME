# Start backend: Docker (PostgreSQL) + Spring Boot server.
# Usage:  powershell -ExecutionPolicy Bypass -File scripts\run-server.ps1
$ErrorActionPreference = 'Stop'
$root = Split-Path $PSScriptRoot -Parent

# 1) Docker engine (start if not running)
docker info *> $null
if (-not $?) {
    Write-Host 'Starting Docker Desktop...' -ForegroundColor Yellow
    Start-Process "$env:ProgramFiles\Docker\Docker\Docker Desktop.exe"
    foreach ($i in 1..40) { Start-Sleep 5; docker info *> $null; if ($?) { break } }
}

# 2) Database
docker compose -f "$root\docker\docker-compose.yml" up -d
foreach ($i in 1..20) {
    Start-Sleep 2
    if ((docker inspect --format '{{.State.Health.Status}}' langgame-db 2>$null) -eq 'healthy') { break }
}
Write-Host 'PostgreSQL ready (localhost:5433)' -ForegroundColor Green

# 3) Build jar if missing
$jar = "$root\server\build\libs\lang-game-0.1.0.jar"
if (-not (Test-Path $jar)) {
    Write-Host 'Building server (first run)...' -ForegroundColor Yellow
    & "$root\server\gradlew.bat" -p "$root\server" bootJar
}

# 4) Run server (Ctrl+C to stop)
$env:DB_URL = 'jdbc:postgresql://localhost:5433/langgame'
$env:DB_USER = 'langgame'; $env:DB_PASSWORD = 'langgame'
$env:JWT_SECRET = 'change-me-dev-secret-change-me-dev-secret-32b'
$df = "$root\data\sozluk.words.json"
Write-Host 'Server: http://localhost:8137/swagger-ui.html   (Ctrl+C to stop)' -ForegroundColor Green
java "-Dapp.seed.sozluk-file=$df" -jar $jar
