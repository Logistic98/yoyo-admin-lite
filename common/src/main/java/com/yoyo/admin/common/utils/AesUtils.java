package com.yoyo.admin.common.utils;

import com.yoyo.admin.common.config.SessionHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Base64;

/**
 * AES加密工具类
 */
public class AesUtils {

    /**
     * 偏移量  AES 为16bytes. DES
     */
    public static final String VIPARA = "0845762876543456";

    /**
     * 编码方式
     */
    public static final String CODE_TYPE = "UTF-8";

    /**
     * 填充类型
     */
    public static final String AES_TYPE = "AES/ECB/PKCS5Padding";

    /**
     * 字符补全
     */
    private static final String[] CONSULT = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B",
            "C", "D", "E", "F", "G"};

    /**
     * AES加密
     *
     * @param cleartext 明文
     * @param aesKey    密钥
     * @return 密文
     */
    public static String encrypt(String cleartext, String aesKey) {
        // 加密方式： AES128(CBC/PKCS5Padding) + Base64
        try {
            if ("AES/ECB/NoPadding".equals(AES_TYPE)) {
                cleartext = completionCodeFor16Bytes(cleartext);
            }
            aesKey = md5(aesKey);
            System.out.println(aesKey);
            // 两个参数，第一个为私钥字节数组， 第二个为加密方式 AES或者DES
            SecretKeySpec key = new SecretKeySpec(aesKey.getBytes(), "AES");
            // 实例化加密类，参数为加密方式，要写全
            // PKCS5Padding比PKCS7Padding效率高，PKCS7Padding可支持IOS加解密
            Cipher cipher = Cipher.getInstance(AES_TYPE);
            // 初始化，此方法可以采用三种方式，按加密算法要求来添加。
            // （1）无第三个参数
            // （2）第三个参数为SecureRandom random = new SecureRandom();中random对象，随机数。(AES不可采用这种方法)
            // （3）采用此代码中的IVParameterSpec 加密时使用:ENCRYPT_MODE; 解密时使用:DECRYPT_MODE;
            // CBC类型的可以在第三个参数传递偏移量zeroIv,ECB没有偏移量
            cipher.init(Cipher.ENCRYPT_MODE, key);
            // 加密操作,返回加密后的字节数组，然后需要编码。主要编解码方式有Base64, HEX,
            // UUE,7bit等等。此处看服务器需要什么编码方式
            byte[] encryptedData = cipher.doFinal(cleartext.getBytes(CODE_TYPE));
            Base64.Encoder encoder = Base64.getMimeEncoder();
            return encoder.encodeToString(encryptedData);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 解密
     *
     * @param encrypted
     * @return
     */
    public static String decrypt(String encrypted, String aesKey) {
        try {
            aesKey = md5(aesKey);
            Base64.Decoder decoder = Base64.getMimeDecoder();
            byte[] byteMi = decoder.decode(encrypted);
            SecretKeySpec key = new SecretKeySpec(aesKey.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance(AES_TYPE);
            // 与加密时不同MODE:Cipher.DECRYPT_MODE
            // CBC类型的可以在第三个参数传递偏移量zeroIv,ECB没有偏移量
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decryptedData = cipher.doFinal(byteMi);
            String content = new String(decryptedData, CODE_TYPE);
            if ("AES/ECB/NoPadding".equals(AES_TYPE)) {
                content = resumeCodeOf16Bytes(content);
            }
            return content;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 补全字符
     * @param str
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String completionCodeFor16Bytes(String str) throws UnsupportedEncodingException {
        int num = str.getBytes(CODE_TYPE).length;
        int index = num % 16;
        // 进行加密内容补全操作, 加密内容应该为 16字节的倍数, 当不足16*n字节是进行补全, 差一位时 补全16+1位
        // 补全字符 以 $ 开始,$后一位代表$后补全字符位数,之后全部以0进行补全;
        if (index != 0) {
            StringBuilder stringBuilder = new StringBuilder(str);
            if (16 - index == 1) {
                stringBuilder.append("$").append(CONSULT[16 - 1]).append(addStr(16 - 1 - 1));
            } else {
                stringBuilder.append("$").append(CONSULT[16 - index - 1]).append(addStr(16 - index - 1 - 1));
            }
            str = stringBuilder.toString();
        }
        return str;
    }

    /**
     * 追加字符
     * @param num
     * @return
     */
    public static String addStr(int num) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < num; i++) {
            stringBuilder.append("0");
        }
        return stringBuilder.toString();
    }

    /**
     * 还原字符(进行字符判断)
     * @param str
     * @return
     */
    public static String resumeCodeOf16Bytes(String str) {
        int indexOf = str.lastIndexOf("$");
        if (indexOf == -1) {
            return str;
        }
        String trim = str.substring(indexOf + 1, indexOf + 2).trim();
        int num = 0;
        for (int i = 0; i < CONSULT.length; i++) {
            if (trim.equals(CONSULT[i])) {
                num = i;
            }
        }
        if (num == 0) {
            return str;
        }
        return str.substring(0, indexOf).trim();
    }

    /**
     * md5
     * @param dateString
     * @return
     * @throws Exception
     */
    public static String md5(String dateString) throws Exception {
        byte[] digest = MessageDigest.getInstance("md5").digest(dateString.getBytes(CODE_TYPE));
        StringBuilder md5code = new StringBuilder(new BigInteger(1, digest).toString(16));
        // 如果生成数字未满32位，需要前面补0
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code.insert(0, "0");
        }
        return md5code.toString();
    }

    public static String sampleEncrypt(String clearText, String aesKey) {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128);
            Cipher cipher = Cipher.getInstance(AES_TYPE);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(aesKey.getBytes(CODE_TYPE), "AES"));
            byte[] b = cipher.doFinal(clearText.getBytes(CODE_TYPE));
            return Base64.getMimeEncoder().encodeToString(b);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static String sampleDecrypt(String encrypted, String aesKey) {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128);
            Cipher cipher = Cipher.getInstance(AES_TYPE);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(aesKey.getBytes(CODE_TYPE), "AES"));
            byte[] b = cipher.doFinal(Base64.getMimeDecoder().decode(encrypted));
            return new String(b, CODE_TYPE);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {

        // AES加密算法（用于数据传输）
        String encrypt = sampleEncrypt("{\"username\":\"admin\",\"password\":\"328mkl#Gt*dhk&u#\"}", SessionHolder.getWebAesKey());
        System.out.println("result:" + encrypt);
        String decrypt = sampleDecrypt("0UlkbADon96HuD2Jyor1naqFJL6dEwoC0U0HulYH+iuexFyXeAQ5T7RgEBBLGmIManKgpwoI7cOj3gJExnZgkA==", SessionHolder.getWebAesKey());
        System.out.println(decrypt);
        String modifyPassword = sampleEncrypt("{\"oldPassword\":\"88888888\",\"newPassword\":\"328mkl#Gt*dhk&u#\"}", SessionHolder.getWebAesKey());
        System.out.println("result:" + modifyPassword);

        // BCrypt加密算法（用于入库存储）--同一种明文，每次被加密后的密文都不一样，并且不可反向破解生成明文，破解难度非常大。
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println(encoder.encode("328mkl#Gt*dhk&u#"));

    }

}