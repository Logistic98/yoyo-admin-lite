package com.yoyo.admin.common.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import org.apache.commons.io.FileUtils;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 发送HTTP请求工具类
 */
public class HttpRequestUtils {

    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url 发送请求的URL
     * @return URL 所代表远程资源的响应结果
     */
    public static String get(String url) throws Exception {
        HttpURLConnection httpURLConnection = getHttpURLConnectionForSimpleRequest(url);
        httpURLConnection.setRequestMethod("GET");
        // 建立实际的连接
        httpURLConnection.connect();
        return getHttpURLConnectionResponse(httpURLConnection);
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url  发送请求的 URL
     * @param body 请求参数，请求参数应该是Json的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String post(String url, String body) {
        try {
            return post(url, body, null);
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    /**
     * 向指定 URL 发送POST方法的请求（Token身份验证）
     *
     * @param url   发送请求的 URL
     * @param body  请求参数，请求参数应该是Json的形式。
     * @param token 身份验证使用的token
     * @return 远程资源的响应结果
     * @throws Exception 异常，包括远程资源返回的异常
     */
    public static String post(String url, String body, String token) throws Exception {
        OutputStream outputStream = null;
        byte[] writeBytes = body.getBytes(StandardCharsets.UTF_8);
        String response;
        try {
            HttpURLConnection httpURLConnection = getHttpURLConnectionForJson(url);
            // 设置文件长度
            httpURLConnection.setRequestProperty("Content-Length", String.valueOf(writeBytes.length));
            if (token != null && !token.isEmpty()) {
                httpURLConnection.setRequestProperty("Authorization", token);
            }
            httpURLConnection.setRequestMethod("POST");
            // 发送数据
            outputStream = httpURLConnection.getOutputStream();
            outputStream.write(writeBytes);
            outputStream.flush();
            response = getHttpURLConnectionResponse(httpURLConnection);
        } catch (Exception ex) {
            if (outputStream != null) {
                outputStream.close();
            }
            throw ex;
        }
        return response;
    }

    /**
     * 以form的方式发送post请求
     *
     * @param url    发送请求的 URL
     * @param params 请求参数，以&连接的字符串
     * @return 远程资源的响应结果
     * @throws Exception 异常，包括远程资源返回的异常
     */
    public static String postForm(String url, String params) throws Exception {
        OutputStream outputStream = null;
        byte[] writeBytes = params.getBytes(StandardCharsets.UTF_8);
        String response;
        try {
            HttpURLConnection httpURLConnection = getHttpURLConnectionForSimpleRequest(url);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            //发送数据
            outputStream = httpURLConnection.getOutputStream();
            outputStream.write(writeBytes);
            outputStream.flush();
            response = getHttpURLConnectionResponse(httpURLConnection);
        } catch (Exception ex) {
            if (outputStream != null) {
                outputStream.close();
            }
            throw ex;
        }
        return response;
    }

    /**
     * 向指定 URL 发送PUT方法的请求
     *
     * @param url  发送请求的 URL
     * @param body 请求参数，请求参数应该是Json的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String put(String url, String body) throws Exception {
        OutputStream outputStream = null;
        byte[] writeBytes = body.getBytes(StandardCharsets.UTF_8);
        String response = "";
        try {
            HttpURLConnection httpURLConnection = getHttpURLConnectionForJson(url);
            //设置文件长度
            httpURLConnection.setRequestProperty("Content-Length", String.valueOf(writeBytes.length));
            httpURLConnection.setRequestMethod("PUT");
            //发送数据
            outputStream = httpURLConnection.getOutputStream();
            outputStream.write(writeBytes);
            outputStream.flush();
            response = getHttpURLConnectionResponse(httpURLConnection);
        } catch (Exception ex) {
            if (outputStream != null) {
                outputStream.close();
            }
            throw ex;
        }
        return response;
    }

    /**
     * 向指定URL发送DELETE方法的请求
     *
     * @param url 发送请求的URL
     * @return URL 所代表远程资源的响应结果
     */
    public static String delete(String url) throws Exception {
        HttpURLConnection httpURLConnection = getHttpURLConnectionForSimpleRequest(url);
        httpURLConnection.setRequestMethod("DELETE");
        httpURLConnection.connect();//建立实际的连接
        return getHttpURLConnectionResponse(httpURLConnection);
    }

    /**
     * 为简单请求准备的url连接
     * @param urlString
     * @return
     * @throws Exception
     */
    private static HttpURLConnection getHttpURLConnectionForSimpleRequest(String urlString) throws Exception {
        URL url = new URL(urlString);
        // 打开和URL之间的连接
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        // 设置超时
        httpURLConnection.setConnectTimeout(3000);
        httpURLConnection.setReadTimeout(6000);
        // 设置通用的请求属性
        httpURLConnection.setRequestProperty("accept", "*/*");
        return httpURLConnection;
    }

    /**
     * 为application/json准备的url连接
     * @param urlString
     * @return
     * @throws Exception
     */
    private static HttpURLConnection getHttpURLConnectionForJson(String urlString) throws Exception {
        URL url = new URL(urlString);
        // 打开和URL之间的tcp连接
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        // 默认为false 发送post请求必须设置setDoOutput(true)
        httpURLConnection.setDoOutput(true);
        // 默认为true 所以不设置也可以
        httpURLConnection.setDoInput(true);
        // 连接超时
        httpURLConnection.setConnectTimeout(3000);
        // 读取超时
        httpURLConnection.setReadTimeout(6000);
        // 设置请求头
        httpURLConnection.setRequestProperty("accept", "*/*");
        // 设置请求头
        httpURLConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        return httpURLConnection;
    }

    /**
     * 获取response
     * @param httpURLConnection
     * @return
     * @throws Exception
     */
    private static String getHttpURLConnectionResponse(HttpURLConnection httpURLConnection) throws Exception {
        StringBuilder response = new StringBuilder();
        int code = httpURLConnection.getResponseCode();
        InputStream inputStream;
        if (code >= 200 && code < 300) {
            inputStream = httpURLConnection.getInputStream();
        } else {
            inputStream = httpURLConnection.getErrorStream();
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            response.append(line);
        }
        bufferedReader.close();
        if (code < 400) {
            return response.toString();
        } else {
            throw new Exception(response.toString());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 以POST上传文件的方式请求接口
     * @param urlStr
     * @param textMap
     * @param fileMap
     * @param contentType 没有传入文件类型默认采用application/octet-stream
     * contentType非空采用filename匹配默认的图片类型
     * @return 返回response数据
     */
    public static String queryApiUsePostByFile(String urlStr, Map<String, String> textMap, Map<String, String> fileMap, String contentType) {

        String res = "";
        HttpURLConnection conn = null;
        // boundary就是request头和上传文件内容的分隔符
        String BOUNDARY = "---------------------------123821742118716";
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(30000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("User-Agent","Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
            conn.setRequestProperty("Content-Type","multipart/form-data; boundary=" + BOUNDARY);
            OutputStream out = new DataOutputStream(conn.getOutputStream());
            // text
            if (textMap != null) {
                StringBuffer strBuf = new StringBuffer();
                Iterator iter = textMap.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    String inputName = (String) entry.getKey();
                    String inputValue = (String) entry.getValue();
                    if (inputValue == null) {
                        continue;
                    }
                    strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
                    strBuf.append("Content-Disposition: form-data; name=\"" + inputName + "\"\r\n\r\n");
                    strBuf.append(inputValue);
                }
                out.write(strBuf.toString().getBytes());
            }
            // file
            if (fileMap != null) {
                Iterator iter = fileMap.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    String inputName = (String) entry.getKey();
                    String inputValue = (String) entry.getValue();
                    if (inputValue == null) {
                        continue;
                    }
                    File file = new File(inputValue);
                    String filename = file.getName();

                    //没有传入文件类型，同时根据文件获取不到类型，默认采用application/octet-stream
                    contentType = new MimetypesFileTypeMap().getContentType(file);
                    //contentType非空采用filename匹配默认的图片类型
                    if(!"".equals(contentType)){
                        if (filename.endsWith(".png")) {
                            contentType = "image/png";
                        }else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg") || filename.endsWith(".jpe")) {
                            contentType = "image/jpeg";
                        }else if (filename.endsWith(".gif")) {
                            contentType = "image/gif";
                        }else if (filename.endsWith(".ico")) {
                            contentType = "image/image/x-icon";
                        }
                    }
                    if (contentType == null || "".equals(contentType)) {
                        contentType = "application/octet-stream";
                    }
                    StringBuffer strBuf = new StringBuffer();
                    strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
                    strBuf.append("Content-Disposition: form-data; name=\"" + inputName + "\"; filename=\"" + filename + "\"\r\n");
                    strBuf.append("Content-Type:" + contentType + "\r\n\r\n");
                    out.write(strBuf.toString().getBytes());
                    DataInputStream in = new DataInputStream(new FileInputStream(file));
                    int bytes = 0;
                    byte[] bufferOut = new byte[1024];
                    while ((bytes = in.read(bufferOut)) != -1) {
                        out.write(bufferOut, 0, bytes);
                    }
                    in.close();
                }
            }
            byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
            out.write(endData);
            out.flush();
            out.close();
            // 读取返回数据
            StringBuffer strBuf = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                strBuf.append(line).append("\n");
            }
            res = strBuf.toString();
            reader.close();
            reader = null;
        } catch (Exception e) {
            System.out.println("发送POST请求出错。" + urlStr);
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
                conn = null;
            }
        }
        return res;
    }

    /**
     * 以POST方式请求接口并返回文件下载到指定目录
     * @param url
     * @param bodyJson
     * @param downloadPath
     * @param extName
     */
    public static String queryApiUsePostReturnFile(String url, String bodyJson, String downloadPath, String extName) {
        String fileName = null;
        FileOutputStream out = null;
        String resultStr = HttpUtil.post(url, bodyJson);
        InputStream inputStream = new ByteArrayInputStream(resultStr.getBytes());
        try {
            // 转化为byte数组
            byte[] data = getByteData(inputStream);
            // 建立文件存储目录
            File file = new File(downloadPath + "/" + DateUtil.formatDate(new Date()));
            if (!file.exists()) {
                file.mkdirs();
            }
            // 修改文件名（文件名不能是中文）
            fileName = IdUtil.simpleUUID() + extName;
            File res = new File(file + File.separator + fileName);
            // 写入输出流
            out = new FileOutputStream(res);
            out.write(data);
        }catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭输出流
            try {
                if (null != out){
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fileName;
    }

    /**
     * 从输入流中获取字节数组
     * @param in
     * @return
     * @throws IOException
     */
    private static byte[] getByteData(InputStream in) throws IOException {
        byte[] b = new byte[1024];
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int len = 0;
        while ((len = in.read(b)) != -1) {
            bos.write(b, 0, len);
        }
        if(null!=bos){
            bos.close();
        }
        return bos.toByteArray();
    }

    ////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 检查指定URL的HTTP请求状态码
     * @param checkUrl
     * @return
     */
    public static int CheckUrlCode(String checkUrl) {
        int httpCode = 0;
        try {
            URL u = new URL(checkUrl);
            try {
                HttpURLConnection uConnection = (HttpURLConnection) u.openConnection();
                try {
                    uConnection.connect();
                    httpCode = uConnection.getResponseCode();
                    //System.out.println(httpCode);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (
                MalformedURLException e) {
            e.printStackTrace();
        }
        return httpCode;
    }

    /**
     * 将网络文件下载到本地的指定文件夹下
     * @param url
     * @param directory
     * @param fileName
     */
    public static void downloadFileToLocal(String url, String directory, String fileName) {
        URL source;
        try {
            source = new URL(url);
            File destination = FileUtils.getFile(directory, fileName);
            FileUtils.copyURLToFile(source, destination);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 文件名进行UrlEncode加密
     *
     * @param filename 文件名
     * @return 加密后名称
     */
    public static String urlEncodeFileName(String filename) {
        try {
            return URLEncoder.encode(filename, "UTF-8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 设置文件下载头
     *
     * @param response 响应
     * @param filename 文件名
     */
    public static void setFileDownloadHeader(HttpServletResponse response, String filename) {
        String headerValue = "attachment;";
        headerValue += " filename=\"" + urlEncodeFileName(filename) + "\";";
        headerValue += " filename*=utf-8''" + urlEncodeFileName(filename);
        response.setHeader("Content-Disposition", headerValue);
    }

    /**
     * 从url读取输入流
     *
     * @param urlPath url路径
     * @return 输入流
     */
    public static InputStream readInputStreamFromUrl(String urlPath) {
        InputStream inStream = null;
        try {
            URL url = new URL(urlPath);
            URLConnection conn = url.openConnection();
            inStream = conn.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inStream;
    }

    /**
     * 拼接get请求的url请求地址
     */
    public static String spliceUrl(String baseUrl, Map<String, Object> params) {
        StringBuilder builder = new StringBuilder(baseUrl);
        boolean isFirst = true;
        for (String key : params.keySet()) {
            if (key != null && params.get(key) != null) {
                if (isFirst) {
                    isFirst = false;
                    builder.append("?");
                } else {
                    builder.append("&");
                }
                builder.append(key)
                        .append("=")
                        .append(params.get(key));
            }
        }
        return builder.toString();
    }

    public static void main(String[] args) throws IOException {

        // 测试1：以POST上传文件的方式请求接口
        String url1 = "API URL";
        String filePath = "本地图片路径";
        Map<String, String> textMap = new HashMap<>();
        // 设置form入参（可选）
        textMap.put("param", "test");
        // 设置文件路径
        Map<String, String> fileMap = new HashMap<>();
        fileMap.put("image", filePath);
        String contentType = "";
        String ret = queryApiUsePostByFile(url1, textMap, fileMap, contentType);
        System.out.println(ret);

        // 测试2：以POST方式请求接口并返回文件下载到指定目录
        String url2 = "API URL";
        String downloadPath = "./downloads";
        Map<String, Object> body = new HashMap<>();
        body.put("param", "test");
        String bodyJson = JSON.toJSONString(body);
        String extName = ".pdf";
        queryApiUsePostReturnFile(url2, bodyJson, downloadPath, extName);

    }

}
