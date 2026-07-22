import React from 'react';
import { motion } from 'framer-motion';

export const ProgressBar = ({ progress }) => {
  return (
    <div className="progress-bg">
      <motion.div 
        className="progress-fill"
        initial={{ width: 0 }}
        animate={{ width: `${progress}%` }}
        transition={{ type: 'spring', stiffness: 50, damping: 10 }}
      />
    </div>
  );
};
