package com;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketTest extends Thread{
    private ServerSocket myServerSocket;
    private final int PORT = 9999;
    public static void main(String[] args){
        ServerSocketTest sst = new ServerSocketTest();
        sst.start();
    }

    public ServerSocketTest(){
        // 初始化一个ServeSocket端  
        try {
            myServerSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(){
        while(true){
            System.out.println("我是服务器，我在9999端口监听....");
            try {
                Socket socket = myServerSocket.accept();
                InputStream din = new BufferedInputStream(socket.getInputStream());
                byte[] buffer = new byte[64];

                int len =  din.read(buffer);
                String msgIn = new String(buffer, 0, len);

//                int len_len =
                InputStream inputStream = socket.getInputStream();
                byte[] another_buffer = new byte[64];
                int readlen = inputStream.read(another_buffer);
                System.out.println("长度是："+readlen);

                System.out.println(msgIn.trim());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}  