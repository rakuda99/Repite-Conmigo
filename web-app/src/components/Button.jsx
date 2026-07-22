import React from 'react';
import { motion } from 'framer-motion';

export const Button = ({ 
  children, 
  variant = 'primary', 
  onClick, 
  disabled = false,
  className = '',
  style = {}
}) => {
  const baseClass = 'btn';
  const variantClass = `btn-${variant}`;
  
  return (
    <motion.button
      whileHover={!disabled ? { scale: 1.02 } : {}}
      whileTap={!disabled ? { scale: 0.95 } : {}}
      className={`${baseClass} ${variantClass} ${className}`}
      onClick={onClick}
      disabled={disabled}
      style={{
        opacity: disabled ? 0.6 : 1,
        cursor: disabled ? 'not-allowed' : 'pointer',
        ...style
      }}
    >
      {children}
    </motion.button>
  );
};
