import { initializeApp } from "firebase/app";
import { getFirestore, doc, setDoc, deleteDoc } from "firebase/firestore";
import { getAuth, signInAnonymously } from "firebase/auth";
import fs from "fs";

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

const app = initializeApp(firebaseConfig);
const db = getFirestore(app);
const auth = getAuth(app);

const filePath = "d:\\\\MY APP\\\\Repite Conmigo\\\\scratch\\\\anki_raw.txt";

function containsArabic(str) {
  for (let i = 0; i < str.length; i++) {
    const code = str.charCodeAt(i);
    if (code >= 0x0600 && code <= 0x06FF) {
      return true;
    }
  }
  return false;
}

function cleanText(s) {
  if (!s) return "";
  // 1. Remove [sound:...]
  s = s.replace(/\[sound:[^\]]+\]/g, "");
  // 2. Remove HTML tags
  s = s.replace(/<[^>]+>/g, "");
  // 3. Replace HTML entities
  s = s.replace(/&nbsp;/g, " ");
  s = s.replace(/&amp;/g, "&");
  s = s.replace(/&quot;/g, '"');
  s = s.replace(/&#x27;/g, "'");
  s = s.replace(/&apos;/g, "'");
  s = s.replace(/&lt;/g, "<");
  s = s.replace(/&gt;/g, ">");
  // 4. Remove double quotes
  s = s.replace(/""/g, '"');
  // 5. Clean up multiple whitespaces
  s = s.replace(/\s+/g, " ");
  return s.trim();
}

async function run() {
  try {
    console.log("Reading Anki file...");
    const content = fs.readFileSync(filePath, "utf-8");
    const lines = content.split(/\r?\n/).filter(line => line.trim().length > 0);

    const decks = {};

    console.log("Parsing cards...");
    for (const line of lines) {
      if (line.startsWith("#")) continue;

      const parts = line.split("\t");
      if (parts.length >= 5) {
        let deckName = parts[2].trim();
        if (deckName.includes("::")) {
          deckName = deckName.split("::").pop().trim();
        }
        if (!deckName) deckName = "General";

        const front = cleanText(parts[3]);
        const back = cleanText(parts[4]);

        if (!front || !back) continue;

        let targetText = front;
        let translationText = back;

        const frontIsArabic = containsArabic(front);
        const backIsArabic = containsArabic(back);

        if (frontIsArabic && !backIsArabic) {
          targetText = back;
          translationText = front;
        } else if (backIsArabic && !frontIsArabic) {
          targetText = front;
          translationText = back;
        }

        if (!decks[deckName]) {
          decks[deckName] = [];
        }

        decks[deckName].push({
          id: 0,
          text: targetText,
          translation: translationText,
          targetLang: "es",
          sourceLang: "ar",
          audioUrl: null,
          slowAudioUrl: null,
          phoneticHint: null,
          validationTags: null,
          category: deckName,
          contentType: targetText.length < 15 ? "word" : "sentence",
          imageUrl: null,
          externalId: null,
          localAudioPath: null,
          pronunciationScore: 0,
          memorizationDifficulty: 0
        });
      }
    }

    console.log("\nDe-duplicating and splitting cards per deck...");
    const finalDecks = {};
    const chunkSize = 25;
    for (const [deckName, cards] of Object.entries(decks)) {
      const seen = new Set();
      const uniqueCards = [];
      for (const card of cards) {
        const key = card.text.toLowerCase().trim();
        if (!seen.has(key)) {
          seen.add(key);
          uniqueCards.push(card);
        }
      }
      
      if (uniqueCards.length > chunkSize) {
        for (let i = 0; i < uniqueCards.length; i += chunkSize) {
          const chunk = uniqueCards.slice(i, i + chunkSize);
          const partNum = Math.floor(i / chunkSize) + 1;
          const chunkDeckName = `${deckName} - Part ${partNum}`;
          
          chunk.forEach(c => {
            c.category = chunkDeckName;
          });
          
          finalDecks[chunkDeckName] = chunk;
        }
        console.log(`- Deck: "${deckName}" split into ${Math.ceil(uniqueCards.length / chunkSize)} parts (total ${uniqueCards.length} unique cards)`);
      } else {
        finalDecks[deckName] = uniqueCards;
        console.log(`- Deck: "${deckName}" has ${uniqueCards.length} unique cards`);
      }
    }

    console.log("\nSigning in anonymously to Firebase...");
    const userCredential = await signInAnonymously(auth);
    console.log("Logged in successfully. User UID:", userCredential.user.uid);

    console.log("\nDeleting old consolidated decks from Firestore...");
    try {
      await deleteDoc(doc(db, "lessons", "Duo Spanish Vocab List duolingo"));
      console.log("Deleted old Duo Spanish Vocab List duolingo doc successfully.");
    } catch (e) {
      console.log("Could not delete old doc or it did not exist:", e.message);
    }

    console.log("\nUploading decks to Firestore...");
    for (const [deckName, cards] of Object.entries(finalDecks)) {
      if (cards.length === 0) continue;
      
      const docId = deckName;
      const lessonDoc = {
        id: docId,
        title: deckName,
        content: cards,
        type: "remote",
        rawLevel: "beginner"
      };

      console.log(`Uploading "${deckName}" (${cards.length} cards)...`);
      await setDoc(doc(db, "lessons", docId), lessonDoc);
    }

    console.log("\n🎉 All decks uploaded successfully to the cloud!");
    process.exit(0);
  } catch (error) {
    console.error("Error during upload:", error);
    process.exit(1);
  }
}

run();
