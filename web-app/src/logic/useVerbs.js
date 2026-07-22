import { useState, useEffect } from 'react';
import verbsData from '../verbs.json';

export const useVerbs = () => {
  const [customVerbs, setCustomVerbs] = useState([]);

  useEffect(() => {
    try {
      const stored = localStorage.getItem('repite_custom_verbs');
      if (stored) {
        setCustomVerbs(JSON.parse(stored));
      }
    } catch (e) {
      console.error("Error parsing custom verbs", e);
    }
  }, []);

  const addCustomVerbs = (newVerbsArray) => {
    const updated = [...customVerbs, ...newVerbsArray];
    setCustomVerbs(updated);
    localStorage.setItem('repite_custom_verbs', JSON.stringify(updated));
  };

  // Merge the standard verbs and custom verbs. Assign IDs dynamically for custom verbs so they display correctly.
  const allVerbs = [
    ...verbsData,
    ...customVerbs.map((v, index) => ({
      ...v,
      id: verbsData.length + index + 1
    }))
  ];

  return {
    allVerbs,
    customVerbs,
    addCustomVerbs
  };
};
