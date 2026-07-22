import React, { useState, useRef, useEffect } from 'react';
import { ArrowLeft, Volume2, ArrowRight, Square, Settings2, Save, BookmarkPlus } from 'lucide-react';
import { playTTS, stopTTS } from '../logic/speech';
import { saveToCustomDeck } from '../logic/storage';

export const StoryReadingPage = ({ story, onBack }) => {
  const [isPlaying, setIsPlaying] = useState(false);
  const [activeIndex, setActiveIndex] = useState(null);
  const [playbackRate, setPlaybackRate] = useState(0.9);
  const [toastMessage, setToastMessage] = useState(null);
  const [activeItem, setActiveItem] = useState(null); // { word: string, sentence: string }
  const isPlayingRef = useRef(false);

  const showToast = (msg) => {
    setToastMessage(msg);
    setTimeout(() => setToastMessage(null), 2500);
  };

  useEffect(() => {
    return () => {
      stopTTS();
      isPlayingRef.current = false;
    };
  }, []);

  const handleTogglePlay = async () => {
    if (isPlaying) {
      isPlayingRef.current = false;
      setIsPlaying(false);
      setActiveIndex(null);
      stopTTS();
      return;
    }

    isPlayingRef.current = true;
    setIsPlaying(true);
    setActiveItem(null); // hide action bar while playing

    for (let i = 0; i < story.sentences.length; i++) {
      if (!isPlayingRef.current) break;
      setActiveIndex(i);
      
      const esText = story.sentences[i].es || story.sentences[i].translation;
      
      await new Promise(resolve => {
        playTTS(esText, 'es-ES', () => resolve(), playbackRate);
        setTimeout(resolve, (esText.length * 90) / playbackRate + 500);
      });
    }

    if (isPlayingRef.current) {
      setIsPlaying(false);
      setActiveIndex(null);
      isPlayingRef.current = false;
    }
  };

  const handleWordClick = (word, sentenceIndex) => {
    if (isPlaying) {
      isPlayingRef.current = false;
      setIsPlaying(false);
      setActiveIndex(null);
    }
    const cleanWord = word.replace(/[.,!?¡¿]/g, '');
    const sentenceText = story.sentences[sentenceIndex].es || story.sentences[sentenceIndex].translation;
    
    // Set active item to show in the action bar
    setActiveItem({ type: 'click', word: cleanWord, sentence: sentenceText, index: sentenceIndex });
    
    // Play the word
    playTTS(cleanWord, 'es-ES', null, playbackRate);
  };

  const handleSelection = () => {
    setTimeout(() => {
      const selection = window.getSelection();
      const text = selection.toString().trim();
      
      if (text.length > 0) {
        // Stop playing if it's playing
        if (isPlaying) {
          isPlayingRef.current = false;
          setIsPlaying(false);
          setActiveIndex(null);
        }
        
        // Show the action bar for the custom selection
        setActiveItem({ type: 'selection', text: text });
      }
    }, 10);
  };

  const saveWord = () => {
    if (activeItem && activeItem.type === 'click') {
      saveToCustomDeck('custom_saved_words', 'الكلمات المختارة', { es: activeItem.word, ar: 'ترجمة الكلمة (انقر لتعديلها)' });
      showToast(`تم حفظ الكلمة: "${activeItem.word}" 🔖`);
    }
  };

  const saveSentence = () => {
    if (activeItem && activeItem.type === 'click') {
      saveToCustomDeck('custom_saved_sentences', 'الجمل المختارة', { es: activeItem.sentence, ar: 'ترجمة الجملة (انقر لتعديلها)' });
      showToast(`تم حفظ الجملة بنجاح 🔖`);
    }
  };

  const saveCustomSelection = () => {
    if (activeItem && activeItem.type === 'selection') {
      // Decide if it's a word or sentence based on spaces
      if (activeItem.text.includes(' ') && activeItem.text.length > 10) {
        saveToCustomDeck('custom_saved_sentences', 'الجمل المختارة', { es: activeItem.text, ar: 'ترجمة الجملة (انقر لتعديلها)' });
        showToast(`تم حفظ الجملة المحددة 🔖`);
      } else {
        const cleanWord = activeItem.text.replace(/[.,!?¡¿]/g, '');
        saveToCustomDeck('custom_saved_words', 'الكلمات المختارة', { es: cleanWord, ar: 'ترجمة الكلمة (انقر لتعديلها)' });
        showToast(`تم حفظ الكلمة: "${cleanWord}" 🔖`);
      }
      window.getSelection().removeAllRanges();
      setActiveItem(null); // hide bar after saving
    }
  };

  const highlightStyle = {
    backgroundColor: 'rgba(88, 204, 2, 0.3)',
    borderRadius: '4px',
    padding: '2px 4px',
    transition: 'background-color 0.3s ease'
  };

  const wordStyle = {
    cursor: 'pointer',
    borderRadius: '4px',
    transition: 'background-color 0.2s ease',
    padding: '0 2px'
  };

  return (
    <div style={{ minHeight: '100vh', backgroundColor: 'var(--background)', color: 'var(--text-main)', paddingBottom: activeItem ? '140px' : '0' }}>
      {/* Header */}
      <div dir="rtl" style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '16px 24px', backgroundColor: '#131f24', position: 'sticky', top: 0, zIndex: 10 }}>
        <button onClick={onBack} style={{ background: 'none', border: 'none', color: 'white', cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '8px', fontSize: '1.1rem', fontWeight: 'bold' }}>
          <ArrowRight size={28} />
          رجوع
        </button>
        <h2 style={{ margin: 0, fontSize: '1.3rem', color: 'white', flex: 1, textAlign: 'center' }}>{story.title['ar-SA']}</h2>
        <div style={{ width: 70 }} />
      </div>

      <div style={{ padding: '24px', maxWidth: '800px', margin: '0 auto' }} dir="rtl" onMouseUp={handleSelection} onTouchEnd={handleSelection}>
        {/* Speed Controls */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '12px', marginBottom: '16px', justifyContent: 'flex-start' }}>
          <Settings2 size={20} color="#9ca3af" />
          <span style={{ color: '#9ca3af', fontSize: '0.9rem' }}>سرعة القراءة:</span>
          <div style={{ display: 'flex', backgroundColor: 'rgba(255,255,255,0.1)', borderRadius: '20px', padding: '4px' }}>
            <button 
              onClick={() => setPlaybackRate(0.6)}
              style={{ background: playbackRate === 0.6 ? '#58cc02' : 'transparent', color: 'white', border: 'none', padding: '4px 12px', borderRadius: '16px', cursor: 'pointer', fontSize: '0.9rem', fontWeight: 'bold' }}>
              بطيء
            </button>
            <button 
              onClick={() => setPlaybackRate(0.9)}
              style={{ background: playbackRate === 0.9 ? '#58cc02' : 'transparent', color: 'white', border: 'none', padding: '4px 12px', borderRadius: '16px', cursor: 'pointer', fontSize: '0.9rem', fontWeight: 'bold' }}>
              عادي
            </button>
            <button 
              onClick={() => setPlaybackRate(1.2)}
              style={{ background: playbackRate === 1.2 ? '#58cc02' : 'transparent', color: 'white', border: 'none', padding: '4px 12px', borderRadius: '16px', cursor: 'pointer', fontSize: '0.9rem', fontWeight: 'bold' }}>
              سريع
            </button>
          </div>
        </div>

        <div style={{ 
          backgroundColor: 'rgba(255,255,255,0.05)', 
          padding: '32px', 
          borderRadius: '16px',
          boxShadow: '0 4px 12px rgba(0,0,0,0.1)',
          marginBottom: '24px'
        }}>
          <div dir="ltr" style={{ marginBottom: '32px', lineHeight: '2' }}>
            <p style={{ fontSize: '1.6rem', margin: 0, fontWeight: 'bold', color: 'var(--text-main)', textAlign: 'left' }}>
              {story.sentences.map((sentence, idx) => {
                const esText = sentence.es || sentence.translation;
                const words = esText.split(' ');
                
                return (
                  <span key={idx} style={activeIndex === idx || (activeItem?.type === 'click' && activeItem?.index === idx) ? highlightStyle : {}}>
                    {words.map((word, wIdx) => (
                      <span 
                        key={wIdx} 
                        style={{...wordStyle, backgroundColor: (activeItem?.type === 'click' && activeItem?.word === word.replace(/[.,!?¡¿]/g, '')) ? 'rgba(28, 176, 246, 0.4)' : 'transparent'}} 
                        onClick={() => handleWordClick(word, idx)}
                        onMouseOver={(e) => { if(activeItem?.word !== word.replace(/[.,!?¡¿]/g, '')) e.currentTarget.style.backgroundColor = 'rgba(28, 176, 246, 0.2)'}}
                        onMouseOut={(e) => { if(activeItem?.word !== word.replace(/[.,!?¡¿]/g, '')) e.currentTarget.style.backgroundColor = 'transparent'}}
                      >
                        {word}
                      </span>
                    )).reduce((prev, curr) => [prev, ' ', curr])}
                  </span>
                );
              })}
            </p>
          </div>
          
          <div style={{ height: '1px', backgroundColor: 'var(--border)', margin: '24px 0' }} />

          <div dir="rtl" style={{ lineHeight: '2' }}>
            <p style={{ fontSize: '1.3rem', margin: 0, color: '#9ca3af', textAlign: 'right' }}>
              {story.sentences.map((sentence, idx) => {
                const arText = sentence.ar || sentence.text;
                return (
                  <span key={idx} style={activeIndex === idx || (activeItem?.type === 'click' && activeItem?.index === idx) ? highlightStyle : {}}>
                    {arText}{' '}
                  </span>
                );
              })}
            </p>
          </div>
        </div>

        <button 
          onClick={handleTogglePlay}
          style={{ 
            width: '100%', 
            padding: '16px', 
            backgroundColor: isPlaying ? '#ef4444' : '#58cc02', 
            color: 'white', 
            border: 'none', 
            borderRadius: '12px', 
            fontSize: '1.2rem', 
            fontWeight: 'bold', 
            display: 'flex', 
            alignItems: 'center', 
            justifyContent: 'center', 
            gap: '12px', 
            cursor: 'pointer', 
            boxShadow: isPlaying ? '0 4px 0 #b91c1c' : '0 4px 0 #58a700',
            transform: 'none',
            transition: 'all 0.1s'
          }}
        >
          {isPlaying ? <Square size={24} fill="white" /> : <Volume2 size={28} />}
          {isPlaying ? 'إيقاف القراءة' : 'استماع للقصة'}
        </button>
      </div>

      {/* Floating Action Bar for Saving */}
      {activeItem && (
        <div style={{
          position: 'fixed',
          bottom: '100px', // moved up to clear the bottom nav
          left: '50%',
          transform: 'translateX(-50%)',
          backgroundColor: '#131f24',
          border: '2px solid #2a3f4a',
          borderRadius: '16px',
          padding: '16px',
          display: 'flex',
          flexDirection: 'column',
          gap: '12px',
          boxShadow: '0 10px 25px rgba(0,0,0,0.5)',
          zIndex: 100,
          width: '90%',
          maxWidth: '500px',
          direction: 'rtl'
        }}>
          {activeItem.type === 'click' ? (
            <>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div style={{ display: 'flex', flexDirection: 'column' }}>
                  <span style={{ fontSize: '0.85rem', color: '#9ca3af' }}>الكلمة:</span>
                  <strong style={{ fontSize: '1.2rem', color: '#1cb0f6' }}>{activeItem.word}</strong>
                </div>
                <button onClick={saveWord} style={{ backgroundColor: '#1cb0f6', color: 'white', border: 'none', borderRadius: '12px', padding: '8px 16px', display: 'flex', alignItems: 'center', gap: '8px', cursor: 'pointer', fontWeight: 'bold' }}>
                  <BookmarkPlus size={20} />
                  حفظ الكلمة
                </button>
              </div>
              
              <div style={{ height: '1px', backgroundColor: '#2a3f4a' }} />
              
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div style={{ display: 'flex', flexDirection: 'column', flex: 1, overflow: 'hidden', paddingLeft: '12px' }}>
                  <span style={{ fontSize: '0.85rem', color: '#9ca3af' }}>الجملة:</span>
                  <strong style={{ fontSize: '1rem', color: '#58cc02', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>{activeItem.sentence}</strong>
                </div>
                <button onClick={saveSentence} style={{ backgroundColor: '#58cc02', color: 'white', border: 'none', borderRadius: '12px', padding: '8px 16px', display: 'flex', alignItems: 'center', gap: '8px', cursor: 'pointer', fontWeight: 'bold' }}>
                  <BookmarkPlus size={20} />
                  حفظ الجملة
                </button>
              </div>
            </>
          ) : (
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <div style={{ display: 'flex', flexDirection: 'column', flex: 1, overflow: 'hidden', paddingLeft: '12px' }}>
                <span style={{ fontSize: '0.85rem', color: '#9ca3af' }}>النص المحدد:</span>
                <strong style={{ fontSize: '1.1rem', color: '#a855f7', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>{activeItem.text}</strong>
              </div>
              <button onClick={saveCustomSelection} style={{ backgroundColor: '#a855f7', color: 'white', border: 'none', borderRadius: '12px', padding: '8px 16px', display: 'flex', alignItems: 'center', gap: '8px', cursor: 'pointer', fontWeight: 'bold' }}>
                <BookmarkPlus size={20} />
                حفظ التحديد
              </button>
            </div>
          )}
        </div>
      )}

      {/* Toast Notification */}
      <div style={{
        position: 'fixed',
        top: toastMessage ? '24px' : '-100px',
        left: '50%',
        transform: 'translateX(-50%)',
        backgroundColor: '#4cc0ea',
        color: 'white',
        padding: '12px 24px',
        borderRadius: '24px',
        fontWeight: 'bold',
        fontSize: '1.1rem',
        boxShadow: '0 4px 12px rgba(0,0,0,0.3)',
        transition: 'top 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275)',
        zIndex: 100,
        pointerEvents: 'none',
        display: 'flex',
        alignItems: 'center',
        gap: '8px'
      }}>
        {toastMessage}
      </div>
    </div>
  );
};
