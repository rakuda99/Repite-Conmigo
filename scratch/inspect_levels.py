import json

with open("app/src/main/assets/lessons.json", "r", encoding="utf-8") as f:
    data = json.load(f)

print("Top-level keys in lessons.json:", list(data.keys()))
