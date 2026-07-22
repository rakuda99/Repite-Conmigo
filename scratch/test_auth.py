import requests

api_key = "AIzaSyCjcEWZU2Y_EBPcTx5lu0Tg-y3tYKu13BQ"
auth_url = f"https://identitytoolkit.googleapis.com/v1/accounts:signUp?key={api_key}"

try:
    r = requests.post(auth_url, json={"returnSecureToken": True})
    res = r.json()
    if "idToken" in res:
        print("Success! Anonymous sign-in working.")
        print(f"Token (first 20 chars): {res['idToken'][:20]}")
    else:
        print("Failed to sign in anonymously:", res)
except Exception as e:
    print("Error:", e)
