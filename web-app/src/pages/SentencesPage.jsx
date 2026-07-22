import React from 'react';
import { DeckListPage } from './DeckListPage';
import libraryData from '../library.json';

const validDecks = libraryData.filter(d => d.sentences && d.sentences.length > 0);

const sentencesPredicate = s => {
  if (s.contentType === 'passage') return false;
  const arText = s.ar || s.translation || s.text || '';
  return arText.trim().split(/\s+/).length > 2;
};

// Filter each deck to only contain sentences (3+ words)
const sentencesDecks = validDecks.map(deck => {
  const filteredSentences = deck.sentences.filter(sentencesPredicate);
  return { ...deck, sentences: filteredSentences };
}).filter(deck => deck.sentences.length > 0).slice(0, 15);

export const SentencesPage = () => {
  return <DeckListPage key="sentences" pageTitle="مجموعات الجمل" decks={sentencesDecks} filterPredicate={sentencesPredicate} />;
};
