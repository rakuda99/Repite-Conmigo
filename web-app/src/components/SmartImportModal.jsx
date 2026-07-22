import React, { useState } from 'react';
import { X, FileText, Wand2 } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';

export const SmartImportModal = ({ isOpen, onClose, onImport }) => {
  const [title, setTitle] = useState('');
  const [pastedText, setPastedText] = useState('');

  if (!isOpen) return null;

  const handleSmartImport = () => {
    if (!title.trim() || !pastedText.trim()) {
      alert("الرجاء إدخال اسم المجموعة والنص المراد استيراده.");
      return;
    }

    const lines = pastedText.split('\n');
    const sentences = [];

    lines.forEach(line => {
      // Skip empty lines
      if (!line.trim()) return;

      // Try to split by common delimiters: tab, dash, comma, or double spaces
      let parts = [];
      if (line.includes('\t')) {
        parts = line.split('\t');
      } else if (line.includes(' - ')) {
        parts = line.split(' - ');
      } else if (line.includes(' – ')) {
        parts = line.split(' – ');
      } else if (line.includes('=')) {
        parts = line.split('=');
      } else if (line.includes(',')) {
        parts = line.split(',');
      }

      if (parts.length >= 2) {
        // Assume first part is Spanish, second is Arabic (or vice versa, we just map it)
        const es = parts[0].trim();
        const ar = parts[1].trim();
        if (es && ar) {
          sentences.push({ es, ar });
        }
      }
    });

    if (sentences.length === 0) {
      alert("لم نتمكن من التعرف على الكلمات. يرجى التأكد من فصل الكلمة عن ترجمتها بعلامة ( - ) أو (مسافة جدولة Tab) أو (فاصلة).");
      return;
    }

    const newDeck = {
      title: { "ar-SA": title, "en-US": title },
      sentences: sentences
    };

    onImport(newDeck);
    alert(`تم استيراد ${sentences.length} بطاقة بنجاح!`);
    setTitle('');
    setPastedText('');
  };

  return (
    <AnimatePresence>
      <div className="modal-overlay" onClick={onClose} style={{ zIndex: 9999 }}>
        <motion.div 
          initial={{ y: 50, opacity: 0 }}
          animate={{ y: 0, opacity: 1 }}
          exit={{ y: 50, opacity: 0 }}
          className="deck-modal"
          onClick={e => e.stopPropagation()}
          style={{ width: '90%', maxWidth: '600px', maxHeight: '90vh', display: 'flex', flexDirection: 'column' }}
        >
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }}>
            <h2 className="modal-title" style={{ margin: 0, display: 'flex', alignItems: 'center', gap: '8px' }}>
              <Wand2 size={24} color="#1cb0f6" />
              المستورد الذكي (Quizlet/Anki)
            </h2>
            <button onClick={onClose} style={{ background: 'none', border: 'none', color: '#9ca3af', cursor: 'pointer' }}>
              <X size={24} />
            </button>
          </div>
          
          <div style={{ display: 'flex', flexDirection: 'column', gap: '16px', overflowY: 'auto', paddingRight: '4px' }}>
            <div>
              <label style={{ color: '#9ca3af', display: 'block', marginBottom: '8px' }}>اسم المجموعة الجديدة:</label>
              <input 
                type="text" 
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                placeholder="مثال: أفعال الحركة، كورس إسباني، إلخ"
                style={{ width: '100%', padding: '12px', borderRadius: '12px', border: '2px solid #2a3f4a', backgroundColor: 'rgba(0,0,0,0.2)', color: 'white', fontSize: '1.1rem' }}
              />
            </div>

            <div>
              <label style={{ color: '#9ca3af', display: 'block', marginBottom: '8px' }}>الصق الكلمات هنا:</label>
              <p style={{ color: '#6b7280', fontSize: '0.85rem', marginTop: 0, marginBottom: '8px' }}>
                انسخ الكلمات من Quizlet أو Excel والصقها هنا. يجب أن تكون الكلمة وترجمتها في نفس السطر ومفصولة بـ ( - ) أو (Tab) أو (فاصلة).
              </p>
              <textarea 
                value={pastedText}
                onChange={(e) => setPastedText(e.target.value)}
                placeholder={"Hola - مرحباً\nAdiós - وداعاً\nPor favor - من فضلك"}
                rows={10}
                dir="ltr"
                style={{ width: '100%', padding: '12px', borderRadius: '12px', border: '2px solid #2a3f4a', backgroundColor: 'rgba(0,0,0,0.2)', color: 'white', fontSize: '1.1rem', resize: 'vertical', fontFamily: 'monospace' }}
              />
            </div>

            <button 
              onClick={handleSmartImport}
              style={{ 
                backgroundColor: '#1cb0f6', 
                color: 'white', 
                border: 'none', 
                borderRadius: '16px', 
                padding: '16px', 
                fontSize: '1.2rem', 
                fontWeight: 'bold', 
                cursor: 'pointer', 
                boxShadow: '0 4px 0 #1899d6', 
                marginTop: '8px',
                display: 'flex',
                justifyContent: 'center',
                alignItems: 'center',
                gap: '8px'
              }}
            >
              <FileText size={24} />
              استيراد وإنشاء المجموعة
            </button>
          </div>
        </motion.div>
      </div>
    </AnimatePresence>
  );
};
