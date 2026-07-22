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
            cards.append((target_text, translation_text))

# 2. Split cards into chunks of 25
chunk_size = 25
library_lessons = []

for i in range(0, len(cards), chunk_size):
    chunk = cards[i:i + chunk_size]
    part_num = (i // chunk_size) + 1
    
    lesson_id = f"lesson_duo_vocab_part_{part_num}"
    title_ar = f"مفردات ديولينجو - الجزء {part_num}"
    title_en = f"Duo Vocab - Part {part_num}"
    
    sentences_data = []
    for es_text, ar_text in chunk:
        sentences_data.append({
            "es": es_text,
            "ar": ar_text,
            "imagePrompt": f"Illustration of {es_text}, flat style",
            "isGenerating": False,
            "translations": {
                "ar": ar_text,
                "en": "",
                "es": es_text
            },
            "exampleTranslations": {
                "es": "",
                "ar": "",
                "en": ""
            },
            "isTranslating": False,
            "imageUrl": "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=400",
            "candidates": None
        })
        
    lesson = {
        "id": lesson_id,
        "title": {
            "ar-SA": title_ar,
            "en-US": title_en
        },
        "rawLevel": "Duo Spanish Vocab 🦉",
        "icon": "🦉",
        "color": "bg-green-500",
        "sentences": sentences_data
    }
    library_lessons.append(lesson)

# 3. Update library.json
lib_path = "library.json"
with open(lib_path, "r", encoding="utf-8") as f:
    lib_data = json.load(f)

# Filter out any old parts to avoid duplication
lib_data = [item for item in lib_data if not item.get("id", "").startswith("lesson_duo_vocab_part_")]
lib_data.extend(library_lessons)

with open(lib_path, "w", encoding="utf-8") as f:
    json.dump(lib_data, f, ensure_ascii=False, indent=2)

print(f"Successfully embedded {len(library_lessons)} lessons into {lib_path}")

# 4. Update web-factory/src/lessons.json
web_path = "web-factory/src/lessons.json"
with open(web_path, "r", encoding="utf-8") as f:
    web_data = json.load(f)

web_data = [item for item in web_data if not item.get("id", "").startswith("lesson_duo_vocab_part_")]
web_data.extend(library_lessons)

with open(web_path, "w", encoding="utf-8") as f:
    json.dump(web_data, f, ensure_ascii=False, indent=2)

print(f"Successfully embedded {len(library_lessons)} lessons into {web_path}")
