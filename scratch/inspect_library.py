import json

with open("library.json", "r", encoding="utf-8") as f:
    data = json.load(f)

for lesson in data:
    if len(lesson.get("sentences", [])) > 0:
        s = lesson["sentences"][0]
        print("Keys:", list(s.keys()))
        for k in list(s.keys()):
            val = s[k]
            t = type(val).__name__
            if t == 'str':
                print(f"  {k} (str): {val[:50]}")
            else:
                print(f"  {k} ({t})")
        break
