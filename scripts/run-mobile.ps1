# Запуск мобильного клиента (Expo Metro).
# Адрес сервера берётся из mobile/.env (EXPO_PUBLIC_API_URL).
$ErrorActionPreference = 'Stop'
$root = Split-Path $PSScriptRoot -Parent
$env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk"
$adb = "$env:ANDROID_HOME\platform-tools\adb.exe"

Set-Location "$root\mobile"
if (-not (Test-Path "$root\mobile\node_modules")) {
    npm install
}

# Проброс портов для подключённого устройства/эмулятора (безвреден при его отсутствии).
if (Test-Path $adb) {
    & $adb reverse tcp:8081 tcp:8081 2>$null | Out-Null
    & $adb reverse tcp:8137 tcp:8137 2>$null | Out-Null
}

npx expo start
