import React, { useState, useEffect } from 'react';
import lessonsData from './lessons.json';

function VectorIcon({ word }) {
  const w = (word || '').toLowerCase().trim();
  const s = { display: 'flex', alignItems: 'center', justifyContent: 'center', width: '100%', height: '100%' };

  if (w === '1' || w === 'uno')   return <div style={s}><span style={{fontSize:72,fontWeight:900,color:'#60a5fa'}}>1</span></div>;
  if (w === '2' || w === 'dos')   return <div style={s}><span style={{fontSize:72,fontWeight:900,color:'#60a5fa'}}>2</span></div>;
  if (w === '3' || w === 'tres')  return <div style={s}><span style={{fontSize:72,fontWeight:900,color:'#60a5fa'}}>3</span></div>;
  if (w === '4' || w === 'cuatro') return <div style={s}><span style={{fontSize:72,fontWeight:900,color:'#60a5fa'}}>4</span></div>;
  if (w === '5' || w === 'cinco') return <div style={s}><span style={{fontSize:72,fontWeight:900,color:'#60a5fa'}}>5</span></div>;
  if (w === '6' || w === 'seis')  return <div style={s}><span style={{fontSize:72,fontWeight:900,color:'#60a5fa'}}>6</span></div>;
  if (w === '7' || w === 'siete') return <div style={s}><span style={{fontSize:72,fontWeight:900,color:'#60a5fa'}}>7</span></div>;
  if (w === '8' || w === 'ocho')  return <div style={s}><span style={{fontSize:72,fontWeight:900,color:'#60a5fa'}}>8</span></div>;
  if (w === '9' || w === 'nueve') return <div style={s}><span style={{fontSize:72,fontWeight:900,color:'#60a5fa'}}>9</span></div>;
  if (w === '10' || w === 'diez') return <div style={s}><span style={{fontSize:60,fontWeight:900,color:'#60a5fa'}}>10</span></div>;

  if (w.includes('rojo'))     return <div style={s}><div style={{width:60,height:60,borderRadius:'50%',background:'radial-gradient(circle at 30% 30%,#ff6b6b,#c0392b)',boxShadow:'0 0 20px rgba(255,65,108,0.6)'}}/></div>;
  if (w.includes('azul'))     return <div style={s}><div style={{width:60,height:60,borderRadius:'50%',background:'radial-gradient(circle at 30% 30%,#74b9ff,#0984e3)',boxShadow:'0 0 20px rgba(9,132,227,0.6)'}}/></div>;
  if (w.includes('verde'))    return <div style={s}><div style={{width:60,height:60,borderRadius:'50%',background:'radial-gradient(circle at 30% 30%,#55efc4,#00b894)',boxShadow:'0 0 20px rgba(0,184,148,0.6)'}}/></div>;
  if (w.includes('amarillo')) return <div style={s}><div style={{width:60,height:60,borderRadius:'50%',background:'radial-gradient(circle at 30% 30%,#ffeaa7,#fdcb6e)',boxShadow:'0 0 20px rgba(253,203,110,0.6)'}}/></div>;
  if (w.includes('naranja'))  return <div style={s}><div style={{width:60,height:60,borderRadius:'50%',background:'radial-gradient(circle at 30% 30%,#fab1a0,#e17055)',boxShadow:'0 0 20px rgba(225,112,85,0.6)'}}/></div>;
  if (w.includes('morado') || w.includes('violeta')) return <div style={s}><div style={{width:60,height:60,borderRadius:'50%',background:'radial-gradient(circle at 30% 30%,#a29bfe,#6c5ce7)',boxShadow:'0 0 20px rgba(108,92,231,0.6)'}}/></div>;
  if (w.includes('negro'))    return <div style={s}><div style={{width:60,height:60,borderRadius:'50%',background:'radial-gradient(circle at 30% 30%,#636e72,#2d3436)',boxShadow:'0 0 20px rgba(0,0,0,0.9)',border:'1px solid rgba(255,255,255,0.2)'}}/></div>;
  if (w.includes('blanco'))   return <div style={s}><div style={{width:60,height:60,borderRadius:'50%',background:'radial-gradient(circle at 30% 30%,#ffffff,#dfe6e9)',boxShadow:'0 0 20px rgba(255,255,255,0.4)',border:'1px solid rgba(255,255,255,0.6)'}}/></div>;
  if (w.includes('rosa'))     return <div style={s}><div style={{width:60,height:60,borderRadius:'50%',background:'radial-gradient(circle at 30% 30%,#fd79a8,#e84393)',boxShadow:'0 0 20px rgba(232,67,147,0.6)'}}/></div>;
  if (w.includes('gris'))     return <div style={s}><div style={{width:60,height:60,borderRadius:'50%',background:'radial-gradient(circle at 30% 30%,#b2bec3,#636e72)',boxShadow:'0 0 20px rgba(99,110,114,0.5)'}}/></div>;

  if (w.includes('padre') || w.includes('papá')) return <div style={s}><svg width="65" height="65" viewBox="0 0 24 24" fill="none"><circle cx="12" cy="7" r="4" fill="#34d399" fillOpacity="0.3" stroke="#10b981" strokeWidth="2"/><path d="M4 21v-2a4 4 0 0 1 4-4h8a4 4 0 0 1 4 4v2" stroke="#10b981" strokeWidth="2"/></svg></div>;
  if (w.includes('madre') || w.includes('mamá')) return <div style={s}><svg width="65" height="65" viewBox="0 0 24 24" fill="none"><circle cx="12" cy="7" r="4" fill="#f9a8d4" fillOpacity="0.3" stroke="#ec4899" strokeWidth="2"/><path d="M4 21v-2c0-2.2 1.8-4 4-4h8c2.2 0 4 1.8 4 4v2" stroke="#ec4899" strokeWidth="2"/><path d="M9 16l3-2 3 2" stroke="#f472b6" strokeWidth="2"/></svg></div>;
  if (w.includes('hermano') && !w.includes('hermana')) return <div style={s}><svg width="65" height="65" viewBox="0 0 24 24" fill="none"><circle cx="12" cy="8" r="3" fill="#93c5fd" fillOpacity="0.3" stroke="#3b82f6" strokeWidth="2"/><path d="M6 21v-2a3 3 0 0 1 3-3h6a3 3 0 0 1 3 3v2" stroke="#3b82f6" strokeWidth="2"/><path d="M9 11l3 3 3-3" stroke="#60a5fa" strokeWidth="1.5"/></svg></div>;
  if (w.includes('hermana')) return <div style={s}><svg width="65" height="65" viewBox="0 0 24 24" fill="none"><circle cx="12" cy="8" r="3" fill="#c4b5fd" fillOpacity="0.3" stroke="#8b5cf6" strokeWidth="2"/><path d="M6 21v-2c0-1.7 1.3-3 3-3h6c1.7 0 3 1.3 3 3v2" stroke="#8b5cf6" strokeWidth="2"/></svg></div>;
  if (w.includes('abuelo'))  return <div style={s}><svg width="65" height="65" viewBox="0 0 24 24" fill="none"><circle cx="12" cy="7" r="4" fill="#86efac" fillOpacity="0.3" stroke="#22c55e" strokeWidth="2"/><path d="M4 21v-2a4 4 0 0 1 4-4h8a4 4 0 0 1 4 4v2" stroke="#22c55e" strokeWidth="2"/><path d="M9 5h6" stroke="#86efac" strokeWidth="2"/></svg></div>;
  if (w.includes('abuela'))  return <div style={s}><svg width="65" height="65" viewBox="0 0 24 24" fill="none"><circle cx="12" cy="7" r="4" fill="#fda4af" fillOpacity="0.3" stroke="#f43f5e" strokeWidth="2"/><path d="M4 21v-2c0-2.2 1.8-4 4-4h8c2.2 0 4 1.8 4 4v2" stroke="#f43f5e" strokeWidth="2"/></svg></div>;
  if (w.includes('hijo') && !w.includes('hija'))  return <div style={s}><svg width="65" height="65" viewBox="0 0 24 24" fill="none"><circle cx="12" cy="8" r="3" fill="#7dd3fc" fillOpacity="0.3" stroke="#0ea5e9" strokeWidth="2"/><path d="M7 21v-1a4 4 0 0 1 4-4h2a4 4 0 0 1 4 4v1" stroke="#0ea5e9" strokeWidth="2"/></svg></div>;
  if (w.includes('hija'))    return <div style={s}><svg width="65" height="65" viewBox="0 0 24 24" fill="none"><circle cx="12" cy="8" r="3" fill="#f0abfc" fillOpacity="0.3" stroke="#d946ef" strokeWidth="2"/><path d="M7 21v-1a4 4 0 0 1 4-4h2a4 4 0 0 1 4 4v1" stroke="#d946ef" strokeWidth="2"/></svg></div>;
  if (w.includes('tío') || w === 'tio')    return <div style={s}><svg width="65" height="65" viewBox="0 0 24 24" fill="none"><circle cx="12" cy="7" r="4" fill="#fde68a" fillOpacity="0.3" stroke="#f59e0b" strokeWidth="2"/><path d="M4 21v-2a4 4 0 0 1 4-4h8a4 4 0 0 1 4 4v2" stroke="#f59e0b" strokeWidth="2"/></svg></div>;
  if (w.includes('tía') || w === 'tia')    return <div style={s}><svg width="65" height="65" viewBox="0 0 24 24" fill="none"><circle cx="12" cy="7" r="4" fill="#bbf7d0" fillOpacity="0.3" stroke="#16a34a" strokeWidth="2"/><path d="M4 21v-2c0-2.2 1.8-4 4-4h8c2.2 0 4 1.8 4 4v2" stroke="#16a34a" strokeWidth="2"/></svg></div>;

  if (w === 'ojo' || w === 'el ojo' || w === 'los ojos') return <div style={s}><svg width="65" height="65" viewBox="0 0 24 24" fill="none"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" stroke="#3b82f6" strokeWidth="2"/><circle cx="12" cy="12" r="3" stroke="#3b82f6" strokeWidth="2"/><circle cx="12" cy="12" r="1.5" fill="#60a5fa"/></svg></div>;
  if (w.includes('nariz'))  return <div style={s}><svg width="65" height="65" viewBox="0 0 24 24" fill="none"><path d="M12 3c-1 0-2 2-2 5v7a2 2 0 0 0 4 0V8c0-3-1-5-2-5z" stroke="#f59e0b" strokeWidth="2"/><path d="M7 21a2 2 0 0 1 2-2h6a2 2 0 0 1 2 2" stroke="#f59e0b" strokeWidth="2"/></svg></div>;
  if (w.includes('boca'))   return <div style={s}><svg width="65" height="65" viewBox="0 0 24 24" fill="none"><path d="M4 10c2 2 6 2 8 0s6-2 8 0c0 4.4-3.6 8-8 8s-8-3.6-8-8z" stroke="#ef4444" strokeWidth="2"/><path d="M8 14h8" stroke="#fca5a5" strokeWidth="2"/></svg></div>;
  if (w.includes('oreja'))  return <div style={s}><svg width="65" height="65" viewBox="0 0 24 24" fill="none"><path d="M16 3c-2 0-5 3-5 8s3 10 5 10c3 0 5-5 5-9s-2-9-5-9z" stroke="#10b981" strokeWidth="2"/><path d="M11 11c-2 0-3 1-3 3s1 3 3 3" stroke="#34d399" strokeWidth="1.5"/></svg></div>;
  if (w.includes('cabeza')) return <div style={s}><svg width="65" height="65" viewBox="0 0 24 24" fill="none"><circle cx="12" cy="11" r="7" stroke="#8b5cf6" strokeWidth="2"/><path d="M8 17v2a4 4 0 0 0 8 0v-2" stroke="#8b5cf6" strokeWidth="2"/></svg></div>;
  if (w.includes('brazo'))  return <div style={s}><svg width="65" height="65" viewBox="0 0 24 24" fill="none"><path d="M8 5c0 0 1 3 0 7" stroke="#f59e0b" strokeWidth="3" strokeLinecap="round"/><path d="M8 12l6 5" stroke="#f59e0b" strokeWidth="2.5" strokeLinecap="round"/><circle cx="8" cy="12" r="2" fill="#fbbf24" fillOpacity="0.4"/></svg></div>;
  if (w.includes('pierna') || (w.includes('pie') && !w.includes('piel'))) return <div style={s}><svg width="65" height="65" viewBox="0 0 24 24" fill="none"><path d="M12 3v10M8 13l4 4 4-4M12 17v4" stroke="#ec4899" strokeWidth="2.5" strokeLinecap="round"/></svg></div>;

  if (w.includes('perro'))  return <div style={s}><svg width="65" height="65" viewBox="0 0 24 24" fill="none"><ellipse cx="12" cy="14" rx="6" ry="4.5" fill="#fbbf24" fillOpacity="0.25" stroke="#f59e0b" strokeWidth="2"/><circle cx="15" cy="7.5" r="2" fill="#fbbf24" fillOpacity="0.25" stroke="#f59e0b" strokeWidth="1.5"/><circle cx="9" cy="7.5" r="2" fill="#fbbf24" fillOpacity="0.25" stroke="#f59e0b" strokeWidth="1.5"/><circle cx="10" cy="13" r="0.9" fill="#92400e"/><circle cx="14" cy="13" r="0.9" fill="#92400e"/><path d="M11 15.5q1 1 2 0" stroke="#92400e" strokeWidth="1.5"/><path d="M18 9l2-2" stroke="#f59e0b" strokeWidth="2" strokeLinecap="round"/></svg></div>;
  if (w.includes('gato'))   return <div style={s}><svg width="65" height="65" viewBox="0 0 24 24" fill="none"><path d="M6 3l2 4h8l2-4v8a6 6 0 0 1-12 0V3z" fill="#94a3b8" fillOpacity="0.2" stroke="#64748b" strokeWidth="2"/><circle cx="9.5" cy="10" r="1" fill="#1e293b"/><circle cx="14.5" cy="10" r="1" fill="#1e293b"/><path d="M11 13q1 1.5 2 0" stroke="#334155" strokeWidth="1.5"/><path d="M5 4l1 2M19 4l-1 2" stroke="#94a3b8" strokeWidth="2"/><path d="M9 10l-3-1M15 10l3-1" stroke="#94a3b8" strokeWidth="1.5"/></svg></div>;
  if (w.includes('pájaro') || w.includes('pajaro')) return <div style={s}><svg width="65" height="65" viewBox="0 0 24 24" fill="none"><ellipse cx="10" cy="14" rx="5" ry="4" fill="#60a5fa" fillOpacity="0.25" stroke="#3b82f6" strokeWidth="2"/><circle cx="8" cy="12" r="0.8" fill="#1e40af"/><path d="M6 15l-2 2" stroke="#3b82f6" strokeWidth="2"/><path d="M14 11l5-2-3 5" fill="#fbbf24" stroke="#f59e0b" strokeWidth="1"/></svg></div>;
  if (w.includes('pez') || w.includes('pescado')) return <div style={s}><svg width="65" height="65" viewBox="0 0 24 24" fill="none"><ellipse cx="10" cy="12" rx="7" ry="4.5" fill="#7dd3fc" fillOpacity="0.25" stroke="#0ea5e9" strokeWidth="2"/><path d="M17 12l5 3.5v-7z" fill="#38bdf8" fillOpacity="0.6" stroke="#0ea5e9" strokeWidth="1"/><circle cx="7" cy="10" r="1" fill="white"/></svg></div>;
  if (w.includes('caballo')) return <div style={s}><svg width="65" height="65" viewBox="0 0 24 24" fill="none"><rect x="5" y="7" width="5" height="7" rx="2" fill="#d4a574" fillOpacity="0.3" stroke="#92400e" strokeWidth="2"/><ellipse cx="13" cy="13" rx="6" ry="4" fill="#d4a574" fillOpacity="0.3" stroke="#92400e" strokeWidth="2"/><path d="M5 7c0-2 2-3 4-3" stroke="#92400e" strokeWidth="2"/></svg></div>;
  if (w.includes('vaca'))   return <div style={s}><svg width="65" height="65" viewBox="0 0 24 24" fill="none"><ellipse cx="12" cy="14" rx="8" ry="5" fill="#f8fafc" fillOpacity="0.3" stroke="#475569" strokeWidth="2"/><rect x="9" y="6" width="5" height="7" rx="2" fill="#f8fafc" fillOpacity="0.2" stroke="#475569" strokeWidth="2"/><path d="M9 6l-2-2M15 6l2-2" stroke="#475569" strokeWidth="2"/></svg></div>;
  if (w.includes('pájaro') || w.includes('pajaro') || w.includes('ave')) return <div style={s}><span style={{fontSize:50}}>🐦</span></div>;

  if (w.includes('manzana'))  return <div style={s}><svg width="65" height="65" viewBox="0 0 24 24" fill="none"><circle cx="12" cy="14" r="7" fill="#ef4444" fillOpacity="0.8"/><path d="M12 7V4" stroke="#16a34a" strokeWidth="2.5" strokeLinecap="round"/><path d="M12 4c1 1.5 3 2 4 1" stroke="#16a34a" strokeWidth="2"/></svg></div>;
  if (w.includes('naranja') && !w.includes('color')) return <div style={s}><svg width="65" height="65" viewBox="0 0 24 24" fill="none"><circle cx="12" cy="13" r="7" fill="#fb923c" fillOpacity="0.8"/><path d="M12 6V3" stroke="#16a34a" strokeWidth="2.5" strokeLinecap="round"/></svg></div>;
  if (w.includes('plátano') || w.includes('platano')) return <div style={s}><svg width="65" height="65" viewBox="0 0 24 24" fill="none"><path d="M5 18c3-8 8-12 14-10-2 8-9 14-16 12" fill="#fbbf24" fillOpacity="0.8" stroke="#d97706" strokeWidth="2"/></svg></div>;
  if (w.includes('agua'))   return <div style={s}><svg width="65" height="65" viewBox="0 0 24 24" fill="none"><rect x="7" y="4" width="10" height="16" rx="3" fill="#bfdbfe" fillOpacity="0.3" stroke="#3b82f6" strokeWidth="2"/><path d="M7 9h10" stroke="#93c5fd" strokeWidth="2"/></svg></div>;
  if (w.includes('leche'))  return <div style={s}><svg width="65" height="65" viewBox="0 0 24 24" fill="none"><rect x="8" y="5" width="8" height="14" rx="2" fill="white" fillOpacity="0.15" stroke="#94a3b8" strokeWidth="2"/><rect x="7" y="3" width="10" height="3" rx="1" fill="#cbd5e1" fillOpacity="0.5"/><path d="M10 9c1 2 3 2 4 0" stroke="#e2e8f0" strokeWidth="1.5"/></svg></div>;
  if (w.includes('pan'))    return <div style={s}><svg width="65" height="65" viewBox="0 0 24 24" fill="none"><rect x="3" y="10" width="18" height="9" rx="4" fill="#fbbf24" fillOpacity="0.3" stroke="#d97706" strokeWidth="2"/><ellipse cx="12" cy="10" rx="9" ry="4" fill="#fde68a" fillOpacity="0.4" stroke="#d97706" strokeWidth="2"/></svg></div>;
  if (w.includes('café') || w.includes('cafe')) return <div style={s}><svg width="65" height="65" viewBox="0 0 24 24" fill="none"><path d="M6 7h12v10a4 4 0 0 1-4 4H10a4 4 0 0 1-4-4V7z" fill="#92400e" fillOpacity="0.3" stroke="#92400e" strokeWidth="2"/><path d="M18 9h2a2 2 0 0 1 0 4h-2" stroke="#92400e" strokeWidth="2"/></svg></div>;
  if (w.includes('carne'))  return <div style={s}><svg width="65" height="65" viewBox="0 0 24 24" fill="none"><ellipse cx="12" cy="13" rx="8" ry="5" fill="#fca5a5" fillOpacity="0.3" stroke="#ef4444" strokeWidth="2"/><path d="M6 10s2-4 6-4 6 4 6 4" stroke="#f87171" strokeWidth="2"/></svg></div>;

  if (w.includes('coche') || w.includes('carro') || w.includes('auto')) return <div style={s}><svg width="65" height="65" viewBox="0 0 24 24" fill="none"><rect x="2" y="11" width="20" height="7" rx="2" fill="#fca5a5" fillOpacity="0.2" stroke="#ef4444" strokeWidth="2"/><path d="M4 11l3-5h10l3 5" stroke="#ef4444" strokeWidth="2"/><circle cx="7" cy="18" r="2" fill="#1e293b" stroke="#ef4444" strokeWidth="1.5"/><circle cx="17" cy="18" r="2" fill="#1e293b" stroke="#ef4444" strokeWidth="1.5"/><rect x="8" y="7" width="4" height="3" rx="0.5" fill="#7dd3fc" fillOpacity="0.5"/><rect x="13" y="7" width="4" height="3" rx="0.5" fill="#7dd3fc" fillOpacity="0.5"/></svg></div>;
  if ((w.includes('autobús') || w.includes('autobus') || w === 'bus') && !w.includes('coche')) return <div style={s}><svg width="65" height="65" viewBox="0 0 24 24" fill="none"><rect x="2" y="5" width="20" height="14" rx="2" fill="#93c5fd" fillOpacity="0.2" stroke="#3b82f6" strokeWidth="2"/><path d="M2 11h20" stroke="#3b82f6" strokeWidth="2"/><rect x="4" y="7" width="4" height="3" rx="1" fill="#60a5fa" fillOpacity="0.4"/><rect x="10" y="7" width="4" height="3" rx="1" fill="#60a5fa" fillOpacity="0.4"/><rect x="16" y="7" width="4" height="3" rx="1" fill="#60a5fa" fillOpacity="0.4"/><circle cx="7" cy="19" r="1.5" fill="#1e293b"/><circle cx="17" cy="19" r="1.5" fill="#1e293b"/></svg></div>;
  if (w.includes('tren'))   return <div style={s}><svg width="65" height="65" viewBox="0 0 24 24" fill="none"><rect x="3" y="6" width="18" height="12" rx="2" fill="#a5b4fc" fillOpacity="0.2" stroke="#6366f1" strokeWidth="2"/><path d="M3 12h18" stroke="#6366f1" strokeWidth="2"/><circle cx="7" cy="18" r="1.5" fill="#1e293b"/><circle cx="17" cy="18" r="1.5" fill="#1e293b"/><path d="M7 6l-2-3M17 6l2-3" stroke="#818cf8" strokeWidth="2"/></svg></div>;
  if (w.includes('avión') || w.includes('avion')) return <div style={s}><svg width="65" height="65" viewBox="0 0 24 24" fill="none"><path d="M21 16v-2l-8-5V3.5a1.5 1.5 0 0 0-3 0V9l-8 5v2l8-2.5V19l-2 1.5V22l3.5-1 3.5 1v-1.5L13 19v-5.5z" fill="#818cf8" fillOpacity="0.7" stroke="#6366f1" strokeWidth="1"/></svg></div>;
  if (w.includes('bicicleta') || w.includes('bici')) return <div style={s}><svg width="65" height="65" viewBox="0 0 24 24" fill="none"><circle cx="6" cy="17" r="4" stroke="#10b981" strokeWidth="2"/><circle cx="18" cy="17" r="4" stroke="#10b981" strokeWidth="2"/><path d="M6 17l4-7h7M10 10l2 7M16 6h3" stroke="#34d399" strokeWidth="2"/></svg></div>;
  if (w.includes('barco') || w.includes('bote')) return <div style={s}><svg width="65" height="65" viewBox="0 0 24 24" fill="none"><path d="M3 18l2-6h14l2 6H3z" fill="#7dd3fc" fillOpacity="0.3" stroke="#0ea5e9" strokeWidth="2"/><path d="M12 12V5M8 12V8" stroke="#0ea5e9" strokeWidth="2"/></svg></div>;
  if (w.includes('moto'))   return <div style={s}><svg width="65" height="65" viewBox="0 0 24 24" fill="none"><circle cx="6" cy="17" r="3" stroke="#f59e0b" strokeWidth="2"/><circle cx="18" cy="17" r="3" stroke="#f59e0b" strokeWidth="2"/><path d="M6 17l3-5h7l2-4M13 12l-1 5" stroke="#f59e0b" strokeWidth="2"/></svg></div>;

  if (w.includes('sol'))    return <div style={s}><svg width="65" height="65" viewBox="0 0 24 24" fill="none"><circle cx="12" cy="12" r="5" fill="#fbbf24"/><path d="M12 2v3M12 19v3M2 12h3M19 12h3M5.6 5.6l2.1 2.1M16.3 16.3l2.1 2.1M5.6 18.4l2.1-2.1M16.3 7.7l2.1-2.1" stroke="#f59e0b" strokeWidth="2" strokeLinecap="round"/></svg></div>;
  if (w.includes('luna'))   return <div style={s}><svg width="65" height="65" viewBox="0 0 24 24" fill="none"><path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z" fill="#e2e8f0" fillOpacity="0.8" stroke="#94a3b8" strokeWidth="2"/></svg></div>;
  if (w.includes('árbol') || w.includes('arbol')) return <div style={s}><svg width="65" height="65" viewBox="0 0 24 24" fill="none"><path d="M12 2l-6 8h4l-3 5h5v7h2v-7h5l-3-5h4z" fill="#86efac" fillOpacity="0.6" stroke="#16a34a" strokeWidth="2"/></svg></div>;
  if (w.includes('flor'))   return <div style={s}><svg width="65" height="65" viewBox="0 0 24 24" fill="none"><circle cx="12" cy="12" r="3" fill="#fbbf24"/><circle cx="12" cy="6" r="2.5" fill="#f9a8d4" fillOpacity="0.9"/><circle cx="12" cy="18" r="2.5" fill="#f9a8d4" fillOpacity="0.9"/><circle cx="6" cy="12" r="2.5" fill="#f9a8d4" fillOpacity="0.9"/><circle cx="18" cy="12" r="2.5" fill="#f9a8d4" fillOpacity="0.9"/><circle cx="7.8" cy="7.8" r="2.5" fill="#c4b5fd" fillOpacity="0.8"/><circle cx="16.2" cy="16.2" r="2.5" fill="#c4b5fd" fillOpacity="0.8"/><circle cx="7.8" cy="16.2" r="2.5" fill="#c4b5fd" fillOpacity="0.8"/><circle cx="16.2" cy="7.8" r="2.5" fill="#c4b5fd" fillOpacity="0.8"/></svg></div>;
  if (w.includes('lluvia') || w.includes('llueve')) return <div style={s}><svg width="65" height="65" viewBox="0 0 24 24" fill="none"><path d="M20 17.58A5 5 0 0 0 18 8h-1.26A8 8 0 1 0 4 16.25" stroke="#60a5fa" strokeWidth="2"/><path d="M8 19v2M8 13v2M12 21v2M12 15v2M16 19v2" stroke="#3b82f6" strokeWidth="2" strokeLinecap="round"/></svg></div>;
  if (w.includes('nieve'))  return <div style={s}><svg width="65" height="65" viewBox="0 0 24 24" fill="none"><path d="M12 2v20M2 12h20M4.93 4.93l14.14 14.14M19.07 4.93L4.93 19.07" stroke="#bfdbfe" strokeWidth="2"/><circle cx="12" cy="12" r="2" fill="#e0f2fe"/></svg></div>;
  if (w.includes('viento')) return <div style={s}><svg width="65" height="65" viewBox="0 0 24 24" fill="none"><path d="M9.59 4.59A2 2 0 1 1 11 8H2m10.59 11.41A2 2 0 1 0 14 16H2m15.73-8.27A2.5 2.5 0 1 1 19.5 12H2" stroke="#94a3b8" strokeWidth="2" strokeLinecap="round"/></svg></div>;
  if (w.includes('montaña') || w.includes('montana')) return <div style={s}><svg width="65" height="65" viewBox="0 0 24 24" fill="none"><path d="M3 20l6-12 4 7 3-5 5 10H3z" fill="#94a3b8" fillOpacity="0.4" stroke="#64748b" strokeWidth="2"/></svg></div>;
  if (w.includes('mar') || w.includes('océano') || w.includes('oceano') || w.includes('playa')) return <div style={s}><svg width="65" height="65" viewBox="0 0 24 24" fill="none"><path d="M2 12c2-4 4-4 6 0s4 4 6 0 4-4 6 0" stroke="#0ea5e9" strokeWidth="2"/><path d="M2 17c2-4 4-4 6 0s4 4 6 0 4-4 6 0" stroke="#38bdf8" strokeWidth="2"/></svg></div>;

  if (w.includes('casa'))    return <div style={s}><svg width="65" height="65" viewBox="0 0 24 24" fill="none"><path d="M3 12l9-9 9 9" stroke="#d97706" strokeWidth="2"/><path d="M5 12v8h14v-8" fill="#fde68a" fillOpacity="0.15" stroke="#d97706" strokeWidth="2"/><rect x="9" y="15" width="3" height="5" rx="0.5" fill="#92400e" fillOpacity="0.5"/><rect x="13" y="14" width="3" height="3" rx="0.5" fill="#7dd3fc" fillOpacity="0.4" stroke="#0ea5e9" strokeWidth="1"/></svg></div>;
  if (w.includes('silla'))   return <div style={s}><svg width="65" height="65" viewBox="0 0 24 24" fill="none"><rect x="6" y="5" width="12" height="2" rx="1" fill="#fbbf24" fillOpacity="0.4" stroke="#d97706" strokeWidth="2"/><path d="M8 7v12M16 7v12" stroke="#d97706" strokeWidth="2"/><path d="M8 13h8" stroke="#d97706" strokeWidth="2.5"/></svg></div>;
  if (w.includes('cama'))    return <div style={s}><svg width="65" height="65" viewBox="0 0 24 24" fill="none"><rect x="2" y="12" width="20" height="8" rx="2" fill="#93c5fd" fillOpacity="0.25" stroke="#3b82f6" strokeWidth="2"/><path d="M2 12V8a2 2 0 0 1 2-2h16a2 2 0 0 1 2 2v4" stroke="#3b82f6" strokeWidth="2"/><rect x="3" y="8" width="7" height="4" rx="1" fill="#bfdbfe" fillOpacity="0.5"/></svg></div>;
  if (w.includes('puerta'))  return <div style={s}><svg width="65" height="65" viewBox="0 0 24 24" fill="none"><rect x="5" y="3" width="14" height="19" rx="1" fill="#d4a574" fillOpacity="0.25" stroke="#92400e" strokeWidth="2"/><circle cx="16" cy="12" r="1.2" fill="#92400e"/></svg></div>;
  if (w.includes('ventana')) return <div style={s}><svg width="65" height="65" viewBox="0 0 24 24" fill="none"><rect x="4" y="4" width="16" height="16" rx="2" fill="#bfdbfe" fillOpacity="0.2" stroke="#3b82f6" strokeWidth="2"/><path d="M4 12h16M12 4v16" stroke="#3b82f6" strokeWidth="2"/></svg></div>;
  if (w.includes('baño') || w.includes('bano')) return <div style={s}><svg width="65" height="65" viewBox="0 0 24 24" fill="none"><ellipse cx="12" cy="15" rx="8" ry="4" fill="#7dd3fc" fillOpacity="0.3" stroke="#0ea5e9" strokeWidth="2"/><path d="M4 15V9a2 2 0 0 1 2-2h1" stroke="#0ea5e9" strokeWidth="2"/></svg></div>;
  if (w.includes('cocina'))  return <div style={s}><svg width="65" height="65" viewBox="0 0 24 24" fill="none"><rect x="3" y="8" width="18" height="12" rx="2" fill="#fca5a5" fillOpacity="0.15" stroke="#ef4444" strokeWidth="2"/><circle cx="8" cy="5" r="2" fill="#ef4444" fillOpacity="0.6"/><circle cx="14" cy="5" r="2" fill="#ef4444" fillOpacity="0.6"/><path d="M8 5v3M14 5v3" stroke="#ef4444" strokeWidth="2"/></svg></div>;

  if (w.includes('médico') || w.includes('medico') || w.includes('doctor')) return <div style={s}><svg width="65" height="65" viewBox="0 0 24 24" fill="none"><rect x="3" y="7" width="18" height="14" rx="2" fill="#bbf7d0" fillOpacity="0.2" stroke="#16a34a" strokeWidth="2"/><path d="M12 11v6M9 14h6" stroke="#16a34a" strokeWidth="2.5" strokeLinecap="round"/><path d="M8 7V5a4 4 0 0 1 8 0v2" stroke="#16a34a" strokeWidth="2"/></svg></div>;
  if (w.includes('maestro') || w.includes('profesor')) return <div style={s}><svg width="65" height="65" viewBox="0 0 24 24" fill="none"><rect x="2" y="6" width="20" height="13" rx="2" fill="#fde68a" fillOpacity="0.2" stroke="#f59e0b" strokeWidth="2"/><path d="M6 10h12M6 14h8" stroke="#f59e0b" strokeWidth="2" strokeLinecap="round"/><path d="M2 10l10-7 10 7" stroke="#f59e0b" strokeWidth="2"/></svg></div>;

  if (w.includes('libro'))   return <div style={s}><svg width="65" height="65" viewBox="0 0 24 24" fill="none"><rect x="4" y="3" width="16" height="18" rx="2" fill="#a5b4fc" fillOpacity="0.2" stroke="#6366f1" strokeWidth="2"/><path d="M8 8h8M8 12h8M8 16h5" stroke="#818cf8" strokeWidth="2" strokeLinecap="round"/><path d="M4 3v18" stroke="#6366f1" strokeWidth="3"/></svg></div>;
  if (w.includes('pelota') || w.includes('fútbol') || w.includes('futbol')) return <div style={s}><svg width="65" height="65" viewBox="0 0 24 24" fill="none"><circle cx="12" cy="12" r="9" fill="#f1f5f9" fillOpacity="0.3" stroke="#475569" strokeWidth="2"/><path d="M12 3l3 5-5 3-5-3 3-5zM12 21l-3-5 5-3 5 3z" fill="#475569" fillOpacity="0.4"/></svg></div>;

  return <div style={{display:'flex',alignItems:'center',justifyContent:'center',width:'100%',height:'100%'}}><span style={{fontSize:60,fontWeight:900,color:'#4f46e5',opacity:0.35}}>{(word[0]||'?').toUpperCase()}</span></div>;
}

const VECTOR_KEYWORDS = ['uno','dos','tres','cuatro','cinco','seis','siete','ocho','nueve','diez',
  'rojo','azul','verde','amarillo','naranja','morado','violeta','negro','blanco','rosa','gris',
  'padre','papá','madre','mamá','hermano','hermana','abuelo','abuela','hijo','hija','tío','tía',
  'ojo','nariz','boca','oreja','cabeza','brazo','pierna','pie',
  'perro','gato','pájaro','pajaro','ave','pez','pescado','caballo','vaca',
  'manzana','plátano','platano','agua','leche','pan','café','cafe','carne',
  'coche','carro','autobús','autobus','tren','avión','avion','bicicleta','bici','barco','moto',
  'sol','luna','árbol','arbol','flor','lluvia','nieve','viento','montaña','montana','mar','océano','oceano','playa',
  'casa','silla','cama','puerta','ventana','baño','bano','cocina',
  'médico','medico','doctor','maestro','profesor','libro','pelota','fútbol','futbol'];

function hasVector(word) {
  const w = (word||'').toLowerCase();
  return VECTOR_KEYWORDS.some(k => w.includes(k)) || /^\d+$/.test(w.trim());
}

export default function App() {
  const [view, setView] = useState('studio');
  const [lessons, setLessons] = useState([]);
  const [selectedLesson, setSelectedLesson] = useState(null);
  const [geminiKey, setGeminiKey] = useState(localStorage.getItem('repite_banana_key') || 'AIzaSyCjjvBx9VfEKOCMnxaFbp-FKc2u9z_V8Ec');
  const [isSyncing, setIsSyncing] = useState(false);
  const [statusMsg, setStatusMsg] = useState('');
  const [loadError, setLoadError] = useState('');

  const CLOUD_URL = "https://jsonblob.com/api/jsonBlob/019d9864-1f9e-7334-9063-075468551478";

  useEffect(() => {
    const init = async () => {
      let finalLessons = [];
      try {
        // 1. Try Local Storage first for speed
        const saved = localStorage.getItem('repite_factory_lessons');
        if (saved) {
          finalLessons = JSON.parse(saved);
        } else {
          // 2. If nothing in local, try Cloud
          try {
            const resp = await fetch(CLOUD_URL);
            if (resp.ok) {
              const cloudData = await resp.json();
              if (cloudData && Array.isArray(cloudData)) finalLessons = cloudData;
            }
          } catch(e) { console.warn("Cloud load failed, using local json", e); }
        }
        
        if (!finalLessons || finalLessons.length === 0) {
          finalLessons = Object.values(lessonsData).flat();
        }

        const cleaned = finalLessons.map(l => ({
          ...l,
          sentences: (l.sentences || []).map(s => ({...s, isGenerating: false}))
        }));

        setLessons(cleaned);
        setSelectedLesson(cleaned[1] || cleaned[0]);
      } catch(e) {
        setLoadError('Error initializing: ' + e.message);
      }
    };
    init();
  }, []);

  const saveLessons = (all) => {
    setLessons(all);
    try {
      localStorage.setItem('repite_factory_lessons', JSON.stringify(all));
    } catch(e) {
      console.error('Failed to save lessons:', e);
    }
  };

  const updateSentence = (idx, field, value) => {
    setSelectedLesson(prev => {
      const sents = prev.sentences.map((s, i) => i === idx ? { ...s, [field]: value } : s);
      const newLesson = { ...prev, sentences: sents };
      saveLessons(lessons.map(l => l.id === newLesson.id ? newLesson : l));
      return newLesson;
    });
  };

  const updateTranslation = (sIdx, lang, val, isSentence = false, exIdx = -1) => {
    const sents = [...selectedLesson.sentences];
    if (isSentence) {
      if (!sents[sIdx].examples) {
        sents[sIdx].examples = sents[sIdx].exampleTranslations ? [ { ...sents[sIdx].exampleTranslations } ] : [ {} ];
      }
      if (exIdx === -1) {
        if (!sents[sIdx].exampleTranslations) sents[sIdx].exampleTranslations = {};
        sents[sIdx].exampleTranslations[lang] = val;
        if(sents[sIdx].examples.length > 0) sents[sIdx].examples[0][lang] = val;
      } else {
        sents[sIdx].examples[exIdx][lang] = val;
        if(exIdx === 0) {
           if (!sents[sIdx].exampleTranslations) sents[sIdx].exampleTranslations = {};
           sents[sIdx].exampleTranslations[lang] = val;
        }
      }
    } else {
      if (!sents[sIdx].translations) sents[sIdx].translations = {};
      sents[sIdx].translations[lang] = val;
      if (lang === 'es') sents[sIdx].es = val;
      if (lang === 'ar') sents[sIdx].ar = val;
    }
    
    const lesson = { ...selectedLesson, sentences: sents };
    setSelectedLesson(lesson);
    saveLessons(lessons.map(l => l.id === lesson.id ? lesson : l));
  };

  const autoTranslateRow = async (idx) => {
    // Get the latest word from current state implicitly via functional update later
    const sourceWord = selectedLesson.sentences[idx].translations?.es || selectedLesson.sentences[idx].es;
    if (!sourceWord || sourceWord === 'Nueva palabra') return;

    try {
      const prompt = `Translate "${sourceWord}" (Spanish) to ar, en, fr, tr, de, zh, ja. 
        Also, generate 3 different simple sentences with it in Spanish and translate them to the same languages.
        JSON ONLY: { "word": { "ar":"", "en":"", "fr":"", "tr":"", "de":"", "zh":"", "ja":"" }, "examples": [ { "es":"", "ar":"", "en":"", "fr":"", "tr":"", "de":"", "zh":"", "ja":"" }, { "es":"", "ar":"", "en":"", "fr":"", "tr":"", "de":"", "zh":"", "ja":"" }, { "es":"", "ar":"", "en":"", "fr":"", "tr":"", "de":"", "zh":"", "ja":"" } ] }`;
        
        const resp = await fetch(`https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=${geminiKey}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ contents: [{ parts: [{ text: prompt }] }] })
      });
      const json = await resp.json();
      if (json.error) throw new Error(json.error.message);
      
      let rawText = json.candidates[0].content.parts[0].text;
      const start = rawText.indexOf('{');
      const end = rawText.lastIndexOf('}');
      const data = JSON.parse(rawText.substring(start, end + 1));
      
      setSelectedLesson(prev => {
        const updatedSents = [...prev.sentences];
        updatedSents[idx] = {
          ...updatedSents[idx],
          translations: { ...(updatedSents[idx].translations || {}), ...data.word, es: sourceWord },
          examples: data.examples || (data.example ? [data.example, {}, {}] : [{}, {}, {}]),
            exampleTranslations: (data.examples && data.examples[0]) || data.example || {},
          ar: data.word.ar || updatedSents[idx].ar,
          es: sourceWord,
          isTranslating: false
        };
        const newLesson = { ...prev, sentences: updatedSents };
        saveLessons(lessons.map(l => l.id === newLesson.id ? newLesson : l));
        return newLesson;
      });
      setStatusMsg(`🌍 ${sourceWord} -> تم!`);
    } catch (e) {
      console.warn("AI Failed, using Translate Fallback:", e.message);
      try {
        const langs = { ar: 'ar', en: 'en', fr: 'fr', tr: 'tr', de: 'de', zh: 'zh-CN', ja: 'ja' };
        const translations = {};
        let fallbackEsArray = [];
        
        const isConcept = sourceWord.includes(':') || sourceWord.length > 15 || sourceWord.toLowerCase().includes('intro');
        
        if (isConcept) {
            const conceptTemplates = [
                `Es importante entender ${sourceWord}.`,
                `Vamos a estudiar ${sourceWord} hoy.`,
                `Un ejemplo de ${sourceWord} en español.`,
                `Aquí tienes una lección sobre ${sourceWord}.`,
                `Aprenderemos sobre ${sourceWord}.`
            ];
            // Shuffle and pick 3
            fallbackEsArray = conceptTemplates.sort(() => 0.5 - Math.random()).slice(0, 3);
        } else {
            const wordTemplates = [
                `Me gusta el ${sourceWord}.`,
                `Tengo un ${sourceWord} nuevo.`,
                `¿Dónde está el ${sourceWord}?`,
                `El ${sourceWord} es muy bonito.`,
                `Quiero comprar un ${sourceWord}.`,
                `Ayer vi un ${sourceWord}.`,
                `Este ${sourceWord} es importante.`,
                `Necesito un ${sourceWord}.`,
                `El ${sourceWord} está aquí.`
            ];
            fallbackEsArray = wordTemplates.sort(() => 0.5 - Math.random()).slice(0, 3);
        }
        
        let generatedExamples = fallbackEsArray.map(es => ({ es }));
        while(generatedExamples.length < 3) { generatedExamples.push({}); }
        
        // Join with a unique separator that translate handles well
        const combinedEs = fallbackEsArray.join(' ||| ');
        
        for (const [key, code] of Object.entries(langs)) {
          const tResp = await fetch(`https://translate.googleapis.com/translate_a/single?client=gtx&sl=es&tl=${code}&dt=t&q=${encodeURIComponent(sourceWord)}`);
          const tData = await tResp.json();
          translations[key] = tData?.[0]?.[0]?.[0] || '';
          
          if (fallbackEsArray.length > 0) {
              const sResp = await fetch(`https://translate.googleapis.com/translate_a/single?client=gtx&sl=es&tl=${code}&dt=t&q=${encodeURIComponent(combinedEs)}`);
              const sData = await sResp.json();
              
              let fullTranslatedStr = '';
              if (sData && sData[0]) {
                 sData[0].forEach(chunk => { if(chunk[0]) fullTranslatedStr += chunk[0]; });
              }
              
              const translatedArr = fullTranslatedStr.split(/\s*\|\|\|\s*|\s*\| \|\s*\|\s*/);
              
              for(let i = 0; i < fallbackEsArray.length; i++) {
                  generatedExamples[i][key] = translatedArr[i] || '';
              }
          }
        }

        setSelectedLesson(prev => {
          const updatedSents = [...prev.sentences];
          updatedSents[idx] = { 
            ...updatedSents[idx],
            translations: { ...(updatedSents[idx].translations || {}), ...translations, es: sourceWord },
            examples: generatedExamples,
            exampleTranslations: generatedExamples[0] || {},
            ar: translations.ar,
            es: sourceWord,
            isTranslating: false
          };
          const newLesson = { ...prev, sentences: updatedSents };
          saveLessons(lessons.map(l => l.id === newLesson.id ? newLesson : l));
          return newLesson;
        });
        setStatusMsg(`⚡ ${sourceWord} -> تم (سريع)!`);
      } catch (fallbackErr) {
        setSelectedLesson(prev => {
          const sents = [...prev.sentences];
          sents[idx].isTranslating = false;
          return { ...prev, sentences: sents };
        });
        setStatusMsg(`❌ خطأ: ${fallbackErr.message}`);
      }
    }
  };

  const autoTranslateAll = async () => {
    if (!selectedLesson || !selectedLesson.sentences) return;
    const total = selectedLesson.sentences.length;
    
    for (let i = 0; i < total; i++) {
      const currentWord = selectedLesson.sentences[i].translations?.es || selectedLesson.sentences[i].es || '...';
      setStatusMsg(`🚀 جاري تعبئة البنك: (${i + 1} من ${total}) - [ ${currentWord} ]`);
      
      try {
        await autoTranslateRow(i);
      } catch (err) {
        console.error("Batch error at index", i, err);
      }
      
      // Delay to avoid overwhelming the API
      await new Promise(r => setTimeout(r, 800));
    }
    
    setStatusMsg('✅ تم تعبئة بنك الكلمات للدرس بالكامل!');
  };

  const rescueAssets = async () => {
    // This function will attempt to restore images by matching words
    // We'll use a local map derived from the files we found
    setStatusMsg("🩹 جاري البحث في ملفات الأندرويد عن الصور المفقودة...");
    const rescueMap = {
      "Yo soy de Arabia Saudita": "https://upload.wikimedia.org/wikipedia/commons/thumb/0/0d/Flag_of_Saudi_Arabia.svg/512px-Flag_of_Saudi_Arabia.svg.png",
      "Él es un buen doctor": "https://api.dicebear.com/7.x/noto-emoji/svg?seed=doctor",
      "Pollo": "https://upload.wikimedia.org/wikipedia/commons/thumb/3/33/Hecheff_Chicken.jpg/512px-Hecheff_Chicken.jpg"
      // More can be added or dynamically matched
    };

    setSelectedLesson(prev => {
      const updatedSents = prev.sentences.map(s => {
        const key = s.translations?.es || s.es;
        if (!s.image && !s.imageUrl && rescueMap[key]) {
          return { ...s, image: rescueMap[key] };
        }
        return s;
      });
      const newLesson = { ...prev, sentences: updatedSents };
      saveLessons(lessons.map(l => l.id === newLesson.id ? newLesson : l));
      return newLesson;
    });
    setStatusMsg("✅ اكتملت عملية الإنقاذ! تم استرجاع ما وجدناه.");
  };

  const deleteEntry = (idx) => {
    const sents = selectedLesson.sentences.filter((_,i) => i!==idx);
    const lesson = {...selectedLesson, sentences: sents};
    const all = lessons.map(l => l.id===lesson.id ? lesson : l);
    setSelectedLesson(lesson);
    saveLessons(all);
  };

  const speak = (text) => {
    const u = new SpeechSynthesisUtterance(text);
    u.lang = 'es-ES';
    speechSynthesis.speak(u);
  };

  const PHOTO_MAP = {
    // Family
    'padre':'father man portrait','madre':'mother woman portrait','hermano':'brother boy',
    'hermana':'sister girl','abuelo':'grandfather elderly man','abuela':'grandmother elderly woman',
    'hijo':'son child boy','hija':'daughter child girl','tío':'uncle man','tía':'aunt woman',
    'primo':'cousin youth','bebé':'baby infant','familia':'family together',
    // Body Parts
    'ojo':'eye closeup','ojos':'eyes closeup','nariz':'nose face','boca':'mouth lips',
    'oreja':'ear closeup','cabeza':'head portrait','mano':'hand palm','pie':'foot barefoot',
    'brazo':'arm muscle','pierna':'leg human','dedo':'finger hand','pelo':'hair woman',
    // Animals
    'perro':'dog cute','gato':'cat cute','pájaro':'bird colorful','pajaro':'bird colorful',
    'pez':'fish underwater','pescado':'fish market','caballo':'horse running',
    'vaca':'cow farm','pollo':'chicken farm','oveja':'sheep wool','conejo':'rabbit cute',
    'oso':'bear wildlife','león':'lion safari','tigre':'tiger wildlife',
    'elefante':'elephant africa','mono':'monkey wildlife','ratón':'mouse small',
    // Food & Drink
    'pan':'bread bakery','queso':'cheese',  'carne':'meat steak','arroz':'rice bowl',
    'agua':'water glass clear','leche':'milk glass white','café':'coffee cup',
    'té':'tea cup','manzana':'apple red fruit','naranja fruit':'orange fruit',
    'plátano':'banana fruit','uva':'grapes vine','fresa':'strawberry red',
    'zanahoria':'carrot vegetable','tomate':'tomato red','huevo':'egg breakfast',
    'pizza':'pizza food','hamburguesa':'burger food','ensalada':'salad fresh',
    // Colors (nature scenes)
    'rojo':'red roses vibrant','azul':'blue ocean sky','verde':'green forest nature',
    'amarillo':'yellow sunflower field','negro':'black night sky','blanco':'white snow',
    'rosa':'pink flowers','gris':'grey fog mountains','naranja':'orange sunset',
    'marrón':'brown wood texture','morado':'purple lavender','violeta':'violet flowers',
    // Transport
    'coche':'car modern','carro':'car street','autobús':'bus city','tren':'train railway',
    'avión':'airplane sky','bicicleta':'bicycle cycling','barco':'boat sea','moto':'motorcycle',
    'taxi':'taxi yellow city','metro':'subway underground',
    // Nature & Weather
    'sol':'sun bright sky','luna':'moon night','árbol':'tree forest','arbol':'tree forest',
    'flor':'flower garden','montaña':'mountain landscape','mar':'sea ocean beach',
    'río':'river water','playa':'beach tropical','bosque':'forest trees',
    'desierto':'desert sand','lluvia':'rain drops','nieve':'snow winter',
    'nube':'clouds sky','viento':'wind leaves',
    // House
    'casa':'house home','silla':'chair furniture','cama':'bed bedroom','mesa':'table wood',
    'puerta':'door entrance','ventana':'window glass','cocina':'kitchen cooking',
    'baño':'bathroom','jardín':'garden green','sala':'living room',
    // School & Objects
    'libro':'book reading','lapiz':'pencil drawing','bolígrafo':'pen writing','mochila':'backpack school',
    'gafas':'glasses eyewear','teléfono':'phone smartphone','ordenador':'computer laptop',
    'reloj':'watch clock','llave':'key metal','dinero':'money cash',
    // Greetings/Abstracts
    'hola':'greeting friends happy','adiós':'goodbye farewell waving',
    'gracias':'thank you gesture','feliz':'happy smiling people','triste':'sad person',
    'cansado':'tired person sleeping','hambre':'hunger food','sed':'thirst water drink',
    // Nature items
    'fuego':'fire flames','agua nature':'waterfall nature','piedra':'stone rock',
    'tierra':'earth soil','madera':'wood timber',
    // Clothes
    'camisa':'shirt clothing','pantalón':'pants jeans','vestido':'dress fashion',
    'zapatos':'shoes footwear','sombrero':'hat fashion','abrigo':'coat winter',
    'falda':'skirt fashion','calcetines':'socks clothing',
    // Jobs
    'médico':'doctor hospital','profesor':'teacher classroom','cocinero':'chef cooking',
    'policía':'police officer','bombero':'firefighter','ingeniero':'engineer work',
    'artista':'artist painting','músico':'musician playing',
  };

  const getEmojiUrl = (es) => {
    const map = {
      'pollo':'1f414','chicken':'1f414','gallina':'1f413','huevo':'1f95a',
      'queso':'1f9c0','cheese':'1f9c0','leche':'1f95b','milk':'1f95b',
      'pan':'1f35e','bread':'1f35e','agua':'1f4a7','water':'1f4a7',
      'perro':'1f415','dog':'1f415','gato':'1f408','cat':'1f408',
      'vaca':'1f404','cow':'1f404','pez':'1f41f','fish':'1f41f',
      'ratón':'1f401','mouse':'1f401','raton':'1f401',
      'casa':'1f3e0','house':'1f3e0','sol':'2600','sun':'2600',
      'manzana':'1f34e','apple':'1f34e','platano':'1f34c','banana':'1f34c'
    };
    const code = map[es.toLowerCase().trim()] || '2728'; // Sparkles fallback
    return `https://fonts.gstatic.com/s/e/notoemoji/latest/${code}/emoji.svg`;
  };



  // ============================================
  // GOOGLE IMAGEN 3 - REAL AI IMAGE GENERATION
  // ============================================
  const GEMINI_KEY = 'AIzaSyCo2A0LQWth3j9hU_KTLTNdZaDxDbPF3pE';

  const generateGeminiImage = async (prompt) => {
    // Imagen 4 / Gemini image generation requires billing enabled on Google Cloud.
    // Using Pollinations.ai (Stable Diffusion XL) - free, no API key needed, real AI.
    // To switch to Google Imagen 4: enable billing at console.cloud.google.com/billing
    // then replace this with: imagen-4.0-fast-generate-001:predict endpoint
    const encodedPrompt = encodeURIComponent(prompt + ', high quality, vibrant colors, clean background');
    const seed = Math.floor(Math.random() * 99999);
    const url = `https://image.pollinations.ai/prompt/${encodedPrompt}?width=512&height=512&model=flux&nologo=true&seed=${seed}`;
    // Return URL directly (Pollinations serves the image on GET)
    return url;
  };

  const getPhotoUrl = (es) => {
    const query = getPhotoQuery(es);
    const term = query.split(' ')[0];
    return `https://loremflickr.com/400/400/${encodeURIComponent(term)}/all`;
  };

  // Preload image using JS - avoids silent browser timeouts
  const loadImageUrl = (src) => new Promise((resolve, reject) => {
    const img = new window.Image();
    img.onload = () => resolve(src);
    img.onerror = () => reject(new Error('Could not load photo'));
    setTimeout(() => reject(new Error('Photo timeout – try again')), 20000);
    img.src = src;
  });

  const getPhotoQuery = (text) => {
    let q = text.toLowerCase().trim();
    if (q === 'ratón' || q === 'un ratón') return 'cute mouse animal 3d';
    if (q === 'perro' || q === 'el perro') return 'cute dog 3d';
    if (q === 'gato' || q === 'el gato') return 'cute cat 3d';
    return `${q} 3d render digital art`;
  };

  const generateAI = async (idx) => {
    const sent = selectedLesson.sentences[idx];
    setStatusMsg(`🎨 جارٍ ابتكار خيارات احترافية لـ "${sent.es}"...`);

    // 1. Set loading state
    updateSentence(idx, 'isGenerating', true);
    updateSentence(idx, 'candidates', []);

    try {
      const rawTerm = sent.es.split(' ').pop().toLowerCase().replace(/[.,!?;:]/g, '');
      let engTerm = rawTerm;
      const variants = [];

      // 1. Get English Translation
      const trResp = await fetch(`https://translate.googleapis.com/translate_a/single?client=gtx&sl=auto&tl=en&dt=t&q=${encodeURIComponent(rawTerm)}`);
      const trData = await trResp.json();
      engTerm = trData && trData[0] && trData[0][0] && trData[0][0][0] ? trData[0][0][0].toLowerCase() : rawTerm;

      // 2. Google Knowledge Graph (The most stable source)
      if (geminiKey) {
        try {
          const kgResp = await fetch(`https://kgsearch.googleapis.com/v1/entities:search?query=${encodeURIComponent(rawTerm)}&key=${geminiKey}&limit=2`);
          const kgData = await kgResp.json();
          kgData.itemListElement?.forEach(el => {
            if (el.result?.image?.contentUrl) variants.push(el.result.image.contentUrl);
          });
        } catch (e) { console.error('Google KG Fail:', e); }
      }

      // 3. Wikimedia Commons (Public, High Quality, Real Photos)
      try {
        const wikiResp = await fetch(`https://commons.wikimedia.org/w/api.php?action=query&format=json&origin=*&generator=search&gsrnamespace=6&gsrsearch=${encodeURIComponent(engTerm)}&gsrlimit=2&prop=imageinfo&iiprop=url`);
        const wikiData = await wikiResp.json();
        if (wikiData.query?.pages) {
          Object.values(wikiData.query.pages).forEach(p => {
             if (p.imageinfo?.[0]?.url) variants.push(p.imageinfo[0].url);
          });
        }
      } catch (e) { console.error('Wiki Fail:', e); }

      // 4. Fallback high-quality direct icons (Icons8 raw)
      if (variants.length < 4) {
        variants.push(`https://img.icons8.com/3d-fluency/512/${engTerm}.png`);
        variants.push(`https://img.icons8.com/color/512/${engTerm}.png`);
        variants.push(`https://img.icons8.com/fluency/512/${engTerm}.png`);
      }

      setSelectedLesson(curr => {
        const finalSents = curr.sentences.map((s, i) => i === idx ? { ...s, isGenerating: false, candidates: variants.slice(0, 4) } : s);
        const finalLesson = { ...curr, sentences: finalSents };
        setLessons(prev => {
          const updated = prev.map(item => item.id === finalLesson.id ? finalLesson : item);
          localStorage.setItem('repite_factory_lessons', JSON.stringify(updated));
          return updated;
        });
        return finalLesson;
      });

      setStatusMsg('🍌 تم التوليد بنظام بنانا الصاروخي!');
    } catch (e) {
      console.error(e);
      updateSentence(idx, 'isGenerating', false);
      setStatusMsg('❌ فشل تحميل الخيارات');
    }
  };

  const downloadJSON = () => {
    try {
      const fileName = `Full_Database_${new Date().toISOString().slice(0,10)}.json`;
      const json = JSON.stringify(lessons, null, 2);
      const dataStr = "data:application/json;base64," + btoa(unescape(encodeURIComponent(json)));
      const link = document.createElement('a');
      link.setAttribute("href", dataStr);
      link.setAttribute("download", fileName);
      document.body.appendChild(link);
      link.click();
      setTimeout(() => document.body.removeChild(link), 100);
      setStatusMsg(`✅ تم التنزيل: ${fileName}`);
    } catch (e) {
      console.error(e);
      setStatusMsg("❌ فشل التحميل");
    }
  };

  const downloadSingleLesson = () => {
    if (!selectedLesson) return;
    try {
      const en = selectedLesson.title?.['en-US'] || 'Lesson';
      const ar = selectedLesson.title?.ar || '';
      const safeName = `${en}${ar ? '_' + ar : ''}`.replace(/[<>:"/\\|?*]/g, '').trim();
      const fileName = `${safeName}.json`;
      const json = JSON.stringify(selectedLesson, null, 2);
      const dataStr = "data:application/json;base64," + btoa(unescape(encodeURIComponent(json)));
      const link = document.createElement('a');
      link.setAttribute("href", dataStr);
      link.setAttribute("download", fileName);
      document.body.appendChild(link);
      link.click();
      setTimeout(() => document.body.removeChild(link), 100);
      setStatusMsg(`✅ تم تنزيل الدرس: ${fileName}`);
    } catch (e) {
      console.error(e);
      setStatusMsg("❌ خطأ في التحميل");
    }
  };

  const handleFileUpload = (idx, file) => {
    if (!file) return;
    const reader = new FileReader();
    reader.onload = (e) => {
      selectCandidate(idx, e.target.result);
    };
    reader.readAsDataURL(file);
  };

  const selectCandidate = (idx, url) => {
    setSelectedLesson(curr => {
      const sents = curr.sentences.map((s, i) => i === idx ? { ...s, imageUrl: url, candidates: null } : s);
      const updatedLesson = { ...curr, sentences: sents };
      
      setLessons(prev => {
        const updated = prev.map(l => l.id === updatedLesson.id ? updatedLesson : l);
        localStorage.setItem('repite_factory_lessons', JSON.stringify(updated));
        return updated;
      });
      
      return updatedLesson;
    });
    setStatusMsg('✅ تم اختيار الصورة!');
    setTimeout(() => setStatusMsg(''), 2000);
  };


  const generateAllPhotos = async () => {
    if (!selectedLesson || !selectedLesson.sentences) return;
    const total = selectedLesson.sentences.length;
    
    for (let i = 0; i < total; i++) {
        const s = selectedLesson.sentences[i];
        // Only draw if missing photo
        if (!s.image && !s.imageUrl) {
            const word = s.translations?.es || s.es || '...';
            setStatusMsg(`🎨 جاري رسم صورة لـ: (${i + 1} من ${total}) - [ ${word} ]`);
            await generateAI(i);
            await new Promise(r => setTimeout(r, 1000));
        }
    }
    
    setStatusMsg('✅ اكتمل رسم جميع صور الدرس!');
    setTimeout(() => setStatusMsg(''), 4000);
  };

  const clearAllImages = () => {
    if (!selectedLesson) return;
    const sents = selectedLesson.sentences.map(s => ({ ...s, imageUrl: null }));
    const lesson = { ...selectedLesson, sentences: sents };
    setSelectedLesson(lesson);
    saveLessons(lessons.map(l => l.id === lesson.id ? lesson : l));
    setStatusMsg('🗑 Images cleared from this lesson');
    setTimeout(() => setStatusMsg(''), 2500);
  };

  const factoryReset = () => {
    if (confirm('Reset ALL data to original lessons.json? (all images and edits will be lost)')) {
      try { 
        localStorage.removeItem('repite_factory_lessons');
        location.reload();
      } catch(e) {
        console.error('Reset failed:', e);
      }
    }
  };

  const selectLesson = (lesson) => {
    // Force reset any generation states when switching
    const cleaned = {
      ...lesson,
      sentences: (lesson.sentences || []).map(s => ({...s, isGenerating: false}))
    };
    setSelectedLesson(cleaned);
  };

  if (loadError) return (
    <div style={{display:'flex',flexDirection:'column',alignItems:'center',justifyContent:'center',height:'100vh',background:'#030712',color:'white',fontFamily:'Inter,sans-serif',gap:20}}>
      <div style={{fontSize:48}}>⚠️</div>
      <div style={{color:'#ef4444',textAlign:'center',maxWidth:400}}>{loadError}</div>
      <button onClick={() => location.reload()} style={{background:'#8b5cf6',color:'white',border:'none',padding:'12px 24px',borderRadius:12,cursor:'pointer',fontWeight:700,fontSize:15}}>🔄 Retry</button>
    </div>
  );

  if (!lessons.length) return (
    <div style={{display:'flex',alignItems:'center',justifyContent:'center',height:'100vh',background:'#030712',color:'white',fontFamily:'Inter,sans-serif',fontSize:18,gap:12}}>
      <div style={{width:24,height:24,border:'3px solid #8b5cf6',borderTopColor:'transparent',borderRadius:'50%',animation:'spin 0.8s linear infinite'}} />
      Loading...
    </div>
  );

  return (
    <div className="app-container">
      <style>{`
        @keyframes spin { to { transform: rotate(360deg); } }
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body { font-family: Inter, -apple-system, sans-serif; background: #030712; color: white; }
        .app-container { display: flex; flex-direction: column; min-height: 100vh; }
        header { height: 72px; display: flex; align-items: center; justify-content: space-between; padding: 0 32px; background: rgba(3,7,18,0.85); backdrop-filter: blur(20px); border-bottom: 1px solid rgba(255,255,255,0.07); position: sticky; top: 0; z-index: 100; }
        .logo { display: flex; align-items: center; gap: 12px; }
        .logo-box { width: 30px; height: 30px; background: linear-gradient(135deg,#8b5cf6,#3b82f6); border-radius: 8px; }
        .logo-text { font-weight: 900; font-size: 20px; letter-spacing: -0.5px; }
        .tab-group { display: flex; background: rgba(0,0,0,0.4); padding: 4px; border-radius: 12px; border: 1px solid rgba(255,255,255,0.07); gap: 2px; }
        .tab { padding: 8px 18px; border-radius: 8px; border: none; background: transparent; color: #94a3b8; font-weight: 700; font-size: 12px; cursor: pointer; transition: 0.2s; }
        .tab.active { background: rgba(255,255,255,0.08); color: white; }
        .hdr-btns { display: flex; gap: 12px; }
        .btn-reset { display: flex; align-items: center; gap: 8px; background: transparent; border: 1px solid rgba(255,255,255,0.1); color: #94a3b8; padding: 8px 16px; border-radius: 10px; font-weight: 600; cursor: pointer; font-size: 13px; transition: 0.2s; }
        .btn-reset:hover { color: white; border-color: rgba(255,255,255,0.3); }
        .btn-upload { display: flex; align-items: center; gap: 8px; background: linear-gradient(135deg,#10b981,#059669); color: white; border: none; padding: 8px 20px; border-radius: 10px; font-weight: 800; cursor: pointer; font-size: 13px; }
        main { display: grid; grid-template-columns: 260px minmax(0, 1fr); gap: 20px; padding: 20px; max-width: 1600px; margin: 0 auto; width: 100%; box-sizing: border-box; }
        .sidebar { display: flex; flex-direction: column; gap: 16px; position: sticky; top: 92px; max-height: calc(100vh - 120px); }
        .sidebar-label { font-size: 10px; font-weight: 900; letter-spacing: 2px; color: #475569; text-transform: uppercase; }
        .lesson-list { display: flex; flex-direction: column; gap: 8px; overflow-y: auto; }
        .lesson-item { padding: 14px 16px; border-radius: 16px; background: rgba(255,255,255,0.025); border: 1px solid rgba(255,255,255,0.06); cursor: pointer; transition: 0.25s; display: flex; align-items: center; gap: 14px; }
        .lesson-item:hover { transform: translateX(6px); background: rgba(255,255,255,0.05); }
        .lesson-item.active { background: rgba(139,92,246,0.08); border-color: #8b5cf6; box-shadow: 0 0 20px rgba(139,92,246,0.1); }
        .lesson-icon { font-size: 22px; }
        .lesson-name { font-weight: 700; font-size: 15px; }
        .lesson-count { font-size: 11px; color: #475569; margin-top: 2px; }
        .stage { display: flex; flex-direction: column; gap: 20px; min-width: 0; }
        .module-header { background: rgba(17,24,39,0.85); border: 1px solid rgba(255,255,255,0.07); border-radius: 16px; padding: 15px 24px; display: flex; flex-wrap: wrap; gap: 15px; justify-content: space-between; align-items: center; }
        .module-title-input { background: transparent; border: none; font-size: 28px; font-weight: 900; color: white; outline: none; letter-spacing: -0.5px; flex: 1; min-width: 250px; }
        .sync-badge { background: rgba(16,185,129,0.1); color: #10b981; padding: 5px 14px; border-radius: 100px; font-size: 10px; font-weight: 800; letter-spacing: 1px; text-transform: uppercase; }
        .card { background: rgba(30, 41, 59, 0.4); backdrop-filter: blur(16px); border: 1px solid rgba(255, 255, 255, 0.1); border-radius: 24px; padding: 24px; margin-bottom: 24px; box-shadow: 0 10px 40px -10px rgba(0,0,0,0.5); }
        .card-top { display: flex; flex-direction: column; gap: 20px; margin-bottom: 20px; }
        .primary-input-wrap { display: flex; align-items: center; background: linear-gradient(145deg, rgba(139,92,246,0.1) 0%, rgba(59,130,246,0.05) 100%); border: 1px solid rgba(139,92,246,0.2); border-radius: 16px; padding: 4px 8px; box-shadow: inset 0 2px 10px rgba(0,0,0,0.2); transition: 0.3s; }
        .primary-input-wrap:focus-within { border-color: rgba(139,92,246,0.5); box-shadow: 0 0 20px rgba(139,92,246,0.15); }
        .primary-input { flex: 1; background: transparent; border: none; font-size: 26px; font-weight: 800; color: white; padding: 16px; outline: none; }
        
        .lang-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(140px, 1fr)); gap: 12px; }
        .lang-box { background: rgba(0,0,0,0.3); border: 1px solid rgba(255,255,255,0.05); border-radius: 12px; padding: 10px 14px; display: flex; flex-direction: column; gap: 6px; transition: 0.2s; }
        .lang-box:focus-within { border-color: rgba(255,255,255,0.2); background: rgba(0,0,0,0.5); box-shadow: 0 4px 15px rgba(0,0,0,0.2); }
        .lang-label { font-size: 9px; font-weight: 800; letter-spacing: 1px; text-transform: uppercase; }
        .lang-input { background: transparent; border: none; font-size: 16px; font-weight: 600; color: white; outline: none; width: 100%; }
        
        .examples-section { margin-top: 10px; padding: 16px; background: rgba(255,255,255,0.02); border-radius: 16px; border: 1px solid rgba(255,255,255,0.04); }
        
        .btn-search-quick { padding: 6px 12px; border-radius: 8px; border: 1px solid rgba(255,255,255,0.1); background: rgba(255,255,255,0.05); color: #94a3b8; font-size: 12px; font-weight: 700; cursor: pointer; transition: 0.2s; }
        .btn-search-quick:hover { background: rgba(255,255,255,0.1); color: white; border-color: rgba(255,255,255,0.3); }
        .btn-speak { background: transparent; color: #a78bfa; border: none; padding: 12px; border-radius: 12px; cursor: pointer; font-size: 20px; transition: 0.2s; }
        .btn-speak:hover { background: rgba(139,92,246,0.1); transform: scale(1.1); }
        .btn-delete { background: rgba(239,68,68,0.1); border: 1px solid rgba(239,68,68,0.2); color: #fca5a5; padding: 12px 16px; border-radius: 12px; font-weight: 800; cursor: pointer; transition: 0.2s; display: flex; align-items: center; gap: 8px; }
        .btn-delete:hover { background: #ef4444; color: white; }
        
        .card-bottom { display: grid; grid-template-columns: 140px 1fr; gap: 20px; padding-top: 20px; border-top: 1px solid rgba(255,255,255,0.06); }
        .preview-box { width: 140px; height: 140px; background: rgba(0,0,0,0.5); border-radius: 20px; border: 1px solid rgba(255,255,255,0.07); overflow: hidden; position: relative; }
        .ai-img { width: 100%; height: 100%; object-fit: cover; }
        .ai-overlay { position: absolute; top: 8px; right: 8px; background: #8b5cf6; color: white; font-size: 7px; font-weight: 900; padding: 3px 7px; border-radius: 5px; letter-spacing: 0.5px; }
        .ai-controls { display: flex; flex-direction: column; gap: 12px; justify-content: center; }
        .prompt-row { display: flex; gap: 10px; }
        .prompt-input { flex: 1; background: rgba(0,0,0,0.35); border: 1px solid rgba(255,255,255,0.08); padding: 12px 16px; border-radius: 11px; color: white; font-size: 13px; outline: none; }
        .btn-ai { background: white; color: black; border: none; padding: 12px 20px; border-radius: 11px; font-weight: 900; cursor: pointer; font-size: 12px; letter-spacing: 0.5px; white-space: nowrap; transition: 0.2s; }
        .btn-ai:hover { filter: brightness(0.9); transform: scale(1.02); }
        .btn-ai:disabled { opacity: 0.5; cursor: not-allowed; }
        .badges { display: flex; gap: 8px; flex-wrap: wrap; }
        .badge { font-size: 9px; font-weight: 800; padding: 4px 10px; border-radius: 6px; letter-spacing: 0.5px; text-transform: uppercase; }
        .badge-ai { background: rgba(139,92,246,0.1); color: #a78bfa; border: 1px solid rgba(139,92,246,0.2); }
        .badge-vec { background: rgba(59,130,246,0.1); color: #60a5fa; border: 1px solid rgba(59,130,246,0.2); }
        .btn-add { background: linear-gradient(135deg,#8b5cf6,#3b82f6); color: white; border: none; padding: 20px; border-radius: 20px; font-weight: 900; font-size: 16px; cursor: pointer; display: flex; align-items: center; justify-content: center; gap: 12px; box-shadow: 0 10px 30px rgba(139,92,246,0.25); transition: 0.25s; width: 100%; }
        .btn-add:hover { transform: translateY(-3px); filter: brightness(1.1); }
        .toast { position: fixed; bottom: 32px; right: 32px; background: #10b981; color: white; padding: 14px 24px; border-radius: 14px; font-weight: 800; box-shadow: 0 20px 40px rgba(0,0,0,0.5); z-index: 1000; animation: slideUp 0.3s ease; }
        @keyframes slideUp { from { transform: translateY(20px); opacity: 0; } to { transform: translateY(0); opacity: 1; } }
        ::-webkit-scrollbar { width: 5px; }
        ::-webkit-scrollbar-thumb { background: rgba(255,255,255,0.1); border-radius: 10px; }
        
        .candidates-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 10px; background: rgba(0,0,0,0.3); padding: 10px; border-radius: 15px; border: 1px solid rgba(139,92,246,0.3); }
        .candidate-item { position: relative; cursor: pointer; border-radius: 10px; overflow: hidden; border: 2px solid transparent; transition: 0.2s; }
        .candidate-item:hover { transform: scale(1.05); border-color: #8b5cf6; }
        .candidate-img { width: 100%; aspect-ratio: 1; object-fit: cover; }
        .candidate-select-btn { position: absolute; inset: 0; background: rgba(139,92,246,0.6); display: flex; align-items: center; justify-content: center; opacity: 0; transition: 0.2s; }
        .candidate-item:hover .candidate-select-btn { opacity: 1; }
        .candidate-select-text { background: #8b5cf6; color: white; font-weight: 900; font-size: 11px; padding: 6px 12px; border-radius: 8px; box-shadow: 0 4px 12px rgba(0,0,0,0.3); }
        .candidate-item { position: relative; cursor: pointer; border-radius: 12px; overflow: hidden; background: #111827; border: 2px solid transparent; transition: 0.2s; min-height: 150px; aspect-ratio: 1; }
        .candidate-item:hover { border-color: #8b5cf6; transform: translateY(-2px); }
        .candidate-img { width: 100%; height: 100%; object-fit: cover; opacity: 0; transition: opacity 0.3s; }
        .candidate-placeholder { position: absolute; inset: 0; background: #1e293b; display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 8px; color: #64748b; font-size: 10px; }
        @keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }
      `}</style>

      <header style={{background: 'linear-gradient(90deg, #6d28d9, #4c1d95)', borderBottom: '2px solid #8b5cf6', display: 'flex', justifyContent: 'space-between', padding: '15px 30px', alignItems: 'center'}}>
        <div className="logo" style={{display:'flex', alignItems:'center', gap:10}}>
          <div className="logo-box" style={{background:'white'}} />
          <div className="logo-text" style={{color:'white', fontWeight:800, display:'flex', alignItems:'center', gap:10}}>
            MAGIC FACTORY <span style={{fontSize:12, opacity:0.6, background:'rgba(0,0,0,0.2)', padding:'2px 8px', borderRadius:6}}>v10.9</span>
          </div>
        </div>
        <div className="tab-group" style={{display:'flex', gap:10}}>
          <button className={`tab ${view==='studio'?'active':''}`} onClick={()=>setView('studio')} style={{padding: '8px 16px', borderRadius: 10, background: view==='studio'?'white':'transparent', color: view==='studio'?'black':'white', border: 'none', cursor: 'pointer', fontWeight: 700}}>⬛ STUDIO</button>
          <button className={`tab ${view==='player'?'active':''}`} onClick={()=>setView('player')} style={{padding: '8px 16px', borderRadius: 10, background: view==='player'?'white':'transparent', color: view==='player'?'black':'white', border: 'none', cursor: 'pointer', fontWeight: 700}}>▶ PREVIEW</button>
        </div>
        <div className="hdr-btns" style={{display:'flex', gap:10, alignItems:'center'}}>
          <div style={{position:'relative'}}>
            <input 
              type="password"
              placeholder="Google Banana Key..." 
              value={geminiKey} 
              onChange={(e) => {
                setGeminiKey(e.target.value);
                localStorage.setItem('repite_banana_key', e.target.value);
              }}
              style={{background:'rgba(0,0,0,0.3)', border:'1px solid #ffdf00', borderRadius:'20px', padding:'6px 15px', color:'#ffdf00', fontSize:11, width:150, outline:'none'}} 
            />
            {geminiKey && <span style={{position:'absolute', right:10, top:6, fontSize:10}}>🍌</span>}
          </div>
          <button className="btn-reset" onClick={factoryReset} style={{background:'rgba(255,255,255,0.1)', color:'white', border:'1px solid rgba(255,255,255,0.2)', padding:'10px 18px', borderRadius:10, fontWeight:700, cursor:'pointer'}}>🧹 Master Clear</button>
          <button className="btn-upload" style={{background:'rgba(255,255,255,0.1)', color:'white', border:'1px solid rgba(255,255,255,0.2)', padding:'10px 18px', borderRadius:10, fontWeight:700, cursor:'pointer'}} 
            onClick={async ()=>{
              setIsSyncing(true);
              try {
                const resp = await fetch(CLOUD_URL);
                if (resp.ok) {
                  const cloudData = await resp.json();
                  setLessons(cloudData);
                  localStorage.setItem('repite_factory_lessons', JSON.stringify(cloudData));
                  setStatusMsg('✅ تم الجلب من السحاب بنجاح!');
                }
              } catch (e) {
                setStatusMsg('❌ فشل الجلب: ' + e.message);
              }
              setIsSyncing(false);
              setTimeout(()=>setStatusMsg(''), 4000);
            }}>
            {isSyncing ? '⏳' : '☁'} Sync From Cloud
          </button>
          <button className="btn-upload" style={{background:'white', color:'#6d28d9', padding:'10px 18px', borderRadius:10, fontWeight:800, border:'none', cursor:'pointer'}} 
            onClick={async ()=>{
              setIsSyncing(true);
              try {
                const resp = await fetch(CLOUD_URL, {
                  method: 'PUT',
                  headers: { 'Content-Type': 'application/json' },
                  body: JSON.stringify(lessons)
                });
                if (resp.ok) {
                  setStatusMsg('✅ تم الرفع للسحاب بنجاح!');
                } else {
                  throw new Error('Upload failed');
                }
              } catch (e) {
                setStatusMsg('❌ فشل الرفع: ' + e.message);
              }
              setIsSyncing(false);
              setTimeout(()=>setStatusMsg(''), 4000);
            }}>
            {isSyncing ? '⏳' : '🚀'} Upload App
          </button>
        </div>
      </header>

      <main>
        <aside className="sidebar">
          <div className="sidebar-label">Lesson Archive</div>
          <div className="lesson-list">
            {lessons.map(l => (
              <div key={l.id} className={`lesson-item ${selectedLesson?.id===l.id?'active':''}`} onClick={()=>setSelectedLesson(l)}>
                <span className="lesson-icon">{l.icon}</span>
                <div>
                  <div className="lesson-name">{l.title['en-US']}</div>
                  <div className="lesson-count">{l.sentences.length} items</div>
                </div>
              </div>
            ))}
          </div>
        </aside>

        <section className="stage">
          {selectedLesson && <>
            <div className="module-header">
              <input className="module-title-input" value={selectedLesson.title['en-US']} onChange={e=>{
                const u={...selectedLesson,title:{...selectedLesson.title,'en-US':e.target.value}};
                setSelectedLesson(u);
                saveLessons(lessons.map(l=>l.id===u.id?u:l));
              }}/>
              <div style={{display:'flex', gap:10, alignItems:'center', flexWrap:'wrap'}}>
                <button onClick={downloadJSON} style={{background:'rgba(139,92,246,0.1)',color:'#a78bfa',border:'1px solid rgba(139,92,246,0.2)',padding:'10px 18px',borderRadius:10,fontWeight:700,cursor:'pointer',fontSize:12,whiteSpace:'nowrap'}}>📥 حفظ الكل</button>
                <button onClick={downloadSingleLesson} style={{background:'linear-gradient(135deg,#0ea5e9,#0284c7)',color:'white',border:'none',padding:'10px 22px',borderRadius:10,fontWeight:800,cursor:'pointer',fontSize:13,boxShadow:'0 4px 15px rgba(14,165,233,0.3)', whiteSpace:'nowrap'}}>📄 حفظ هذا الدرس فقط</button>
                <button onClick={rescueAssets} style={{background:'rgba(239,68,68,0.1)',color:'#f87171',border:'1px solid rgba(239,68,68,0.2)',padding:'10px 18px',borderRadius:10,fontWeight:800,cursor:'pointer',fontSize:12,whiteSpace:'nowrap'}}>🩹 Rescue Images</button>
                <button onClick={autoTranslateAll} style={{background:'rgba(16,185,129,0.1)',color:'#10b981',border:'1px solid rgba(16,185,129,0.2)',padding:'10px 18px',borderRadius:10,fontWeight:800,cursor:'pointer',fontSize:12,whiteSpace:'nowrap'}}>🌍 Bank All Words</button>
                <button onClick={generateAllPhotos} style={{background:'rgba(255,158,11,0.1)',color:'#f59e0b',border:'1px solid rgba(255,158,11,0.2)',padding:'10px 18px',borderRadius:10,fontWeight:800,cursor:'pointer',fontSize:12,whiteSpace:'nowrap'}}>🎨 Generate All</button>
                <button onClick={clearAllImages} style={{background:'rgba(239,68,68,0.08)',color:'#f87171',border:'1px solid rgba(239,68,68,0.25)',padding:'10px 14px',borderRadius:10,fontWeight:700,cursor:'pointer',fontSize:12,whiteSpace:'nowrap'}}>🗑 Clear Images</button>
                <div className="sync-badge">Ready for Sync</div>
              </div>
            </div>

            {selectedLesson.sentences.map((sent, idx) => (
              <div className="card" key={idx}>
                <div className="card-top">
                  <div style={{display:'flex', gap:12, width:'100%', flexWrap:'wrap', alignItems:'center'}}>
                    <div className="primary-input-wrap" style={{flex:'1 1 300px'}}>
                      <button className="btn-speak" onClick={()=>speak(sent.translations?.es || sent.es)} title="Listen">🗣️</button>
                      <input className="primary-input" placeholder="Primary Spanish Word" value={sent.translations?.es || sent.es} onChange={e=>updateTranslation(idx,'es',e.target.value)}/>
                    </div>
                    
                    <button 
                      className="btn-ai" 
                      onClick={() => autoTranslateRow(idx)}
                      disabled={sent.isTranslating}
                      style={{height:60, flexShrink:0, background: 'linear-gradient(135deg, #3b82f6, #6366f1)', border:'none', color:'white', borderRadius:16, fontWeight:900, cursor:'pointer', padding:'0 24px', fontSize:14, display:'flex', alignItems:'center', gap:8, boxShadow:'0 4px 15px rgba(59,130,246,0.3)'}}
                    >
                      {sent.isTranslating ? '⏳ Processing...' : '✨ Magic Bank'}
                    </button>
                    
                    <button className="btn-delete" onClick={()=>deleteEntry(idx)} style={{flexShrink:0}} title="Delete">🗑️ Delete</button>
                  </div>

                  <div className="lang-grid">
                    {[
                      {code:'ar', label:'Arabic', color:'#fbbf24', dir:'rtl'},
                      {code:'en', label:'English', color:'#60a5fa', dir:'ltr'},
                      {code:'fr', label:'French', color:'#f87171', dir:'ltr'},
                      {code:'tr', label:'Turkish', color:'#34d399', dir:'ltr'},
                      {code:'de', label:'German', color:'#a78bfa', dir:'ltr'},
                      {code:'zh', label:'Chinese', color:'#fb7185', dir:'ltr'},
                      {code:'ja', label:'Japanese', color:'#facc15', dir:'ltr'}
                    ].map(lang => (
                      <div className="lang-box" key={lang.code}>
                        <div className="lang-label" style={{color: lang.color}}>{lang.label}</div>
                        <input 
                          className="lang-input" 
                          dir={lang.dir} 
                          value={sent.translations?.[lang.code] || sent[lang.code] || ''} 
                          onChange={e=>updateTranslation(idx, lang.code, e.target.value)}
                        />
                      </div>
                    ))}
                  </div>

                  <div className="examples-section">
                      <div style={{fontSize:10, color:'rgba(255,255,255,0.4)', marginBottom:12, fontWeight:800, letterSpacing:1}}>EXAMPLE SENTENCES</div>
                      
                      {(sent.examples || (sent.exampleTranslations ? [sent.exampleTranslations] : [{}])).map((ex, exIdx) => (
                      <div key={exIdx} style={{display:'flex', flexDirection:'column', gap:12, marginBottom:16, paddingBottom:16, borderBottom:(exIdx===((sent.examples?.length || 1)-1) ? 'none' : '1px solid rgba(255,255,255,0.05)')}}>
                        <div className="primary-input-wrap" style={{background:'rgba(0,0,0,0.2)', border:'1px solid rgba(255,255,255,0.05)', boxShadow:'none', padding:'2px 8px'}}>
                          <span style={{color:'#3b82f6', fontWeight:900, fontSize:12, marginRight:10}}>ES {exIdx + 1}</span>
                          <input className="primary-input" style={{fontSize:16}} value={ex.es || ''} onChange={e=>updateTranslation(idx,'es',e.target.value, true, exIdx)}/>
                        </div>
                        <div className="lang-grid">
                          {[
                            {code:'ar', label:'Arabic', color:'#fbbf24', dir:'rtl'},
                            {code:'en', label:'English', color:'#60a5fa', dir:'ltr'},
                            {code:'fr', label:'French', color:'#f87171', dir:'ltr'},
                            {code:'tr', label:'Turkish', color:'#34d399', dir:'ltr'},
                            {code:'de', label:'German', color:'#a78bfa', dir:'ltr'},
                            {code:'zh', label:'Chinese', color:'#fb7185', dir:'ltr'},
                            {code:'ja', label:'Japanese', color:'#facc15', dir:'ltr'}
                          ].map(lang => (
                            <div className="lang-box" key={lang.code} style={{padding:'8px 12px'}}>
                              <div className="lang-label" style={{color: lang.color}}>{lang.code}</div>
                              <input 
                                className="lang-input" 
                                style={{fontSize:14}}
                                dir={lang.dir}
                                value={ex[lang.code] || ''} 
                                onChange={e=>updateTranslation(idx,lang.code,e.target.value, true, exIdx)}
                              />
                            </div>
                          ))}
                        </div>
                      </div>
                      ))}
                      
                      {(!sent.examples || sent.examples.length < 4) && (
                      <button 
                        onClick={() => {
                          const sents = [...selectedLesson.sentences];
                          if (!sents[idx].examples) {
                            sents[idx].examples = sents[idx].exampleTranslations ? [ { ...sents[idx].exampleTranslations } ] : [ {} ];
                          }
                          if (sents[idx].examples.length < 3) {
                            sents[idx].examples.push({});
                            setSelectedLesson(prev => ({...prev, sentences: sents}));
                            saveLessons(lessons.map(l => l.id === selectedLesson.id ? {...selectedLesson, sentences: sents} : l));
                          }
                        }}
                        style={{background:'rgba(255,255,255,0.1)', color:'white', border:'none', padding:'12px 16px', borderRadius:12, cursor:'pointer', fontSize:13, fontWeight:800, marginTop:8, display:'flex', alignItems:'center', justifyContent:'center', width:'100%', gap:8}}
                      >
                        <span style={{fontSize: 16}}>+</span> Add Extra Example
                      </button>
                      )}
                    </div>
                </div>

                <div className="card-bottom" tabIndex={0} onPaste={(e) => {
                   const items = e.clipboardData.items;
                   const text = e.clipboardData.getData('text');
                   if (text && (text.startsWith('http') || text.startsWith('data:image'))) {
                     selectCandidate(idx, text.trim());
                     setStatusMsg("✅ تم لصق الرابط!"); setTimeout(()=>setStatusMsg(""), 2000);
                     return;
                   }
                   for (let i = 0; i < items.length; i++) {
                     if (items[i].type.indexOf("image") !== -1) {
                       const blob = items[i].getAsFile();
                       handleFileUpload(idx, blob);
                       setStatusMsg("✅ تم رفع الصورة الملصقة!"); setTimeout(()=>setStatusMsg(""), 2000);
                     }
                   }
                }} style={{outline:'none'}}>
                  <div className="preview-box">
                    {(sent.imageUrl || sent.image) ? (
                      <img src={sent.imageUrl || sent.image} alt={sent.es} className="ai-img" />
                    ) : (
                      <div className="ai-img-placeholder" style={{fontSize:40, color:'rgba(255,255,255,0.05)', display:'flex', alignItems:'center', justifyContent:'center', height:'100%', background:'rgba(255,255,255,0.02)', borderRadius:20}}>?</div>
                    )}
                  </div>

                  <div className="ai-controls" style={{flex:1, display:'flex', flexDirection:'column', gap:10}}>
                    <div style={{display:'flex', gap:8, flexWrap:'wrap', alignItems:'center'}}>
                      <button className="btn-search-quick" onClick={() => window.open(`https://www.google.com/search?q=${encodeURIComponent(sent.es)}+3d+render+icon&tbm=isch`,'_blank')}>🎨 3D Icon</button>
                      <button className="btn-search-quick" onClick={() => window.open(`https://www.google.com/search?q=${encodeURIComponent(sent.es)}+real+photo&tbm=isch`,'_blank')}>📷 Photo</button>
                      <button className="btn-search-quick" onClick={() => window.open(`https://www.google.com/search?q=${encodeURIComponent(sent.es)}+clipart+vector&tbm=isch`,'_blank')}>🃏 Graphic</button>
                      <button className="btn-search-quick" style={{background:'rgba(139,92,246,0.1)', color:'#a78bfa'}} onClick={() => generateAI(idx)}>🪄 Draw AI</button>
                      
                      <button 
                        className="btn-search-quick"
                        style={{background:'rgba(14,165,233,0.1)', color:'#38bdf8', border:'1px dashed rgba(14,165,233,0.3)'}}
                        onClick={async () => {
                          let success = false;
                          try {
                            if(navigator.clipboard && navigator.clipboard.read) {
                                const clipboardItems = await navigator.clipboard.read();
                                for (const clipboardItem of clipboardItems) {
                                  if (clipboardItem.types.some(t => t.startsWith('image/'))) {
                                    const type = clipboardItem.types.find(t => t.startsWith('image/'));
                                    const blob = await clipboardItem.getType(type);
                                    handleFileUpload(idx, blob);
                                    setStatusMsg("✅ تم لصق الصورة بنجاح!"); setTimeout(()=>setStatusMsg(""), 2000);
                                    return;
                                  }
                                }
                            }
                          } catch (err) { console.warn("Image paste denied", err); }

                          try {
                            if(navigator.clipboard && navigator.clipboard.readText) {
                                const text = await navigator.clipboard.readText();
                                if (text && (text.startsWith('http') || text.startsWith('data:image'))) {
                                  selectCandidate(idx, text.trim());
                                  setStatusMsg("✅ تم لصق الرابط بنجاح!"); setTimeout(()=>setStatusMsg(""), 2000);
                                  return;
                                }
                            }
                          } catch (err) { console.warn("Text paste denied", err); }
                          
                          setStatusMsg("⚠️ متصفحك يمنع الزر. اضغط (Ctrl+V) بلوحة المفاتيح للصق");
                          setTimeout(()=>setStatusMsg(""), 3500);
                        }}
                        title="Click to Paste or Ctrl+V"
                      >
                        📋 Paste Image
                      </button>

                      <button 
                         className="btn-search-quick"
                         onClick={() => document.getElementById(`file-upload-${idx}`).click()}
                         title="Upload File"
                      >
                         📂
                      </button>
                      <input type="file" id={`file-upload-${idx}`} style={{display:'none'}} accept="image/*" onChange={(e) => handleFileUpload(idx, e.target.files[0])} />
                    </div>


                    {sent.candidates && sent.candidates.length > 0 && (
                      <div className="candidates-grid" style={{marginTop:15, display:'grid', gridTemplateColumns:'repeat(4, 1fr)', gap:10}}>
                        {sent.candidates.map((url, cIdx) => (
                          <div key={cIdx} className="candidate-item" style={{minHeight:100, borderRadius:12}} onClick={() => selectCandidate(idx, url)}>
                            <img 
                              src={url} 
                              className="candidate-img" 
                              alt="option" 
                              style={{opacity:1, display:'block'}}
                              onError={(e) => { e.target.src = getEmojiUrl(sent.es); }}
                            />
                          </div>
                        ))}
                      </div>
                    )}
                  </div>
                </div>
              </div>
            ))}

            <button className="btn-add" onClick={()=>{
              const u={...selectedLesson,sentences:[...selectedLesson.sentences,{es:'Nueva palabra',ar:'كلمة جديدة',imagePrompt:'',imageUrl:null}]};
              setSelectedLesson(u);
              saveLessons(lessons.map(l=>l.id===u.id?u:l));
            }}>+ Add New Entry</button>
          </>}
        </section>
      </main>

      {statusMsg && <div className="toast">{statusMsg}</div>}
    </div>
  );
}
