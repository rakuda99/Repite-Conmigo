import json

log_path = r"C:\Users\sulai\.gemini\antigravity\brain\95936790-0843-4c95-a816-deec03852da1\.system_generated\logs\transcript_full.jsonl"

anki_lines = []
with open(log_path, "r", encoding="utf-8") as f:
    for line in f:
        try:
            data = json.loads(line)
            if data.get("type") == "USER_INPUT":
                content = data.get("content", "")
                if "#separator:tab" in content:
                    # Found the Anki export!
                    print("Found USER_INPUT with Anki export.")
                    anki_lines.append(content)
        except Exception as e:
            pass

if anki_lines:
    # Save the last user input containing the Anki export
    raw_content = anki_lines[-1]
    with open("scratch/anki_raw.txt", "w", encoding="utf-8") as out:
        out.write(raw_content)
    print(f"Successfully saved {len(raw_content.splitlines())} lines to scratch/anki_raw.txt")
else:
    print("No Anki export found in transcript_full.jsonl")
