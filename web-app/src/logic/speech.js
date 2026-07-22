export function playTTS(text, language = 'es-ES', onComplete = null, rate = 0.9) {
  if (!('speechSynthesis' in window)) {
    console.error("Text-to-speech not supported in this browser.");
    if (onComplete) onComplete();
    return;
  }
  
  window.speechSynthesis.cancel(); // Cancel any ongoing speech
  const utterance = new SpeechSynthesisUtterance(text);
  utterance.lang = language;
  utterance.rate = rate;
  
  if (onComplete) {
    utterance.onend = onComplete;
  }
  utterance.onerror = (e) => {
    console.error("TTS Error:", e);
    // Don't alert here to avoid spamming the user during word clicks
    if (onComplete) onComplete(e);
  };
  
  window.speechSynthesis.speak(utterance);
}

export function stopTTS() {
  if ('speechSynthesis' in window) {
    window.speechSynthesis.cancel();
  }
}

// Speech-to-Text (STT)
export function startSTT(language = 'es-ES', onResult, onError, onEnd) {
  const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
  
  if (!SpeechRecognition) {
    if (onError) onError("Speech recognition not supported in this browser.");
    return null;
  }
  
  const recognition = new SpeechRecognition();
  recognition.lang = language;
  recognition.continuous = false;
  recognition.interimResults = true;
  
  recognition.onresult = (event) => {
    let finalTranscript = '';
    let interimTranscript = '';
    
    for (let i = event.resultIndex; i < event.results.length; ++i) {
      if (event.results[i].isFinal) {
        finalTranscript += event.results[i][0].transcript;
      } else {
        interimTranscript += event.results[i][0].transcript;
      }
    }
    
    if (onResult) {
      onResult(finalTranscript || interimTranscript, finalTranscript !== '');
    }
  };
  
  recognition.onerror = (event) => {
    if (onError) onError(event.error);
  };
  
  recognition.onend = () => {
    if (onEnd) onEnd();
  };
  
  try {
    recognition.start();
    return recognition;
  } catch (e) {
    console.error(e);
    if (onError) onError(e.message);
    return null;
  }
}
