import hashlib

print("aaa"=="aaa")

md5 = hashlib.md5()
md5.update("aaa".encode('utf-8'))

rc4_key = md5.hexdigest()

print(rc4_key)