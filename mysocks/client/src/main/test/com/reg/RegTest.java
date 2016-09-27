package com.reg;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/8/19.
 */
public class RegTest {

    public static void main(String[] args) {
//        Pattern pattern = Pattern.compile("^([a-zA-Z]*) [htps]{0,4}[:/]{0,3}(\\S[^/]*)(\\S*) (\\S*)");
        Pattern pattern = Pattern.compile("^[ttps]{0,6}");

        Matcher matcher = pattern.matcher("https");
        if (matcher.find()) {
            System.out.println(matcher.group());
        }
    }
}
