import urllib.request
import urllib.parse
import json

sentence = "El agua es muy fría."
url = f"https://translate.googleapis.com/translate_a/single?client=gtx&sl=es&tl=ar&dt=t&q={urllib.parse.quote(sentence)}"

req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
try:
    with urllib.request.urlopen(req) as resp:
        data = json.loads(resp.read().decode())
        translation = data[0][0][0]
        print(f"Original: {sentence}")
        print(f"Translation: {translation}")
except Exception as e:
    print(f"Error: {e}")
