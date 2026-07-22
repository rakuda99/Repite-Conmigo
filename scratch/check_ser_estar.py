import json

json_path = r"d:\MY APP\Repite Conmigo\app\src\main\assets\lessons.json"

try:
    with open(json_path, "r", encoding="utf-8") as f:
        data = json.load(f)
        
    print("Main keys in lessons.json:", list(data.keys()))
    
    # Let's find the lesson with id "ser_vs_estar" or title "Ser vs Estar"
    found = False
    for level, lessons in data.items():
        if isinstance(lessons, list):
            for lesson in lessons:
                titles = lesson.get("title", {})
                lesson_id = lesson.get("id", "")
                if "Ser vs Estar" in titles.values() or "ser_vs_estar" in lesson_id.lower():
                    print(f"\nFound Lesson! ID: {lesson_id}, Titles: {titles}")
                    sentences = lesson.get("sentences", [])
                    print(f"Total sentences: {len(sentences)}")
                    print("Samples:")
                    for idx, s in enumerate(sentences[:10]):
                        print(f"  {idx+1}. {s}")
                    found = True
                    break
        if found:
            break
            
    if not found:
        print("Lesson 'Ser vs Estar' not found directly. Searching for category in sentences...")
except Exception as e:
    print("Error:", e)
