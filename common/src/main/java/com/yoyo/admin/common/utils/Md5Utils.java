package com.yoyo.admin.common.utils;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * MD5通用类
 */
public class Md5Utils {

    /**
     * MD5方法
     *
     * @param text 明文
     * @param key  密钥
     * @return 密文
     * @throws Exception
     */
    public static String md5(String text, String key) throws Exception {
        // 加密后的字符串
        String encodeStr = DigestUtils.md5Hex(text + key);
        return encodeStr;
    }

    /**
     * MD5验证方法
     *
     * @param text 明文
     * @param key  密钥
     * @param md5  密文
     * @return true/false
     * @throws Exception
     */
    public static boolean verify(String text, String key, String md5) throws Exception {
        // 根据传入的密钥进行验证
        String md5Text = md5(text, key);
        if (md5Text.equalsIgnoreCase(md5)) {
            System.out.println("MD5验证通过");
            return true;
        }
        return false;
    }

    public static void main(String[] args) throws Exception {
        String md5 = md5("123456", "yoyo-admin");
        System.out.println(md5);
        boolean verify = verify("123456","yoyo-admin","6c256580c6aa720510f7c15853c29f7a");
        System.out.println(verify);
    }
}
