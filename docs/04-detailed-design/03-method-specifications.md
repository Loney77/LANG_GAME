# Спецификация ключевых методов

Сигнатуры и контракты центральных методов ядра.

## AlphabetTokenizer (Foundation)

```java
/** Разбивает слово на буквы карач. алфавита (диграфы гъ/къ/нъ/нг/дж = 1 буква). */
public static List<String> tokenize(String word)
```
- **Вход:** слово (любой регистр).
- **Выход:** список «букв»; диграфы склеены.
- **Инвариант:** `tokenize("азгъан") == [а, з, гъ, а, н]` (size 5).
- **Граничные случаи:** пустая строка → пустой список; `null` → исключение.

```java
public static int letterCount(String word)   // = tokenize(word).size()
```

## SozdlService.evaluateGuess (Mediator)

```java
GuessResult evaluateGuess(Long puzzleId, String guess, Long userId)
```
- **Предусловия:** задание существует и активно; `userId` аутентифицирован.
- **Логика:**
  1. `tokenize(guess)`; проверить длину == длине целевого слова;
  2. если слова нет в словаре → `WordNotFoundException` (HTTP 422);
  3. вычислить статусы плиток (`computeTiles`);
  4. `attempts++`; при `win` или `attempts == MAX_ATTEMPTS` - зафиксировать сессию.
- **Выход:** `GuessResult{ tiles, attempt, finished, win }`.
- **Постусловия:** при завершении создана `GameSession` со `score` от `ScoringService`.

### computeTiles (приватный) - алгоритм подсветки

```java
TileStatus[] computeTiles(List<String> guess, List<String> target)
```
Двухпроходный алгоритм (как в оригинальном Сёздл):
1. **Проход 1:** буквы на своих местах → `CORRECT`; уменьшить счётчик буквы в target.
2. **Проход 2:** для оставшихся - если буква ещё есть в остатке target → `PRESENT`
   (с декрементом), иначе `ABSENT`.

Это корректно обрабатывает повторяющиеся буквы (не помечает лишние дубликаты жёлтым).

## ScoringService.score (Mediator)

```java
int score(int attempts, long durationMs, boolean win)
```
- **Контракт:** проигрыш → 0; победа → базовые очки минус штраф за попытки и время.
- Формула (MVP): `win ? max(BASE - (attempts-1)*ATTEMPT_PENALTY - timePenalty, MIN_WIN) : 0`.
- **Инвариант:** результат ≥ 0; меньше попыток и быстрее ⇒ больше очков.

## GameSession.calculateScore (Entity)

```java
void calculateScore(ScoringService scoring)   // устанавливает this.score
```
- Доменный метод сущности (не анемичная модель): делегирует расчёт сервису и
  фиксирует результат в поле `score`.
