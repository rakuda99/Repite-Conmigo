import re

file_path = r"C:\Users\sulai\OneDrive\سطح المكتب\23-06-2026All Decks.txt"

html_tags = set()
sound_tags = set()
entities = set()

with open(file_path, "r", encoding="utf-8") as f:
    for line in f:
        if line.startswith("#"):
            continue
        parts = line.strip().split("\t")
        if len(parts) >= 5:
            front = parts[3]
            back = parts[4]
            # Find html tags
            for tag in re.findall(r"<[^>]+>", front + " " + back):
                html_tags.add(tag)
            # Find sound tags
            for tag in re.findall(r"\[sound:[^\]]+\]", front + " " + back):
                sound_tags.add(tag)
            # Find html entities
            for ent in re.findall(r"&[a-zA-Z0-9#]+;", front + " " + back):
                entities.add(ent)

print("Unique HTML Tags:")
print(html_tags)
print("\nUnique HTML Entities:")
print(entities)
print(f"\nTotal unique sound tags found: {len(sound_tags)}")
if sound_tags:
    print("Sample sound tags:", list(sound_tags)[:5])
