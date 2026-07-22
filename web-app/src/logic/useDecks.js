import { useState, useEffect } from 'react';

export function useDecks(initialDecks) {
  const [customDecks, setCustomDecks] = useState(() => {
    try {
      const saved = localStorage.getItem('repite_custom_decks');
      return saved ? JSON.parse(saved) : [];
    } catch (e) {
      return [];
    }
  });

  const [favorites, setFavorites] = useState(() => {
    try {
      const saved = localStorage.getItem('repite_favorites');
      return saved ? JSON.parse(saved) : [];
    } catch (e) {
      return [];
    }
  });

  useEffect(() => {
    localStorage.setItem('repite_custom_decks', JSON.stringify(customDecks));
  }, [customDecks]);

  useEffect(() => {
    const handleStorageUpdate = () => {
      try {
        const saved = localStorage.getItem('repite_custom_decks');
        if (saved) setCustomDecks(JSON.parse(saved));
      } catch (e) {}
    };
    window.addEventListener('repite_decks_updated', handleStorageUpdate);
    return () => window.removeEventListener('repite_decks_updated', handleStorageUpdate);
  }, []);

  useEffect(() => {
    localStorage.setItem('repite_favorites', JSON.stringify(favorites));
  }, [favorites]);

  const allDecks = [...customDecks, ...initialDecks];

  const addCustomDeck = (deck) => {
    setCustomDecks([{ ...deck, id: 'custom_' + Date.now().toString() }, ...customDecks]);
  };
  
  const updateCustomDeck = (updatedDeck) => {
    setCustomDecks(customDecks.map(d => d.id === updatedDeck.id ? updatedDeck : d));
  };

  const deleteCustomDeck = (deckId) => {
    setCustomDecks(customDecks.filter(d => d.id !== deckId));
    setFavorites(favorites.filter(id => id !== deckId));
  };

  const toggleFavorite = (deckId) => {
    if (favorites.includes(deckId)) {
      setFavorites(favorites.filter(id => id !== deckId));
    } else {
      setFavorites([...favorites, deckId]);
    }
  };

  const isFavorite = (deckId) => favorites.includes(deckId);

  return {
    allDecks,
    customDecks,
    favorites,
    addCustomDeck,
    updateCustomDeck,
    deleteCustomDeck,
    toggleFavorite,
    isFavorite
  };
}
