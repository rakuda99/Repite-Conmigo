import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.jsx'

// Global Error Catch for Black Screen Debugging
window.onerror = function(msg, url, lineNo, columnNo, error) {
  const root = document.getElementById('root');
  if (root) {
    root.innerHTML = `<div style="padding:40px; color:#f87171; background:#030712; height:100vh; font-family:sans-serif;">
      <h1 style="color:white">FATAL STARTUP ERROR</h1>
      <p>${msg}</p>
      <pre style="background:rgba(255,255,255,0.1); padding:20px; border-radius:12px; overflow:auto;">${error?.stack || ''}</pre>
      <button onclick="location.reload()" style="padding:10px 20px; background:#3b82f6; color:white; border:none; borderRadius:8px; cursor:pointer">Retry</button>
    </div>`;
  }
  return false;
};

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <App />
  </StrictMode>,
)

