#coding:utf-8
import socket,select
#xiaorui.cc
from Tools.scripts.treesync import raw_input

host = "localhost"
port = 10000
s = socket.socket(socket.AF_INET,socket.SOCK_STREAM)
s.connect((host,port))

data = raw_input("Please input some string > ")
#在python2中是可以这样用的，但是3以后就必须使用编码了
s.sendall(data.encode('utf-8'))
s.close()