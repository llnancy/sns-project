package org.lilu.sns.util;

import java.util.List;

/**
 * @Auther: lilu
 * @Date: 2019/1/27
 * @Description:
 */
public class TestUtil {
    public static void main(String[] args) {
        List<String> strings = null;
        for (String s : strings) {
            System.out.println(s);
        }
    }

    public static String test() {
        try {
            int i = 1/0;
            return "aaaa";
        } catch (Exception e) {
            System.out.println("ddddddd");
            e.printStackTrace();
        }
        System.out.println("ccccc");
        return "bbbb";
    }
}