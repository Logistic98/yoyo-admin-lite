package com.yoyo.admin.common.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 */
public class StringUtils {

    /**
     * 截取指定长度的字符串
     *
     * @param str 原字符串
     * @param len 长度
     * @return 如果str为null，则返回null；如果str长度小于len，则返回str；如果str的长度大于len，则返回截取后的字符串
     */
    public static String subStrByStrAndLen(String str, int len) {
        return null != str ? str.substring(0, Math.min(str.length(), len)) : null;
    }

    /**
     * 生成指定位数的数字标号
     * 0：表示前面补0
     * digit：表示保留数字位数
     * d：表示参数为正数类型
     */
    public static String fillString(int num, int digit) {
        return String.format("%0"+digit+"d", num);
    }

    /**
     * 根据总价与活动价计算折扣
     * @param totalPrice
     * @param activityPrice
     * @return
     */
    public static String calPriceDiscount(int totalPrice, int activityPrice) {
        BigDecimal totalPriceBigDecimal = new BigDecimal(totalPrice);
        BigDecimal activityPriceBigDecimal = new BigDecimal(activityPrice);
        double num = activityPriceBigDecimal.divide(totalPriceBigDecimal, 10, BigDecimal.ROUND_HALF_UP).doubleValue();
        DecimalFormat df=new DecimalFormat("0.0");
        return df.format(num*10);
    }

    /**
     * 检查字符串中是否包含某子串
     */
    public static boolean checkSubString(String wholeString, String subString){
        boolean flag = false;
        int result = wholeString.indexOf(subString);
        if(result != -1){
            flag = true;
        }
        return flag;
    }

    /**
     * 将List转化成指定字符分隔的字符串
     * @param strList
     * @param delimiter
     * @return
     */
    public static String listToStr(List<String> strList, String delimiter) {
        String result = "";
        try{
            result = String.join(delimiter, strList);
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 去除字符串里的所有空格
     * @param str
     * @return
     */
    public static String replaceAllSpace(String str) {
        return str.replaceAll(" +","");
    }

    /**
     * 检查是否为空
     */
    public static boolean isBlank(String string) {
        if (string == null || "".equals(string.trim())) {
            return true;
        }
        return false;
    }

    /**
     * 检查是否不为空
     */
    public static boolean isNotBlank(String string) {
        return !isBlank(string);
    }

    /**
     * 取某分隔符（第一处）之前的字符串
     * @param str
     * @param delimiter
     * @return
     */
    public static String substringBefore(String str, String delimiter) {
        String result = "";
        try{
            result = StringUtils.substringBefore(str, delimiter);
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 取某分隔符（最后一处）之前的字符串
     * @param str
     * @param delimiter
     * @return
     */
    public static String substringBeforeLast(String str, String delimiter) {
        String result = "";
        try{
            result = StringUtils.substringBeforeLast(str, delimiter);
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 删除html标签
     */
    public static String delHtmlTag(String htmlStr) {
        //定义script的正则表达式
        String regExScript = "<script[^>]*?>[\\s\\S]*?<\\/script>";
        //定义style的正则表达式
        String regExStyle = "<style[^>]*?>[\\s\\S]*?<\\/style>";
        //定义HTML标签的正则表达式
        String regExHtml = "<[^>]+>";
        //定义HTML标签的正则表达式
        String regExHtml2 = "\\{[^\\}]+\\}";
        Pattern pScript = Pattern.compile(regExScript, Pattern.CASE_INSENSITIVE);
        Matcher mScript = pScript.matcher(htmlStr);
        //过滤script标签
        htmlStr = mScript.replaceAll("");
        Pattern pStyle = Pattern.compile(regExStyle, Pattern.CASE_INSENSITIVE);
        Matcher mStyle = pStyle.matcher(htmlStr);
        //过滤style标签
        htmlStr = mStyle.replaceAll("");
        Pattern pHtml = Pattern.compile(regExHtml, Pattern.CASE_INSENSITIVE);
        Matcher mHtml = pHtml.matcher(htmlStr);
        //过滤html标签
        htmlStr = mHtml.replaceAll("");
        Pattern pHtml2 = Pattern.compile(regExHtml2, Pattern.CASE_INSENSITIVE);
        Matcher mHtml2 = pHtml2.matcher(htmlStr);
        //过滤html标签
        htmlStr = mHtml2.replaceAll("");
        //返回文本字符串
        return htmlStr.trim();
    }

    public static void main(String[] args) {

        System.out.println(subStrByStrAndLen("test",2));

        List<String> strList = new ArrayList<>();
        strList.add("zhangsan");
        strList.add("lisi");
        strList.add("wanger");
        System.out.println(listToStr(strList,","));

    }

}
