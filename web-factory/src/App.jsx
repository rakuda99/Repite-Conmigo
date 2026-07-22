import React, { useState, useEffect } from 'react';
import { Search, Plus, Trash2, Download, Cloud, Sparkles, Image as ImageIcon, Volume2, Save, Wand2, Globe, Copy, Check, ExternalLink, Smartphone, Archive, Languages, FileJson, Share2, Upload, LogOut, Lock, Unlock, Mail, Key } from 'lucide-react';
import { initializeApp, getApps, getApp } from "firebase/app";
import { getDatabase, ref, set, get, child } from "firebase/database";
import { getFirestore, collection, getDocs, doc, setDoc, serverTimestamp, increment, arrayUnion } from "firebase/firestore";
import { getAuth, signInWithEmailAndPassword, onAuthStateChanged, signOut, GoogleAuthProvider, signInWithPopup, signInWithRedirect, getRedirectResult, signInAnonymously } from "firebase/auth";
import lessonsData from './lessons.json';

const firebaseConfig = {
  apiKey: "AIzaSyCjcEWZU2Y_EBPcTx5lu0Tg-y3tYKu13BQ",
  authDomain: "repite-conmigo.firebaseapp.com",
  databaseURL: "https://repite-conmigo-default-rtdb.europe-west1.firebasedatabase.app",
  projectId: "repite-conmigo",
  storageBucket: "repite-conmigo.firebasestorage.app",
  messagingSenderId: "245336262893",
  appId: "1:245336262893:web:4423a5cc24e87e7043a4e4",
  measurementId: "G-QPX3W5VTZ7"
};

const app = !getApps().length ? initializeApp(firebaseConfig) : getApp();
const db = getDatabase(app);
const firestore = getFirestore(app);
const auth = getAuth(app);

console.log("Magic Factory: Module Loading...");
const googleProvider = new GoogleAuthProvider();
googleProvider.setCustomParameters({ prompt: 'select_account' });


function Login({ onLogin }) {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleGoogleLogin = async () => {
    setLoading(true);
    setError('');
    try {
      const result = await signInWithPopup(auth, googleProvider);
      if (result.user) {
        onLogin(result.user);
      }
    } catch (err) {
      console.error("Login Error:", err);
      if (err.code === 'auth/popup-closed-by-user') {
        setError('❌ تم إغلاق النافذة قبل إكمال الدخول');
      } else {
        setError('❌ فشل تسجيل الدخول: ' + err.message);
      }
    }
    setLoading(false);
  };

  return (
    <div style={{ height: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center', background: '#030712', color: 'white', fontFamily: 'system-ui, sans-serif' }}>
      <div style={{ width: '100%', maxWidth: '400px', padding: '40px', background: '#111827', borderRadius: '24px', textAlign: 'center', border: '1px solid #1f2937', boxShadow: '0 20px 50px rgba(0,0,0,0.5)' }}>
        <div style={{ width: '64px', height: '64px', background: 'linear-gradient(135deg, #6366f1, #a855f7)', borderRadius: '16px', margin: '0 auto 24px', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
          <Lock size={32} />
        </div>
        <h1 style={{ fontSize: '28px', fontWeight: 900, marginBottom: '8px' }}>MAGIC FACTORY</h1>
        <p style={{ color: '#9ca3af', marginBottom: '32px' }}>Panel de Control - Repite Conmigo</p>
        
        {error && (
          <div style={{ background: 'rgba(239, 68, 68, 0.1)', color: '#f87171', padding: '12px', borderRadius: '12px', marginBottom: '24px', fontSize: '14px', border: '1px solid rgba(239, 68, 68, 0.2)' }}>
            {error}
          </div>
        )}

        <button 
          onClick={handleGoogleLogin}
          disabled={loading}
          style={{ 
            width: '100%', 
            padding: '16px', 
            background: 'white', 
            color: '#111827', 
            border: 'none', 
            borderRadius: '12px', 
            fontWeight: 800, 
            cursor: 'pointer',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            gap: '12px',
            fontSize: '16px',
            transition: '0.2s'
          }}
        >
          <img src="https://www.gstatic.com/firebasejs/ui/2.0.0/images/layout/google.svg" width="20" alt="" />
          {loading ? 'Cargando...' : 'Entrar con Gmail'}
        </button>

        <p style={{ marginTop: '24px', fontSize: '12px', color: '#4b5563' }}>
          Solo el administrador autorizado tiene acceso.
        </p>
      </div>
    </div>
  );
}

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
  const [user, setUser] = useState(null);
  const [authLoading, setAuthLoading] = useState(true);
  const isSuperAdmin = user && user.email === 'rakuda99@gmail.com';
  const isEditor = !!user;
  const isAdmin = isSuperAdmin; 

  const [view, setView] = useState('studio'); 
  const [lessons, setLessons] = useState([]);
  const [allUsers, setAllUsers] = useState([]);
  const [usersLoading, setUsersLoading] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedLesson, setSelectedLesson] = useState(null);
  
  // Safe localStorage access
  const getInitialKey = () => {
    try {
      return localStorage.getItem('repite_banana_key') || 'AIzaSyCjjvBx9VfEKOCMnxaFbp-FKc2u9z_V8Ec';
    } catch(e) { return 'AIzaSyCjjvBx9VfEKOCMnxaFbp-FKc2u9z_V8Ec'; }
  };
  const [geminiKey, setGeminiKey] = useState(getInitialKey());
  
  const [isSyncing, setIsSyncing] = useState(false);
  const [statusMsg, setStatusMsg] = useState('');
  const [loadError, setLoadError] = useState('');
  const [lastSaved, setLastSaved] = useState(null);

  // Load Initial Data
  useEffect(() => {
    try {
      console.log("Loading Local Lessons Data...");
      const cleaned = (lessonsData || []).map(l => ({
        ...l,
        sentences: (l.sentences || []).map(s => ({ ...s, isGenerating: false }))
      }));
      setLessons(cleaned);
      if (cleaned.length > 0) setSelectedLesson(cleaned[0]);
    } catch(e) {
      console.error("Data Load Error:", e);
      setLoadError("خطأ في تحميل البيانات المحلية: " + e.message);
    }
  }, []);


  useEffect(() => {
    console.log("Initializing Auth...");
    const unsubscribe = onAuthStateChanged(auth, async (u) => {
      console.log("Auth State Changed:", u ? u.email : "No User");
      setUser(u);
      if (u && !u.isAnonymous) {
         try {
            await setDoc(doc(firestore, "users", u.uid), {
                name: u.displayName || "Unknown User",
                email: u.email || "",
                lastLogin: serverTimestamp(),
                visits: increment(1)
            }, { merge: true });
         } catch(e) { console.error("Tracking error:", e); }
      }
      setAuthLoading(false);
    }, (err) => {
      console.error("Auth State Error:", err);
      setLoadError("Firebase Auth Error: " + err.message);
      setAuthLoading(false);
    });

    const timer = setTimeout(() => {
      setAuthLoading((prev) => {
        if(prev) {
          console.warn("Auth loading timed out!");
          setLoadError("يبدو أن الاتصال بسيرفر Firebase بطيء جداً أو هناك مشكلة في الإعدادات.");
        }
        return false;
      });
    }, 8000);

    return () => {
      unsubscribe();
      clearTimeout(timer);
    };
  }, []);

  // Track Time Spent
  useEffect(() => {
    if (!user || user.isAnonymous) return;
    const interval = setInterval(async () => {
      try {
         await setDoc(doc(firestore, "users", user.uid), {
             timeSpentMinutes: increment(1)
         }, { merge: true });
      } catch(e) {}
    }, 60000); // every 1 minute
    return () => clearInterval(interval);
  }, [user]);

  const syncWithCloud = async (allLessons) => {
    setIsSyncing(true);
    setStatusMsg('🚀 Uploading to server (Firestore)...');
    try {
      for (const l of allLessons) {
        const enTitle = l.title['en-US'] || 'Untitled';
        const formattedLesson = {
          id: l.id,
          title: enTitle,
          categoryId: enTitle,
          type: 'remote',
          rawLevel: l.rawLevel || 'Custom Lessons 🌟',
          icon: l.icon || '📚',
          content: (l.sentences || []).map(s => ({
            text: s.translations?.es || s.es || '',
            translations: s.translations || { ar: s.ar || '' },
            translation: s.translations?.ar || s.ar || '', // Fallback for old apps
            targetLang: 'es',
            sourceLang: 'auto',
            category: enTitle,
            contentType: (s.translations?.es || s.es || '').length < 15 ? 'word' : 'sentence',
            imageUrl: s.imageUrl || null
          }))
        };
        await setDoc(doc(firestore, "lessons", l.id), formattedLesson);
      }
      setStatusMsg('✅ All lessons uploaded successfully!');
    } catch (e) {
      console.error(e);
      setStatusMsg('❌ Upload failed: ' + e.message);
    }
    setIsSyncing(false);
    setTimeout(() => setStatusMsg(''), 4000);
  };

  const syncSingleLessonWithCloud = async () => {
    if(!selectedLesson) return;
    setIsSyncing(true);
    setStatusMsg(`🚀 Uploading lesson [${selectedLesson.title['en-US']}] to server...`);
    try {
      const enTitle = selectedLesson.title['en-US'] || 'Untitled';
      const formattedLesson = {
        id: selectedLesson.id,
        title: enTitle,
        categoryId: enTitle,
        type: 'remote',
        rawLevel: 1,
        authorId: user?.uid || 'admin',
        authorEmail: user?.email || 'admin',
        icon: selectedLesson.icon || '📚',
        content: (selectedLesson.sentences || []).map(s => ({
          text: s.translations?.es || s.es || '',
          translations: s.translations || { ar: s.ar || '' },
          translation: s.translations?.ar || s.ar || '',
          targetLang: 'es',
          sourceLang: 'auto',
          category: selectedLesson.title, // Use actual title
          contentType: (s.translations?.es || s.es || '').length < 15 ? 'word' : 'sentence',
          imageUrl: s.imageUrl || null
        }))
      };
      await setDoc(doc(firestore, "lessons", selectedLesson.id), formattedLesson);
      
      // Track created lesson for normal users
      if (user && !user.isAnonymous) {
         await setDoc(doc(firestore, "users", user.uid), {
             createdLessons: arrayUnion(selectedLesson.title['en-US'])
         }, { merge: true });
      }
      
      setStatusMsg('✅ Lesson uploaded successfully!');
    } catch (e) {
      console.error(e);
      setStatusMsg('❌ Upload failed: ' + e.message);
    }
    setIsSyncing(false);
    setTimeout(() => setStatusMsg(''), 4000);
  };


  useEffect(() => {
    const init = async () => {
      let finalLessons = [];
      try {
        // 1. Try Local Storage
        const saved = localStorage.getItem('repite_factory_lessons');
        if (saved) {
          finalLessons = JSON.parse(saved);
        } else {
          // 2. Try Firebase (Public Read)
          const dbRef = ref(db);
          const snapshot = await get(child(dbRef, 'lessons'));
          if (snapshot.exists()) {
            finalLessons = snapshot.val();
          }
        }
      } catch (e) {
        console.error('Initial load failed:', e);
      }
      
      // 3. Fallback to bundled JSON
      if (!finalLessons || !Array.isArray(finalLessons) || finalLessons.length === 0) {
        finalLessons = Array.isArray(lessonsData) ? lessonsData : Object.values(lessonsData).flat();
      }

      const cleaned = finalLessons.map(l => ({
        ...l,
        sentences: (l.sentences || []).map(s => ({...s, isGenerating: false}))
      }));
      setLessons(cleaned);
      if (cleaned.length > 0) setSelectedLesson(cleaned[0]);
    };
    init();
  }, []); // Load on mount, regardless of user

  const fetchUsers = async () => {
    if (!isAdmin) return;
    setUsersLoading(true);
    try {
      const querySnapshot = await getDocs(collection(firestore, "users"));
      const usersList = [];
      querySnapshot.forEach((doc) => {
        usersList.push({ id: doc.id, ...doc.data() });
      });
      setAllUsers(usersList);
    } catch (e) {
      console.error("Error fetching users:", e);
    }
    setUsersLoading(false);
  };

  useEffect(() => {
    if (view === 'users') {
      fetchUsers();
    }
  }, [view, isAdmin]);

  const saveLessons = (all) => {
    if (!isEditor) {
      setStatusMsg("🚫 Unauthorized: Please login to save");
      return;
    }
    
    // Safety check: if editing a locked lesson and not super admin
    if (selectedLesson?.isLocked && !isSuperAdmin) {
       setStatusMsg("🔒 This lesson is locked by the Super Admin");
       return;
    }

    setLessons(all);
    try {
      localStorage.setItem('repite_factory_lessons', JSON.stringify(all));
      setLastSaved(new Date().toLocaleTimeString());
      setTimeout(() => setLastSaved(null), 2000);
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
    const sourceWord = selectedLesson.sentences[idx].translations?.es || selectedLesson.sentences[idx].es;
    if (!sourceWord || sourceWord === 'Nueva palabra') return;

    try {
      const prompt = `Translate "${sourceWord}" (Spanish) to ar. 
        Also, generate 3 different simple sentences with it in Spanish and translate them to the same languages.
        JSON ONLY: { "word": { "ar":"" }, "examples": [ { "es":"", "ar":"" }, { "es":"", "ar":"" }, { "es":"", "ar":"" } ] }`;
        
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
      setStatusMsg(`🌍 ${sourceWord} -> Done!`);
    } catch (e) {
      console.warn("AI Failed, using Translate Fallback:", e.message);
      try {
        const langs = { ar: 'ar' };
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
        setStatusMsg(`⚡ ${sourceWord} -> Done (Fast)!`);
      } catch (fallbackErr) {
        setSelectedLesson(prev => {
          const sents = [...prev.sentences];
          sents[idx].isTranslating = false;
          return { ...prev, sentences: sents };
        });
        setStatusMsg(`❌ Error: ${fallbackErr.message}`);
      }
    }
  };

  const autoTranslateAll = async () => {
    if (!selectedLesson || !selectedLesson.sentences) return;
    const total = selectedLesson.sentences.length;
    
    for (let i = 0; i < total; i++) {
      const currentWord = selectedLesson.sentences[i].translations?.es || selectedLesson.sentences[i].es || '...';
      setStatusMsg(`🚀 Filling bank: (${i + 1} of ${total}) - [ ${currentWord} ]`);
      
      try {
        await autoTranslateRow(i);
      } catch (err) {
        console.error("Batch error at index", i, err);
      }
      
      await new Promise(r => setTimeout(r, 800));
    }
    
    setStatusMsg('✅ Lesson word bank filled completely!');
  };

  const rescueAssets = async () => {
    setStatusMsg("🩹 Searching Android files for missing images...");
    const rescueMap = {
      "Yo soy de Arabia Saudita": "https://upload.wikimedia.org/wikipedia/commons/thumb/0/0d/Flag_of_Saudi_Arabia.svg/512px-Flag_of_Saudi_Arabia.svg.png",
      "Él es un buen doctor": "https://api.dicebear.com/7.x/noto-emoji/svg?seed=doctor",
      "Pollo": "https://upload.wikimedia.org/wikipedia/commons/thumb/3/33/Hecheff_Chicken.jpg/512px-Hecheff_Chicken.jpg"
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
    setStatusMsg("✅ Rescue complete! Recovered what we found.");
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

  const generateAI = async (idx) => {
    const sent = selectedLesson.sentences[idx];
    setStatusMsg(`🎨 Generating professional options for "${sent.es}"...`);

    updateSentence(idx, 'isGenerating', true);
    updateSentence(idx, 'candidates', []);

    try {
      const rawTerm = sent.es.split(' ').pop().toLowerCase().replace(/[.,!?;:]/g, '');
      let engTerm = rawTerm;
      const variants = [];

      const trResp = await fetch(`https://translate.googleapis.com/translate_a/single?client=gtx&sl=auto&tl=en&dt=t&q=${encodeURIComponent(rawTerm)}`);
      const trData = await trResp.json();
      engTerm = trData && trData[0] && trData[0][0] && trData[0][0][0] ? trData[0][0][0].toLowerCase() : rawTerm;

      if (geminiKey) {
        try {
          const kgResp = await fetch(`https://kgsearch.googleapis.com/v1/entities:search?query=${encodeURIComponent(rawTerm)}&key=${geminiKey}&limit=2`);
          const kgData = await kgResp.json();
          kgData.itemListElement?.forEach(el => {
            if (el.result?.image?.contentUrl) variants.push(el.result.image.contentUrl);
          });
        } catch (e) { console.error('Google KG Fail:', e); }
      }

      try {
        const wikiResp = await fetch(`https://commons.wikimedia.org/w/api.php?action=query&format=json&origin=*&generator=search&gsrnamespace=6&gsrsearch=${encodeURIComponent(engTerm)}&gsrlimit=2&prop=imageinfo&iiprop=url`);
        const wikiData = await wikiResp.json();
        if (wikiData.query?.pages) {
          Object.values(wikiData.query.pages).forEach(p => {
             if (p.imageinfo?.[0]?.url) variants.push(p.imageinfo[0].url);
          });
        }
      } catch (e) { console.error('Wiki Fail:', e); }

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

      setStatusMsg('🍌 Generated with the Banana Rocket system!');
    } catch (e) {
      console.error(e);
      updateSentence(idx, 'isGenerating', false);
      setStatusMsg('❌ Failed to load options');
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
      setStatusMsg(`✅ Downloaded: ${fileName}`);
    } catch (e) {
      console.error(e);
      setStatusMsg("❌ Download failed");
    }
  };

  const exportForAndroid = () => {
    try {
      const androidFormat = {
        "Custom Lessons 🌟": lessons.map(l => ({
          ...l,
          title: { "en-US": l.title['en-US'], "ar-SA": l.title.ar || l.title['en-US'] }
        }))
      };
      const json = JSON.stringify(androidFormat, null, 2);
      const dataStr = "data:application/json;base64," + btoa(unescape(encodeURIComponent(json)));
      const link = document.createElement('a');
      link.setAttribute("href", dataStr);
      link.setAttribute("download", "lessons.json");
      document.body.appendChild(link);
      link.click();
      setTimeout(() => document.body.removeChild(link), 100);
      setStatusMsg('✅ Exported lessons.json successfully! Place it in your Android project.');
    } catch (err) {
      setStatusMsg('❌ Export error: ' + err.message);
    }
  };
  
  const importJSON = () => {
    const input = document.createElement('input');
    input.type = 'file';
    input.accept = '.json';
    input.onchange = (e) => {
      const file = e.target.files[0];
      const reader = new FileReader();
      reader.onload = (re) => {
        try {
          const data = JSON.parse(re.target.result);
          if (Array.isArray(data)) {
            setLessons(data);
            saveLessons(data);
            setStatusMsg("✅ Full lesson bank imported!");
          } else if (data.id && data.sentences) {
            const exists = lessons.find(l => l.id === data.id);
            if (exists) {
               if(confirm("Lesson already exists, do you want to overwrite it?")) {
                 const updated = lessons.map(l => l.id === data.id ? data : l);
                 setLessons(updated);
                 saveLessons(updated);
                 setSelectedLesson(data);
               }
            } else {
              const updated = [...lessons, data];
              setLessons(updated);
              saveLessons(updated);
              setSelectedLesson(data);
              setStatusMsg("✅ Lesson imported successfully!");
            }
          }
        } catch (err) {
          setStatusMsg("❌ Import failed: Invalid file");
        }
      };
      reader.readAsText(file);
    };
    input.click();
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

  const deleteLesson = (id, e) => {
    e.stopPropagation();
    if (confirm('Are you sure you want to delete this lesson entirely?')) {
      const updated = lessons.filter(l => l.id !== id);
      setLessons(updated);
      saveLessons(updated);
      if (selectedLesson?.id === id) setSelectedLesson(null);
      setStatusMsg('✅ Lesson deleted successfully');
      setTimeout(() => setStatusMsg(''), 2000);
    }
  };

  const selectLesson = (lesson) => {
    const cleaned = {
      ...lesson,
      sentences: (lesson.sentences || []).map(s => ({...s, isGenerating: false}))
    };
    setSelectedLesson(cleaned);
  };

  if (authLoading || loadError) return (
    <div style={{height:'100vh', background:'#030712', display:'flex', alignItems:'center', justifyContent:'center', color:'white', fontSize:20, flexDirection:'column', gap:24, padding:40, textAlign:'center'}}>
      <style>{`
        @keyframes spin { to { transform: rotate(360deg); } }
        body { margin: 0; background: #030712; }
      `}</style>
      {authLoading ? (
        <>
          <div style={{width:40, height:40, border:'4px solid #3b82f6', borderTopColor:'transparent', borderRadius:'50%', animation:'spin 1s linear infinite'}} />
          <div style={{fontWeight:800, letterSpacing:1}}>⏳ Loading Magic Factory...</div>
          <div style={{fontSize:12, color:'#4b5563'}}>جاري التحقق من الهوية والاتصال بقاعدة البيانات</div>
        </>
      ) : (
        <>
          <div style={{fontSize:60}}>⚠️</div>
          <div style={{color:'#f87171', fontWeight:900, fontSize:24}}>حدث خطأ أثناء التشغيل</div>
          <div style={{color:'#94a3b8', maxWidth:500}}>{loadError}</div>
          <button onClick={()=>window.location.reload()} style={{marginTop:20, padding:'14px 28px', background:'#3b82f6', color:'white', border:'none', borderRadius:14, fontWeight:800, cursor:'pointer', boxShadow:'0 10px 20px rgba(59,130,246,0.3)'}}>إعادة المحاولة</button>
        </>
      )}
    </div>
  );


  if (!user || user.isAnonymous) return <Login onLogin={setUser} />;

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
        .module-title-input { background: rgba(0,0,0,0.2); border: 2px dashed rgba(255,255,255,0.2); border-radius: 12px; padding: 8px 16px; font-size: 28px; font-weight: 900; color: white; outline: none; letter-spacing: -0.5px; flex: 1; min-width: 250px; transition: 0.2s; }
        .module-title-input:focus { border-color: #8b5cf6; background: rgba(139,92,246,0.1); }
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
        .save-indicator { position: fixed; bottom: 32px; left: 32px; background: rgba(16,185,129,0.9); color: white; padding: 8px 16px; border-radius: 100px; font-size: 11px; font-weight: 800; display: flex; align-items: center; gap: 8px; backdrop-filter: blur(10px); z-index: 1000; animation: fadeIn 0.3s; }
        @keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }
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
            MAGIC FACTORY <span style={{fontSize:12, opacity:0.6, background:'rgba(0,0,0,0.2)', padding:'2px 8px', borderRadius:6}}>v11.0</span>
          </div>
        </div>
        <div className="tab-group" style={{display:'flex', gap:10}}>
          <button className={`tab ${view==='studio'?'active':''}`} onClick={()=>setView('studio')} style={{padding: '8px 16px', borderRadius: 10, background: view==='studio'?'white':'transparent', color: view==='studio'?'black':'white', border: 'none', cursor: 'pointer', fontWeight: 700}}>⬛ STUDIO</button>
          <button className={`tab ${view==='player'?'active':''}`} onClick={()=>setView('player')} style={{padding: '8px 16px', borderRadius: 10, background: view==='player'?'white':'transparent', color: view==='player'?'black':'white', border: 'none', cursor: 'pointer', fontWeight: 700}}>▶ PREVIEW</button>
          {isAdmin && (
            <button className={`tab ${view==='users'?'active':''}`} onClick={()=>setView('users')} style={{padding: '8px 16px', borderRadius: 10, background: view==='users'?'#10b981':'transparent', color: 'white', border: 'none', cursor: 'pointer', fontWeight: 700}}>👥 USERS</button>
          )}
        </div>
        <div className="hdr-btns" style={{display:'flex', gap:12, alignItems:'center'}}>
          <div style={{display:'flex', alignItems:'center', gap:10, color:'#e9d5ff', fontSize:12, fontWeight:700}}>
            {isAdmin ? '🛡️ ADMIN' : '👤 USER'} | {user.email}
          </div>
          {isAdmin && (
            <button className="btn-upload" style={{background:'white', color:'#6d28d9', padding:'8px 16px', borderRadius:10, fontWeight:800, border:'none', cursor:'pointer'}} 
              onClick={async ()=>{
                await syncWithCloud(lessons);
                setStatusMsg('🚀 Cloud Sync Complete!');
              }}>
              🚀 Update
            </button>
          )}
          <button onClick={() => { signOut(auth); window.location.reload(); }} style={{ background: 'rgba(239,68,68,0.1)', color: '#ef4444', border: 'none', padding: '8px 12px', borderRadius: 8, fontWeight: 700, cursor: 'pointer' }}>
            Logout
          </button>
        </div>
      </header>

      <main style={{flex: 1, display: 'flex', flexDirection: 'column'}}>
        {view === 'users' && isAdmin && (
        <div style={{padding: 40, maxWidth: 1200, margin: '0 auto'}}>
          <div style={{display:'flex', justifyContent:'space-between', alignItems:'center', marginBottom:30}}>
            <h2 style={{fontSize:32, fontWeight:900}}>Registered Users</h2>
            <button onClick={fetchUsers} disabled={usersLoading} style={{background:'#10b981', color:'white', border:'none', padding:'10px 20px', borderRadius:10, fontWeight:700, cursor:'pointer'}}>
              {usersLoading ? 'Refreshing...' : '🔄 Refresh List'}
            </button>
          </div>
          <div style={{background:'rgba(255,255,255,0.03)', borderRadius:24, border:'1px solid rgba(255,255,255,0.08)', overflow:'hidden'}}>
            <table style={{width:'100%', borderCollapse:'collapse', textAlign:'left'}}>
              <thead>
                <tr style={{background:'rgba(255,255,255,0.05)', color:'#94a3b8', fontSize:12, textTransform:'uppercase', letterSpacing:1}}>
                  <th style={{padding:'20px 24px'}}>Name</th>
                  <th style={{padding:'20px 24px'}}>Gender</th>
                  <th style={{padding:'20px 24px'}}>Country</th>
                  <th style={{padding:'20px 24px'}}>Language</th>
                  <th style={{padding:'20px 24px'}}>Age</th>
                  <th style={{padding:'20px 24px'}}>UID</th>
                </tr>
              </thead>
              <tbody>
                {allUsers.length === 0 ? (
                  <tr><td colSpan="6" style={{padding:40, textAlign:'center', color:'#64748b'}}>No users found yet.</td></tr>
                ) : allUsers.map(u => (
                  <tr key={u.id} style={{borderBottom:'1px solid rgba(255,255,255,0.05)', transition:'0.2s'}} className="user-row">
                    <td style={{padding:'20px 24px', fontWeight:700}}>{u.fullName || 'Anonymous'}</td>
                    <td style={{padding:'20px 24px'}}>{u.gender === 'Male' ? '♂️ Male' : u.gender === 'Female' ? '♀️ Female' : u.gender || 'Unknown'}</td>
                    <td style={{padding:'20px 24px'}}>{u.country ? `📍 ${u.country}` : 'Not Set'}</td>
                    <td style={{padding:'20px 24px'}}>{u.motherTongue || 'Not Set'}</td>
                    <td style={{padding:'20px 24px'}}>{u.age || '-'}</td>
                    <td style={{padding:'20px 24px', fontSize:10, color:'#475569'}}>{u.id}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
          <style>{`
            .user-row:hover { background: rgba(255,255,255,0.02); }
          `}</style>
        </div>
      )}

      {view === 'studio' && (
  <div style={{ display: 'grid', gridTemplateColumns: '260px minmax(0, 1fr)', gap: '20px', padding: '20px', maxWidth: '1600px', margin: '0 auto', width: '100%', boxSizing: 'border-box' }}>
            <aside className="sidebar">
              <div style={{display:'flex', justifyContent:'space-between', alignItems:'center', marginBottom:10}}>
                 <div className="sidebar-label">Lesson Archive</div>
              </div>
              
              {user && (
                <button onClick={()=>{
                  const newId = Date.now().toString();
                  const newLesson = { id: newId, icon: "🆕", title: { "en-US": "Lesson NAME", "ar": "Lesson NAME" }, sentences: [] };
                  const updated = [newLesson, ...lessons];
                  setLessons(updated);
                  saveLessons(updated);
                  setSelectedLesson(newLesson);
                }} style={{width:'100%', background:'linear-gradient(135deg,#8b5cf6,#3b82f6)', color:'white', border:'none', padding:'12px', borderRadius:10, fontWeight:800, cursor:'pointer', fontSize:14, marginBottom:15, boxShadow:'0 4px 15px rgba(139,92,246,0.3)', display:'flex', alignItems:'center', justifyContent:'center', gap:8}}>
                  <Plus size={18} /> Create New Lesson
                </button>
              )}
              <div style={{position:'relative', marginBottom:15}}>
                 <input 
                   type="text" 
                   placeholder="Search lessons..." 
                   value={searchQuery}
                   onChange={(e) => setSearchQuery(e.target.value)}
                   style={{width:'100%', background:'rgba(255,255,255,0.03)', border:'1px solid rgba(255,255,255,0.1)', padding:'10px 12px 10px 32px', borderRadius:10, color:'white', fontSize:13, outline:'none'}}
                 />
                 <span style={{position:'absolute', left:10, top:10, opacity:0.4}}>🔍</span>
              </div>
              <div className="lesson-list">
                {lessons.filter(l => l.title['en-US'].toLowerCase().includes(searchQuery.toLowerCase()) || l.title.ar?.includes(searchQuery)).map(l => (
                  <div key={l.id} className={`lesson-item ${selectedLesson?.id===l.id?'active':''}`} onClick={()=>selectLesson(l)}>
                    <span className="lesson-icon">{l.icon}</span>
                    <div style={{flex: 1}}>
                      <div className="lesson-name">{l.title['en-US']}</div>
                      <div className="lesson-count">{l.sentences ? l.sentences.length : 0} items</div>
                    </div>
                    {l.isLocked && <Lock size={14} color="#fbbf24" style={{marginRight: 6}} />}
                    {isSuperAdmin && (
                       <button 
                        onClick={(e) => {
                          e.stopPropagation();
                          const updated = lessons.map(item => item.id === l.id ? {...item, isLocked: !item.isLocked} : item);
                          saveLessons(updated);
                          if(selectedLesson?.id === l.id) setSelectedLesson({...selectedLesson, isLocked: !l.isLocked});
                        }}
                        style={{background: l.isLocked ? 'rgba(251,191,36,0.1)' : 'rgba(255,255,255,0.05)', border:'none', borderRadius:6, padding:4, cursor:'pointer', display:'flex', alignItems:'center', justifyContent:'center', marginRight: 4}}
                        title={l.isLocked ? "Unlock Lesson" : "Lock Lesson"}
                       >
                         {l.isLocked ? <Lock size={14} color="#fbbf24" /> : <Unlock size={14} color="#94a3b8" />}
                       </button>
                    )}
                    {(isSuperAdmin || !l.isLocked) && (
                      <button onClick={(e) => deleteLesson(l.id, e)} style={{background:'rgba(239,68,68,0.1)', border:'none', color:'#ef4444', cursor:'pointer', padding:'4px 8px', borderRadius:6}} title="Delete Lesson">🗑</button>
                    )}
                  </div>
                ))}
              </div>
            </aside>

            <section className="stage">
              {selectedLesson && <>
                <div className="module-header">
                  <input className="module-title-input" value={selectedLesson.title['en-US']} onChange={e=>{
                    if(!user) return;
                    const u={...selectedLesson,title:{...selectedLesson.title,'en-US':e.target.value}};
                    setSelectedLesson(u);
                    saveLessons(lessons.map(l=>l.id===u.id?u:l));
                  }}/>
                  <div style={{display:'flex', gap:10, alignItems:'center', flexWrap:'wrap'}}>
                    {user && (
                      <button onClick={syncSingleLessonWithCloud} style={{background:'linear-gradient(135deg,#3b82f6,#2563eb)',color:'white',border:'none',padding:'10px 18px',borderRadius:10,fontWeight:900,cursor:'pointer',fontSize:13,boxShadow:'0 4px 15px rgba(59,130,246,0.3)',whiteSpace:'nowrap'}}>☁ Sync Lesson to Cloud</button>
                    )}
                    {isAdmin && (
                      <>
                        <button onClick={exportForAndroid} style={{background:'linear-gradient(135deg,#10b981,#059669)',color:'white',border:'none',padding:'10px 18px',borderRadius:10,fontWeight:900,cursor:'pointer',fontSize:13,boxShadow:'0 4px 15px rgba(16,185,129,0.3)',whiteSpace:'nowrap'}}>📱 Export for Android</button>
                        <button onClick={downloadJSON} style={{background:'rgba(139,92,246,0.1)',color:'#a78bfa',border:'1px solid rgba(139,92,246,0.2)',padding:'10px 18px',borderRadius:10,fontWeight:700,cursor:'pointer',fontSize:12,whiteSpace:'nowrap'}}>📥 Save Web Backup</button>
                        <button onClick={downloadSingleLesson} style={{background:'linear-gradient(135deg,#0ea5e9,#0284c7)',color:'white',border:'none',padding:'10px 22px',borderRadius:10,fontWeight:800,cursor:'pointer',fontSize:13,boxShadow:'0 4px 15px rgba(14,165,233,0.3)', whiteSpace:'nowrap'}}>📄 Save This Lesson</button>
                        <button onClick={rescueAssets} style={{background:'rgba(239,68,68,0.1)',color:'#f87171',border:'1px solid rgba(239,68,68,0.2)',padding:'10px 18px',borderRadius:10,fontWeight:800,cursor:'pointer',fontSize:12,whiteSpace:'nowrap'}}>🩹 Rescue Images</button>
                        <button onClick={autoTranslateAll} style={{background:'rgba(16,185,129,0.1)',color:'#10b981',border:'1px solid rgba(16,185,129,0.2)',padding:'10px 18px',borderRadius:10,fontWeight:800,cursor:'pointer',fontSize:12,whiteSpace:'nowrap'}}>🌍 Bank All Words</button>
                        <button onClick={generateAllPhotos} style={{background:'rgba(255,158,11,0.1)',color:'#f59e0b',border:'1px solid rgba(255,158,11,0.2)',padding:'10px 18px',borderRadius:10,fontWeight:800,cursor:'pointer',fontSize:12,whiteSpace:'nowrap'}}>🎨 Generate All</button>
                        <button onClick={clearAllImages} style={{background:'rgba(239,68,68,0.08)',color:'#f87171',border:'1px solid rgba(239,68,68,0.25)',padding:'10px 14px',borderRadius:10,fontWeight:700,cursor:'pointer',fontSize:12,whiteSpace:'nowrap'}}>🗑 Clear Images</button>
                      </>
                    )}
                    <div className="sync-badge">Ready for Sync</div>
                  </div>
                </div>

                {selectedLesson.sentences.map((sent, idx) => (
                  <div className="card" key={idx}>
                    <div className="card-top">
                      <div style={{display:'flex', gap:12, width:'100%', flexWrap:'wrap', alignItems:'center'}}>
                        <div className="primary-input-wrap" style={{flex:'1 1 300px'}}>
                          <button className="btn-speak" onClick={()=>speak(sent.translations?.es || sent.es)} title="Listen">🗣️</button>
                          <input className="primary-input" placeholder="Primary Spanish Word" value={sent.translations?.es || sent.es} onChange={e=>{if(user)updateTranslation(idx,'es',e.target.value)}}/>
                        </div>
                        
                        {user && (
                          <button 
                            className="btn-ai" 
                            onClick={() => autoTranslateRow(idx)}
                            disabled={sent.isTranslating}
                            style={{height:60, flexShrink:0, background: 'linear-gradient(135deg, #3b82f6, #6366f1)', border:'none', color:'white', borderRadius:16, fontWeight:900, cursor:'pointer', padding:'0 24px', fontSize:14, display:'flex', alignItems:'center', gap:8, boxShadow:'0 4px 15px rgba(59,130,246,0.3)'}}
                          >
                            {sent.isTranslating ? '⏳ Processing...' : '✨ Magic Bank'}
                          </button>
                        )}
                        
                        {user && <button className="btn-delete" onClick={()=>deleteEntry(idx)} style={{flexShrink:0}} title="Delete">🗑️ Delete</button>}
                      </div>

                      <div className="lang-grid">
                        {[
                          {code:'ar', label:'Arabic', color:'#fbbf24', dir:'rtl'}
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
                          <div style={{fontSize:22, color:'#8b5cf6', marginBottom:20, marginTop:10, fontWeight:900, letterSpacing:1, textShadow:'0 2px 10px rgba(139,92,246,0.2)', borderBottom:'2px solid rgba(139,92,246,0.2)', paddingBottom:10, display:'flex', alignItems:'center', gap: 10}}>
                            <span>📝 EXAMPLE SENTENCES</span>
                            <span style={{fontSize: 20, opacity: 0.8}}>⬇️</span>
                          </div>
                          
                          {(sent.examples || (sent.exampleTranslations ? [sent.exampleTranslations] : [{}])).map((ex, exIdx) => (
                          <div key={exIdx} style={{display:'flex', flexDirection:'column', gap:12, marginBottom:16, paddingBottom:16, borderBottom:(exIdx===((sent.examples?.length || 1)-1) ? 'none' : '1px solid rgba(255,255,255,0.05)')}}>
                            <div className="primary-input-wrap" style={{background:'rgba(0,0,0,0.2)', border:'1px solid rgba(255,255,255,0.05)', boxShadow:'none', padding:'2px 8px'}}>
                              <span style={{color:'#3b82f6', fontWeight:900, fontSize:12, marginRight:10}}>ES {exIdx + 1}</span>
                              <input className="primary-input" style={{fontSize:16}} value={ex.es || ''} onChange={e=>updateTranslation(idx,'es',e.target.value, true, exIdx)}/>
                            </div>
                            <div className="lang-grid">
                              {[
                                {code:'ar', label:'Arabic', color:'#fbbf24', dir:'rtl'}
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
                                  onError={(e) => { e.target.src = "https://api.dicebear.com/7.x/shapes/svg?seed=" + encodeURIComponent(sent.es); }}
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
                  const u={...selectedLesson,sentences:[...selectedLesson.sentences,{es:'Insert a new word',ar:'Insert a new word',imagePrompt:'',imageUrl:null}]};
                  setSelectedLesson(u);
                  saveLessons(lessons.map(l=>l.id===u.id?u:l));
                }}>+ Add New Entry</button>
              </>}
            </section>
          </div>
        )}

          {view === 'player' && (
            <div style={{ flex: 1, display: 'flex', alignItems: 'center', justifyContent: 'center', padding: '40px', background: '#030712' }}>
            {/* Phone Mockup Preview */}
            <div style={{ width: '360px', height: '740px', background: '#000', borderRadius: '48px', border: '8px solid #334155', boxShadow: '0 50px 100px -20px rgba(0,0,0,0.7)', overflow: 'hidden', position: 'relative', display: 'flex', flexDirection: 'column' }}>
                <div style={{ height: '40px', background: '#000', display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
                  <div style={{ width: '60px', height: '18px', background: '#1e293b', borderRadius: '10px' }}></div>
                </div>
                
                <div style={{ flex: 1, background: '#f8fafc', overflowY: 'auto', padding: '20px', display: 'flex', flexDirection: 'column', gap: '15px' }}>
                  <div style={{ fontSize: '24px', fontWeight: 900, color: '#1e293b', textAlign: 'center', marginBottom: '10px' }}>
                    {selectedLesson?.title?.['en-US']}
                  </div>
                  
                  {selectedLesson?.sentences.map((s, i) => (
                    <div key={i} style={{ background: 'white', borderRadius: '24px', padding: '16px', boxShadow: '0 4px 6px -1px rgba(0,0,0,0.1)', display: 'flex', flexDirection: 'column', gap: '12px', border: '1px solid #f1f5f9' }}>
                      <div style={{ width: '100%', aspectRatio: '1', borderRadius: '16px', background: '#f1f5f9', overflow: 'hidden', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                         {(s.imageUrl || s.image) ? (
                           <img src={s.imageUrl || s.image} style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
                         ) : (
                           <span style={{ fontSize: '40px' }}>{s.es?.[0]}</span>
                         )}
                      </div>
                      <div style={{ textAlign: 'center' }}>
                        <div style={{ fontSize: '20px', fontWeight: 800, color: '#1e293b' }}>{s.translations?.es || s.es}</div>
                        <div style={{ fontSize: '14px', color: '#64748b', marginTop: '4px' }}>{s.translations?.ar || s.ar}</div>
                      </div>
                      <div style={{ marginTop: '8px', borderTop: '1px solid #f1f5f9', paddingTop: '12px' }}>
                        {(s.examples || (s.exampleTranslations ? [s.exampleTranslations] : [])).slice(0, 1).map((ex, exIdx) => (
                          <div key={exIdx} style={{ fontSize: '13px', color: '#334155', fontStyle: 'italic', textAlign: 'center', lineHeight: '1.4' }}>
                            "{ex.es}"
                            <div style={{ fontSize: '11px', color: '#94a3b8', marginTop: '2px' }}>{ex.ar}</div>
                          </div>
                        ))}
                      </div>
                    </div>
                  ))}
                  
                  <div style={{ height: '40px' }}></div>
                </div>

                <div style={{ height: '60px', background: 'white', borderTop: '1px solid #e2e8f0', display: 'flex', justifyContent: 'space-around', alignItems: 'center' }}>
                   <div style={{ width: '24px', height: '24px', borderRadius: '50%', background: '#cbd5e1' }}></div>
                   <div style={{ width: '40px', height: '40px', borderRadius: '50%', background: '#8b5cf6', display:'flex', alignItems:'center', justifyContent:'center', color:'white', fontSize:20 }}>▶</div>
                   <div style={{ width: '24px', height: '24px', borderRadius: '50%', background: '#cbd5e1' }}></div>
                </div>
            </div>
            
            <div style={{ marginLeft: '40px', maxWidth: '400px', color: '#94a3b8' }}>
              <h2 style={{ color: 'white', fontSize: '32px', marginBottom: '20px' }}>Mobile Preview</h2>
              <p style={{ lineHeight: '1.6', marginBottom: '20px' }}>This is how your lesson will appear on the student's mobile device. All images, translations, and example sentences are synchronized in real-time.</p>
              <div style={{ background: 'rgba(255,255,255,0.05)', padding: '20px', borderRadius: '20px', border: '1px solid rgba(255,255,255,0.1)' }}>
                <div style={{ fontSize: '12px', fontWeight: 800, color: '#8b5cf6', marginBottom: '10px' }}>DEVICE STATS</div>
                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '8px' }}>
                  <span>Screen Resolution</span>
                  <span style={{ color: 'white' }}>360 x 740 (HD)</span>
                </div>
                <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                  <span>Items Count</span>
                  <span style={{ color: 'white' }}>{selectedLesson?.sentences.length} items</span>
                </div>
              </div>
              <button onClick={() => setView('studio')} style={{ marginTop: '30px', background: '#8b5cf6', color: 'white', border: 'none', padding: '12px 30px', borderRadius: '12px', fontWeight: 700, cursor: 'pointer' }}>Back to Editor</button>
            </div>
          </div>
        )}
      </main>

      {statusMsg && <div className="toast">{statusMsg}</div>}
    </div>
  );
}
