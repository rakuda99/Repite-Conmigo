import React, { useState } from 'react';
import { Moon, Volume2, Trash2, Bell, Shield, ChevronLeft } from 'lucide-react';

export const SettingsPage = () => {
  const [darkMode, setDarkMode] = useState(false);
  const [volume, setVolume] = useState(80);
  const [notifications, setNotifications] = useState(true);
  const [defaultMode, setDefaultMode] = useState(() => {
    return localStorage.getItem('repite_default_mode') || 'es-ar';
  });
  const [apiKey, setApiKey] = useState(() => {
    return localStorage.getItem('repite_gemini_api_key') || '';
  });

  const handleApiKeyChange = (e) => {
    const val = e.target.value;
    setApiKey(val);
    localStorage.setItem('repite_gemini_api_key', val);
  };

  const handleModeChange = (newMode) => {
    setDefaultMode(newMode);
    localStorage.setItem('repite_default_mode', newMode);
  };

  const handleResetData = () => {
    if (window.confirm('هل أنت متأكد من مسح جميع بيانات التطبيق (المجموعات المخصصة، المفضلة، الإحصائيات)؟ لا يمكن التراجع عن هذا الإجراء.')) {
      localStorage.removeItem('repite_custom_decks');
      localStorage.removeItem('repite_favorites');
      localStorage.removeItem('repite_default_mode');
      alert('تم مسح جميع البيانات بنجاح. سيتم إعادة تحميل التطبيق.');
      window.location.reload();
    }
  };

  return (
    <div style={{ flex: 1, backgroundColor: 'var(--background)', color: 'var(--text-main)', padding: '24px 16px', paddingBottom: '100px' }} dir="rtl">
      <h2 style={{ textAlign: 'center', marginBottom: '32px', fontSize: '1.75rem' }}>الإعدادات</h2>

      <div className="card" style={{ padding: '0', overflow: 'hidden', marginBottom: '24px' }}>
        <div style={{ padding: '16px', borderBottom: '1px solid var(--border)', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
            <Moon size={24} color="#1cb0f6" />
            <span style={{ fontSize: '1.1rem', fontWeight: 'bold' }}>الوضع الليلي</span>
          </div>
          <label className="switch">
            <input type="checkbox" checked={darkMode} onChange={() => setDarkMode(!darkMode)} />
            <span className="slider round"></span>
          </label>
        </div>

        <div style={{ padding: '16px', borderBottom: '1px solid var(--border)' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '16px', marginBottom: '16px' }}>
            <span style={{ fontSize: '1.1rem', fontWeight: 'bold' }}>طريقة الدراسة الافتراضية</span>
          </div>
          <div style={{ display: 'flex', gap: '12px' }}>
            <button 
              onClick={() => handleModeChange('es-ar')}
              style={{ flex: 1, padding: '12px', borderRadius: '8px', border: defaultMode === 'es-ar' ? '2px solid #58cc02' : '2px solid var(--border)', backgroundColor: defaultMode === 'es-ar' ? 'rgba(88, 204, 2, 0.1)' : 'transparent', color: 'var(--text-main)', cursor: 'pointer', fontWeight: 'bold' }}
            >
              إسباني ➔ عربي
            </button>
            <button 
              onClick={() => handleModeChange('ar-es')}
              style={{ flex: 1, padding: '12px', borderRadius: '8px', border: defaultMode === 'ar-es' ? '2px solid #58cc02' : '2px solid var(--border)', backgroundColor: defaultMode === 'ar-es' ? 'rgba(88, 204, 2, 0.1)' : 'transparent', color: 'var(--text-main)', cursor: 'pointer', fontWeight: 'bold' }}
            >
              عربي ➔ إسباني
            </button>
          </div>
        </div>

        <div style={{ padding: '16px', borderBottom: '1px solid var(--border)' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '16px', marginBottom: '8px' }}>
            <span style={{ fontSize: '1.1rem', fontWeight: 'bold' }}>مفتاح Gemini API</span>
          </div>
          <p style={{ fontSize: '0.9rem', color: 'var(--text-muted)', marginBottom: '12px' }}>
            لإضافة أفعال وجمل مخصصة باستخدام الذكاء الاصطناعي، يرجى إدخال مفتاح الـ API الخاص بك من Google Gemini.
          </p>
          <input 
            type="password" 
            placeholder="AIzaSy..." 
            value={apiKey}
            onChange={handleApiKeyChange}
            style={{ 
              width: '100%', 
              padding: '12px', 
              borderRadius: '8px', 
              border: '2px solid var(--border)',
              backgroundColor: 'var(--background)',
              color: 'var(--text-main)',
              fontSize: '1rem',
              direction: 'ltr'
            }}
          />
        </div>

        <div style={{ padding: '16px', borderBottom: '1px solid var(--border)', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
            <Bell size={24} color="#ff9600" />
            <span style={{ fontSize: '1.1rem', fontWeight: 'bold' }}>الإشعارات والتذكير</span>
          </div>
          <label className="switch">
            <input type="checkbox" checked={notifications} onChange={() => setNotifications(!notifications)} />
            <span className="slider round"></span>
          </label>
        </div>

        <div style={{ padding: '16px', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '16px', width: '100%' }}>
            <Volume2 size={24} color="#58cc02" />
            <div style={{ flex: 1 }}>
              <span style={{ fontSize: '1.1rem', fontWeight: 'bold', display: 'block', marginBottom: '8px' }}>مستوى الصوت</span>
              <input 
                type="range" 
                min="0" max="100" 
                value={volume} 
                onChange={(e) => setVolume(e.target.value)} 
                style={{ width: '100%', direction: 'ltr' }}
              />
            </div>
          </div>
        </div>
      </div>

      <div className="card" style={{ padding: '0', overflow: 'hidden', marginBottom: '24px' }}>
        <div style={{ padding: '16px', borderBottom: '1px solid var(--border)', display: 'flex', alignItems: 'center', justifyContent: 'space-between', cursor: 'pointer' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
            <Shield size={24} color="#ce82ff" />
            <span style={{ fontSize: '1.1rem', fontWeight: 'bold' }}>الخصوصية والشروط</span>
          </div>
          <ChevronLeft size={24} color="var(--text-muted)" />
        </div>
        
        <div 
          onClick={handleResetData}
          style={{ padding: '16px', display: 'flex', alignItems: 'center', gap: '16px', cursor: 'pointer', backgroundColor: 'var(--danger)', color: 'white' }}
        >
          <Trash2 size={24} color="white" />
          <span style={{ fontSize: '1.1rem', fontWeight: 'bold' }}>مسح جميع البيانات</span>
        </div>
      </div>

      <style>{`
        .switch {
          position: relative;
          display: inline-block;
          width: 50px;
          height: 28px;
        }
        .switch input {
          opacity: 0;
          width: 0;
          height: 0;
        }
        .slider {
          position: absolute;
          cursor: pointer;
          top: 0;
          left: 0;
          right: 0;
          bottom: 0;
          background-color: var(--border);
          transition: .4s;
        }
        .slider:before {
          position: absolute;
          content: "";
          height: 20px;
          width: 20px;
          left: 4px;
          bottom: 4px;
          background-color: white;
          transition: .4s;
        }
        input:checked + .slider {
          background-color: #58cc02;
        }
        input:checked + .slider:before {
          transform: translateX(22px);
        }
        .slider.round {
          border-radius: 34px;
        }
        .slider.round:before {
          border-radius: 50%;
        }
      `}</style>
    </div>
  );
};
