import http.server
import socketserver
import json
import urllib.request
import urllib.parse
import os
import sys

HOST = "0.0.0.0" 
PORT = 9009
BASE_DIR = "d:/MY APP/Repite Conmigo"
LIB_FILE = os.path.join(BASE_DIR, "library.json")

# الرابط السحابي الدائم والجديد (تم إنشاؤه الآن)
GLOBAL_CATALOG_URL = "https://jsonblob.com/api/jsonBlob/019d9864-1f9e-7334-9063-075468551478"

def sync_to_cloud():
    try:
        if not os.path.exists(LIB_FILE): return
        with open(LIB_FILE, "r", encoding="utf-8") as f:
            lib = json.load(f)
            # تصحيح الروابط لتكون كاملة عالمياً
            for item in lib:
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

class FinalHandler(http.server.SimpleHTTPRequestHandler):
    def _send_cors_headers(self):
        self.send_header('Access-Control-Allow-Origin', '*')
        self.send_header('Access-Control-Allow-Methods', 'GET, POST, OPTIONS')
        self.send_header('Access-Control-Allow-Headers', 'Content-Type')

    def do_OPTIONS(self):
        self.send_response(200)
        self._send_cors_headers()
        self.end_headers()

    def do_GET(self):
        clean_path = self.path.split('?')[0]
        if clean_path == '/api/library':
            self.send_response(200); self.send_header('Content-Type', 'application/json')
            self._send_cors_headers()
            self.end_headers()
            with open(LIB_FILE, "r", encoding="utf-8") as f: self.wfile.write(f.read().encode())
        elif clean_path == '/api/fetch':
            try:
                query = urllib.parse.parse_qs(urllib.parse.urlparse(self.path).query)
                url = query.get('url', [''])[0]
                if "api/jsonBlob" in url and not url.startswith("http"):
                    url = "https://jsonblob.com/" + url[url.find("api/"):]
                req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
                with urllib.request.urlopen(req) as resp:
                    self.send_response(200); self.send_header('Content-Type', 'application/json')
                    self._send_cors_headers()
                    self.end_headers()
                    self.wfile.write(resp.read())
            except: self.send_response(500); self.end_headers()
        else: super().do_GET()

    def do_POST(self):
        content_length = int(self.headers['Content-Length'])
        post_data = self.rfile.read(content_length)
        
        if self.path == '/api/save-full-library':
            try:
                # 1. Save to library.json
                with open(LIB_FILE, "w", encoding="utf-8") as f:
                    f.write(post_data.decode('utf-8'))
                
                # 2. Save to Android Assets
                android_assets_path = os.path.join(BASE_DIR, "app/src/main/assets/lessons.json")
                if os.path.exists(os.path.dirname(android_assets_path)):
                    # Transform list back to asset structure if necessary, but for now simple sync
                    pass
                
                # 3. Save to Web-Factory Source
                web_src_path = os.path.join(BASE_DIR, "web-factory/src/lessons.json")
                with open(web_src_path, "w", encoding="utf-8") as f:
                    f.write(post_data.decode('utf-8'))
                
                sync_to_cloud()
                self.send_response(200); self.send_header('Content-Type', 'application/json')
                self._send_cors_headers()
                self.end_headers()
                self.wfile.write(json.dumps({'status': 'success'}).encode())
            except Exception as e:
                self.send_response(500); self._send_cors_headers(); self.end_headers()
                self.wfile.write(str(e).encode())

        elif self.path.startswith('/api/upload'):
            try:
                req = urllib.request.Request('https://jsonblob.com/api/jsonBlob', data=post_data, headers={'Content-Type': 'application/json'}, method='POST')
                with urllib.request.urlopen(req) as resp:
                    lesson_url = resp.getheader('Location').replace('http:', 'https:')
                    with open(LIB_FILE, "r+", encoding="utf-8") as f:
                        lib = json.load(f)
                        lesson_dict = json.loads(post_data)
                        entry = {"title": lesson_dict['metadata']['title'], "target_lang": lesson_dict['metadata']['target_lang'], "url": lesson_url}
                        lib = [i for i in lib if i['title'] != entry['title']]; lib.append(entry)
                        f.seek(0); json.dump(lib, f, ensure_ascii=False, indent=2); f.truncate()
                    sync_to_cloud()
                    self.send_response(200); self.send_header('Content-Type', 'application/json'); self.end_headers()
                    self.wfile.write(json.dumps({'url': lesson_url}).encode())
            except: self.send_response(500); self.end_headers()

os.chdir(BASE_DIR)
sync_to_cloud() 
print(f"💎 Super Factory GLOBAL v4.5 Ready on http://localhost:{PORT}")
sys.stdout.flush()
with socketserver.TCPServer(("", PORT), FinalHandler) as httpd:
    httpd.serve_forever()
