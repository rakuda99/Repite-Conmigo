import json
import os

def get_spanish_number(n):
    if n == 100:
        return "cien"
    units = ["", "uno", "dos", "tres", "cuatro", "cinco", "seis", "siete", "ocho", "nueve"]
    tens = ["", "diez", "veinte", "treinta", "cuarenta", "cincuenta", "sesenta", "setenta", "ochenta", "noventa"]
    
    if n < 10:
        return units[n]
    if 11 <= n <= 15:
        return {11: "once", 12: "doce", 13: "trece", 14: "catorce", 15: "quince"}[n]
    if 16 <= n <= 19:
        if n == 16:
            return "dieciséis"
        return "dieci" + units[n % 10]
    if n == 20:
        return "veinte"
    if 21 <= n <= 29:
        if n == 21:
            return "veintiuno"
        if n == 22:
            return "veintidós"
        if n == 23:
            return "veintitrés"
        if n == 26:
            return "veintiséis"
        return "veinti" + units[n % 10]
    t = n // 10
    u = n % 10
    if u == 0:
        return tens[t]
    else:
        return f"{tens[t]} y {units[u]}"

def get_arabic_number(n):
    if n == 100:
        return "مئة"
    units = ["", "واحد", "اثنان", "ثلاثة", "أربعة", "خمسة", "ستة", "سبعة", "ثمانية", "تسعة"]
    tens = ["", "عشرة", "عشرون", "ثلاثون", "أربعون", "خمسون", "ستون", "سبعون", "ثمانون", "تسعون"]
    
    if n < 10:
        return units[n]
    if n == 10:
        return "عشرة"
    if n == 11:
        return "أحد عشر"
    if n == 12:
        return "اثنا عشر"
    if 13 <= n <= 19:
        return f"{units[n % 10]} عشر"
    t = n // 10
    u = n % 10
    if u == 0:
        return tens[t]
    else:
        return f"{units[u]} و{tens[t]}"

def format_number_for_noun(number_spanish, is_feminine):
    if number_spanish == "uno":
        return "una" if is_feminine else "un"
    if number_spanish.endswith(" y uno"):
        return number_spanish.replace(" y uno", " y una") if is_feminine else number_spanish.replace(" y uno", " y un")
    if number_spanish == "veintiuno":
        return "veintiuna" if is_feminine else "veintiún"
    return number_spanish

def get_number_example(n):
    es_num = get_spanish_number(n)
    ar_num = get_arabic_number(n)
    
    scenario = n % 5
    if scenario == 1:
        if n == 1:
            return ("Compré una manzana.", "اشتريت تفاحة واحدة.")
        elif n == 2:
            return ("Compré dos manzanas.", "اشتريت تفاحتين.")
        else:
            es_formatted = format_number_for_noun(es_num, is_feminine=True)
            ar_text = f"{ar_num} تفاحات" if 3 <= n <= 10 else f"{ar_num} تفاحة"
            return (f"Compré {es_formatted} manzanas.", f"اشتريت {ar_text}.")
    elif scenario == 2:
        if n == 1:
            return ("Leo un libro.", "أقرأ كتاباً واحداً.")
        elif n == 2:
            return ("Leo dos libros.", "أقرأ كتابين.")
        else:
            es_formatted = format_number_for_noun(es_num, is_feminine=False)
            if 3 <= n <= 10:
                ar_text = f"{ar_num} كتب"
            elif 11 <= n <= 99:
                ar_text = f"{ar_num} كتاباً"
            else:
                ar_text = f"{ar_num} كتاب"
            return (f"Leo {es_formatted} libros.", f"أقرأ {ar_text}.")
    elif scenario == 3:
        if n == 1:
            return ("Mi hermano tiene un año.", "عمر أخي سنة واحدة.")
        elif n == 2:
            return ("Mi hermano tiene dos años.", "عمر أخي سنتان.")
        else:
            es_formatted = format_number_for_noun(es_num, is_feminine=False)
            ar_text = f"{ar_num} سنوات" if 3 <= n <= 10 else f"{ar_num} سنة"
            return (f"Mi hermano tiene {es_formatted} años.", f"عمر أخي {ar_text}.")
    elif scenario == 4:
        if n == 1:
            return ("Esto cuesta un euro.", "هذا يكلف يورواً واحداً.")
        elif n == 2:
            return ("Esto cuesta dos euros.", "هذا يكلف يورويْن.")
        else:
            es_formatted = format_number_for_noun(es_num, is_feminine=False)
            ar_text = f"{ar_num} يورو"
            return (f"Esto cuesta {es_formatted} euros.", f"هذا يكلف {ar_text}.")
    else: # scenario == 0
        if n == 1:
            return ("Estaremos allí un día.", "سنبقى هناك يوماً واحداً.")
        elif n == 2:
            return ("Estaremos allí dos días.", "سنبقى هناك يومين.")
        else:
            es_formatted = format_number_for_noun(es_num, is_feminine=False)
            if 3 <= n <= 10:
                ar_text = f"{ar_num} أيام"
            elif 11 <= n <= 99:
                ar_text = f"{ar_num} يوماً"
            else:
                ar_text = f"{ar_num} يوم"
            return (f"Estaremos allí {es_formatted} días.", f"سنبقى هناك {ar_text}.")

def generate_sentences():
    sentences = []
    for n in range(1, 101):
        es_word = get_spanish_number(n)
        ar_word = get_arabic_number(n)
        es_word_cap = es_word[0].upper() + es_word[1:] if es_word else ""
        
        # Word card
        word_card = {
            "es": es_word_cap,
            "ar": ar_word,
            "imagePrompt": f"Illustration of {es_word_cap}"
        }
        if n == 1:
            word_card["imageUrl"] = "numbers"
        sentences.append(word_card)
        
        # Example sentence card
        es_ex, ar_ex = get_number_example(n)
        sentences.append({
            "es": es_ex,
            "ar": ar_ex,
            "imagePrompt": "Scene showing Numbers 1-100 concept"
        })
    return sentences

def update_lesson_json(file_path):
    if not os.path.exists(file_path):
        print(f"File not found: {file_path}")
        return
    
    print(f"Updating: {file_path}")
    with open(file_path, 'r', encoding='utf-8') as f:
        data = json.load(f)
    
    new_sentences = generate_sentences()
    target_id = "lesson_1771530939995_j1n51"
    
    updated = False
    if isinstance(data, dict):
        # Format: {"Level": [lessons...]}
        for level, lessons in data.items():
            for lesson in lessons:
                if lesson.get("id") == target_id:
                    lesson["title"] = {
                        "ar-SA": "الأرقام 1-100",
                        "en-US": "Numbers 1-100"
                    }
                    lesson["sentences"] = new_sentences
                    updated = True
                    break
    elif isinstance(data, list):
        # Format: [lessons...]
        for lesson in data:
            if lesson.get("id") == target_id:
                lesson["title"] = {
                    "ar-SA": "الأرقام 1-100",
                    "en-US": "Numbers 1-100"
                }
                lesson["sentences"] = new_sentences
                updated = True
                break
                
    if updated:
        with open(file_path, 'w', encoding='utf-8') as f:
            json.dump(data, f, ensure_ascii=False, indent=2)
        print(f"Successfully updated: {file_path}")
    else:
        print(f"Lesson {target_id} not found in {file_path}")

if __name__ == "__main__":
    paths = [
        "d:/MY APP/Repite Conmigo/app/src/main/assets/lessons.json",
        "d:/MY APP/Repite Conmigo/web-factory/src/lessons.json",
        "d:/MY APP/Repite Conmigo/web-factory/public/lessons.json"
    ]
    for p in paths:
        update_lesson_json(p)
