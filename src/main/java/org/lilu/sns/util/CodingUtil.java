package org.lilu.sns.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * @Auther: lilu
 * @Date: 2019/1/26
 * @Description: 编码工具类
 */
public class CodingUtil {
    private static final Logger logger = LoggerFactory.getLogger(CodingUtil.class);

    /**
     * 进行md5加密
     * @param key
     * @return
     */
    public static String md5(String key) {
        try {
            // 获得MD5摘要算法的对象
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            // 使用要加密的字节更新摘要
            md5.update(key.getBytes());
            // 获取密文
            String md5code = new BigInteger(1,md5.digest()).toString(16);
            // 如果生成的数字未满32位则在前面补0
            for (int i = 0;i < 32 - md5code.length();i++) {
                md5code = "0" + md5code;
            }
            return md5code;
        } catch (NoSuchAlgorithmException e) {
            logger.error("md5生成失败：",e);
            return null;
        }
    }

    /**
     * 对传入的字符串进行base64编码
     * @param str
     * @return
     */
    public static String base64Encode(String str) {
        try {
            Base64.Encoder encoder = Base64.getEncoder();
            return encoder.encodeToString(str.getBytes("UTF-8"));
        } catch (Exception e) {
            logger.error("base64编码失败：",e);
            return null;
        }
    }

    /**
     * 对传入的字符串进行base64解码
     * @param str
     * @return
     */
    public static String base64Decode(String str) {
        try {
            Base64.Decoder decoder = Base64.getDecoder();
            return new String(decoder.decode(str),"UTF-8");
        } catch (Exception e) {
            logger.error("base64解码失败：",e);
            return null;
        }
    }
}