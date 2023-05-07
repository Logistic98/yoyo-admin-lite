package com.yoyo.admin.common.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;


/**
 * base64及图片文件格式转换工具类
 */
public class Base64Utils implements MultipartFile {

    private final byte[] imgContent;
    private final String header;

    public Base64Utils(byte[] imgContent, String header) {
        this.imgContent = imgContent;
        this.header = header.split(";")[0];
    }

    @Override
    public String getName() {
        return System.currentTimeMillis() + Math.random() + "." + header.split("/")[1];
    }

    @Override
    public String getOriginalFilename() {
        return System.currentTimeMillis() + (int) Math.random() * 10000 + "." + header.split("/")[1];
    }

    @Override
    public String getContentType() {
        return header.split(":")[1];
    }

    @Override
    public boolean isEmpty() {
        return imgContent == null || imgContent.length == 0;
    }

    @Override
    public long getSize() {
        return imgContent.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return imgContent;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(imgContent);
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        new FileOutputStream(dest).write(imgContent);
    }

    /**
     * base64转MultipartFile
     *
     * @param base64
     * @return
     */
    public static MultipartFile base64ToMultipart(String base64) {
        // 输入的base64字符串注意带上类似于"data:image/jpg;base64,"的前缀。
        try {
            String[] baseStrs = base64.split(",");
            byte[] b = Base64.decodeBase64(base64);

            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {
                    b[i] += 256;
                }
            }
            return new Base64Utils(b, baseStrs[0]);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    ///////////////////////////////////////////////////////////////////////////////

    /**
     * 网络图片转换Base64的方法
     * @param netImagePath
     * @return
     */
    public static String networkImageToBase64(String netImagePath) {
        final ByteArrayOutputStream data = new ByteArrayOutputStream();
        String strNetImageToBase64 = null;
        try {
            // 创建URL
            URL url = new URL(netImagePath);
            final byte[] by = new byte[1024];
            // 创建链接
            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(100);
            InputStream is = conn.getInputStream();
            // 将内容读取内存中
            int len = -1;
            while ((len = is.read(by)) != -1) {
                data.write(by, 0, len);
            }
            // 对字节数组Base64编码
            strNetImageToBase64 = new String(Base64.encodeBase64(data.toByteArray()));
            // 关闭流
            is.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return strNetImageToBase64;
    }

    /**
     * 本地图片转换Base64的方法
     * @param imgPath
     * @return
     */
    public static String localImageToBase64(String imgPath) {
        byte[] data = null;
        // 读取图片字节数组
        try {
            InputStream in = Files.newInputStream(Paths.get(imgPath));
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 返回Base64编码过的字节数组字符串
        String strLocalImageToBase64 = new String(Base64.encodeBase64(Objects.requireNonNull(data)));
        return strLocalImageToBase64;
    }

    /**
     * base64字符串转换成图片
     *
     * @param imgStr      base64字符串
     * @param imgFilePath 图片存放路径
     * @return
     */
    public static boolean Base64ToImage(String imgStr, String imgFilePath) {

        // 图像数据判空校验
        if (StringUtils.isEmpty(imgStr)){
            return false;
        }
        try {
            // Base64解码
            byte[] b =Base64.decodeBase64(imgStr);
            for (int i = 0; i < b.length; ++i) {
                // 调整异常数据
                if (b[i] < 0) {
                    b[i] += 256;
                }
            }
            OutputStream out = Files.newOutputStream(Paths.get(imgFilePath));
            out.write(b);
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void main(String[] args) {

        //String localImgFilePath = "/Users/yoyo/Desktop/local.jpg";
        //String localImgStr = localImageToBase64(localImgFilePath);
        //String newLocalImgFilePath = "/Users/yoyo/Desktop/newLocal.jpg";
        //System.out.println(Base64ToImage(localImgStr,newLocalImgFilePath));

        //String netImagePath = "https://www.baidu.com/img/PCtm_d9c8750bed0b3c7d089fa7d55720d6cf.png";
        //String netImgStr = networkImageToBase64(netImagePath);
        //String newNetImgFilePath = "/Users/yoyo/Desktop/newNet.jpg";
        //System.out.println(Base64ToImage(netImgStr,newNetImgFilePath));

        //String base64 = networkImageToBase64("https://www.baidu.com/img/PCtm_d9c8750bed0b3c7d089fa7d55720d6cf.png");
        //System.out.println(base64);
        //MultipartFile multipartFile = Base64Utils.base64ToMultipart("data:image/jpg;base64,"+base64);
        //System.out.println(multipartFile.getName());

    }

}