package com.yoyo.admin.toolbox.builder_tools.qr_code;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.EnumMap;

/**
 *  在指定路径生成一张二维码，内容类型为字符串，可以是普通文本或者是超链接
 *
 *  生成二维码的步骤
 *      1.根据Zxingjar包生成二维码，这只是生成二维码，并不是生成二维码图片
 *      2.创建一张图片，大小和二维码的大小一致。
 *      3.二维码可以看成是二维数组，所以遍历二维码，然后把二维码的每一个点添加到图片上，添加的时候，判断是黑色点，还是白色点。
 *          encode.get(i,j)?BLACK:WHITE
 *      4.最后把真正的二维码图片生成到指定路径即可。
 */
public class EncodeQRCodeTool {

    //0x：代表是16进制，ff：代表完全不透明度，其余6位代表颜色值（000000：代表黑色）
    //定义二维码的颜色
    private static final int BLACK = 0xff000000;
    //定义二维码的背景颜色
    private static final int WHITE = 0xffffffff;

    //二维码格式参数集合
    private static final EnumMap<EncodeHintType,Object> hints =
            new EnumMap<EncodeHintType, Object>(EncodeHintType.class);

    //静态代码块初始化参数，在类加载的时候就初始化，而且在类的生命周期内只初始化一次
    static {
        /*
         *  二维码的纠错级别(排错率),4个级别： L(7%)、M(15%)、Q (25%)、H(30%)
         *  最高级别为:H
         *  纠错信息同样存储在二维码中，纠错级别越高，纠错信息占用的空间越多，那么能存储的有用信息就越少；
         */
        hints.put(EncodeHintType.ERROR_CORRECTION,ErrorCorrectionLevel.L);
        // 二维码边界空白大小：1，2，3，4(4为默认，最大)
        hints.put(EncodeHintType.MARGIN,1);
        //设置二维码存储内容的字符集
        hints.put(EncodeHintType.CHARACTER_SET,"UTF-8");
    }

    public static void createZXingCodeSaveToDisk(String content,int width,int height,String savePath,String imageType){
        try {
            BitMatrix encode = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            //获得二维码的宽高
            int codeWidth = encode.getWidth();
            int codeHeight = encode.getHeight();
            //创建图片
            BufferedImage image = new BufferedImage(codeWidth, codeHeight, BufferedImage.TYPE_INT_RGB);
            //把二维码里面的信息写到图片里面
            for (int i = 0; i < codeWidth; i++) {
                for (int j = 0; j < codeHeight; j++) {
                    image.setRGB(i,j,encode.get(i,j)?BLACK:WHITE);
                }
            }
            //保存图片到指定位置
            File file = new File(savePath);
            if(!file.exists()){
                //文件不存在则创建
                file.createNewFile();
            }
            ImageIO.write(image,imageType,file);
            //打印一句话，给个提示是否成功
            System.out.println("二维码生成成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // 将字符串"hello world"存储在二维码中
        createZXingCodeSaveToDisk("hello world",300,300,"./text.jpeg","JPEG");
        // 将我的博客地址存储在二维码中
        createZXingCodeSaveToDisk("https://www.eula.club/",300,300,"./blog.jpeg","JPEG");
    }

}
