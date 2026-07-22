import React from 'react';
import { Star, ClipboardList, Play, AlertTriangle, Dices, X } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';

export const DeckOptionsModal = ({ isOpen, onClose, deckTitle, isFav, onToggleFavorite, onEditDeck, onStudyFull, onStudyHard, onRandomTest, onExportDeck, onPublishDeck }) => {
  if (!isOpen) return null;

  return (
    <AnimatePresence>
      <div className="modal-overlay" onClick={onClose}>
        <motion.div 
          initial={{ y: 50, opacity: 0 }}
          animate={{ y: 0, opacity: 1 }}
          exit={{ y: 50, opacity: 0 }}
          className="deck-modal"
          onClick={e => e.stopPropagation()}
        >
          <h2 className="modal-title">{deckTitle}</h2>
          
          <div className="modal-options">
            <button className="modal-btn option-btn" onClick={onToggleFavorite}>
              <Star size={24} className="icon-blue" fill={isFav ? '#3b82f6' : 'none'} />
              <div className="option-text">
                <h4>{isFav ? 'إزالة من المفضلة' : 'إضافة إلى المفضلة'}</h4>
                <p>حفظ هذه المجموعة في قائمة المفضلة للوصول السريع.</p>
              </div>
            </button>
            
            <button className="modal-btn option-btn" onClick={onEditDeck}>
              <ClipboardList size={24} className="icon-cyan" />
              <div className="option-text">
                <h4>تخطيط وتعديل كلمات الجلسة</h4>
                <p>عرض القائمة، زيادة/نقصان عددها، وتبديل الكلمات.</p>
              </div>
            </button>

            <button className="modal-btn option-btn" onClick={onStudyFull}>
              <Play size={24} className="icon-cyan" />
              <div className="option-text">
                <h4>دراسة كامل المجموعة</h4>
                <p>دراسة كل بطاقات المجموعة بالترتيب الأساسي.</p>
              </div>
            </button>

            <button className="modal-btn option-btn" onClick={onStudyHard}>
              <AlertTriangle size={24} className="icon-orange" />
              <div className="option-text">
                <h4>الأشياء الصعبة فقط</h4>
                <p>دراسة عشوائية فقط للكلمات الصعبة أو ذات النطق السيء.</p>
              </div>
            </button>

            <button className="modal-btn option-btn" onClick={onRandomTest}>
              <Dices size={24} className="icon-green" />
              <div className="option-text">
                <h4>اختبار عشوائي (Random)</h4>
                <p>خلط بطاقات المجموعة كاملة ودراستها عشوائياً.</p>
              </div>
            </button>

            <button className="modal-btn option-btn" onClick={onExportDeck} style={{ border: '2px solid rgba(28, 176, 246, 0.3)' }}>
              <ClipboardList size={24} color="#1cb0f6" />
              <div className="option-text">
                <h4>تصدير / مشاركة</h4>
                <p>حفظ المجموعة كملف لإرسالها ومشاركتها مع أصدقائك.</p>
              </div>
            </button>

            <button className="modal-btn option-btn" onClick={onPublishDeck} style={{ border: '2px solid rgba(255, 193, 7, 0.3)' }}>
              <Star size={24} color="#ffc107" />
              <div className="option-text">
                <h4>نشر في متجر المجموعات 🌍</h4>
                <p>شارك مجموعتك مع جميع مستخدمي التطبيق في المتجر السحابي!</p>
              </div>
            </button>
          </div>

          <button className="modal-close-btn" onClick={onClose}>
            إغلاق
          </button>
        </motion.div>
      </div>
    </AnimatePresence>
  );
};
