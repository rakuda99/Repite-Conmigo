import os
import datetime

folder = "app/build/outputs/apk/debug"
for name in os.listdir(folder):
    path = os.path.join(folder, name)
    mtime = os.path.getmtime(path)
    dt = datetime.datetime.fromtimestamp(mtime)
    size = os.path.getsize(path)
    print(f"File: {name}")
    print(f"  Size: {size} bytes")
    print(f"  Modified: {dt.isoformat()}")
