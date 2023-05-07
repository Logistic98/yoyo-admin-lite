package com.yoyo.admin.common.utils;

import cn.hutool.core.date.DateUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 日期时间工具类
 */
public class DateTimeUtils{

    private static final List<String> FORMATS = new ArrayList<>();

    static {
        FORMATS.add("yyyy-MM-dd HH:mm:ss");
        FORMATS.add("yyyy-MM-dd");
        FORMATS.add("yyyy-MM-dd HH:mm");
        FORMATS.add("yyyy-MM");
        FORMATS.add("yyyy/MM/dd HH:mm:ss");
        FORMATS.add("yyyy/MM/dd");
        FORMATS.add("yyyy/MM/dd HH:mm");
        FORMATS.add("yyyy/MM");
        FORMATS.add("yyyyMMdd");
        FORMATS.add("yyyyMM");
        FORMATS.add("yyyy-MM-dd HH:mm:ss.SSS");
        FORMATS.add("yyyy/MM/dd HH:mm:ss.SSS");
    }

    /**
     * 将字符串按指定格式解析成日期
     * @param dateStr
     * @param format
     * @return
     */
    public static Date parseDate(String dateStr, String format) {
        Date date = null;
        try {
            DateFormat dateFormat = new SimpleDateFormat(format);
            date = dateFormat.parse(dateStr);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return date;
    }

    /**
     * 将日期按指定格式解析成字符串
     * @param date
     * @param format
     * @return
     */
    public static String parseStr(Date date, String format) {
        String dateStr = null;
        try {
            DateFormat dateFormat = new SimpleDateFormat(format);
            dateStr = dateFormat.format(date);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return dateStr;
    }

    /**
     * 自动将字符串解析成日期
     * @param source
     * @return
     */
    public static Date autoParseDate(String source) {
        if (source == null) {
            return null;
        }
        String dateStr = source.trim();
        if (dateStr.isEmpty()) {
            return null;
        }
        if (dateStr.matches("^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}:\\d{1,2}$")) {
            return parseDate(dateStr, FORMATS.get(0));
        }
        if (dateStr.matches("^\\d{4}-\\d{1,2}-\\d{1,2}$")) {
            return parseDate(dateStr, FORMATS.get(1));
        }
        if (dateStr.matches("^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}$")) {
            return parseDate(dateStr, FORMATS.get(2));
        }
        if (dateStr.matches("^\\d{4}-\\d{1,2}$")) {
            return parseDate(dateStr, FORMATS.get(3));
        }
        if (dateStr.matches("^\\d{4}/\\d{1,2}/\\d{1,2} {1}\\d{1,2}:\\d{1,2}:\\d{1,2}$")) {
            return parseDate(dateStr, FORMATS.get(4));
        }
        if (dateStr.matches("^\\d{4}/\\d{1,2}/\\d{1,2}$")) {
            return parseDate(dateStr, FORMATS.get(5));
        }
        if (dateStr.matches("^\\d{4}/\\d{1,2}/\\d{1,2} {1}\\d{1,2}:\\d{1,2}$")) {
            return parseDate(dateStr, FORMATS.get(6));
        }
        if (dateStr.matches("^\\d{4}/\\d{1,2}$")) {
            return parseDate(dateStr, FORMATS.get(7));
        }
        if (dateStr.matches("^\\d{4}\\d{1,2}\\d{1,2}$")) {
            return parseDate(dateStr, FORMATS.get(8));
        }
        if (dateStr.matches("^\\d{4}\\d{1,2}$")) {
            return parseDate(dateStr, FORMATS.get(9));
        }
        if (dateStr.matches("^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}:\\d{1,2}.\\d{1,3}$")) {
            return parseDate(dateStr, FORMATS.get(10));
        }
        if (dateStr.matches("^\\d{4}/\\d{1,2}/\\d{1,2} {1}\\d{1,2}:\\d{1,2}:\\d{1,2}.\\d{1,3}$")) {
            return parseDate(dateStr, FORMATS.get(4));
        }
        try {
            long timestamp = Long.parseLong(dateStr);
            return new Date(timestamp);
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 指定年份的当前日期
     * @param date
     * @param yearNumber
     * @return
     */
    public static Date setYear(Date date, Integer yearNumber) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.YEAR, yearNumber);
        return calendar.getTime();
    }

    /**
     * 获取当年的开始时间
     * @param date
     * @return
     */
    public static Date getYearStart(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取当年的结束时间
     * @param date
     * @return
     */
    public static Date getYearEnd(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MONTH, 11);
        calendar.set(Calendar.DAY_OF_MONTH, 31);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 29);
        return calendar.getTime();
    }

    /**
     * 获取今天零时
     * @return
     */
    public static Date todayBegin() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取今天中午12时
     * @return
     */
    public static Date todayNoon() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取指定日期零时
     * @param date
     * @return
     */
    public static Date dayBegin(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取指定日期23时59分29秒
     * @param date
     * @return
     */
    public static Date dayEnd(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    /**
     * 获取明天
     * @param date
     * @return
     */
    public static Date tomorrow(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 1);
        return calendar.getTime();
    }

    /**
     * 获取一天前
     * @param date
     * @return
     */
    public static Date yesterday(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -1);
        return calendar.getTime();
    }

    /**
     * 获取相隔指定秒的日期
     * @param date
     * @param seconds
     * @return
     */
    public static Date secondAdd(Date date, Integer seconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, seconds);
        return calendar.getTime();
    }

    /**
     * 获取相隔指定分钟的日期
     * @param date
     * @param minutes
     * @return
     */
    public static Date minuteAdd(Date date, Integer minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, minutes);
        return calendar.getTime();
    }

    /**
     * 获取相隔指定小时的日期
     * @param date
     * @param hours
     * @return
     */
    public static Date hourAdd(Date date, Integer hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR, hours);
        return calendar.getTime();
    }

    /**
     * 获取相隔指定天数的日期
     * @param date
     * @param days
     * @return
     */
    public static Date dayAdd(Date date, Integer days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, days);
        return calendar.getTime();
    }

    /**
     * 获取相隔指定月数的日期
     * @param date
     * @param months
     * @return
     */
    public static Date monthAdd(Date date, Integer months) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, months);
        return calendar.getTime();
    }

    /**
     * 获取相隔指定年数的日期
     * @param date
     * @param years
     * @return
     */
    public static Date yearAdd(Date date, Integer years) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, years);
        return calendar.getTime();
    }

    /**
     * 获取一周前
     * @param date
     * @return
     */
    public static Date lastWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.WEEK_OF_YEAR, -1);
        return calendar.getTime();
    }

    /**
     * 获取一个月前
     * @param date
     * @return
     */
    public static Date lastMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, -1);
        return calendar.getTime();
    }

    /**
     * 获取季度
     * @param date
     * @return
     */
    public static Integer getSeason(Date date) {
        Calendar calendar = Calendar.getInstance();
        int season = 0;
        calendar.setTime(date);
        int month = calendar.get(Calendar.MONTH);
        switch (month) {
            case Calendar.JANUARY:
            case Calendar.FEBRUARY:
            case Calendar.MARCH:
                season = 1;
                break;
            case Calendar.APRIL:
            case Calendar.MAY:
            case Calendar.JUNE:
                season = 2;
                break;
            case Calendar.JULY:
            case Calendar.AUGUST:
            case Calendar.SEPTEMBER:
                season = 3;
                break;
            case Calendar.OCTOBER:
            case Calendar.NOVEMBER:
            case Calendar.DECEMBER:
                season = 4;
                break;
            default:
                break;
        }
        return season;
    }

    /**
     * 计算时间差
     * @param date1
     * @param date2
     * @return
     */
    public static String getDiffTime(Date date1, Date date2, String formatType){

        Long milliseconds = date1.getTime() - date2.getTime();  // 相差的毫秒值
        long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
        long nh = 1000 * 60 * 60;// 一小时的毫秒数
        long nm = 1000 * 60;// 一分钟的毫秒数
        long ns = 1000;// 一秒钟的毫秒数

        long day = milliseconds / nd; // 计算相差多少天
        long hour = milliseconds % nd / nh; // 计算相差剩余多少小时
        long min = milliseconds % nd % nh / nm; // 计算相差剩余多少分钟
        long sec = milliseconds % nd % nh % nm / ns; // 计算相差剩余多少秒
        long hourAll = milliseconds / nh; // 计算相差多少小时
        long min2 = milliseconds / nm; // 计算相差多少分钟

        String diffTime = null;
        if(Objects.equals(formatType, "day")){
            diffTime = day + "天" + hour + "小时" + min + "分钟" + sec + "秒";
        }else if(Objects.equals(formatType, "hour")){
            diffTime = hourAll + "小时" + min + "分钟" + sec + "秒";
        }else if(Objects.equals(formatType, "minute")){
            diffTime = min2 + "分钟" + sec + "秒";
        }

        return diffTime;
    }

    /**
     * 获取默认统计时间间隔，用于mysql
     * @param beginTime
     * @param endTime
     * @return
     */
    public static String getDefaultCountInterval(Date beginTime, Date endTime) {
        Calendar calendar = Calendar.getInstance();
        //默认以天为单位统计
        String interval = "%Y-%m%-%d";
        if (beginTime == null || endTime == null) {
            return interval;
        }
        Date tempTime;
        //如果时间间隔大于4周,以周为单位统计
        calendar.setTime(beginTime);
        calendar.add(Calendar.WEEK_OF_YEAR, 4);
        tempTime = calendar.getTime();
        if (tempTime.getTime() < endTime.getTime()) {
            interval = "%X年第%V周";
        }
        //如果时间间隔大于6个月,以月为单位统计
        calendar.setTime(beginTime);
        calendar.add(Calendar.MONTH, 6);
        tempTime = calendar.getTime();
        if (tempTime.getTime() < endTime.getTime()) {
            interval = "%Y年%c月";
        }
        //如果时间间隔大于3年,以年为单位统计
        calendar.setTime(beginTime);
        calendar.add(Calendar.YEAR, 3);
        tempTime = calendar.getTime();
        if (tempTime.getTime() < endTime.getTime()) {
            interval = "%Y年";
        }
        return interval;
    }

    /**
     * 自动判定聚合interval，用于es
     * @param beginTime
     * @param endTime
     * @return
     */
    public static String getDefaultAggregationInterval(Date beginTime, Date endTime) {
        Calendar calendar = Calendar.getInstance();
        String interval = "year";
        if (beginTime == null || endTime == null) {
            return interval;
        }
        Date tempTime;
        //如果时间间隔大于3天
        calendar.setTime(beginTime);
        calendar.add(Calendar.DAY_OF_YEAR, 3);
        tempTime = calendar.getTime();
        if (tempTime.getTime() < endTime.getTime()) {
            interval = "day";
        }
        //如果时间间隔大于3周
        calendar.setTime(beginTime);
        calendar.add(Calendar.WEEK_OF_YEAR, 3);
        tempTime = calendar.getTime();
        if (tempTime.getTime() < endTime.getTime()) {
            interval = "week";
        }
        //如果时间间隔大于3个月
        calendar.setTime(beginTime);
        calendar.add(Calendar.MONTH, 3);
        tempTime = calendar.getTime();
        if (tempTime.getTime() < endTime.getTime()) {
            interval = "month";
        }
        //如果时间间隔大于12个月
        calendar.setTime(beginTime);
        calendar.add(Calendar.MONTH, 12);
        tempTime = calendar.getTime();
        if (tempTime.getTime() < endTime.getTime()) {
            interval = "quarter";
        }
        //如果时间间隔大于6年
        calendar.setTime(beginTime);
        calendar.add(Calendar.YEAR, 5);
        tempTime = calendar.getTime();
        if (tempTime.getTime() < endTime.getTime()) {
            interval = "year";
        }
        return interval;
    }

    public static void main(String[] args) {

        // 获取当前时间 2022-02-15 09:55:01
        Date date = DateUtil.date();
        System.out.println(date);

        // 获取当前时间 Tue Feb 15 09:43:55 CST 2022
        Date dateCST = new Date();
        System.out.println(dateCST);

        // 获取当前时间戳 1644890101230
        Long timestamp = Calendar.getInstance().getTimeInMillis();
        System.out.println(timestamp);

        // 将时间戳转成字符串 2022-02-15 09:55:01
        SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestampStr = format.format(timestamp);
        System.out.println(timestampStr);

        // 将字符串按指定格式解析成日期 Tue Feb 15 09:43:55 CST 2022
        System.out.println(parseDate("2022-02-15 09:43:55","yyyy-MM-dd HH:mm:ss"));

        // 将日期按指定格式解析成字符串 2022-02-15 09:55:01
        System.out.println(parseStr(date,"yyyy-MM-dd HH:mm:ss"));

        // 自动将字符串解析成日期 Tue Feb 15 09:43:55 CST 2022
        System.out.println(autoParseDate("2022-02-15 09:43:55"));

        // 指定年份的当前日期 Tue Feb 15 09:43:55 CST 2022
        System.out.println(setYear(date,2022));

        // 获取当年开始时间 Sat Jan 01 00:00:00 CST 2022
        System.out.println(getYearStart(date));

        // 获取当年结束时间 Sat Dec 31 23:59:29 CST 2022
        System.out.println(getYearEnd(date));

        // 获取当日零时 Tue Feb 15 00:00:00 CST 2022
        System.out.println(todayBegin());

        // 获取当日12时 Tue Feb 15 12:00:00 CST 2022
        System.out.println(todayNoon());

        // 获取指定日期0时 Tue Feb 15 00:00:00 CST 2022
        System.out.println(dayBegin(date));

        // 获取指定日期23时59分29秒 Tue Feb 15 23:59:59 CST 2022
        System.out.println(dayEnd(date));

        // 获取明天 Wed Feb 16 09:55:01 CST 2022
        System.out.println(tomorrow(date));

        // 获取一天前 Mon Feb 14 09:55:01 CST 2022
        System.out.println(yesterday(date));

        // 获取相隔指定分钟的日期 Mon Feb 15 09:56:01 CST 2022
        System.out.println(minuteAdd(date,1));

        // 获取相隔指定小时的日期 Mon Feb 15 10:55:01 CST 2022
        System.out.println(hourAdd(date,1));

        // 获取相隔指定天数的日期 Wed Feb 16 09:55:01 CST 2022
        System.out.println(dayAdd(date,1));

        // 获取相隔指定月数的日期 Sat Jan 15 09:55:01 CST 2022
        System.out.println(monthAdd(date,-1));

        // 获取相隔指定年数的日期 Tue Feb 15 09:55:01 CST 2022
        System.out.println(yearAdd(date,0));

        // 获取一周前 Tue Feb 08 09:55:01 CST 2022
        System.out.println(lastWeek(date));

        // 获取一月前 Sat Jan 15 09:55:01 CST 2022
        System.out.println(lastMonth(date));

        // 获取季度 1
        System.out.println(getSeason(date));

        // 获取时间差 2天1小时10分钟1秒
        System.out.println(getDiffTime(dateCST, parseDate("2022-02-13 08:33:54","yyyy-MM-dd HH:mm:ss"), "day"));

        // 获取默认统计时间间隔，用于mysql %X年第%V周
        System.out.println(getDefaultCountInterval(date,dayAdd(date,100)));

        // 自动判定聚合interval，用于es month
        System.out.println(getDefaultAggregationInterval(date,dayAdd(date,100)));

    }
}
