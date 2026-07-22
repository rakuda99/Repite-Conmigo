import { initializeApp } from "firebase/app";
import { getFirestore, collection, addDoc, serverTimestamp } from "firebase/firestore";
import fs from "fs";

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
const db = getFirestore(app);

const libraryPath = './src/library.json';

async function uploadLibrary() {
  try {
    const data = JSON.parse(fs.readFileSync(libraryPath, 'utf-8'));
    console.log(`Loaded library with ${data.length} decks.`);

    const DECKS_COLLECTION = 'community_decks';
    let count = 0;

    for (const deck of data) {
      const deckData = {
        title: deck.title,
        sentences: deck.sentences,
        originalId: deck.id || `legacy_${count}`,
        uploadedAt: new Date(), // using local date since serverTimestamp might not work in node sdk
        author: 'المكتبة الرسمية'
      };

      await addDoc(collection(db, DECKS_COLLECTION), deckData);
      console.log(`Uploaded deck: ${deck.title['ar-SA'] || deck.title['en-US']}`);
      count++;
    }

    console.log(`Successfully uploaded ${count} decks to Firestore!`);
    process.exit(0);
  } catch (error) {
    console.error("Error uploading: ", error);
    process.exit(1);
  }
}

uploadLibrary();
