// Levenshtein distance algorithm for scoring pronunciation
export function calculateSimilarity(str1, str2) {
  const normalize = (s) => {
    return s.toLowerCase()
            .replace(/[.,!?¡¿]/g, '')
            .normalize("NFD").replace(/[\u0300-\u036f]/g, "") // Remove accents
            .trim();
  };

  const s1 = normalize(str1);
  const s2 = normalize(str2);

  if (s1 === s2) return 100;
  if (s1.length === 0) return 0;
  if (s2.length === 0) return 0;

  const matrix = Array(s2.length + 1).fill(null).map(() => Array(s1.length + 1).fill(null));

  for (let i = 0; i <= s1.length; i += 1) {
    matrix[0][i] = i;
  }

  for (let j = 0; j <= s2.length; j += 1) {
    matrix[j][0] = j;
  }

  for (let j = 1; j <= s2.length; j += 1) {
    for (let i = 1; i <= s1.length; i += 1) {
      const indicator = s1[i - 1] === s2[j - 1] ? 0 : 1;
      matrix[j][i] = Math.min(
        matrix[j][i - 1] + 1, // insertion
        matrix[j - 1][i] + 1, // deletion
        matrix[j - 1][i - 1] + indicator // substitution
      );
    }
  }

  const distance = matrix[s2.length][s1.length];
  const maxLength = Math.max(s1.length, s2.length);
  const similarity = ((maxLength - distance) / maxLength) * 100;
  
  return Math.max(0, Math.round(similarity));
}
