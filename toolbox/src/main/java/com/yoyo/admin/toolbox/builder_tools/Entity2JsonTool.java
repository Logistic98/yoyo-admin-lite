package com.yoyo.admin.toolbox.builder_tools;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yoyo.admin.common.domain.User;

/**
 * 将实体类转换成JSON，用于造测试数据
 */
public class Entity2JsonTool {

    public static String prettifyJson(String jsonStr) {
        JSONObject object = JSONObject.parseObject(jsonStr);
        return JSON.toJSONString(object, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteDateUseDateFormat);
    }

    public static void main(String[] args) throws JsonProcessingException {
        // 将实体类转换成JSON
        ObjectMapper mapper = new ObjectMapper();
        User user = new User();
        String result = mapper.writeValueAsString(user);
        // JSON字符串美化
        System.out.println(prettifyJson(result));
    }

}