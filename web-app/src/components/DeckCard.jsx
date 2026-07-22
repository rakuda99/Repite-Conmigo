import React from 'react';
import { Star } from 'lucide-react';
import { motion } from 'framer-motion';

export const DeckCard = ({ title, totalCards, newCount, hardCount, reviewCount, onClick }) => {
  return (
    <motion.div 
      whileHover={{ scale: 1.02 }}
      whileTap={{ scale: 0.98 }}
      onClick={onClick}
      className="deck-card"
    >
      <div className="deck-card-header">
        <div>
          <h3 className="deck-title">{title}</h3>
          <p className="deck-subtitle">بطاقة {totalCards}</p>
        </div>
        <button className="favorite-btn">
          <Star size={20} color="#9ca3af" />
        </button>
      </div>
      
      <div className="deck-stats">
        <div className="stat-badge stat-new">
          <span>جديد:</span> <span>{newCount}</span>
        </div>
        <div className="stat-badge stat-hard">
          <span>صعب:</span> <span>{hardCount}</span>
        </div>
        <div className="stat-badge stat-review">
          <span>مراجعة:</span> <span>{reviewCount}</span>
        </div>
      </div>
    </motion.div>
  );
};
