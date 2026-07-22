import json

with open("app/src/main/assets/lessons.json", "r", encoding="utf-8") as f:
    data = json.load(f)

for lvl in ["Primary Level 👶", "Intermediate Level 🚀"]:
    print(f"Level: {lvl}")
    for cat in data.get(lvl, []):
        cat_id = cat.get("id")
        title_ar = cat.get("title", {}).get("ar-SA", cat.get("title", {}).get("en-US", ""))
        sentences = cat.get("sentences", [])
        words_count = sum(1 for s in sentences if len(s.get("es", "")) < 15)
        sent_count = sum(1 for s in sentences if len(s.get("es", "")) >= 15)
        print(f"  - [{cat_id}] {title_ar}: {len(sentences)} total ({words_count} words, {sent_count} sentences)")
