import { db } from './firebase';
import { collection, addDoc, getDocs, query, orderBy, limit, serverTimestamp } from 'firebase/firestore';

const DECKS_COLLECTION = 'community_decks';

// Upload a deck to the cloud
export const uploadDeckToCloud = async (deck) => {
  try {
    const deckData = {
      ...deck,
      // Ensure we don't upload personal local IDs like "custom_123"
      originalId: deck.id,
      uploadedAt: serverTimestamp(),
      author: 'مستخدم مجهول', // We can add user names later if we implement auth
    };
    
    // Remove undefined or invalid fields
    delete deckData.id;

    const docRef = await addDoc(collection(db, DECKS_COLLECTION), deckData);
    return docRef.id;
  } catch (error) {
    console.error("Error uploading deck: ", error);
    throw error;
  }
};

// Fetch community decks from the cloud
export const fetchCommunityDecks = async () => {
  try {
    const q = query(collection(db, DECKS_COLLECTION), orderBy('uploadedAt', 'desc'), limit(20));
    const querySnapshot = await getDocs(q);
    const decks = [];
    querySnapshot.forEach((doc) => {
      decks.push({ id: doc.id, ...doc.data() });
    });
    return decks;
  } catch (error) {
    console.error("Error fetching decks: ", error);
    throw error;
  }
};
