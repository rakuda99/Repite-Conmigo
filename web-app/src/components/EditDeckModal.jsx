import React, { useState } from 'react';
import { X, Plus, Trash2, Save } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';

export const EditDeckModal = ({ deck, onClose, onSave, onDelete }) => {
  const [titleAr, setTitleAr] = useState(deck?.title?.['ar-SA'] || '');
  const [titleEn, setTitleEn] = useState(deck?.title?.['en-US'] || '');
  const [sentences, setSentences] = useState(deck?.sentences || []);

  const handleAddSentence = () => {
    setSentences([...sentences, { ar: '', es: '', translation: '' }]);
  };

  const handleUpdateSentence = (index, field, value) => {
    const newSentences = [...sentences];
    newSentences[index][field] = value;
    if (field === 'es') newSentences[index].translation = value;
    setSentences(newSentences);
  };

  const handleDeleteSentence = (index) => {
    setSentences(sentences.filter((_, i) => i !== index));
  };

  const handleSave = () => {
    onSave({
      ...deck,
      title: { ...deck.title, 'ar-SA': titleAr, 'en-US': titleEn },
      sentences: sentences.filter(s => s.ar.trim() || s.es.trim()) // filter empty ones
    });
  };

  if (!deck) return null;

  return (
    <div className="modal-overlay" onClick={onClose} style={{ zIndex: 2000 }}>
      <motion.div 
        initial={{ y: 50, opacity: 0 }}
        animate={{ y: 0, opacity: 1 }}
        exit={{ y: 50, opacity: 0 }}
        className="deck-modal"
        style={{ maxWidth: '600px', width: '90%', maxHeight: '90vh', overflowY: 'auto' }}
        onClick={e => e.stopPropagation()}
        dir="rtl"
      >
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <h2 className="modal-title" style={{ margin: 0 }}>تعديل المجموعة</h2>
          <button onClick={onClose} style={{ background: 'none', border: 'none', color: 'white', cursor: 'pointer' }}>
            <X size={24} />
          </button>
        </div>
        
        <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          <div>
            <label style={{ color: '#4cc0ea', fontWeight: 'bold' }}>اسم المجموعة (عربي)</label>
            <input 
              value={titleAr}
              onChange={e => setTitleAr(e.target.value)}
              style={{ width: '100%', padding: '12px', borderRadius: '8px', border: 'none', marginTop: '8px', fontSize: '1.1rem' }}
            />
          </div>
          <div>
            <label style={{ color: '#4cc0ea', fontWeight: 'bold' }}>اسم المجموعة (إنجليزي / لاتيني)</label>
            <input 
              value={titleEn}
              onChange={e => setTitleEn(e.target.value)}
              style={{ width: '100%', padding: '12px', borderRadius: '8px', border: 'none', marginTop: '8px', fontSize: '1.1rem', textAlign: 'left' }}
              dir="ltr"
            />
          </div>

          <div style={{ marginTop: '16px' }}>
            <h3 style={{ color: 'white', marginBottom: '16px' }}>الكلمات / الجمل ({sentences.length})</h3>
            {sentences.map((s, idx) => (
              <div key={idx} style={{ backgroundColor: '#1a2f26', padding: '16px', borderRadius: '12px', marginBottom: '12px', position: 'relative' }}>
                <button 
                  onClick={() => handleDeleteSentence(idx)}
                  style={{ position: 'absolute', top: '16px', left: '16px', background: 'none', border: 'none', color: '#ff4b4b', cursor: 'pointer' }}
                >
                  <Trash2 size={20} />
                </button>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '8px', paddingLeft: '32px' }}>
                  <input 
                    placeholder="النص بالعربية"
                    value={s.ar || s.text || ''}
                    onChange={e => handleUpdateSentence(idx, 'ar', e.target.value)}
                    style={{ width: '100%', padding: '8px', borderRadius: '4px', border: 'none' }}
                  />
                  <input 
                    placeholder="النص بالإسبانية"
                    value={s.es || s.translation || ''}
                    onChange={e => handleUpdateSentence(idx, 'es', e.target.value)}
                    style={{ width: '100%', padding: '8px', borderRadius: '4px', border: 'none', textAlign: 'left' }}
                    dir="ltr"
                  />
                </div>
              </div>
            ))}
            <button 
              onClick={handleAddSentence}
              style={{ width: '100%', padding: '12px', borderRadius: '12px', border: '2px dashed #4cc0ea', background: 'none', color: '#4cc0ea', fontWeight: 'bold', display: 'flex', justifyContent: 'center', alignItems: 'center', gap: '8px', cursor: 'pointer', marginTop: '8px' }}
            >
              <Plus size={20} /> إضافة كلمة جديدة
            </button>
          </div>
        </div>

        <div style={{ display: 'flex', gap: '12px', marginTop: '24px' }}>
          <button 
            onClick={handleSave}
            style={{ flex: 2, padding: '16px', borderRadius: '12px', border: 'none', backgroundColor: '#58cc02', color: 'white', fontWeight: 'bold', fontSize: '1.1rem', cursor: 'pointer', display: 'flex', justifyContent: 'center', alignItems: 'center', gap: '8px' }}
          >
            <Save size={24} /> حفظ التعديلات
          </button>
          {deck.id && String(deck.id).startsWith('custom_') && (
            <button 
              onClick={() => {
                if (window.confirm("هل أنت متأكد من حذف هذه المجموعة؟")) {
                  onDelete(deck.id);
                }
              }}
              style={{ flex: 1, padding: '16px', borderRadius: '12px', border: 'none', backgroundColor: '#ff4b4b', color: 'white', fontWeight: 'bold', fontSize: '1.1rem', cursor: 'pointer' }}
            >
              حذف
            </button>
          )}
        </div>
      </motion.div>
    </div>
  );
};
