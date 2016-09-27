#coding:utf-8
import socket,select
import time
import os
#xiaorui.cc
host = "localhost"
port = 50000
s = socket.socket(socket.AF_INET,socket.SOCK_STREAM)
s.bind((host,port))
s.listen(5)
while 1:
    #select 模式使用，传入的是分别是 1，可读触发，2，可写触发， 3，异常触发的socket
    #对应返回的则是可读的select(类似java中的key),
     infds,outfds,errfds = select.select([s,],[],[],5)
     if len(infds) != 0:
        clientsock,clientaddr = s.accept()
        buf = clientsock.recv(8196)
        if len(buf) != 0:
            print (buf)
            aa= os.popen('cdm')
            print(aa)
        clientsock.close()
#     print "no data coming"