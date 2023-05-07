package com.yoyo.admin.toolbox.builder_tools;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 生成指定位数的随机密码，密码中需要包含大写字母、小写字母、数字和特殊字符。
 */
public class RandPasswordTool {
    public static final char[] allowedSpecialCharactors = {
            '`', '~', '@', '#', '$', '%', '^', '&',
            '*', '(', ')', '-', '_', '=', '+', '[',
            '{', '}', ']', '\\', '|', ';', ':', '"',
            '\'', ',', '<', '.', '>', '/', '?'};//密码能包含的特殊字符
    private static final int letterRange = 26;
    private static final int numberRange = 10;
    private static final int spCharactorRange = allowedSpecialCharactors.length;
    private static final SecureRandom random = new SecureRandom();
    private int passwordLength;//密码的长度
    private int minVariousType;//密码包含字符的最少种类

    public RandPasswordTool(int passwordLength, int minVariousType) {
        if (minVariousType > CharactorType.values().length) minVariousType = CharactorType.values().length;
        if (minVariousType > passwordLength) minVariousType = passwordLength;
        this.passwordLength = passwordLength;
        this.minVariousType = minVariousType;
    }

    public String generateRandomPassword() {
        char[] password = new char[passwordLength];
        List<Integer> pwCharsIndex = new ArrayList();
        for (int i = 0; i < password.length; i++) {
            pwCharsIndex.add(i);
        }
        List<CharactorType> takeTypes = new ArrayList(Arrays.asList(CharactorType.values()));
        List<CharactorType> fixedTypes = Arrays.asList(CharactorType.values());
        int typeCount = 0;
        while (pwCharsIndex.size() > 0) {
            int pwIndex = pwCharsIndex.remove(random.nextInt(pwCharsIndex.size()));//随机填充一位密码
            Character c;
            if (typeCount < minVariousType) {//生成不同种类字符
                c = generateCharacter(takeTypes.remove(random.nextInt(takeTypes.size())));
                typeCount++;
            } else {//随机生成所有种类密码
                c = generateCharacter(fixedTypes.get(random.nextInt(fixedTypes.size())));
            }
            password[pwIndex] = c.charValue();
        }
        return String.valueOf(password);
    }

    private Character generateCharacter(CharactorType type) {
        Character c = null;
        int rand;
        switch (type) {
            case LOWERCASE://随机小写字母
                rand = random.nextInt(letterRange);
                rand += 97;
                c = new Character((char) rand);
                break;
            case UPPERCASE://随机大写字母
                rand = random.nextInt(letterRange);
                rand += 65;
                c = new Character((char) rand);
                break;
            case NUMBER://随机数字
                rand = random.nextInt(numberRange);
                rand += 48;
                c = new Character((char) rand);
                break;
            case SPECIAL_CHARACTOR://随机特殊字符
                rand = random.nextInt(spCharactorRange);
                c = new Character(allowedSpecialCharactors[rand]);
                break;
        }
        return c;
    }

    public static void main(String[] args) {
        System.out.println(new RandPasswordTool(32, 4).generateRandomPassword());
    }
}

enum CharactorType {
    LOWERCASE,
    UPPERCASE,
    NUMBER,
    SPECIAL_CHARACTOR
}
