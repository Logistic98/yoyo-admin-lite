package com.yoyo.admin.common.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * 字段脱敏工具类
 */
public class SensitiveFieldUtils {

    /**
     * 用户名脱敏
     * @param userName 名字
     * @return 脱敏结果
     */
    public static String chineseName(String userName) {
        if (StringUtils.isEmpty(userName)) {
            return "";
        }
        String name = StringUtils.left(userName, 1);
        return StringUtils.rightPad(name, StringUtils.length(userName), "*");
    }

    /**
     * 身份证号脱敏
     * @param idCard 身份证号
     * @return 脱敏结果
     */
    public static String idCard(String idCard) {
        if (StringUtils.isEmpty(idCard)) {
            return "";
        }
        String id = StringUtils.right(idCard, 4);
        return StringUtils.leftPad(id, StringUtils.length(idCard), "*");
    }

    /**
     * 手机号脱敏
     * @param phone 手机号
     * @return 脱敏结果
     */
    public static String telephone(String phone) {
        if (StringUtils.isEmpty(phone)) {
            return "";
        }
        return StringUtils.left(phone, 3).concat(StringUtils.removeStart(StringUtils.leftPad(StringUtils.right(phone, 4), StringUtils.length(phone), "*"), "***"));
    }

    /**
     * 地址信息脱敏
     * @param address 地址信息
     * @param sensitiveSize 敏感信息长度
     * @return 脱敏结果
     */
    public static String address(String address, int sensitiveSize) {
        if (StringUtils.isBlank(address)) {
            return "";
        }
        int length = StringUtils.length(address);
        return StringUtils.rightPad(StringUtils.left(address, length - sensitiveSize), length, "*");
    }

    /**
     * 邮箱信息脱敏
     * @param email 邮箱
     * @return 脱敏结果
     */
    public static String email(String email) {
        if (StringUtils.isBlank(email)) {
            return "";
        }
        int index = StringUtils.indexOf(email, "@");
        if (index <= 1) {
            return email;
        } else {
            return StringUtils.rightPad(StringUtils.left(email, 1), index, "*").concat(StringUtils.mid(email, index, StringUtils.length(email)));
        }
    }

    /**
     * 银行卡号脱敏
     * @param cardNum 银行卡号
     * @return 脱敏结果
     */
    public static String bankCard(String cardNum) {
        if (StringUtils.isBlank(cardNum)) {
            return "";
        }
        return StringUtils.left(cardNum, 6).concat(StringUtils.removeStart(StringUtils.leftPad(StringUtils.right(cardNum, 4), StringUtils.length(cardNum), "*"), "******"));
    }

    public static void main(String[] args) {

        // 测试姓名脱敏（张**）
        String name = chineseName("张三丰");
        System.out.println("name = " + name);

        // 测试身份证号脱敏（**************7812）
        String idCard = idCard("123456781234567812");
        System.out.println("idCard = " + idCard);

        // 测试手机号脱敏（186****0000）
        String telephone = telephone("18600000000");
        System.out.println("telephone = " + telephone);

        // 测试地址脱敏（天津市滨海新区*********）
        String address = address("天津市滨海新区经济开发区第三大街", 9);
        System.out.println("address = " + address);

        // 测试邮箱脱敏（t***@163.com）
        String email = email("test@163.com");
        System.out.println("email = " + email);

        // 测试银行卡号脱敏（622316******6887）
        String bankCard = bankCard("6223165905596887");
        System.out.println("bankCard = " + bankCard);

    }

}
