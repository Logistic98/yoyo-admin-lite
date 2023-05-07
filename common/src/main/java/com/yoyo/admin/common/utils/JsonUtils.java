package com.yoyo.admin.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonUtils {

    /**
     * 读取json文件，返回json字符串
     * @param fileName
     * @return
     */
    public static String readJsonFile(String fileName) {
        String jsonStr = "";
        try {
            File jsonFile = new File(fileName);
            FileReader fileReader = new FileReader(jsonFile);
            Reader reader = new InputStreamReader(new FileInputStream(jsonFile), "utf-8");
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            fileReader.close();
            reader.close();
            jsonStr = sb.toString();
            return jsonStr;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将json字符串转成List<Map<String, Object>>
     * @param jsonStr
     * @return
     */
    public static List<Map<String, Object>> jsonToListOfMap(String jsonStr) {
        List<Map<String, Object>> result = new ArrayList<>();
        try{
            result = (List<Map<String, Object>>) JSONArray.parse(jsonStr);
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    /**
     * JSON字符串美化
     * @param jsonStr
     * @return
     */
    public static String prettifyJson(String jsonStr) {
        JSONObject object = JSONObject.parseObject(jsonStr);
        return JSON.toJSONString(object, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteDateUseDateFormat);
    }

    public static void main(String[] args) {
        String path = "./test.json";
        String s = readJsonFile(path);
        System.out.println(s);
        List<Map<String, Object>> result = jsonToListOfMap(s);
        System.out.println(result);
    }

}
