package com.stfl.util.console;

/**
 * Created by Administrator on 2016/8/9.
 */
public class Console {

    /**
     * 打印字符串
     * @param key
     * @param body
     */
    public  static synchronized void print(Object key, String body) {

        String msg_key;
        if (key instanceof String) {

            msg_key = (String) key;
        } else {
            msg_key = key.getClass().getName();
        }

        if (null == body) {
            System.out.println("####" + key + " : " + " is null !!!");
        }
        System.out.println("$$$$$" + key + " : " + body +"    **");

    }

    public static synchronized void printBytesAndString(Object key, byte[] src) {

        String msg_key;
        if (key instanceof String) {

            msg_key = (String) key;
        } else {
            msg_key = key.getClass().getName();
        }

        if (null == src) {
            System.out.println("" + msg_key + " : " + " is null !!!");
        } else {

            System.out.println(msg_key + " :key##########start");
            System.out.println("字符串打印开始：|||");
            String aaa =  new String(src) ;
            System.out.println(aaa);
            System.out.println("字符串打印结束。。。。。。");

            System.out.println("十六打印开始：");
            System.out.println(LayoutUtil.toHexString(src));
            System.out.println("十六进制打印结束。。。。");

            System.out.println(msg_key+" :key##########end!");

        }

        System.out.println("-----------------------------------------------------------------");
        System.out.println();
    }
    public static synchronized void printString(Object key, byte[] src) {

        String msg_key;
        if (key instanceof String) {

            msg_key = (String) key;
        } else {
            msg_key = key.getClass().getName();
        }

        if (null == src) {
            System.out.println("####" + key + " : " + " is null !!!");
        } else {

            System.out.println(key +":|||"+LayoutUtil.toNormalString(src)+"|||***");

        }
    }

    public static synchronized void simplePrint(String content) {

        System.out.println(content);
    }
}
