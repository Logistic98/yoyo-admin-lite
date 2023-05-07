package com.yoyo.admin.common.utils;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * RSA工具类
 */
public class RsaUtils {

    /**
     * 使用公钥字符串加密
     *
     * @param plainText       明文
     * @param publicKeyString 公钥字符串
     * @return 密文
     * @throws Exception 异常
     */
    public static String encryptByPublicString(String plainText, String publicKeyString) throws Exception {
        PublicKey publicKey = getPublicKeyByString(publicKeyString);
        return encrypt(plainText, publicKey);
    }

    /**
     * 使用私钥字符串解密
     *
     * @param cipherText       密文
     * @param privateKeyString 私钥字符串
     * @return 明文
     * @throws Exception 异常
     */
    public static String decryptByPrivateString(String cipherText, String privateKeyString) throws Exception {
        PrivateKey privateKey = getPrivateKeyByString(privateKeyString);
        return decrypt(cipherText, privateKey);
    }

    /**
     * 使用私钥字符串签名
     *
     * @param plainText        明文
     * @param privateKeyString 私钥字符串
     * @return 签名
     * @throws Exception 异常
     */
    public static String signByPrivateString(String plainText, String privateKeyString) throws Exception {
        PrivateKey privateKey = getPrivateKeyByString(privateKeyString);
        return sign(plainText, privateKey);
    }

    /**
     * 使用公钥字符串验签
     *
     * @param plainText       明文
     * @param signature       签名
     * @param publicKeyString 公钥字符串
     * @return 是否通过签名验证
     * @throws Exception 异常
     */
    public static boolean verifyByPublicString(String plainText, String signature, String publicKeyString) throws Exception {
        PublicKey publicKey = getPublicKeyByString(publicKeyString);
        return verify(plainText, signature, publicKey);
    }

    /**
     * 生成2048位的RSA密钥对
     *
     * @return 密钥对
     * @throws Exception 异常
     */
    public static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048, new SecureRandom());
        return generator.generateKeyPair();
    }

    /**
     * 从RSA密钥对中获取私钥字符串
     *
     * @param keyPair RSA密钥对
     * @return 私钥字符串
     */
    public static String getPrivateKeyString(KeyPair keyPair) {
        return getPrivateKeyString(keyPair.getPrivate());
    }

    public static String getPrivateKeyString(PrivateKey privateKey) {
        return Base64.encodeBase64String(privateKey.getEncoded());
    }

    /**
     * 从RSA密钥对中获取公钥字符串
     *
     * @param keyPair RSA密钥对
     * @return 公钥字符串
     */
    public static String getPublicKeyString(KeyPair keyPair) {
        return Base64.encodeBase64String(keyPair.getPublic().getEncoded());
    }

    /**
     * 将私钥字符串还原为私钥
     *
     * @param privateKeyString 私钥字符串
     * @return 私钥
     * @throws Exception 异常
     */
    public static PrivateKey getPrivateKeyByString(String privateKeyString) throws Exception {
        byte[] keyBytes = Base64.decodeBase64(privateKeyString);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * 将公钥字符串还原为公钥
     *
     * @param publicKeyString 公钥字符串
     * @return 公钥
     * @throws Exception 异常
     */
    public static PublicKey getPublicKeyByString(String publicKeyString) throws Exception {
        byte[] keyBytes = Base64.decodeBase64(publicKeyString);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    /**
     * RSA加密
     *
     * @param plainText 明文
     * @param publicKey 公钥
     * @return 密文
     * @throws Exception 异常
     */
    public static String encrypt(String plainText, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        return Base64.encodeBase64String(cipherText);
    }

    /**
     * RSA解密
     *
     * @param cipherText 密文
     * @param privateKey 私钥
     * @return 明文
     * @throws Exception 异常
     */
    public static String decrypt(String cipherText, PrivateKey privateKey) throws Exception {
        byte[] bytes = Base64.decodeBase64(cipherText);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(cipher.doFinal(bytes), StandardCharsets.UTF_8);
    }

    /**
     * RSA签名
     *
     * @param plainText  明文
     * @param privateKey 私钥
     * @return 签名
     * @throws Exception 异常
     */
    public static String sign(String plainText, PrivateKey privateKey) throws Exception {
        Signature privateSignature = Signature.getInstance("SHA256withRSA");
        privateSignature.initSign(privateKey);
        privateSignature.update(plainText.getBytes(StandardCharsets.UTF_8));
        byte[] signedBytes = privateSignature.sign();
        return Base64.encodeBase64String(signedBytes);
    }

    /**
     * RSA验签
     *
     * @param plainText 明文
     * @param signature 签名
     * @param publicKey 公钥
     * @return 是否通过验证
     * @throws Exception 异常
     */
    public static boolean verify(String plainText, String signature, PublicKey publicKey) throws Exception {
        Signature publicSignature = Signature.getInstance("SHA256withRSA");
        publicSignature.initVerify(publicKey);
        publicSignature.update(plainText.getBytes(StandardCharsets.UTF_8));
        byte[] signedBytes = Base64.decodeBase64(signature);
        return publicSignature.verify(signedBytes);
    }

    public static void main(String[] args) throws Exception {

        // 生成2048位密钥对
        KeyPair keyPair = generateKeyPair();
        System.out.println("=====公钥=====");
        System.out.println(keyPair.getPublic());
        System.out.println("=====私钥=====");
        System.out.println(keyPair.getPrivate());

        // RSA公钥加密
        System.out.println("=====测试RSA公钥加密=====");
        String testText = "测试文本";
        String encryptText = encrypt(testText, keyPair.getPublic());
        System.out.println(encryptText);

        // RSA私钥解密
        System.out.println("=====测试RSA私钥解密=====");
        String decryptText = decrypt(encryptText, keyPair.getPrivate());
        System.out.println(decryptText);

    }

}
