package com.encrypt;

import java.security.MessageDigest;

/**
 * Created by Administrator on 2016/8/18.
 */
public class Md5 {

    public static void main(String[] args) throws Exception {

        test01();
    }


    public static void test01() throws Exception {
        String plainText = "Hello , world !";
        MessageDigest md5 = MessageDigest.getInstance("md5");
        byte[] cipherData = md5.digest(plainText.getBytes());
        StringBuilder builder = new StringBuilder();
        for(byte cipher : cipherData) {
            String toHexStr = Integer.toHexString(cipher & 0xff);
            builder.append(toHexStr.length() == 1 ? "0" + toHexStr : toHexStr);
        }
        System.out.println(builder.toString());
        //c0bb4f54f1d8b14caf6fe1069e5f93ad
    }
}
