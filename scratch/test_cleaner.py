import re

file_path = r"C:\Users\sulai\OneDrive\سطح المكتب\23-06-2026All Decks.txt"

def clean_string(s):
    # Remove sound tags
    s = re.sub(r"\[sound:[^\]]+\]", "", s)
    # Remove HTML tags
    s = re.sub(r"<[^>]+>", "", s)
    # Replace entities
    s = s.replace("&nbsp;", " ")
    s = s.replace("&amp;", "&")
    s = s.replace("&quot;", '"')
    s = s.replace("&#x27;", "'")
    s = s.replace("&apos;", "'")
    s = s.replace("&lt;", "<")
    s = s.replace("&gt;", ">")
    # Clean up double/multiple spaces
    s = re.sub(r"\s+", " ", s)
    return s.strip()

with open(file_path, "r", encoding="utf-8") as f:
    count = 0
    for line in f:
        if line.startswith("#"):
            continue
        parts = line.strip().split("\t")
        if len(parts) >= 5:
            front = parts[3]
            back = parts[4]
            cleaned_front = clean_string(front)
            cleaned_back = clean_string(back)
            
            # If there was an HTML tag or sound tag, let's see before and after
            if "<" in front or "[" in front or "&" in front or "<" in back or "[" in back or "&" in back:
                print(f"Original Front: {front}")
                print(f"Cleaned Front:  {cleaned_front}")
                print(f"Original Back:  {back}")
                print(f"Cleaned Back:   {cleaned_back}")
                print("-" * 50)
                count += 1
                if count >= 10:
                    break
