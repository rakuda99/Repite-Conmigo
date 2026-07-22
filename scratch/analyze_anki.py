import sys
import collections

file_path = r"C:\Users\sulai\OneDrive\سطح المكتب\23-06-2026All Decks.txt"

decks = collections.defaultdict(int)
sample_cards = collections.defaultdict(list)

try:
    with open(file_path, "r", encoding="utf-8") as f:
        for line in f:
            if line.startswith("#"):
                continue
            parts = line.strip().split("\t")
            if len(parts) >= 5:
                deck_name = parts[2]
                front = parts[3]
                back = parts[4]
                decks[deck_name] += 1
                if len(sample_cards[deck_name]) < 3:
                    sample_cards[deck_name].append((front, back))
                    
    print(f"Total Unique Decks Found: {len(decks)}")
    print("-----------------------------------------")
    for deck, count in decks.items():
        print(f"Deck: {deck} ({count} cards)")
        print("Samples:")
        for front, back in sample_cards[deck]:
            print(f"  - Front: {front}  |  Back: {back}")
        print("-----------------------------------------")
except Exception as e:
    print(f"Error: {e}")
