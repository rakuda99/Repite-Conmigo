import requests
import collections

api_key = "AIzaSyCjcEWZU2Y_EBPcTx5lu0Tg-y3tYKu13BQ"

# 1. Sign in anonymously to get idToken
auth_url = f"https://identitytoolkit.googleapis.com/v1/accounts:signUp?key={api_key}"
try:
    auth_res = requests.post(auth_url, json={"returnSecureToken": True}).json()
    id_token = auth_res.get("idToken")
    if not id_token:
        print("Auth failed:", auth_res)
        exit(1)
except Exception as e:
    print("Auth error:", e)
    exit(1)

# 2. Fetch all documents from 'lessons' collection using REST API
url = "https://firestore.googleapis.com/v1/projects/repite-conmigo/databases/(default)/documents/lessons"
headers = {"Authorization": f"Bearer {id_token}"}

try:
    print("Fetching lessons from Firestore...")
    r = requests.get(url, headers=headers)
    res = r.json()
    documents = res.get("documents", [])
    
    # Handle pagination if there are more than 100 documents (Firestore default limit)
    next_page_token = res.get("nextPageToken")
    while next_page_token:
        r = requests.get(url, headers=headers, params={"pageToken": next_page_token})
        res = r.json()
        documents.extend(res.get("documents", []))
        next_page_token = res.get("nextPageToken")
        
    print(f"Loaded {len(documents)} decks.")
    
    # 3. Analyze duplicates
    word_map = collections.defaultdict(list) # word -> list of deck titles
    
    for doc in documents:
        fields = doc.get("fields", {})
        title = fields.get("title", {}).get("stringValue", "Unknown")
        content_val = fields.get("content", {}).get("arrayValue", {}).get("values", [])
        
        for val in content_val:
            map_val = val.get("mapValue", {}).get("fields", {})
            text = map_val.get("text", {}).get("stringValue", "").strip().lower()
            if text:
                word_map[text].append(title)
                
    # Find duplicates
    duplicates = {word: decks for word, decks in word_map.items() if len(decks) > 1}
    
    print(f"\nTotal unique words across all decks: {len(word_map)}")
    print(f"Total words that appear in multiple decks: {len(duplicates)}")
    print("--------------------------------------------------")
    
    # Print a few samples of duplicates
    count = 0
    for word, decks in sorted(duplicates.items(), key=lambda x: len(x[1]), reverse=True):
        print(f"Word: \"{word}\" appears in {len(decks)} decks: {', '.join(decks)}")
        count += 1
        if count >= 20:
            break
            
except Exception as e:
    print("Error fetching/analyzing data:", e)
