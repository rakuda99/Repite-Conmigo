import http.server
import socketserver
import json
import urllib.request
import os

PORT = 5000
LIB_FILE = "d:/MY APP/Repite Conmigo/library.json"

# تأكد من وجود الملف
if not os.path.exists(LIB_FILE):
    with open(LIB_FILE, "w", encoding="utf-8") as f:
        json.dump([], f)

class CloudBridgeHandler(http.server.BaseHTTPRequestHandler):
    def do_OPTIONS(self):
        self.send_response(200)
        self.send_header('Access-Control-Allow-Origin', '*')
        self.send_header('Access-Control-Allow-Methods', 'GET, POST, OPTIONS')
        self.send_header('Access-Control-Allow-Headers', 'Content-Type')
        self.end_headers()

    def do_GET(self):
        # تقديم المكتبة من الملف المحلي
        if os.path.exists(LIB_FILE):
            self.send_response(200)
            self.send_header('Access-Control-Allow-Origin', '*')
            self.send_header('Content-Type', 'application/json')
            self.end_headers()
            with open(LIB_FILE, "r", encoding="utf-8") as f:
                self.wfile.write(f.read().encode('utf-8'))

    def do_POST(self):
        content_length = int(self.headers['Content-Length'])
        post_data = self.rfile.read(content_length)
        lesson_data = json.loads(post_data)
        
        try:
            # 1. الرفع للسحاب لعمل رابط دائم للدرس
            req = urllib.request.Request(
                'https://jsonblob.com/api/jsonBlob',
                data=post_data,
                headers={'Content-Type': 'application/json'},
                method='POST'
            )
            with urllib.request.urlopen(req) as resp:
                lesson_url = resp.getheader('Location').replace('http:', 'https:')
                
                # 2. التحديث المحلي للمكتبة (الذي لا يخطئ)
                with open(LIB_FILE, "r+", encoding="utf-8") as f:
                    library = json.load(f)
                    new_entry = {
                        "title": lesson_data['metadata']['title'],
                        "target_lang": lesson_data['metadata']['target_lang'],
                        "url": lesson_url
                    }
                    library = [i for i in library if i['title'] != new_entry['title']]
                    library.append(new_entry)
                    f.seek(0)
                    json.dump(library, f, ensure_ascii=False, indent=2)
                    f.truncate()

                print(f"✅ Lesson synced locally and cloud URL created: {lesson_url}")
                self.send_response(200)
                self.send_header('Access-Control-Allow-Origin', '*')
                self.send_header('Content-Type', 'application/json')
                self.end_headers()
                self.wfile.write(json.dumps({'url': lesson_url}).encode('utf-8'))
        except Exception as e:
            print(f"❌ Error: {e}")
            self.send_response(500)
            self.send_header('Access-Control-Allow-Origin', '*')
            self.end_headers()

print(f"🌩️ Cloud Bridge ULTIMATE (Local Proxy) running on http://localhost:{PORT}")
with socketserver.TCPServer(("", PORT), CloudBridgeHandler) as httpd:
    httpd.serve_forever()
