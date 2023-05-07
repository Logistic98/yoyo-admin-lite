package com.yoyo.admin.toolbox.format_tools;

import cn.hutool.core.util.StrUtil;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 驼峰字符串工具类
 */
public class CamelTool {

    /**
     * 下划线转驼峰
     * @param underscore
     * @return
     */
    public static String underscoreToCamel(String underscore){
        String[] ss = underscore.split("_");
        if(ss.length ==1){
            return underscore;
        }
        StringBuffer sb = new StringBuffer();
        sb.append(ss[0]);
        for (int i = 1; i < ss.length; i++) {
            sb.append(upperFirstCase(ss[i]));
        }
        return sb.toString();
    }

    /**
     * 驼峰转下划线
     * @param camelCase
     * @return
     */
    public static String camelToUnderscore(String camelCase){
        Pattern humpPattern = Pattern.compile("[A-Z]");
        Matcher matcher = humpPattern.matcher(camelCase);
        StringBuffer sb = new StringBuffer();
        while(matcher.find()){
            matcher.appendReplacement(sb, "_"+matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 首字母 转小写
     * @param str
     * @return
     */
    private static String lowerFirstCase(String str) {
        char[] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    /**
     * 首字母 转大写
     * @param str
     * @return
     */
    private static String upperFirstCase(String str) {
        char[] chars = str.toCharArray();
        chars[0] -= 32;
        return String.valueOf(chars);
    }

    /**
     * Logstash数据同步的重命名
     * @param fields
     */
    public static void logstashRename(String fields){
        fields = fields.replaceAll(" +","");
        List<String> fieldList = Arrays.asList(fields.split(","));
        System.out.println("rename => {");
        for (int i = 0; i < fieldList.size(); i++) {
            String fieldName = fieldList.get(i);
            System.out.println(StrUtil.format("\"{}\" => \"{}\"",fieldName,underscoreToCamel(fieldName)));
        }
        System.out.println("}");
    }


    public static void main(String[] args) {

        // 下划线转驼峰
        String camelStr = underscoreToCamel("cteate_time");
        System.out.println(camelStr);

        // 驼峰转下划线
        String underscoreStr = camelToUnderscore("cteateTime");
        System.out.println(underscoreStr);

        // Logstash数据同步的重命名（输入为逗号分割的字符串）
        // SELECT GROUP_CONCAT(COLUMN_NAME SEPARATOR ",") FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = '数据库名' AND TABLE_NAME = '表名'
        String fields = "yoyo_id, yoyo_name, yoyo_url";
        try{
            logstashRename(fields);
        }catch(Exception e){
            e.printStackTrace();
        }

    }

}
