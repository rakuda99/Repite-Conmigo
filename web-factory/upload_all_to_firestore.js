import { initializeApp } from "firebase/app";
import { getFirestore, doc, setDoc } from "firebase/firestore";
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

const assetsPath = "d:\\\\MY APP\\\\Repite Conmigo\\\\app\\\\src\\\\main\\\\assets\\\\lessons.json";

async function run() {
  try {
    console.log("Reading assets lessons.json...");
    const rawData = fs.readFileSync(assetsPath, "utf-8");
    const lessonsDb = JSON.parse(rawData);

    console.log("\nSigning in anonymously to Firebase...");
    const userCredential = await signInAnonymously(auth);
    console.log("Logged in successfully. User UID:", userCredential.user.uid);

    let totalLessonsCount = 0;
    for (const [levelName, lessons] of Object.entries(lessonsDb)) {
      console.log(`\nProcessing Level: "${levelName}"...`);
      for (const lesson of lessons) {
        const docId = lesson.id;
        const title = lesson.title["ar-SA"] || lesson.title["en-US"] || lesson.id;
        
        const cards = lesson.sentences.map((s, idx) => {
          const rawContentType = s.contentType || (s.es.length < 15 ? "word" : "sentence");
          return {
            id: idx,
            text: s.es,
            translation: s.ar || "",
            targetLang: "es",
            sourceLang: "ar",
            category: title,
            contentType: levelName.includes("Stories") ? "passage" : rawContentType,
            audioUrl: null,
            slowAudioUrl: null,
            phoneticHint: null,
            validationTags: null,
            imageUrl: s.imageUrl || null,
            externalId: null,
            localAudioPath: null,
            pronunciationScore: 0,
            memorizationDifficulty: 0
          };
        });

        const lessonDoc = {
          id: docId,
          title: title,
          content: cards,
          type: "remote",
          rawLevel: levelName.toLowerCase().replace(" level", "").replace(" 👶", "").replace(" 📘", "").replace(" 🚀", "").replace(" 🎓", "").replace(" 🦉", "").replace(" 📚", "").trim()
        };

        console.log(`Uploading [${levelName}] -> "${title}" (${cards.length} cards)...`);
        await setDoc(doc(db, "lessons", docId), lessonDoc);
        totalLessonsCount++;
      }
    }

    console.log(`\n🎉 All ${totalLessonsCount} lessons successfully uploaded to Firestore!`);
    process.exit(0);
  } catch (error) {
    console.error("Error during upload:", error);
    process.exit(1);
  }
}

run();
