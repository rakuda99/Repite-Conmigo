import urllib.request
import json
import urllib.error

keys = [
    'AIzaSyCjjvBx9VfEKOCMnxaFbp-FKc2u9z_V8Ec',
    'AIzaSyCo2A0LQWth3j9hU_KTLTNdZaDxDbPF3pE'
]

# We will try:
# 1. v1beta/models/gemini-1.5-flash-latest
# 2. v1/models/gemini-1.5-flash
# 3. v1beta/models/gemini-pro

tests = [
    ("v1beta/models/gemini-1.5-flash-latest", keys[0]),
    ("v1/models/gemini-1.5-flash", keys[0]),
    ("v1beta/models/gemini-pro", keys[0]),
    ("v1beta/models/gemini-1.5-flash-latest", keys[1]),
    ("v1/models/gemini-1.5-flash", keys[1]),
    ("v1beta/models/gemini-pro", keys[1])
]

for i, (endpoint, key) in enumerate(tests):
    url = f"https://generativelanguage.googleapis.com/{endpoint}:generateContent?key={key}"
    body = {
        "contents": [{
            "parts": [{
                "text": "Say hello in one word."
            }]
        }]
    }
    req = urllib.request.Request(
        url,
        data=json.dumps(body).encode(),
        headers={'Content-Type': 'application/json'},
        method='POST'
    )
    try:
        with urllib.request.urlopen(req) as resp:
            data = json.loads(resp.read().decode())
            text = data['candidates'][0]['content']['parts'][0]['text']
            print(f"Test {i+1} ({endpoint}) works! Response: {text.strip()}")
            break
    except urllib.error.HTTPError as e:
        body_err = e.read().decode()
        print(f"Test {i+1} ({endpoint}) HTTP Error: {e.code} - {body_err[:150]}")
    except Exception as e:
        print(f"Test {i+1} ({endpoint}) failed: {e}")
