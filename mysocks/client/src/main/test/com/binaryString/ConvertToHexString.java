package com.binaryString;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2016/8/15.
 */
public class ConvertToHexString {

    public static void main(String[] args) throws IOException {

        String dir = "D:\\a_git_lab\\lab\\fan_qiang\\test.png";

        File png = new File(dir);
        InputStream in = new FileInputStream(png);

        byte[] temp = new byte[1024 * 1024];
        int len = in.read(temp);

        byte[] real = new byte[len];
        System.arraycopy(temp, 0, real, 0, len);
//        Console.printBytes("二进制", real);



    }
}
