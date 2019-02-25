package org.lilu.sns.util;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * @Auther: lilu
 * @Date: 2019/2/10
 * @Description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CodingUtilTest {

    @Test
    public void md5() {
        String str = "123456789fsfsdffd";
        System.out.println(str.substring(0,8));
        System.out.println(CodingUtil.md5(str));
        System.out.println(CodingUtil.md5(str));
        System.out.println(CodingUtil.md5(str));
        System.out.println(CodingUtil.md5(str));
        System.out.println(CodingUtil.md5(str).length());
//        for (int i = 0;i < 100;i++) {
//            System.out.println(CodingUtil.md5(str));
//            System.out.println(CodingUtil.md5(str).length());
//        }
    }

    @Test
    public void base64Encode() {
        String str1 = "123_456";
        String str2 = CodingUtil.base64Encode(str1);
        System.out.println(str2);
    }

    @Test
    public void base64Decode() {
        String str1 = "MTIzXzQ1Ng==";
        String str2 = CodingUtil.base64Decode(str1);
        System.out.println(str2);
    }

    @Test
    public void Test() {
        String str = "123_456";
        int index1 = str.indexOf("");
        int index2 = str.indexOf("1");
//        int index3 = str.indexOf(null);
        System.out.println(index1);
        System.out.println(index2);
//        System.out.println(index3);
        System.out.println(String.valueOf(-9));

        System.out.println(str.contains(""));
        System.out.println(str.contains("1"));
        System.out.println(str.contains("13"));
//        System.out.println(str.contains(null));
    }
}