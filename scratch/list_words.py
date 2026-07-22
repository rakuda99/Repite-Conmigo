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

with open("scratch/anki_raw.txt", "r", encoding="utf-8") as f:
    lines = f.read().splitlines()

words = []
seen = set()
for line in lines:
    if line.startswith("#") or not line.strip() or line.startswith("<") or line.startswith("NOTE:"):
        continue
    parts = line.split("\t")
    if len(parts) >= 5:
        es = clean_anki_text(parts[3])
        ar = clean_anki_text(parts[4])
        if es and ar and es.lower() not in seen:
            seen.add(es.lower())
            words.append((es, ar))

print(f"Total words: {len(words)}")
for idx, (es, ar) in enumerate(words[:50]):
    print(f"{idx+1}. {es} -> {ar}")
