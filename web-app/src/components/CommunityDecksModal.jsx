import React, { useState, useEffect } from 'react';
import { Download, X, BookOpen, Loader2 } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import { fetchCommunityDecks } from '../logic/cloud';

export const CommunityDecksModal = ({ isOpen, onClose, onDownload }) => {
  const [decks, setDecks] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (isOpen) {
      setLoading(true);
      fetchCommunityDecks().then(data => {
        setDecks(data);
        setLoading(false);
      }).catch(err => {
        console.error(err);
        setLoading(false);
      });
    }
  }, [isOpen]);

  if (!isOpen) return null;

  return (
    <AnimatePresence>
      <div className="modal-overlay" onClick={onClose} style={{ zIndex: 9999 }}>
        <motion.div 
          initial={{ y: 50, opacity: 0 }}
          animate={{ y: 0, opacity: 1 }}
          exit={{ y: 50, opacity: 0 }}
          className="deck-modal"
          onClick={e => e.stopPropagation()}
          style={{ width: '90%', maxWidth: '500px', maxHeight: '80vh', overflowY: 'auto' }}
        >
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }}>
            <h2 className="modal-title" style={{ margin: 0, display: 'flex', alignItems: 'center', gap: '8px' }}>
              <BookOpen size={24} color="#1cb0f6" />
              المجموعات المشتركة
            </h2>
            <button onClick={onClose} style={{ background: 'none', border: 'none', color: '#9ca3af', cursor: 'pointer' }}>
              <X size={24} />
            </button>
          </div>
          
          <p style={{ color: '#9ca3af', fontSize: '0.95rem', marginBottom: '24px', lineHeight: '1.6' }}>
            هنا تجد مجموعات جاهزة صممها المجتمع لتبدأ تعلمك فوراً. انقر على "تحميل" لإضافتها لمكتبتك الخاصة.
          </p>

          <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
            {loading ? (
              <div style={{ display: 'flex', justifyContent: 'center', padding: '32px 0' }}>
                <Loader2 size={32} color="#1cb0f6" className="animate-spin" style={{ animation: 'spin 2s linear infinite' }} />
              </div>
            ) : decks.length === 0 ? (
              <div style={{ textAlign: 'center', color: '#9ca3af', padding: '32px 0' }}>
                لا توجد مجموعات متاحة حالياً. كن أول من ينشر مجموعة!
              </div>
            ) : decks.map((deck, idx) => (
              <div key={idx} style={{ 
                backgroundColor: 'rgba(255,255,255,0.05)', 
                border: '2px solid rgba(255,255,255,0.1)', 
                borderRadius: '16px', 
                padding: '16px',
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center'
              }}>
                <div>
                  <h3 style={{ margin: 0, color: 'white', fontSize: '1.1rem', marginBottom: '4px' }}>{deck.title['ar-SA'] || 'بدون عنوان'}</h3>
                  <span style={{ color: '#9ca3af', fontSize: '0.85rem' }}>{deck.sentences?.length || 0} بطاقة • {deck.author || 'مستخدم مجهول'}</span>
                </div>
                <button 
                  onClick={() => {
                    onDownload(deck);
                    alert(`تم تنزيل "${deck.title['ar-SA']}" إلى مجموعاتك بنجاح!`);
                  }}
                  style={{ 
                    backgroundColor: '#1cb0f6', 
                    color: 'white', 
                    border: 'none', 
                    borderRadius: '12px', 
                    padding: '8px 16px', 
                    display: 'flex', 
                    alignItems: 'center', 
                    gap: '8px', 
                    cursor: 'pointer',
                    fontWeight: 'bold'
                  }}
                >
                  <Download size={20} />
                  تحميل
                </button>
              </div>
            ))}
          </div>
        </motion.div>
      </div>
    </AnimatePresence>
  );
};
