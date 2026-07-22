import React from 'react';
import { BrowserRouter, Routes, Route, NavLink, Outlet } from 'react-router-dom';
import { BookOpen, MessageSquare, Layers, BarChart2, Settings, List } from 'lucide-react';
import './index.css';

import { WordsPage } from './pages/WordsPage';
import { SentencesPage } from './pages/SentencesPage';
import { StoriesPage } from './pages/StoriesPage';
import { StatsPage } from './pages/StatsPage';
import { SettingsPage } from './pages/SettingsPage';
import { VerbsPage } from './pages/VerbsPage';

// Layout with Bottom Navigation
const Layout = () => {
  return (
    <>
      <main>
        <Outlet />
      </main>
      <nav className="bottom-nav">
        <NavLink to="/" className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`} end>
          <BookOpen size={24} />
          <span>الكلمات</span>
        </NavLink>
        <NavLink to="/sentences" className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}>
          <MessageSquare size={24} />
          <span>الجمل</span>
        </NavLink>
        <NavLink to="/stories" className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}>
          <Layers size={24} />
          <span>القصص</span>
        </NavLink>
        <NavLink to="/verbs" className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}>
          <List size={24} />
          <span>الأفعال</span>
        </NavLink>
        <NavLink to="/stats" className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}>
          <BarChart2 size={24} />
          <span>إحصائيات</span>
        </NavLink>
        <NavLink to="/settings" className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}>
          <Settings size={24} />
          <span>إعدادات</span>
        </NavLink>
      </nav>
    </>
  );
};

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Layout />}>
          <Route index element={<WordsPage />} />
          <Route path="sentences" element={<SentencesPage />} />
          <Route path="stories" element={<StoriesPage />} />
          <Route path="verbs" element={<VerbsPage />} />
          <Route path="stats" element={<StatsPage />} />
          <Route path="settings" element={<SettingsPage />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App;
