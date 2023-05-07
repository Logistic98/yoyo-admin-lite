package com.yoyo.admin.common.utils;

import java.math.BigDecimal;

/**
 * 数学计算工具类
 */
public class MathUtils {

    // 默认除法运算精度
    private static final int DEF_DIV_SCALE = 10;
    // PI的取值精度
    public static double PI = 3.1415926535897932384626;

    /**
     * 提供精确的加法运算
     * @param v1 被加数
     * @param v2 加数
     * @return v1加v2
     */
    public static double add(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2).doubleValue();
    }

    /**
     * 提供精确的减法运算
     * @param v1
     * @param v2
     * @return v1减v2
     */
    public static double sub(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2).doubleValue();
    }

    /**
     * 提供精确的乘法运算
     * @param v1
     * @param v2
     * @return v1乘v2
     */
    public static double mul(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2).doubleValue();
    }

    /**
     * 提供相对精确的除法运算，当发生除不尽的情况，精确到小数点后10位
     * @param v1
     * @param v2
     * @return v1除v2
     */
    public static double div(double v1, double v2) {
        if(DEF_DIV_SCALE < 0){
            throw new IllegalArgumentException(" the scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, DEF_DIV_SCALE, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 计算两点之间的距离
     * @param point1
     * @param point2
     * @return
     */
    public static double distanceToPoint(double[] point1, double[] point2) {
        double x1 = point1[0];
        double y1 = point1[1];
        double x2 = point2[0];
        double y2 = point2[1];
        double lineLength = 0;
        lineLength = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
        return lineLength;
    }

    /**
     * 点到直线的最短距离的判断
     * @param start （x1,y1）
     * @param end    (x2,y2)
     * @param point （x0,y0）
     * @return
     */
    public static double distanceToSegment(double[] start, double[] end, double[] point) {
        double space = 0;
        double a, b, c;
        a = distanceToPoint(start, end); // 线段的长度
        b = distanceToPoint(start, point); // (x1,y1)到点的距离
        c = distanceToPoint(end, point); // (x2,y2)到点的距离
        if (c+b == a) { //点在线段上
            space = 0;
            return space;
        }
        if (a <= 0.000001) { //不是线段，是一个点
            space = b;
            return space;
        }
        if (c * c >= a * a + b * b) { //组成直角三角形或钝角三角形，(x1,y1)为直角或钝角
            space = b;
            return space;
        }
        if (b * b >= a * a + c * c) { //组成直角三角形或钝角三角形，(x2,y2)为直角或钝角
            space = c;
            return space;
        }
        // 组成锐角三角形，则求三角形的高
        double p = (a + b + c) / 2; // 半周长
        double s = Math.sqrt(p * (p - a) * (p - b) * (p - c)); // 海伦公式求面积
        space = 2 * s / a; // 返回点到线的距离（利用三角形面积公式求高）
        return space;
    }

    public static void main(String[] args) {
        double[] start = {0,2};
        double[] end = {2,0};
        double[] point = {0,0};
        System.out.println(distanceToSegment(start, end, point));
    }

}