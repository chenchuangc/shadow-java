package com.stfl.util.console;

/**
 * Created by Administrator on 2016/8/9.
 */
public class LayoutUtil {

    public static String toHexString(byte[] src) {
        StringBuffer dest = new StringBuffer("");

        int index = 0;
        for (byte one_bit : src) {
            index++;
           int  bit =  (one_bit&0xff);
            String temp =Integer.toHexString(bit);
            if (1 == temp.length()) {
                temp = '0'+temp;
            }
            dest.append(" : ");
            dest.append(temp);

            if (index % 10 == 0) {
                dest.append('\n');
            }

        }
        return dest.toString();
    }

    public static String toNormalString(byte[] src) {
        if (null == src) {
            return null;
        }
        return new String(src);
    }

    public static void main(String[] args) {
        System.out.println(toHexString(new byte[]{(byte) 0x80,0x01}));
    }
}
