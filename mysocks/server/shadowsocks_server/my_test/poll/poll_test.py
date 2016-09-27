import socket,select
s=socket.socket()
host=""
port=10000
s.bind((host,port))
fdmap={s.fileno():s}
s.listen(5)
p=select.poll()

p.register(s.fileno(),select.POLLIN|select.POLLERR|select.POLLHUP)
while 1:
    #感觉poll模式的使用和java的类似
    events=p.poll(5000)
    if len(events)!=0:
        if events[0][1]==select.POLLIN:
            sock,addr=s.accept()
            buf=sock.recv(8196)
            if len(buf)!=0:
                print (buf)
                sock.close()
    print("no data")