import { initializeApp } from "firebase/app";
import { getFirestore, collection, getDocs, doc, setDoc, deleteDoc } from "firebase/firestore";
import { getAuth, signInAnonymously } from "firebase/auth";

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

async function run() {
  try {
    console.log("Signing in anonymously to Firebase...");
    await signInAnonymously(auth);
    console.log("Authentication successful.");

    console.log("Fetching all decks from Firestore...");
    const querySnapshot = await getDocs(collection(db, "lessons"));
    const decks = [];
    
    querySnapshot.forEach((doc) => {
      decks.push({ id: doc.id, ...doc.data() });
    });

    console.log(`Loaded ${decks.length} decks.`);

    // Sort decks alphabetically by title to ensure stable, predictable priority
    decks.sort((a, b) => a.title.localeCompare(b.title));

    const seenWords = {}; // word -> deckTitle
    const updatedDecks = [];
    let totalDuplicatesRemoved = 0;
    const removalLog = [];

    console.log("Analyzing duplicates across decks...");
    for (const deck of decks) {
      if (!deck.content || !Array.isArray(deck.content)) continue;

      const uniqueContent = [];
      for (const card of deck.content) {
        const wordKey = card.text.toLowerCase().trim();
        if (seenWords[wordKey]) {
          // This is a duplicate!
          totalDuplicatesRemoved++;
          if (removalLog.length < 50) {
            removalLog.push(`Removed "${card.text}" from "${deck.title}" (already exists in "${seenWords[wordKey]}")`);
          }
        } else {
          // First time seeing this word, keep it
          seenWords[wordKey] = deck.title;
          uniqueContent.push(card);
        }
      }

      updatedDecks.push({
        ...deck,
        content: uniqueContent
      });
    }

    console.log(`\nDuplicate cards detected and removed: ${totalDuplicatesRemoved}`);
    console.log("\nSamples of removed duplicates:");
    removalLog.slice(0, 15).forEach(log => console.log(` - ${log}`));

    console.log("\nSaving updated decks back to Firestore...");
    for (const deck of updatedDecks) {
      const docRef = doc(db, "lessons", deck.id);
      if (deck.content.length === 0) {
        console.log(`Deck "${deck.title}" is now empty, deleting document...`);
        await deleteDoc(docRef);
      } else {
        await setDoc(docRef, {
          id: deck.id,
          title: deck.title,
          content: deck.content,
          type: deck.type || "remote",
          rawLevel: deck.rawLevel || "beginner"
        });
      }
    }

    console.log("\n🎉 Global cross-deck de-duplication completed successfully!");
    process.exit(0);
  } catch (error) {
    console.error("Error during global de-duplication:", error);
    process.exit(1);
  }
}

run();
