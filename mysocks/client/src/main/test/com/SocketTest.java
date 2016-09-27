package com;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketTest {
    Socket mySocket;
    OutputStream dout;
    public static void main(String[] args){
        new SocketTest();
    }

    public SocketTest(){
        // 输出流关闭的测试一：socket关闭吗？
        test1();
//        // 输出流关闭测试二：该流是否可以重新开启？
//        test2();
        // 输出流关闭测试三：输出缓冲区里的数据是丢弃，还是发送？
//        test3();
    }

    private void test1() {
        // 输出流关闭的测试一：socket关闭吗？
        System.out.println("\n****2种方式关闭输出流，Socket是否关闭？***\n");
        try {
            mySocket = new Socket("127.0.0.1",9999);

            dout =mySocket.getOutputStream();
            dout.write("123456".getBytes());
            dout.flush();

            Thread.sleep(1000);
            dout.write("852963741".getBytes());
            dout.flush();

            //下面这一句主要是用来证明socket确实处于开启状态
//            System.out.println("输出流刚打开,Socket是否关闭？" + mySocket.isClosed());
//            mySocket.shutdownOutput();
//            System.out.println("使用shutdownOutput关闭输出流，Socket是否关闭？" + mySocket.isClosed());
//            dout.close();
//            System.out.println("使用close关闭输出流，Socket是否关闭？" + mySocket.isClosed());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

   /* private void test2() {
        // 输出流关闭测试二：使用shutdownOutputStream后，输出流是否可以重新开启？
        System.out.println("\n****使用shutdownOutputStream后，输出流是否可以重新开启？***\n");
        try {
            mySocket = new Socket("127.0.0.1",9999);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            dout = new DataOutputStream(new BufferedOutputStream(mySocket.getOutputStream()));
            mySocket.shutdownOutput();
            // 重开输出流
            dout = new DataOutputStream(mySocket.getOutputStream());
            dout.writeUTF("是否允许我重开？");
            // 清空输出缓存，确保当dout通道没问题时，消息可以到达服务器
            dout.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                mySocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
//    }

    private void test3(){
        // 输出流关闭测试三：输出缓冲区里的数据是丢弃，还是发送？
        System.out.println("\n***输出缓冲区里的数据是丢弃，还是发送？****\n");
        try {
            mySocket = new Socket("127.0.0.1",9999);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

      /*  try {
            dout = new DataOutputStream(new BufferedOutputStream(mySocket.getOutputStream()));
            dout.writeUTF("shutdownOutput后，数据发得得出去吗？");
            dout.flush();
            mySocket.shutdownOutput();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
}

