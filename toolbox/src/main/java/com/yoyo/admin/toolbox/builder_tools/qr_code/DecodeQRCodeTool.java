package com.yoyo.admin.toolbox.builder_tools.qr_code;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.EnumMap;

/**
 *  该类主要解析二维码
 *      步骤：
 *          1.读取二维码图片
 *          2.读取二维码图片中的二维码
 *          3.读取二维码中的信息
 */
public class DecodeQRCodeTool {

    //二维码格式参数集合
    private static final EnumMap<DecodeHintType,Object> HINTS = new EnumMap<DecodeHintType, Object>(DecodeHintType.class);

    static {
        //设置解析二维码后信息的字符集
        HINTS.put(DecodeHintType.CHARACTER_SET,"UTF-8");
    }

    /**
     *  解析二维码
     * @param path 二维码图片路径
     * @return 二维码中的文本内容
     */
    public static String decodeQRCodeForPath(String path){
        //该文件对象映射二维码图片
        File file = new File(path);
        if(file.exists()){
            try {
                return decodeQRCodeStreamForStream(new FileInputStream(file));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    /**
     *  解析二维码
     * @param ins 读取二维码图片的流
     * @return 二维码中的文本内容
     */
    public static String decodeQRCodeStreamForStream(InputStream ins) {
        if(ins != null){
            try {
                //将读取二维码图片的流转为图片对象
                BufferedImage image = ImageIO.read(ins);
                BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(image);
                HybridBinarizer binarizer = new HybridBinarizer(source);
                BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);
                MultiFormatReader reader = new MultiFormatReader();
                Result result = reader.decode(binaryBitmap, HINTS);
                //返回二维码中的文本内容
                String content = result.getText();
                System.out.println("二维码解析成功");
                return content;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        String myName = decodeQRCodeForPath("./text.jpeg");
        System.out.println(myName);
        String myBlog = decodeQRCodeForPath("./blog.jpeg");
        System.out.println(myBlog);
    }

}
