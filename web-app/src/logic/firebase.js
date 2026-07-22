import { initializeApp } from "firebase/app";
import { getFirestore } from "firebase/firestore";

const firebaseConfig = {
  apiKey: "AIzaSyBIVcKbdbOYdcGXcJylq5fxpynUMbTUmLI",
  authDomain: "repiteapp-6d79b.firebaseapp.com",
  projectId: "repiteapp-6d79b",
  storageBucket: "repiteapp-6d79b.firebasestorage.app",
  messagingSenderId: "929558025509",
  appId: "1:929558025509:web:5fd034a0c626c66fd3f6cc",
  measurementId: "G-1KFR1M78NM"
};

const app = initializeApp(firebaseConfig);
export const db = getFirestore(app);
