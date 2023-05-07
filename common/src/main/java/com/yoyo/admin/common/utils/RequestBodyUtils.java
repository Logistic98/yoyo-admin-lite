package com.yoyo.admin.common.utils;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

/**
 * 从ASE加密后的HttpServletRequest对象中以Map的形式获取Body
 */
public class RequestBodyUtils {

    private static final ObjectMapper _objectMapper = new ObjectMapper();

    /**
     * 从ASE加密后的HttpServletRequest对象中以Map的形式获取Body，注意：同一个request只能调用本方法一次
     * @param request
     * @param aesKey
     * @return
     */
    public static Map<String, Object> getBodyMapFromRequest(HttpServletRequest request, String aesKey) {
        Map<String, Object> body = new HashMap<>();
        try {
            BufferedReader bufferedReader = request.getReader();
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            if (stringBuilder.length() > 0) {
                String bodyString = stringBuilder.toString();
                if (aesKey != null && !aesKey.isEmpty()) {
                    bodyString = AesUtils.sampleDecrypt(bodyString, aesKey);
                    if (bodyString == null || bodyString.isEmpty()) {
                        return body;
                    }
                }
                JavaType javaType = _objectMapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class);
                body = _objectMapper.readValue(bodyString, javaType);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return body;
    }

}
