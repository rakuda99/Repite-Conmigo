import json
import re
import urllib.request
import urllib.parse
import time

def clean_anki_text(s):
    if not s:
        return ""
    text = s
    text = re.sub(r"\[sound:[^\]]+\]", "", text)
    text = re.sub(r"<[^>]*>", "", text)
    text = text.replace("&amp;", "&")
    text = text.replace("&nbsp;", " ")
    text = text.replace("&quot;", "\"")
    text = text.replace("&#x27;", "'")
    text = text.replace("&apos;", "'")
    text = text.replace("&lt;", "<")
    text = text.replace("&gt;", ">")
    text = text.replace('""', '"')
    text = re.sub(r"\s+", " ", text)
    return text.strip()

def contains_arabic(str_val):
    for char in str_val:
        if 0x0600 <= ord(char) <= 0x06FF:
            return True
    return False

def translate_to_arabic(text):
    url = f"https://translate.googleapis.com/translate_a/single?client=gtx&sl=es&tl=ar&dt=t&q={urllib.parse.quote(text)}"
    req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
    try:
        with urllib.request.urlopen(req) as resp:
            data = json.loads(resp.read().decode())
            return data[0][0][0].strip()
    except Exception as e:
        print(f"Translation error for '{text}': {e}")
        return ""

# Pre-defined high-quality daily life sentences for first 100 common vocabulary words
custom_sentences = {
    "el agua": "Quiero beber un vaso de agua.",
    "beber": "Bebo agua todos los días.",
    "comer": "Me gusta comer manzanas.",
    "él": "Él es un hombre muy inteligente.",
    "ella": "Ella es una mujer elegante.",
    "el hombre": "El hombre está en el hotel.",
    "la leche": "Bebo leche por la mañana.",
    "la manzana": "Compro una manzana roja.",
    "la mujer": "La mujer camina en el parque.",
    "el niño": "El niño juega en la calle.",
    "la niña": "La niña lee un libro interesante.",
    "el pan": "Comemos pan con queso.",
    "ser": "Quiero ser médico.",
    "eres": "Tú eres mi mejor amigo.",
    "es": "El carro es muy bonito.",
    "soy": "Soy estudiante de español.",
    "tú": "¿Tú tienes un perro grande?",
    "usted": "¿Cómo está usted hoy, señor?",
    "yo": "Yo vivo en una casa grande.",
    "adiós": "Él dice adiós al salir.",
    "disculpe": "Disculpe, ¿dónde está el baño?",
    "por favor": "Un café con leche, por favor.",
    "buenos días": "Buenos días a todos.",
    "gracias": "Muchas gracias por la ayuda.",
    "hola": "Hola, ¿cómo estás?",
    "no": "No tengo dinero hoy.",
    "buenas noches": "Buenas noches, mi familia.",
    "buenas tardes": "Buenas tardes, señor profesor.",
    "sí": "Sí, quiero comer pizza hoy.",
    "mucho gusto": "Mucho gusto en conocerte.",
    "de nada": "Gracias por todo. - De nada.",
    "lo siento": "Lo siento, no hablo inglés.",
    "hablar": "Quiero hablar español muy bien.",
    "perdón": "Perdón, no te escuché.",
    "inglés": "Él estudia inglés en la escuela.",
    "nada": "No hay nada en la maleta.",
    "español": "Hablo un poco de español.",
    "la tarde": "Estudio español por la tarde.",
    "el pasaporte": "Necesito mi pasaporte para viajar.",
    "mi": "Esta es mi maleta de viaje.",
    "la maleta": "Mi maleta es de color gris.",
    "el taxi": "Llamo a un taxi ahora.",
    "el telefono": "Mi teléfono está en la mesa.",
    "necesitar": "Necesito comprar ropa nueva.",
    "el hotel": "El hotel está cerca de la playa.",
    "tener": "Tengo una reserva en el hotel.",
    "la reserva": "Tengo una reserva para hoy.",
    "en": "Vivo en un apartamento cómodo.",
    "dónde": "¿Dónde está la estación de autobús?",
    "estar": "El hospital está muy lejos.",
    "aquí": "Aquí está tu boleto de tren.",
    "el autobús": "Tomo el autobús para ir a la escuela.",
    "el tren": "El tren sale a las diez.",
    "el dinero": "Necesito dinero para el taxi.",
    "el boleto": "Compro un boleto de ida.",
    "el supermercado": "Voy al supermercado a comprar comida.",
    "el baño": "El baño está muy limpio.",
    "cerrado": "El banco está cerrado hoy.",
    "el banco": "Voy al banco a cambiar dinero.",
    "la calle": "Camino por la calle principal.",
    "el hospital": "Mi hermano trabaja en el hospital.",
    "el museo": "El museo está abierto los sábados.",
    "el restaurante": "Comemos en un restaurante elegante.",
    "la mesa": "La mesa es grande y de madera.",
    "la persona": "Él es una persona muy simpática.",
    "para": "Este regalo es para mi madre.",
    "uno": "Tengo uno o dos libros de español.",
    "dos": "Compro dos botellas de jugo.",
    "tres": "Tengo tres hermanos mayores.",
    "el sándwich": "Quiero un sándwich de queso.",
    "de": "El sándwich es de carne.",
    "la carne": "No como carne los viernes.",
    "el pescado": "Me gusta comer pescado fresco.",
    "el queso": "Prefiero el queso con pan.",
    "la hamburguesa": "Quiero una hamburguesa con papas.",
    "con": "Tomo té caliente con azúcar.",
    "el café": "El café está muy caliente.",
    "el vaso": "Necesito un vaso de agua fría.",
    "la taza": "Tomo una taza de café por la mañana.",
    "el azúcar": "Prefiero el café sin azúcar.",
    "sin": "Quiero agua sin gas, por favor.",
    "la naranja": "Comemos una naranja dulce.",
    "el jugo": "Me gusta el jugo de naranja.",
    "la cuenta": "La cuenta, por favor, señor.",
    "querer": "Quiero pagar con tarjeta.",
    "pagar": "Tengo que pagar la cuenta del restaurante.",
    "la ensalada": "La ensalada tiene tomate y sal.",
    "el tomate": "Compro tomate fresco hoy.",
    "la sal": "La comida necesita un poco de sal.",
    "o": "¿Prefieres té o jugo?",
    "la madre": "Mi madre es una mujer muy buena.",
    "el padre": "Mi padre trabaja en la oficina.",
    "inteligente": "Mi hermano es muy inteligente.",
    "elegante": "El restaurante es muy elegante.",
    "el carro": "Compro un carro nuevo.",
    "la casa": "Mi casa está cerca del parque.",
    "hermano": "Mi hermano estudia medicina.",
    "muy": "El examen es muy fácil.",
    "el perro": "Mi perro corre en el parque.",
    "el gato": "Mi gato duerme en la cama.",
    "bonito": "Este apartamento es muy bonito.",
    "grande": "Vivo en una casa grande."
}

def get_sentence_for_word(word):
    word_cleaned = word.lower().strip()
    if word_cleaned in custom_sentences:
        return custom_sentences[word_cleaned]
    
    # Simple templates for other words
    if word_cleaned.startswith("el ") or word_cleaned.startswith("la ") or word_cleaned.startswith("un ") or word_cleaned.startswith("una "):
        return f"Yo tengo {word_cleaned} en mi casa."
    elif word_cleaned.endswith("ar") or word_cleaned.endswith("er") or word_cleaned.endswith("ir"):
        return f"Quiero {word_cleaned} hoy por la tarde."
    elif word_cleaned.endswith("o") or word_cleaned.endswith("a"):
        return f"El carro es muy {word_cleaned}."
    else:
        return f"Yo uso {word_cleaned} todos los días."

with open("scratch/anki_raw.txt", "r", encoding="utf-8") as f:
    lines = f.read().splitlines()

cards = []
seen = set()

print("Parsing words...")
for line in lines:
    if line.startswith("#") or not line.strip() or line.startswith("<") or line.startswith("NOTE:"):
        continue
    parts = line.split("\t")
    if len(parts) >= 5:
        front = clean_anki_text(parts[3])
        back = clean_anki_text(parts[4])
        if not front or not back:
            continue
        
        target_text = front
        translation_text = back
        
        front_is_arabic = contains_arabic(front)
        back_is_arabic = contains_arabic(back)
        
        if front_is_arabic and not back_is_arabic:
            target_text = back
            translation_text = front
        elif back_is_arabic and not front_is_arabic:
            target_text = front
            translation_text = back
            
        key = target_text.lower().strip()
        if key not in seen:
            seen.add(key)
            cards.append((target_text, translation_text))

print(f"Total unique words parsed: {len(cards)}")
print("Generating sentences and translating to Arabic...")

full_list = []
for idx, (es_word, ar_trans) in enumerate(cards):
    # Add word card
    full_list.append({
        "es": es_word,
        "ar": ar_trans,
        "type": "word"
    })
    
    # Generate daily sentence
    es_sentence = get_sentence_for_word(es_word)
    ar_sentence = translate_to_arabic(es_sentence)
    
    if not ar_sentence:
        ar_sentence = f"جملة تحتوي على {ar_trans}" # Fallback
        
    full_list.append({
        "es": es_sentence,
        "ar": ar_sentence,
        "type": "sentence"
    })
    
    if (idx + 1) % 20 == 0:
        print(f"Processed {idx+1}/{len(cards)} words...")
        time.sleep(0.5) # Avoid API rate limit

print(f"Total cards generated (words + sentences): {len(full_list)}")

# Split into chunks of 24 (12 words + 12 sentences each)
chunk_size = 24
lessons_list_assets = []
lessons_list_library = []

for i in range(0, len(full_list), chunk_size):
    chunk = full_list[i:i + chunk_size]
    part_num = (i // chunk_size) + 1
    
    lesson_id = f"lesson_duo_vocab_part_{part_num}"
    title_ar = f"مفردات وجمل ديولينجو - الجزء {part_num}"
    title_en = f"Duo Vocab & Sentences - Part {part_num}"
    
    # 1. Format for assets/lessons.json
    sentences_assets = []
    for item in chunk:
        sentences_assets.append({
            "es": item["es"],
            "ar": item["ar"]
        })
        
    lesson_assets = {
        "id": lesson_id,
        "title": {
            "ar-SA": title_ar,
            "en-US": title_en
        },
        "rawLevel": "Duo Spanish Vocab 🦉",
        "icon": "🦉",
        "color": "bg-green-500",
        "sentences": sentences_assets
    }
    lessons_list_assets.append(lesson_assets)
    
    # 2. Format for library.json
    sentences_library = []
    for item in chunk:
        sentences_library.append({
            "es": item["es"],
            "ar": item["ar"],
            "imagePrompt": f"Illustration of {item['es']}, flat style",
            "isGenerating": False,
            "translations": {
                "ar": item["ar"],
                "en": "",
                "es": item["es"]
            },
            "exampleTranslations": {
                "es": "",
                "ar": "",
                "en": ""
            },
            "isTranslating": False,
            "imageUrl": "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=400",
            "candidates": None
        })
        
    lesson_library = {
        "id": lesson_id,
        "title": {
            "ar-SA": title_ar,
            "en-US": title_en
        },
        "rawLevel": "Duo Spanish Vocab 🦉",
        "icon": "🦉",
        "color": "bg-green-500",
        "sentences": sentences_library
    }
    lessons_list_library.append(lesson_library)

# Save to assets/lessons.json
assets_path = "app/src/main/assets/lessons.json"
with open(assets_path, "r", encoding="utf-8") as f:
    lessons_db = json.load(f)
lessons_db["Duo Spanish Vocab 🦉"] = lessons_list_assets
with open(assets_path, "w", encoding="utf-8") as f:
    json.dump(lessons_db, f, ensure_ascii=False, indent=2)
print(f"Successfully embedded {len(lessons_list_assets)} lessons into {assets_path}")

# Save to library.json
lib_path = "library.json"
with open(lib_path, "r", encoding="utf-8") as f:
    lib_data = json.load(f)
lib_data = [item for item in lib_data if not item.get("id", "").startswith("lesson_duo_vocab_part_")]
lib_data.extend(lessons_list_library)
with open(lib_path, "w", encoding="utf-8") as f:
    json.dump(lib_data, f, ensure_ascii=False, indent=2)
print(f"Successfully embedded {len(lessons_list_library)} lessons into {lib_path}")

# Save to web-factory/src/lessons.json
web_path = "web-factory/src/lessons.json"
with open(web_path, "r", encoding="utf-8") as f:
    web_data = json.load(f)
web_data = [item for item in web_data if not item.get("id", "").startswith("lesson_duo_vocab_part_")]
web_data.extend(lessons_list_library)
with open(web_path, "w", encoding="utf-8") as f:
    json.dump(web_data, f, ensure_ascii=False, indent=2)
print(f"Successfully embedded {len(lessons_list_library)} lessons into {web_path}")

print("🎉 Sentence generation and embedding completed successfully!")
