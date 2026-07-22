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

// Define the target consolidated decks
const MERGE_MAPS = {
  // 1. أهم الأفعال الإسبانية
  "الأفعال الإسبانية": "أهم الأفعال الإسبانية",
  "أفعال أساسية للتعلم": "أهم الأفعال الإسبانية",
  "40 Spanish verbs": "أهم الأفعال الإسبانية",
  "most important verbs in the Spanish to Arabic": "أهم الأفعال الإسبانية",
  "الافعال الاسبانية الاهم": "أهم الأفعال الإسبانية",
  "Verbs 1": "أهم الأفعال الإسبانية",
  "Verbs 2": "أهم الأفعال الإسبانية",
  "أمثلة مع أفعال": "أهم الأفعال الإسبانية",

  // 2. منهج Duolingo - جمل ومحادثات
  "1. الأشياء المفقودة والبحث عنها": "منهج Duolingo - جمل ومحادثات",
  "2. الأشياء الشخصية والممتلكات": "منهج Duolingo - جمل ومحادثات",
  "3. الصفات الشخصية": "منهج Duolingo - جمل ومحادثات",
  "4. الأسئلة الشائعة والمحادثات ا": "منهج Duolingo - جمل ومحادثات",
  "5. التعريف بالنفس والهوايات": "منهج Duolingo - جمل ومحادثات",
  "6. الفنادق والإقامة": "منهج Duolingo - جمل ومحادثات",
  "7. المواصلات": "منهج Duolingo - جمل ومحادثات",
  "8. الخدمات المصرفية والعملات": "منهج Duolingo - جمل ومحادثات",
  "9. الصحة": "منهج Duolingo - جمل ومحادثات",
  "10. التعبيرات العاطفية": "منهج Duolingo - جمل ومحادثات",
  "11. الأماكن والمواقع": "منهج Duolingo - جمل ومحادثات",
  "12. الترحيب والمحادثات الاجتماع": "منهج Duolingo - جمل ومحادثات",
  "Duolingo 1": "منهج Duolingo - جمل ومحادثات",
  "Duolingo": "منهج Duolingo - جمل ومحادثات",
  "Doulingowords26-06-2025": "منهج Duolingo - جمل ومحادثات",
  "El clima": "منهج Duolingo - جمل ومحادثات",
  "Mercato": "منهج Duolingo - جمل ومحادثات",
  "words 1": "منهج Duolingo - جمل ومحادثات",
  "words 2": "منهج Duolingo - جمل ومحادثات",
  "مراجعه عامه 1": "منهج Duolingo - جمل ومحادثات",
  "مراجعه1": "منهج Duolingo - جمل ومحادثات",
  "‏المستوى 8": "منهج Duolingo - جمل ومحادثات",

  // 3. المفردات اليومية والمنزلية
  "أجزاء جسم الإنسان": "المفردات اليومية والمنزلية",
  "الاوانLos Colores": "المفردات اليومية والمنزلية",
  "الحيوانات (animal)": "المفردات اليومية والمنزلية",
  "الصفاتCualidades": "المفردات اليومية والمنزلية",
  "العائلة والأقارب La Familia y Los": "المفردات اليومية والمنزلية",
  "الفواكه والخضروات las frutas y los": "المفردات اليومية والمنزلية",
  "الوقتLos Tiempos": "المفردات اليومية والمنزلية",
  "على المائدةSobre la mesa": "المفردات اليومية والمنزلية",
  "في المطبخ En la cocina": "المفردات اليومية والمنزلية",
  "في المنزل En la casa": "المفردات اليومية والمنزلية",
  "كلمات متداولة palabras habituales": "المفردات اليومية والمنزلية",
  "في النطعم": "المفردات اليومية والمنزلية",

  // 4. جمل الثناء والتعبيرات العاطفية
  "جمل ثناء  2": "جمل الثناء والتعبيرات العاطفية",
  "جمل ثناء مهذبة": "جمل الثناء والتعبيرات العاطفية",
  "رومانسيات": "جمل الثناء والتعبيرات العاطفية",

  // 5. منهج تعلم الإسبانية في 30 يوماً
  "أدوات ومفاهيم التعلم": "منهج تعلم الإسبانية في 30 يوماً",
  "الأهداف والدوافع": "منهج تعلم الإسبانية في 30 يوماً",
  "المحادثة والأنشطة": "منهج تعلم الإسبانية في 30 يوماً",
  "المشاعر والصفات": "منهج تعلم الإسبانية في 30 يوماً",
  "الوقت والتكرار": "منهج تعلم الإسبانية في 30 يوماً",
  "Spanish Questions": "منهج تعلم الإسبانية في 30 يوماً",

  // 6. منهج الأستاذة نورة
  "1-15": "منهج الأستاذة نورة",
  "15-19": "منهج الأستاذة نورة",
  "28 29 30": "منهج الأستاذة نورة",

  // 7. أدوات الاستفهام
  "Spanish question words": "أدوات الاستفهام",
  "كلمات الاستفهام": "أدوات الاستفهام",

  // 8. إعادة تسمية المجموعات الفردية لتبدو ممتازة
  "Arabic Spanish proverbs": "الأمثال والحكم الإسبانية",
  "100 common Spanish vocabulary words": "أهم 100 كلمة إسبانية",
  "كلمات أصلها عربي": "كلمات إسبانية ذات أصل عربي",
  "سوال": "أسئلة إسبانية شائعة",
  "اهم 200كلمه": "أهم 200 كلمة إسبانية"
};

async function run() {
  try {
    console.log("Signing in anonymously to Firebase...");
    await signInAnonymously(auth);
    console.log("Authentication successful.");

    console.log("Fetching all decks from Firestore...");
    const querySnapshot = await getDocs(collection(db, "lessons"));
    const oldDecks = [];
    
    querySnapshot.forEach((doc) => {
      oldDecks.push({ id: doc.id, ...doc.data() });
    });

    console.log(`Loaded ${oldDecks.length} decks from Firestore.`);

    const consolidatedDecks = {};
    const decksToDelete = new Set();

    for (const deck of oldDecks) {
      const title = deck.title.trim();
      const targetTitle = MERGE_MAPS[title] || title; // If not in map, keep its original name

      if (targetTitle !== title) {
        decksToDelete.add(deck.id);
      }

      if (!consolidatedDecks[targetTitle]) {
        consolidatedDecks[targetTitle] = {
          id: targetTitle,
          title: targetTitle,
          content: [],
          type: "remote",
          rawLevel: "beginner"
        };
      }

      if (deck.content && Array.isArray(deck.content)) {
        // Add cards, updating their internal category property to the new name
        const updatedCards = deck.content.map(card => ({
          ...card,
          category: targetTitle
        }));
        consolidatedDecks[targetTitle].content.push(...updatedCards);
      }
    }

    console.log("\nDe-duplicating consolidated decks...");
    const finalDecks = [];
    for (const [title, deck] of Object.entries(consolidatedDecks)) {
      const seen = new Set();
      const uniqueContent = [];
      for (const card of deck.content) {
        const key = card.text.toLowerCase().trim();
        if (!seen.has(key)) {
          seen.add(key);
          uniqueContent.push(card);
        }
      }
      deck.content = uniqueContent;
      if (deck.content.length > 0) {
        finalDecks.push(deck);
      }
    }

    console.log(`Consolidated into ${finalDecks.length} unique, rich decks.`);
    finalDecks.forEach(d => {
      console.log(` - "${d.title}": ${d.content.length} unique cards`);
    });

    console.log("\nDeleting old merged/renamed documents from Firestore...");
    for (const docId of decksToDelete) {
      console.log(`Deleting old deck: "${docId}"...`);
      await deleteDoc(doc(db, "lessons", docId));
    }

    console.log("\nUploading new consolidated decks to Firestore...");
    for (const deck of finalDecks) {
      console.log(`Uploading consolidated deck: "${deck.title}" (${deck.content.length} cards)...`);
      await setDoc(doc(db, "lessons", deck.id), deck);
    }

    console.log("\n🎉 Decks successfully consolidated and uploaded to the cloud!");
    process.exit(0);
  } catch (error) {
    console.error("Error during consolidation:", error);
    process.exit(1);
  }
}

run();
