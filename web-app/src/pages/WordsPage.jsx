import React from 'react';
import { DeckListPage } from './DeckListPage';
import libraryData from '../library.json';

const validDecks = libraryData.filter(d => d.sentences && d.sentences.length > 0);

const wordsPredicate = s => {
  if (s.contentType === 'passage') return false;
  const esText = s.es || s.translation || '';
  return esText.trim().split(/\s+/).length <= 2;
};

// Filter each deck to only contain words (1-2 words in Spanish)
const wordsDecks = validDecks.map(deck => {
  const filteredSentences = deck.sentences.filter(wordsPredicate);
  return { ...deck, sentences: filteredSentences };
}).filter(deck => deck.sentences.length > 0).slice(0, 15);

export const WordsPage = () => {
  return <DeckListPage key="words" pageTitle="مجموعات الكلمات" decks={wordsDecks} filterPredicate={wordsPredicate} />;
};
