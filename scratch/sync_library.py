import json
import urllib.request
import urllib.parse
import os
import sys

BASE_DIR = "d:/MY APP/Repite Conmigo"
LIB_FILE = os.path.join(BASE_DIR, "library.json")
GLOBAL_CATALOG_URL = "https://jsonblob.com/api/jsonBlob/019d9864-1f9e-7334-9063-075468551478"

def sync_to_cloud():
    try:
        if not os.path.exists(LIB_FILE): return
        with open(LIB_FILE, "r", encoding="utf-8") as f:
            lib = json.load(f)
            # تصحيح الروابط لتكون كاملة عالمياً
            for item in lib:
                if 'url' in item:
                    if item['url'].startswith('/api/jsonBlob'):
                        item['url'] = "https://jsonblob.com" + item['url']
                    elif not item['url'].startswith('http'):
                        item['url'] = "https://jsonblob.com/api/jsonBlob/" + item['url']
            
            data = json.dumps(lib, ensure_ascii=False)
            req = urllib.request.Request(GLOBAL_CATALOG_URL, data=data.encode(), headers={'Content-Type': 'application/json'}, method='PUT')
            urllib.request.urlopen(req)
            print("✅ GLOBAL CLOUD SYNC SUCCESSFUL!")
            sys.stdout.flush()
    except Exception as e:
        print(f"⚠️ Cloud Sync Failed: {e}")
        sys.stdout.flush()

sync_to_cloud()
