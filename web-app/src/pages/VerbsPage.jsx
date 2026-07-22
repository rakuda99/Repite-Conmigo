import React, { useState } from 'react';
import { Search, Volume2, Mic, Plus, Loader } from 'lucide-react';
import { playTTS, startSTT } from '../logic/speech';
import { calculateSimilarity } from '../logic/levenshtein';
import { useVerbs } from '../logic/useVerbs';
import { useDecks } from '../logic/useDecks';
import { generateVerbDetails } from '../logic/aiGenerator';
import { useNavigate } from 'react-router-dom';

const PronounceablePhrase = ({ phrase, meanings }) => {
  const [isRecording, setIsRecording] = useState(false);
  const [score, setScore] = useState(null);

  const handleListen = (e) => {
    e.stopPropagation();
    const phraseWithPauses = phrase.split(' ').join('. ');
    playTTS(phraseWithPauses, 'es-ES');
  };

  const handleSpeak = (e) => {
    e.stopPropagation();
    if (isRecording) return;
    setIsRecording(true);
    setScore(null);

    startSTT('es-ES', 
      (text, isFinal) => {
        if (isFinal) {
          const sim = calculateSimilarity(phrase, text);
          setScore(sim);
        }
      },
      (err) => {
        console.error("STT Error:", err);
        setIsRecording(false);
      },
      () => {
        setIsRecording(false);
      }
    );
  };

  let scoreClass = '';
  if (score !== null) {
    if (score >= 80) scoreClass = 'score-good';
    else if (score >= 50) scoreClass = 'score-ok';
    else scoreClass = 'score-bad';
  }

  const words = phrase.split(' ');

  return (
    <div className="phrase-container">
      <div className="phrase-words">
        {words.map((w, i) => (
          <div key={i} className="word-column">
            <span className={`phrase-word word-${i}`}>{w}</span>
            {meanings && meanings[i] && (
              <span className={`word-meaning-small meaning-${i}`}>{meanings[i]}</span>
            )}
          </div>
        ))}
      </div>
      
      <div className="phrase-actions">
        <button className="icon-btn action-listen" onClick={handleListen} title="استمع للسلسلة">
          <Volume2 size={24} />
        </button>
        <button className={`icon-btn action-speak ${isRecording ? 'recording' : ''}`} onClick={handleSpeak} title="انطق السلسلة">
          <Mic size={24} />
        </button>
        {score !== null && (
          <div className={`phrase-score ${scoreClass}`}>
            {score}%
          </div>
        )}
      </div>
    </div>
  );
};

export const VerbsPage = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const [showAddModal, setShowAddModal] = useState(false);
  const [verbsInput, setVerbsInput] = useState('');
  const [isGenerating, setIsGenerating] = useState(false);
  const [progressMsg, setProgressMsg] = useState('');
  
  const { allVerbs, addCustomVerbs } = useVerbs();
  const { addCustomDeck } = useDecks([]);
  const navigate = useNavigate();

  const handleGenerate = async () => {
    const apiKey = localStorage.getItem('repite_gemini_api_key');
    if (!apiKey) {
      alert("الرجاء إضافة مفتاح Gemini API في شاشة الإعدادات أولاً.");
      navigate('/settings');
      return;
    }

    const verbsList = verbsInput
      .split(/[\n,]+/)
      .map(v => v.trim().toLowerCase())
      .filter(v => v.length > 0);

    if (verbsList.length === 0) return;

    const existingVerbsSet = new Set(allVerbs.map(v => v.infinitive.toLowerCase()));
    const newVerbsToProcess = verbsList.filter(v => !existingVerbsSet.has(v));

    if (newVerbsToProcess.length === 0) {
      alert("جميع الأفعال المدخلة موجودة مسبقاً!");
      return;
    }

    setIsGenerating(true);
    setShowAddModal(false);

    const generatedVerbs = [];
    const generatedSentences = [];

    try {
      for (let i = 0; i < newVerbsToProcess.length; i++) {
        const v = newVerbsToProcess[i];
        setProgressMsg(`جاري معالجة الفعل (${i + 1}/${newVerbsToProcess.length}): ${v}`);
        
        const details = await generateVerbDetails(apiKey, v);
        
        generatedVerbs.push({
          infinitive: v,
          meaning: details.infinitive_meaning,
          past: details.past,
          past_meaning: details.past_meaning,
          present: details.present,
          present_meaning: details.present_meaning,
          future: details.future,
          future_meaning: details.future_meaning
        });

        if (details.sentences && Array.isArray(details.sentences)) {
          details.sentences.forEach(s => {
            generatedSentences.push({
              id: Date.now().toString() + Math.random().toString(36).substring(7),
              es: s.sentence,
              ar: s.translation
            });
          });
        }
      }

      addCustomVerbs(generatedVerbs);

      if (generatedSentences.length > 0) {
        addCustomDeck({
          id: 'custom_verbs_deck_' + Date.now(),
          title: { 'ar-SA': 'الأفعال المخصصة (جديد)', 'en-US': 'Custom Verbs (New)' },
          description: 'جمل تم توليدها للأفعال المضافة حديثاً.',
          sentences: generatedSentences
        });
      }

      alert("تمت الإضافة بنجاح!");
      setVerbsInput('');
    } catch (error) {
      alert("حدث خطأ أثناء الاتصال بالذكاء الاصطناعي: " + error.message);
    } finally {
      setIsGenerating(false);
      setProgressMsg('');
    }
  };

  const filteredVerbs = allVerbs.filter(v => 
    v.infinitive.toLowerCase().includes(searchTerm.toLowerCase()) || 
    v.meaning.includes(searchTerm)
  );

  return (
    <div className="page-container verbs-page">
      <header className="page-header">
        <h1>تصريف الأفعال</h1>
        <p>تدرب على الأفعال المضافة والسلسلة كاملة</p>
      </header>

      <div className="search-container-wrapper">
        <div className="search-container">
          <Search className="search-icon" size={20} />
          <input 
            type="text" 
            placeholder="ابحث عن فعل بالإسبانية أو معناه بالعربية..." 
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="search-input"
          />
        </div>
      </div>

      <div className="verbs-grid">
        {filteredVerbs.map(verb => {
          const phrase = `${verb.infinitive} ${verb.past} ${verb.present} ${verb.future}`;
          return (
            <div key={verb.id} className="verb-card sequence-card">
              <div className="verb-header sequence-header">
                <span className="verb-number">#{verb.id}</span>
                <span className="verb-meaning">{verb.meaning}</span>
              </div>
              
              <div className="verb-sequence">
                <PronounceablePhrase 
                  phrase={phrase} 
                  meanings={[verb.meaning, verb.past_meaning, verb.present_meaning, verb.future_meaning]}
                />
              </div>
            </div>
          );
        })}
        {filteredVerbs.length === 0 && (
          <div className="no-results">
            لم يتم العثور على أفعال مطابقة لبحثك.
          </div>
        )}
      </div>

      {/* Floating Action Button */}
      <button 
        className="fab-button"
        style={{
          position: 'fixed',
          bottom: '80px',
          left: '20px',
          backgroundColor: '#ff9600',
          color: 'white',
          border: 'none',
          borderRadius: '50%',
          width: '60px',
          height: '60px',
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center',
          boxShadow: '0 4px 10px rgba(0,0,0,0.3)',
          cursor: 'pointer',
          zIndex: 100
        }}
        onClick={() => setShowAddModal(true)}
      >
        <Plus size={30} />
      </button>

      {/* Add Verb Modal */}
      {showAddModal && (
        <div className="modal-overlay" onClick={() => setShowAddModal(false)}>
          <div className="modal-content" onClick={e => e.stopPropagation()}>
            <h2 style={{ marginBottom: '16px', color: 'var(--text-main)' }}>إضافة أفعال جديدة</h2>
            <p style={{ marginBottom: '12px', fontSize: '0.9rem', color: 'var(--text-muted)' }}>
              أدخل الأفعال الإسبانية مفصولة بفاصلة أو كل فعل في سطر جديد.
            </p>
            <textarea 
              value={verbsInput}
              onChange={(e) => setVerbsInput(e.target.value)}
              placeholder="مثال: comer, vivir, hablar"
              style={{
                width: '100%',
                height: '150px',
                padding: '12px',
                borderRadius: '8px',
                border: '1px solid var(--border)',
                backgroundColor: 'var(--background)',
                color: 'var(--text-main)',
                fontSize: '1rem',
                marginBottom: '16px',
                resize: 'vertical',
                direction: 'ltr'
              }}
            />
            <div style={{ display: 'flex', gap: '12px', justifyContent: 'flex-end' }}>
              <button 
                onClick={() => setShowAddModal(false)}
                style={{ padding: '10px 16px', borderRadius: '8px', border: '1px solid var(--border)', backgroundColor: 'transparent', color: 'var(--text-main)', cursor: 'pointer' }}
              >
                إلغاء
              </button>
              <button 
                onClick={handleGenerate}
                style={{ padding: '10px 16px', borderRadius: '8px', border: 'none', backgroundColor: '#1cb0f6', color: 'white', cursor: 'pointer', fontWeight: 'bold' }}
              >
                توليد بالذكاء الاصطناعي
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Loading Modal */}
      {isGenerating && (
        <div className="modal-overlay">
          <div className="modal-content" style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', gap: '16px' }}>
            <Loader className="spinner" size={40} color="#1cb0f6" />
            <h3 style={{ color: 'var(--text-main)' }}>جاري المعالجة...</h3>
            <p style={{ color: 'var(--text-muted)' }}>{progressMsg}</p>
          </div>
        </div>
      )}

      <style>{`
        .spinner {
          animation: spin 1s linear infinite;
        }
        @keyframes spin {
          100% { transform: rotate(360deg); }
        }
      `}</style>
    </div>
  );
};
