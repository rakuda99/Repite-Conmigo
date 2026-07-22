import React, { useState, useRef } from 'react';
import { RefreshCw, MessageSquare, Menu, Search, Star, BookOpen } from 'lucide-react';
import { DeckCard } from '../components/DeckCard';
import { DeckOptionsModal } from '../components/DeckOptionsModal';
import { EditDeckModal } from '../components/EditDeckModal';
import { CommunityDecksModal } from '../components/CommunityDecksModal';
import { SmartImportModal } from '../components/SmartImportModal';
import { LearningPage } from './LearningPage';
import { useDecks } from '../logic/useDecks';
import { AnimatePresence } from 'framer-motion';

export const DeckListPage = ({ pageTitle, decks, onDeckClickOverride, headerAction, filterPredicate, hideCustomDecks, isStoriesPage }) => {
  const [search, setSearch] = useState('');
  const [selectedDeck, setSelectedDeck] = useState(null);
  const [learningDeck, setLearningDeck] = useState(null);
  const [editingDeck, setEditingDeck] = useState(null);
  const [showCommunityModal, setShowCommunityModal] = useState(false);
  const [showSmartImportModal, setShowSmartImportModal] = useState(false);
  const fileInputRef = useRef(null);

  const handleDeckClick = (deck) => {
    if (onDeckClickOverride) {
      onDeckClickOverride(deck);
    } else {
      setSelectedDeck(deck);
    }
  };

  const { allDecks: rawAllDecks, addCustomDeck, updateCustomDeck, deleteCustomDeck, toggleFavorite, isFavorite } = useDecks(decks);

  const allDecks = hideCustomDecks ? decks : rawAllDecks;

  const filteredDecksSearch = allDecks.filter(d => {
    const titleAr = d.title && d.title['ar-SA'] ? d.title['ar-SA'] : '';
    const titleEn = d.title && d.title['en-US'] ? d.title['en-US'] : '';
    return titleAr.includes(search) || titleEn.includes(search);
  });

  const displayDecks = filteredDecksSearch.map(deck => {
    if (filterPredicate && deck.sentences) {
      return { ...deck, sentences: deck.sentences.filter(filterPredicate) };
    }
    return deck;
  }).filter(deck => !filterPredicate || (deck.sentences && deck.sentences.length > 0));

  const handleImport = (e) => {
    const file = e.target.files[0];
    if (!file) return;
    const reader = new FileReader();
    reader.onload = (event) => {
      try {
        const data = JSON.parse(event.target.result);
        if (data && data.title && data.sentences) {
          addCustomDeck(data);
          alert("تم استيراد المجموعة بنجاح!");
        } else if (Array.isArray(data)) {
          data.forEach(d => {
            if (d.title && d.sentences) addCustomDeck(d);
          });
          alert("تم استيراد المجموعات بنجاح!");
        } else {
          alert("ملف غير صالح.");
        }
      } catch (err) {
        alert("خطأ في قراءة الملف.");
      }
    };
    reader.readAsText(file);
    e.target.value = '';
  };

  const handleExport = (deck) => {
    try {
      const jsonString = JSON.stringify(deck, null, 2);
      const blob = new Blob([jsonString], { type: "application/json" });
      const url = URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = `${deck.title['en-US'] || deck.title['ar-SA'] || 'deck'}_repite.json`;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      URL.revokeObjectURL(url);
    } catch (e) {
      console.error(e);
      alert("خطأ في تصدير الملف.");
    }
  };

  const handleCreate = () => {
    const name = prompt("أدخل اسم المجموعة الجديدة:");
    if (name) {
      addCustomDeck({
        title: { "ar-SA": name, "en-US": name },
        sentences: []
      });
    }
  };

  if (learningDeck) {
    return (
      <div style={{ paddingBottom: '100px' }}>
        <LearningPage 
          deck={learningDeck} 
          onBack={() => setLearningDeck(null)} 
        />
      </div>
    );
  }

  return (
    <div style={{ paddingBottom: '100px' }}>
      {/* Top Header */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '16px', backgroundColor: '#131f24', color: 'white', position: 'sticky', top: 0, zIndex: 10 }}>
        <div style={{ display: 'flex', gap: '16px', alignItems: 'center' }}>
          <Menu size={24} />
          <RefreshCw size={24} color="#32b5e3" />
        </div>
        <div style={{ display: 'flex', gap: '16px', alignItems: 'center' }}>
          <MessageSquare size={24} />
          <h2 style={{ margin: 0, fontSize: '1.25rem', paddingLeft: '8px' }}>{pageTitle}</h2>
        </div>
      </div>

      {/* Top Actions */}
      <div className="container">
        {headerAction && (
          <div className="top-actions" dir="rtl" style={{ marginBottom: '16px' }}>
            {headerAction}
          </div>
        )}
        
        {!isStoriesPage && (
          <div className="top-actions" dir="rtl">
            <button 
              className="action-btn action-smart-review"
              onClick={() => {
                const reviewSentences = [];
                allDecks.forEach(d => {
                  if (isFavorite(d.id) || String(d.id).startsWith('custom_')) {
                    if (d.sentences) reviewSentences.push(...d.sentences);
                  }
                });
                if (reviewSentences.length === 0) {
                  alert("لا توجد مجموعات مفضلة أو مخصصة للمراجعة.");
                  return;
                }
                setLearningDeck({
                  title: { 'ar-SA': 'مراجعة ذكية', 'en-US': 'Smart Review' },
                  sentences: reviewSentences.sort(() => Math.random() - 0.5).slice(0, 20)
                });
              }}
            >
              مراجعة ذكية
            </button>
          </div>
        )}

        {!isStoriesPage && !selectedDeck && !learningDeck && !editingDeck && (
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px', marginBottom: '24px' }}>
          <button 
            className="action-btn action-selected"
            style={{ width: '100%' }}
            onClick={() => {
              const savedWordsDeck = allDecks.find(d => d.id === 'custom_saved_words');
              if (!savedWordsDeck || !savedWordsDeck.sentences || savedWordsDeck.sentences.length === 0) {
                alert('لا يوجد كلمات مختارة حتى الآن. قم بالضغط مرتين (Double Click) على الكلمات في القصص لحفظها هنا.');
                return;
              }
              setLearningDeck(savedWordsDeck);
            }}
          >
            <BookOpen size={24} />
            <span>الكلمات المختارة</span>
          </button>
          
          <button 
            className="action-btn"
            style={{ 
              width: '100%', 
              backgroundColor: 'rgba(28, 176, 246, 0.1)', 
              color: '#1cb0f6',
              border: '2px solid rgba(28, 176, 246, 0.2)',
              borderRadius: '16px',
              padding: '16px',
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'center',
              gap: '8px',
              cursor: 'pointer',
              fontWeight: 'bold',
              transition: 'all 0.2s ease'
            }}
            onClick={() => {
              const savedSentencesDeck = allDecks.find(d => d.id === 'custom_saved_sentences');
              if (!savedSentencesDeck || !savedSentencesDeck.sentences || savedSentencesDeck.sentences.length === 0) {
                alert('لا يوجد جمل مختارة حتى الآن. قم بتحديد أي جملة بالماوس في صفحة القصص لحفظها هنا.');
                return;
              }
              setLearningDeck(savedSentencesDeck);
            }}
          >
            <BookOpen size={24} />
            <span>الجمل المختارة</span>
          </button>
        </div>
      )}
        
        <div className="top-actions">
          <div className="search-bar" style={{ width: '100%', marginBottom: 0 }}>
            <Search size={20} color="#9ca3af" />
            <input 
              type="text" 
              placeholder="بحث عن مجموعة..." 
              className="search-input"
              value={search}
              onChange={e => setSearch(e.target.value)}
              dir="rtl"
            />
          </div>
        </div>

        <div style={{ marginTop: '24px' }} dir="rtl">
          {displayDecks.map((deck, idx) => (
            <div key={deck.id || idx} style={{ position: 'relative' }}>
              {isFavorite(deck.id) && (
                <div style={{ position: 'absolute', top: '-10px', right: '-10px', zIndex: 5, backgroundColor: '#f59e0b', borderRadius: '50%', padding: '4px' }}>
                  <Star size={16} color="white" fill="white" />
                </div>
              )}
              <DeckCard 
                title={deck.title['ar-SA'] || 'Unnamed Deck'}
                totalCards={deck.sentences?.length || 0}
                newCount={deck.sentences?.length || 0}
                hardCount={0}
                reviewCount={0}
                onClick={() => handleDeckClick(deck)}
              />
            </div>
          ))}
        </div>

        {!isStoriesPage && (
          <div className="bottom-floating-actions" dir="rtl" style={{ display: 'flex', gap: '8px', overflowX: 'auto', padding: '12px' }}>
            <input type="file" ref={fileInputRef} style={{ display: 'none' }} accept=".json" onChange={handleImport} />
            <button className="bottom-btn btn-import" onClick={() => setShowSmartImportModal(true)} style={{ whiteSpace: 'nowrap', backgroundColor: '#58cc02', color: 'white', border: 'none', borderRadius: '12px', padding: '10px 16px', fontWeight: 'bold' }}>استيراد ذكي (Quizlet)</button>
            <button className="bottom-btn btn-import" onClick={() => fileInputRef.current.click()} style={{ whiteSpace: 'nowrap' }}>استيراد ملف</button>
            <button className="bottom-btn btn-create" onClick={handleCreate} style={{ whiteSpace: 'nowrap' }}>إنشاء مجموعة</button>
            <button className="bottom-btn btn-browse" onClick={() => setShowCommunityModal(true)} style={{ whiteSpace: 'nowrap' }}>تصفح المشترك</button>
          </div>
        )}
      </div>

      <DeckOptionsModal 
        isOpen={!!selectedDeck}
        onClose={() => setSelectedDeck(null)}
        deckTitle={selectedDeck?.title ? selectedDeck.title['ar-SA'] : ''}
        isFav={selectedDeck ? isFavorite(selectedDeck.id) : false}
        onToggleFavorite={() => {
          if (selectedDeck) toggleFavorite(selectedDeck.id);
        }}
        onEditDeck={() => {
          const originalDeck = allDecks.find(d => d.id === selectedDeck.id) || selectedDeck;
          setEditingDeck(originalDeck);
          setSelectedDeck(null);
        }}
        onStudyFull={() => {
          setLearningDeck(selectedDeck);
          setSelectedDeck(null);
        }}
        onStudyHard={() => {
          const sentences = selectedDeck?.sentences || [];
          const hardSentences = sentences.slice(0, Math.max(1, Math.floor(sentences.length / 2)));
          setLearningDeck({ ...selectedDeck, sentences: hardSentences });
          setSelectedDeck(null);
        }}
        onRandomTest={() => {
          const shuffledDeck = { ...selectedDeck, sentences: [...(selectedDeck?.sentences || [])].sort(() => Math.random() - 0.5) };
          setLearningDeck(shuffledDeck);
          setSelectedDeck(null);
        }}
        onExportDeck={() => {
          if (selectedDeck) {
            handleExport(selectedDeck);
            setSelectedDeck(null);
          }
        }}
        onPublishDeck={async () => {
          if (selectedDeck) {
            try {
              const { uploadDeckToCloud } = await import('../logic/cloud');
              await uploadDeckToCloud(selectedDeck);
              alert('تم نشر مجموعتك بنجاح في المتجر السحابي! 🌍');
            } catch (err) {
              console.error(err);
              alert('حدث خطأ أثناء النشر. تأكد من إعداد قاعدة البيانات بشكل صحيح.');
            }
          }
        }}
      />

      <CommunityDecksModal 
        isOpen={showCommunityModal}
        onClose={() => setShowCommunityModal(false)}
        onDownload={(deck) => {
          addCustomDeck(deck);
        }}
      />

      <SmartImportModal 
        isOpen={showSmartImportModal}
        onClose={() => setShowSmartImportModal(false)}
        onImport={(newDeck) => {
          addCustomDeck(newDeck);
          setShowSmartImportModal(false);
        }}
      />

      <AnimatePresence>
        {editingDeck && (
          <EditDeckModal 
            deck={editingDeck}
            onClose={() => setEditingDeck(null)}
            onSave={(updatedDeck) => {
              if (updatedDeck.id && String(updatedDeck.id).startsWith('custom_')) {
                updateCustomDeck(updatedDeck);
              } else {
                // If editing a built-in deck, we save it as a new custom deck
                addCustomDeck(updatedDeck);
              }
              setEditingDeck(null);
            }}
            onDelete={(id) => {
              deleteCustomDeck(id);
              setEditingDeck(null);
            }}
          />
        )}
      </AnimatePresence>
    </div>
  );
};
