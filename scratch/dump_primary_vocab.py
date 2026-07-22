import json

with open("app/src/main/assets/lessons.json", "r", encoding="utf-8") as f:
    data = json.load(f)

primary_categories = data.get("Primary Level 👶", [])
for cat in primary_categories:
    title_ar = cat.get("title", {}).get("ar-SA", "")
    sentences = cat.get("sentences", [])
    words = [s.get("es", "") for s in sentences if len(s.get("es", "")) < 15]
    print(f"Category: {title_ar}")
    print(f"  Words: {words}")
