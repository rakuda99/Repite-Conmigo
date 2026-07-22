import React, { useState, useEffect } from 'react';
import { ArrowLeft, ArrowRight, Mic, Eye, ChevronLeft, ChevronRight, Volume2, ArrowLeftRight, Trash2, Check, Edit2 } from 'lucide-react';
import { playTTS, startSTT } from '../logic/speech';
import { calculateSimilarity } from '../logic/levenshtein';
import { motion, AnimatePresence } from 'framer-motion';
import { updateItemInCustomDeck, deleteItemFromCustomDeck } from '../logic/storage';

export const LearningPage = ({ deck, onBack }) => {
  const [localDeck, setLocalDeck] = useState(deck);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [showAnswer, setShowAnswer] = useState(false);
  const [isRecording, setIsRecording] = useState(false);
  const [transcript, setTranscript] = useState('');
  const [score, setScore] = useState(null);
  const [mode, setMode] = useState(() => {
    return localStorage.getItem('repite_default_mode') || 'es-ar';
  });

  const [isEditing, setIsEditing] = useState(false);
  const [editValue, setEditValue] = useState('');

  const sentences = localDeck?.sentences || [];
  const currentItem = sentences[currentIndex] || {};
  const total = sentences.length || 1;
  const progressPercent = ((currentIndex + 1) / total) * 100;
  const isCustom = String(localDeck?.id).startsWith('custom_');

  // If we deleted the last item and the deck is now empty
  if (sentences.length === 0) {
    return (
      <div style={{ display: 'flex', flexDirection: 'column', height: '100vh', background: '#f7f7f8', alignItems: 'center', justifyContent: 'center' }}>
        <h2>المجموعة فارغة الآن</h2>
        <button onClick={onBack} style={{ padding: '12px 24px', backgroundColor: '#58cc02', color: 'white', borderRadius: '12px', border: 'none', cursor: 'pointer', marginTop: '16px' }}>العودة</button>
      </div>
    );
  }

  const handleToggleMode = () => {
    setMode(prev => prev === 'ar-es' ? 'es-ar' : 'ar-es');
    setShowAnswer(false);
    setTranscript('');
    setScore(null);
    setIsEditing(false);
  };

  const handleNext = () => {
    if (currentIndex < total - 1) {
      setCurrentIndex(currentIndex + 1);
      setShowAnswer(false);
      setTranscript('');
      setScore(null);
      setIsEditing(false);
    } else {
      onBack();
    }
  };

  const handlePrev = () => {
    if (currentIndex > 0) {
      setCurrentIndex(currentIndex - 1);
      setShowAnswer(false);
      setTranscript('');
      setScore(null);
      setIsEditing(false);
    }
  };

  const handleDeleteCard = () => {
    if (window.confirm('هل أنت متأكد من مسح هذه البطاقة؟')) {
      deleteItemFromCustomDeck(localDeck.id, currentIndex);
      const newSentences = [...sentences];
      newSentences.splice(currentIndex, 1);
      setLocalDeck({ ...localDeck, sentences: newSentences });
      
      if (currentIndex >= newSentences.length && newSentences.length > 0) {
        setCurrentIndex(newSentences.length - 1);
      }
      setShowAnswer(false);
      setTranscript('');
      setScore(null);
      setIsEditing(false);
    }
  };

  const handleSaveEdit = () => {
    updateItemInCustomDeck(localDeck.id, currentIndex, editValue);
    const newSentences = [...sentences];
    newSentences[currentIndex] = { ...newSentences[currentIndex], ar: editValue };
    setLocalDeck({ ...localDeck, sentences: newSentences });
    setIsEditing(false);
  };

  const handleStartRecording = () => {
    setTranscript('');
    setScore(null);
    setIsRecording(true);
    let sessionTranscript = '';
    
    const lang = 'es-ES'; // Always listen for Spanish since this is a Spanish learning app
    
    startSTT(lang, 
      (text, isFinal) => {
        setTranscript(text);
        sessionTranscript = text;
      },
      (err) => {
        setIsRecording(false);
        if (err !== 'no-speech') {
          console.error("STT Error:", err);
          alert("خطأ في الميكروفون: " + err);
        } else {
          alert("لم يتم سماع أي صوت. تأكد من إعطاء صلاحية الميكروفون وتحدث بصوت واضح.");
        }
      },
      () => {
        setIsRecording(false);
        if (sessionTranscript) {
          const expectedText = currentItem.es || currentItem.translation || '';
          if (expectedText) {
            setScore(calculateSimilarity(expectedText, sessionTranscript));
          }
          handleReveal();
        }
      }
    );
  };

  const handleReveal = () => {
    setShowAnswer(true);
    setTimeout(() => {
      try {
        const textToPlay = currentItem.es || currentItem.translation;
        if (textToPlay) {
          playTTS(textToPlay, 'es-ES');
        }
      } catch (e) {
        console.error(e);
      }
    }, 100);
  };

  const handlePlaySound = () => {
    const textToPlay = currentItem.es || currentItem.translation;
    if (textToPlay) {
      playTTS(textToPlay, 'es-ES');
    }
  };

  const title = localDeck?.title?.['ar-SA'] || 'بدون عنوان';
  const questionText = mode === 'ar-es' 
    ? (currentItem.ar || currentItem.text || 'لا يوجد نص')
    : (currentItem.es || currentItem.translation || 'Sin texto');
  const answerText = mode === 'ar-es' 
    ? (currentItem.es || currentItem.translation)
    : (currentItem.ar || currentItem.text);

  return (
    <div style={{ display: 'flex', flexDirection: 'column', minHeight: 'calc(100vh - 100px)', background: '#f7f7f8' }}>
      {/* Top Header */}
      <div style={{ backgroundColor: '#13241d', color: 'white', position: 'sticky', top: 0, zIndex: 10 }}>
        <div dir="rtl" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '16px 24px' }}>
          <button onClick={onBack} style={{ background: 'none', border: 'none', color: 'white', cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '8px', fontSize: '1.1rem', fontWeight: 'bold' }}>
            <ArrowRight size={28} />
            رجوع
          </button>
          <h2 style={{ margin: 0, fontSize: '1.5rem', flex: 1, textAlign: 'center' }}>{title}</h2>
          <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
            {isCustom && (
              <button 
                onClick={handleDeleteCard}
                style={{ background: 'none', border: 'none', color: '#ef4444', cursor: 'pointer', display: 'flex', alignItems: 'center', padding: '8px', backgroundColor: 'rgba(239, 68, 68, 0.1)', borderRadius: '50%' }}
                title="مسح البطاقة"
              >
                <Trash2 size={20} />
              </button>
            )}
            <button 
              onClick={handleToggleMode}
              style={{ 
                cursor: 'pointer', 
                backgroundColor: 'rgba(255,255,255,0.2)', 
                border: '1px solid rgba(255,255,255,0.4)', 
                color: 'white',
                padding: '6px 14px',
                borderRadius: '20px',
                display: 'flex',
                alignItems: 'center',
                gap: '8px',
                fontSize: '0.95rem',
                fontWeight: 'bold',
                boxShadow: '0 2px 4px rgba(0,0,0,0.2)'
              }}
            >
              <ArrowLeftRight size={18} />
              {mode === 'ar-es' ? 'عربي ➔ إسباني' : 'إسباني ➔ عربي'}
            </button>
            <div style={{ fontSize: '1.25rem', fontWeight: 'bold', color: '#4cc0ea', direction: 'ltr' }}>
              {currentIndex + 1} / {total}
            </div>
          </div>
        </div>
        {/* Progress Bar under header */}
        <div style={{ width: '100%', backgroundColor: '#1a2f26', height: '4px' }}>
          <div style={{ width: `${progressPercent}%`, backgroundColor: '#4cc0ea', height: '100%', transition: 'width 0.3s' }} />
        </div>
      </div>

      {/* Main Content */}
      <div style={{ flex: 1, display: 'flex', flexDirection: 'column', padding: '24px', maxWidth: '600px', margin: '0 auto', width: '100%' }}>
        <div className="main-learning-card" dir={mode === 'ar-es' ? 'rtl' : 'ltr'} style={{ flex: '0 1 auto' }}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '16px' }}>
            {mode === 'ar-es' && isCustom && isEditing && !showAnswer ? (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '8px', width: '100%' }}>
                <textarea 
                  value={editValue} 
                  onChange={(e) => setEditValue(e.target.value)} 
                  style={{ width: '100%', padding: '12px', fontSize: '1.2rem', borderRadius: '12px', border: '2px solid #4cc0ea' }}
                  rows={3}
                />
                <button onClick={handleSaveEdit} style={{ padding: '8px 16px', backgroundColor: '#58cc02', color: 'white', border: 'none', borderRadius: '8px', cursor: 'pointer', alignSelf: 'flex-end', display: 'flex', gap: '8px' }}>
                  <Check size={20} /> حفظ
                </button>
              </div>
            ) : (
              <h1 
                className="arabic-text" 
                style={{ fontSize: mode === 'ar-es' ? '2.5rem' : '2.2rem', margin: 0, cursor: (mode === 'ar-es' && isCustom && !showAnswer) ? 'pointer' : 'default' }}
                onClick={() => {
                  if (mode === 'ar-es' && isCustom && !showAnswer) {
                    setEditValue(questionText);
                    setIsEditing(true);
                  }
                }}
              >
                {questionText}
                {mode === 'ar-es' && isCustom && !showAnswer && !isEditing && (
                  <Edit2 size={16} color="#9ca3af" style={{ marginLeft: '12px' }} />
                )}
              </h1>
            )}

            {mode === 'es-ar' && (
              <button onClick={handlePlaySound} style={{ background: 'none', border: 'none', color: '#1cb0f6', cursor: 'pointer', display: 'flex', alignItems: 'center' }}>
                <Volume2 size={32} />
              </button>
            )}
          </div>

          {showAnswer && (
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '12px', marginTop: '24px', flexDirection: 'column' }} dir={mode === 'ar-es' ? 'ltr' : 'rtl'}>
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '12px' }}>
                {mode === 'es-ar' && isCustom && isEditing ? (
                  <div style={{ display: 'flex', flexDirection: 'column', gap: '8px', width: '100%' }}>
                    <textarea 
                      value={editValue} 
                      onChange={(e) => setEditValue(e.target.value)} 
                      style={{ width: '100%', padding: '12px', fontSize: '1.2rem', borderRadius: '12px', border: '2px solid #4cc0ea' }}
                      rows={3}
                    />
                    <button onClick={handleSaveEdit} style={{ padding: '8px 16px', backgroundColor: '#58cc02', color: 'white', border: 'none', borderRadius: '8px', cursor: 'pointer', alignSelf: 'flex-start', display: 'flex', gap: '8px' }}>
                      <Check size={20} /> حفظ
                    </button>
                  </div>
                ) : (
                  <h2 
                    style={{ color: '#58cc02', fontSize: '1.5rem', margin: 0, cursor: (mode === 'es-ar' && isCustom) ? 'pointer' : 'default', display: 'flex', alignItems: 'center' }}
                    onClick={() => {
                      if (mode === 'es-ar' && isCustom) {
                        setEditValue(answerText);
                        setIsEditing(true);
                      }
                    }}
                  >
                    {answerText}
                    {mode === 'es-ar' && isCustom && !isEditing && (
                      <Edit2 size={16} color="#9ca3af" style={{ marginRight: '12px' }} />
                    )}
                  </h2>
                )}

                {mode === 'ar-es' && !isEditing && (
                  <button onClick={handlePlaySound} style={{ background: 'none', border: 'none', color: '#1cb0f6', cursor: 'pointer', display: 'flex', alignItems: 'center' }}>
                    <Volume2 size={28} />
                  </button>
                )}
              </div>
            </div>
          )}

          <button 
            className="mic-btn" 
            onClick={isRecording ? null : handleStartRecording}
            style={{ backgroundColor: isRecording ? 'var(--danger)' : '#58cc02', boxShadow: isRecording ? '0 4px 0 var(--danger-shadow)' : '0 4px 0 #58a700' }}
          >
            {isRecording ? (
              <motion.div animate={{ scale: [1, 1.2, 1] }} transition={{ repeat: Infinity, duration: 1.5 }}>
                <Mic size={40} />
              </motion.div>
            ) : (
              <Mic size={40} />
            )}
          </button>

          <AnimatePresence>
            {transcript && (
              <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }} style={{ color: '#9ca3af', fontSize: '1.2rem', marginTop: '8px' }}>
                {transcript}
              </motion.div>
            )}
          </AnimatePresence>

          <AnimatePresence>
            {score !== null && (
              <motion.div 
                initial={{ scale: 0.8, opacity: 0 }} 
                animate={{ scale: 1, opacity: 1 }} 
                exit={{ scale: 0.8, opacity: 0 }}
                style={{ 
                  marginTop: '12px', 
                  fontSize: '1.5rem', 
                  fontWeight: 'bold', 
                  color: score >= 80 ? '#58cc02' : score >= 50 ? '#ffc800' : '#ff4b4b' 
                }}
              >
                {score}% دقة النطق
              </motion.div>
            )}
          </AnimatePresence>

          <button className="reveal-btn" onClick={handleReveal}>
            <Eye size={24} />
            كشف الإجابة
          </button>
        </div>

        {/* Footer */}
        <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: '32px', paddingBottom: '24px' }}>
          <button 
            onClick={handlePrev} 
            disabled={currentIndex === 0}
            style={{ 
              display: 'flex', alignItems: 'center', gap: '8px', 
              padding: '12px 24px', backgroundColor: 'transparent', 
              color: currentIndex === 0 ? '#d1d5db' : '#4b5563', 
              border: `2px solid ${currentIndex === 0 ? '#e5e7eb' : '#d1d5db'}`, 
              borderRadius: '12px', fontSize: '1.1rem', fontWeight: 'bold', 
              cursor: currentIndex === 0 ? 'not-allowed' : 'pointer',
              transition: 'all 0.2s'
            }}
          >
            <ChevronRight size={24} />
            السابق
          </button>
          
          <button 
            onClick={handleNext}
            style={{ 
              display: 'flex', alignItems: 'center', gap: '8px', 
              padding: '12px 24px', backgroundColor: '#1cb0f6', 
              color: 'white', border: 'none', 
              borderRadius: '12px', fontSize: '1.1rem', fontWeight: 'bold', 
              cursor: 'pointer', boxShadow: '0 4px 0 #1899d6',
              transition: 'all 0.2s'
            }}
          >
            {currentIndex < total - 1 ? 'التالي' : 'إنهاء'}
            <ChevronLeft size={24} />
          </button>
        </div>
      </div>
    </div>
  );
};
