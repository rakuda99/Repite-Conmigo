import React from 'react';
import { BarChart2, Flame, Award, Clock } from 'lucide-react';

export const StatsPage = () => {
  return (
    <div style={{ flex: 1, backgroundColor: 'var(--background)', color: 'var(--text-main)', padding: '24px 16px', paddingBottom: '100px' }}>
      <h2 style={{ textAlign: 'center', marginBottom: '24px', fontSize: '1.75rem' }}>إحصائيات التعلم</h2>
      
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px', marginBottom: '32px' }} dir="rtl">
        <div className="card" style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '12px' }}>
          <Flame size={40} color="#ff9600" />
          <h3 style={{ margin: 0, fontSize: '2rem' }}>14</h3>
          <p style={{ color: 'var(--text-muted)', margin: 0, fontWeight: 'bold' }}>أيام حماس متتالية</p>
        </div>
        
        <div className="card" style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '12px' }}>
          <Award size={40} color="#fcc900" />
          <h3 style={{ margin: 0, fontSize: '2rem' }}>850</h3>
          <p style={{ color: 'var(--text-muted)', margin: 0, fontWeight: 'bold' }}>إجمالي النقاط</p>
        </div>
        
        <div className="card" style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '12px' }}>
          <BookOpenIcon size={40} color="#1cb0f6" />
          <h3 style={{ margin: 0, fontSize: '2rem' }}>124</h3>
          <p style={{ color: 'var(--text-muted)', margin: 0, fontWeight: 'bold' }}>كلمة تم تعلمها</p>
        </div>
        
        <div className="card" style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '12px' }}>
          <Clock size={40} color="#ce82ff" />
          <h3 style={{ margin: 0, fontSize: '2rem' }}>2.5</h3>
          <p style={{ color: 'var(--text-muted)', margin: 0, fontWeight: 'bold' }}>ساعات الدراسة</p>
        </div>
      </div>
      
      <div className="card" dir="rtl">
        <h3 style={{ marginBottom: '16px' }}>نشاط الأسبوع</h3>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end', height: '150px', padding: '16px 0' }}>
          {[30, 50, 20, 80, 40, 60, 100].map((h, i) => (
            <div key={i} style={{ width: '12%', backgroundColor: h === 100 ? '#58cc02' : '#e5e5e5', height: `${h}%`, borderRadius: '4px' }}></div>
          ))}
        </div>
        <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: '8px', color: 'var(--text-muted)' }}>
          <span>أ</span><span>ث</span><span>أ</span><span>خ</span><span>ج</span><span>س</span><span>ح</span>
        </div>
      </div>
    </div>
  );
};

const BookOpenIcon = ({ size, color }) => (
  <svg xmlns="http://www.w3.org/2000/svg" width={size} height={size} viewBox="0 0 24 24" fill="none" stroke={color} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"></path>
    <path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"></path>
  </svg>
);
