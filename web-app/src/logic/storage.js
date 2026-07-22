export const saveToCustomDeck = (deckId, title, newItem) => {
  let customDecks = [];
  try {
    const saved = localStorage.getItem('repite_custom_decks');
    if (saved) {
      customDecks = JSON.parse(saved);
    }
  } catch (e) {
    console.error('Error loading custom decks', e);
  }

  let deck = customDecks.find(d => d.id === deckId);
  
  if (deck) {
    // Check for duplicates
    if (!deck.sentences.some(s => s.es === newItem.es)) {
      deck.sentences.push(newItem);
    }
  } else {
    // Create new deck
    deck = {
      id: deckId,
      title: { "ar-SA": title, "en-US": title },
      sentences: [newItem]
    };
    customDecks.push(deck);
  }

  localStorage.setItem('repite_custom_decks', JSON.stringify(customDecks));
  window.dispatchEvent(new Event('repite_decks_updated'));
};

export const updateItemInCustomDeck = (deckId, itemIndex, newAr) => {
  try {
    const saved = localStorage.getItem('repite_custom_decks');
    if (saved) {
      let customDecks = JSON.parse(saved);
      let deck = customDecks.find(d => d.id === deckId);
      if (deck && deck.sentences && deck.sentences[itemIndex]) {
        deck.sentences[itemIndex].ar = newAr;
        localStorage.setItem('repite_custom_decks', JSON.stringify(customDecks));
        window.dispatchEvent(new Event('repite_decks_updated'));
      }
    }
  } catch (e) {
    console.error('Error updating custom deck', e);
  }
};

export const deleteItemFromCustomDeck = (deckId, itemIndex) => {
  try {
    const saved = localStorage.getItem('repite_custom_decks');
    if (saved) {
      let customDecks = JSON.parse(saved);
      let deck = customDecks.find(d => d.id === deckId);
      if (deck && deck.sentences) {
        deck.sentences.splice(itemIndex, 1);
        localStorage.setItem('repite_custom_decks', JSON.stringify(customDecks));
        window.dispatchEvent(new Event('repite_decks_updated'));
      }
    }
  } catch (e) {
    console.error('Error deleting from custom deck', e);
  }
};

export const saveCustomStory = (title, sentences) => {
  let customStories = [];
  try {
    const saved = localStorage.getItem('repite_custom_stories');
    if (saved) {
      customStories = JSON.parse(saved);
    }
  } catch (e) {
    console.error('Error loading custom stories', e);
  }

  const newStory = {
    id: `custom_story_${Date.now()}`,
    title: { "ar-SA": title, "en-US": title },
    sentences: sentences
  };
  
  customStories.push(newStory);
  localStorage.setItem('repite_custom_stories', JSON.stringify(customStories));
  window.dispatchEvent(new Event('repite_stories_updated'));
};

export const getCustomStories = () => {
  try {
    const saved = localStorage.getItem('repite_custom_stories');
    if (saved) {
      return JSON.parse(saved);
    }
  } catch (e) {
    console.error('Error loading custom stories', e);
  }
  return [];
};
