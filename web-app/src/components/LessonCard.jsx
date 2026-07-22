import React from 'react';
import { motion } from 'framer-motion';

export const LessonCard = ({ title, subtitle, icon: Icon, onClick, isCompleted, color = 'var(--secondary)' }) => {
  return (
    <motion.div
      whileHover={{ y: -4 }}
      whileTap={{ scale: 0.98 }}
      className="card"
      onClick={onClick}
      style={{
        cursor: 'pointer',
        display: 'flex',
        alignItems: 'center',
        gap: '16px',
        borderBottom: `4px solid var(--border)`
      }}
    >
      <div 
        style={{ 
          width: '56px', 
          height: '56px', 
          borderRadius: '16px', 
          background: color,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          color: 'white',
          opacity: isCompleted ? 0.8 : 1
        }}
      >
        {Icon && <Icon size={32} />}
      </div>
      
      <div style={{ flex: 1 }}>
        <h3 style={{ fontSize: '1.1rem', margin: 0, opacity: isCompleted ? 0.7 : 1 }}>
          {title}
        </h3>
        {subtitle && (
          <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem', margin: 0 }}>
            {subtitle}
          </p>
        )}
      </div>
      
      {isCompleted && (
        <div style={{ color: 'var(--warning)' }}>
          ★
        </div>
      )}
    </motion.div>
  );
};
