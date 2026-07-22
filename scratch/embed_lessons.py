import json
import re

def clean_anki_text(s):
    if not s:
        return ""
    text = s
    text = re.sub(r"\[sound:[^\]]+\]", "", text)
    text = re.sub(r"<[^>]*>", "", text)
    text = text.replace("&amp;", "&")
    text = text.replace("&nbsp;", " ")
    text = text.replace("&quot;", "\"")
    text = text.replace("&#x27;", "'")
    text = text.replace("&apos;", "'")
    text = text.replace("&lt;", "<")
    text = text.replace("&gt;", ">")
    text = text.replace('""', '"')
    text = re.sub(r"\s+", " ", text)
    return text.strip()

def contains_arabic(str_val):
    for char in str_val:
        if 0x0600 <= ord(char) <= 0x06FF:
            return True
    return False

# 1. Read and parse scratch/anki_raw.txt
with open("scratch/anki_raw.txt", "r", encoding="utf-8") as f:
    lines = f.read().splitlines()

cards = []
seen = set()

for line in lines:
    if line.startswith("#") or not line.strip() or line.startswith("<") or line.startswith("NOTE:"):
        continue
    parts = line.split("\t")
    if len(parts) >= 5:
        front = clean_anki_text(parts[3])
        back = clean_anki_text(parts[4])
        if not front or not back:
            continue
        
        target_text = front
        translation_text = back
        
        front_is_arabic = contains_arabic(front)
        back_is_arabic = contains_arabic(back)
        
        if front_is_arabic and not back_is_arabic:
            target_text = back
            translation_text = front
        elif back_is_arabic and not front_is_arabic:
            target_text = front
            translation_text = back
            
        key = target_text.lower().strip()
        if key not in seen:
            seen.add(key)
            cards.append({
                "es": target_text,
                "ar": translation_text
            })

print(f"Total unique cards parsed: {len(cards)}")

# 2. Split cards into chunks of 25
chunk_size = 25
lessons_list = []

for i in range(0, len(cards), chunk_size):
    chunk = cards[i:i + chunk_size]
    part_num = (i // chunk_size) + 1
    
    lesson_id = f"lesson_duo_vocab_part_{part_num}"
    title_ar = f"مفردات ديولينجو - الجزء {part_num}"
    title_en = f"Duo Vocab - Part {part_num}"
    
    lesson = {
        "id": lesson_id,
        "title": {
            "ar-SA": title_ar,
            "en-US": title_en
        },
        "rawLevel": "Duo Spanish Vocab 🦉",
        "icon": "🦉",
        "color": "bg-green-500",
        "sentences": chunk
    }
    lessons_list.append(lesson)

# 3. Load app/src/main/assets/lessons.json
assets_path = "app/src/main/assets/lessons.json"
with open(assets_path, "r", encoding="utf-8") as f:
    lessons_db = json.load(f)

# 4. Append/overwrite our level key
lessons_db["Duo Spanish Vocab 🦉"] = lessons_list

# 5. Save back to lessons.json
with open(assets_path, "w", encoding="utf-8") as f:
    json.dump(lessons_db, f, ensure_ascii=False, indent=2)

print(f"Successfully embedded {len(lessons_list)} lessons under level 'Duo Spanish Vocab 🦉' inside {assets_path}")
