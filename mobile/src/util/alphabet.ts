// Токенизатор карачаевского алфавита на клиенте (зеркало серверного).
// Диграфы гъ, къ, нъ, нг, дж — одна буква (одна плитка).

const DIGRAPHS = new Set(['гъ', 'къ', 'нъ', 'нг', 'дж']);

export function tokenize(word: string): string[] {
  const w = word.trim().toLowerCase();
  const letters: string[] = [];
  let i = 0;
  while (i < w.length) {
    const pair = w.substring(i, i + 2);
    if (DIGRAPHS.has(pair)) {
      letters.push(pair);
      i += 2;
    } else {
      letters.push(w[i]);
      i += 1;
    }
  }
  return letters;
}

export function letterCount(word: string): number {
  return tokenize(word).length;
}
