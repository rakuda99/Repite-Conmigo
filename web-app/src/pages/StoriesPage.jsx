import React, { useState, useEffect } from 'react';
import { DeckListPage } from './DeckListPage';
import { StoryReadingPage } from './StoryReadingPage';
import { getCustomStories, saveCustomStory } from '../logic/storage';
import { Plus, X } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';

import libraryData from '../library.json';

const storiesPredicate = s => {
  if (s.contentType) return s.contentType === 'passage';
  const esText = s.es || s.text || '';
  return esText.trim().split(/\s+/).length > 20;
};

const validDecks = libraryData.filter(d => d.sentences && d.sentences.length > 0);

// Filter each deck to only contain sentences that are passages (stories)
const storiesDecks = validDecks.map(deck => {
  const filteredSentences = deck.sentences.filter(storiesPredicate);
  return { ...deck, sentences: filteredSentences };
}).filter(deck => deck.sentences.length > 0);

export const StoriesPage = () => {
  const [readingStory, setReadingStory] = useState(null);
  const [customStories, setCustomStories] = useState([]);
  const [showAddModal, setShowAddModal] = useState(false);
  const [newTitle, setNewTitle] = useState('');
  const [newText, setNewText] = useState('');

  const loadStories = () => {
    setCustomStories(getCustomStories());
  };

  useEffect(() => {
    loadStories();
    window.addEventListener('repite_stories_updated', loadStories);
    return () => window.removeEventListener('repite_stories_updated', loadStories);
  }, []);

  const handleSaveStory = () => {
    if (!newTitle.trim() || !newText.trim()) {
      alert("الرجاء إدخال عنوان ونص القصة.");
      return;
    }

    // Split text into sentences based on punctuation, but keep the punctuation.
    // We can use a simple regex for Spanish/English: split by . ! ?
    const splitRegex = /([^.!?¡¿]+[.!?¡¿]*)/g;
    const matches = newText.match(splitRegex);
    
    if (!matches) {
      alert("لم يتم العثور على جمل في النص.");
      return;
    }

    const sentences = matches
      .map(s => s.trim())
      .filter(s => s.length > 0)
      .map(s => ({
        es: s,
        ar: "ترجمة الجملة (انقر لتعديلها)" // Placeholder translation
      }));

    if (sentences.length === 0) {
      alert("الرجاء إدخال نص صالح.");
      return;
    }

    saveCustomStory(newTitle, sentences);
    setNewTitle('');
    setNewText('');
    setShowAddModal(false);
  };

  const allStories = [...customStories, ...storiesDecks];

  if (readingStory) {
    return (
      <div style={{ paddingBottom: '100px' }}>
        <StoryReadingPage 
          story={readingStory} 
          onBack={() => setReadingStory(null)} 
        />
      </div>
    );
  }

  return (
    <>
      <DeckListPage 
        key="stories"
        pageTitle="القصص" 
        decks={allStories} 
        filterPredicate={storiesPredicate}
        onDeckClickOverride={setReadingStory} 
        isStoriesPage={true}
        headerAction={
          <button 
            className="action-btn"
            style={{ 
              width: '100%', 
              backgroundColor: '#58cc02', 
              color: 'white',
              border: 'none',
              borderRadius: '16px',
              padding: '16px',
              display: 'flex',
              justifyContent: 'center',
              alignItems: 'center',
              gap: '8px',
              cursor: 'pointer',
              fontWeight: 'bold',
              boxShadow: '0 4px 0 #58a700',
              fontSize: '1.1rem'
            }}
            onClick={() => setShowAddModal(true)}
          >
            <Plus size={24} />
            إضافة قصة جديدة (لصق)
          </button>
        }
      />

      <AnimatePresence>
        {showAddModal && (
          <div style={{ position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, backgroundColor: 'rgba(0,0,0,0.7)', zIndex: 9999, display: 'flex', justifyContent: 'center', alignItems: 'center', padding: '16px' }}>
            <motion.div 
              initial={{ opacity: 0, scale: 0.9 }}
              animate={{ opacity: 1, scale: 1 }}
              exit={{ opacity: 0, scale: 0.9 }}
              style={{ backgroundColor: '#131f24', padding: '24px', borderRadius: '24px', width: '100%', maxWidth: '500px', border: '2px solid #2a3f4a', display: 'flex', flexDirection: 'column', gap: '16px', direction: 'rtl' }}
            >
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <h2 style={{ margin: 0, color: 'white' }}>إضافة قصة جديدة</h2>
                <button onClick={() => setShowAddModal(false)} style={{ background: 'none', border: 'none', color: '#9ca3af', cursor: 'pointer' }}>
                  <X size={24} />
                </button>
              </div>

              <div>
                <label style={{ color: '#9ca3af', display: 'block', marginBottom: '8px' }}>عنوان القصة:</label>
                <input 
                  type="text" 
                  value={newTitle}
                  onChange={(e) => setNewTitle(e.target.value)}
                  placeholder="مثال: رحلتي إلى إسبانيا"
                  style={{ width: '100%', padding: '12px', borderRadius: '12px', border: '2px solid #2a3f4a', backgroundColor: 'rgba(0,0,0,0.2)', color: 'white', fontSize: '1.1rem' }}
                />
              </div>

              <div>
                <label style={{ color: '#9ca3af', display: 'block', marginBottom: '8px' }}>النص الإسباني (الصق النص هنا):</label>
                <textarea 
                  value={newText}
                  onChange={(e) => setNewText(e.target.value)}
                  placeholder="Hola! Me llamo..."
                  rows={8}
                  dir="ltr"
                  style={{ width: '100%', padding: '12px', borderRadius: '12px', border: '2px solid #2a3f4a', backgroundColor: 'rgba(0,0,0,0.2)', color: 'white', fontSize: '1.1rem', resize: 'vertical' }}
                />
              </div>

              <button 
                onClick={handleSaveStory}
                style={{ backgroundColor: '#1cb0f6', color: 'white', border: 'none', borderRadius: '16px', padding: '16px', fontSize: '1.2rem', fontWeight: 'bold', cursor: 'pointer', boxShadow: '0 4px 0 #1899d6', marginTop: '8px' }}
              >
                حفظ القصة
              </button>
            </motion.div>
          </div>
        )}
      </AnimatePresence>
    </>
  );
};
