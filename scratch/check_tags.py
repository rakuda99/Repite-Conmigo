with open("scratch/anki_raw.txt", "r", encoding="utf-8") as f:
    lines = f.read().splitlines()

non_empty_tags = 0
unique_tags = set()
for line in lines:
    if line.startswith("#") or not line.strip() or line.startswith("<"):
        continue
    parts = line.split("\t")
    if len(parts) > 5:
        tag = parts[5].strip()
        if tag:
            non_empty_tags += 1
            unique_tags.add(tag)

print(f"Cards with tags: {non_empty_tags}")
print(f"Unique tags: {list(unique_tags)[:10]}")
