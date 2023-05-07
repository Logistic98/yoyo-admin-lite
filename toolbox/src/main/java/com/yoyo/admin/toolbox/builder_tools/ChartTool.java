package com.yoyo.admin.toolbox.builder_tools;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import java.awt.*;
import java.io.File;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 使用jfreechart绘制图表（折线图/饼图）
 */
@SuppressWarnings("rawtypes")
public class ChartTool {

    private static String NO_DATA_MSG = "暂无数据";
    private static Font FONT = new Font("宋体", Font.PLAIN, 20);
    public static Color[] CHART_COLORS = {
            new Color(31,129,188), new Color(92,92,97), new Color(144,237,125), new Color(255,188,117),
            new Color(153,158,255), new Color(255,117,153), new Color(253,236,109), new Color(128,133,232),
            new Color(158,90,102),new Color(255, 204, 102) };// 颜色

    static{
        setChartTheme();
    }
    /**
     * 中文主题样式 解决乱码
     */
    public static void setChartTheme() {
        // 设置中文主题样式 解决乱码
        StandardChartTheme chartTheme = new StandardChartTheme("CN");
        // 设置标题字体
        chartTheme.setExtraLargeFont(FONT);
        // 设置图例的字体
        chartTheme.setRegularFont(FONT);
        // 设置轴向的字体
        chartTheme.setLargeFont(FONT);
        chartTheme.setSmallFont(FONT);
        chartTheme.setTitlePaint(new Color(51, 51, 51));
        chartTheme.setSubtitlePaint(new Color(85, 85, 85));

        chartTheme.setLegendBackgroundPaint(Color.WHITE);// 设置标注
        chartTheme.setLegendItemPaint(Color.BLACK);//
        chartTheme.setChartBackgroundPaint(Color.WHITE);

        Paint[] OUTLINE_PAINT_SEQUENCE = new Paint[] { Color.WHITE };
        // 绘制器颜色源
        DefaultDrawingSupplier drawingSupplier = new DefaultDrawingSupplier(CHART_COLORS, CHART_COLORS, OUTLINE_PAINT_SEQUENCE,
                DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE, DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE,
                DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE);
        chartTheme.setDrawingSupplier(drawingSupplier);

        chartTheme.setPlotBackgroundPaint(Color.WHITE);// 绘制区域
        chartTheme.setPlotOutlinePaint(Color.WHITE);// 绘制区域外边框
        chartTheme.setLabelLinkPaint(new Color(8, 55, 114));// 链接标签颜色
        chartTheme.setLabelLinkStyle(PieLabelLinkStyle.CUBIC_CURVE);

        chartTheme.setAxisOffset(new RectangleInsets(5, 12, 5, 12));
        chartTheme.setDomainGridlinePaint(new Color(192, 208, 224));// X坐标轴垂直网格颜色
        chartTheme.setRangeGridlinePaint(new Color(192, 192, 192));// Y坐标轴水平网格颜色

        chartTheme.setBaselinePaint(Color.WHITE);
        chartTheme.setCrosshairPaint(Color.BLUE);// 不确定含义
        chartTheme.setAxisLabelPaint(new Color(51, 51, 51));// 坐标轴标题文字颜色
        chartTheme.setTickLabelPaint(new Color(67, 67, 72));// 刻度数字
        chartTheme.setBarPainter(new StandardBarPainter());// 设置柱状图渲染
        chartTheme.setXYBarPainter(new StandardXYBarPainter());// XYBar 渲染

        chartTheme.setItemLabelPaint(Color.black);
        chartTheme.setThermometerPaint(Color.white);// 温度计

        ChartFactory.setChartTheme(chartTheme);
    }

    /**
     * 必须设置文本抗锯齿
     */
    public static void setAntiAlias(JFreeChart chart) {
        chart.setTextAntiAlias(false);

    }

    /**
     * 设置图例无边框，默认黑色边框
     */
    public static void setLegendEmptyBorder(JFreeChart chart) {
        chart.getLegend().setFrame(new BlockBorder(Color.WHITE));
    }

    /**
     * 提供静态方法：获取报表图形1：饼状图
     * @param title    标题
     * @param datas    数据
     * @param
     */
    public static void createPiePort(String title,Map<String,Double> datas,String url){
        try {
            // 如果不使用Font,中文将显示不出来
            DefaultPieDataset pds = new DefaultPieDataset();

            // 获取迭代器：
            Set<Entry<String, Double>> set =  datas.entrySet();
            Iterator iterator=(Iterator) set.iterator();
            Entry entry=null;
            while(iterator.hasNext()){
                entry=(Entry) iterator.next();
                pds.setValue(entry.getKey().toString(),Double.parseDouble(entry.getValue().toString()));
            }
            /**
             * 生成一个饼图的图表
             * 分别是:显示图表的标题、需要提供对应图表的DateSet对象、是否显示图例、是否生成贴士以及是否生成URL链接
             */
            JFreeChart chart = ChartFactory.createPieChart(title, pds, true, false, true);
            setPieRender((PiePlot) chart.getPlot());

            //将内存中的图片写到本地硬盘
            org.jfree.chart.ChartUtils.saveChartAsPNG(new File(url), chart,800,500);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 提供静态方法：获取报表图形3：折线图
     * @param title        标题
     * @param datas        数据
     */
    public static void createLinePort(String title,Map<String,Double> datas,String xName,String yName,String url){
        try {
            //种类数据集
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            //获取迭代器：
            Set<Entry<String, Double>> set =  datas.entrySet();
            Iterator iterator=(Iterator) set.iterator();
            Entry entry=null;
            while(iterator.hasNext()){
                entry=(Entry) iterator.next();
                dataset.setValue(Double.parseDouble(entry.getValue().toString()),//y
                        title,                         //名称
                        entry.getKey().toString());      //x
            }
            //创建折线图,折线图分水平显示和垂直显示两种
            JFreeChart chart = ChartFactory.createLineChart(title, xName, yName, dataset,//2D折线图
                    PlotOrientation.VERTICAL,
                    false, // 是否显示图例(对于简单的柱状图必须是false)
                    true, // 是否生成工具
                    true);// 是否生成URL链接
            //得到绘图区
            setLineRender((CategoryPlot)chart.getPlot(),true,true);
            org.jfree.chart.ChartUtils.saveChartAsPNG(new File(url), chart, 1000,600);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置折线图样式
     *
     * @param plot
     * @param isShowDataLabels  是否显示数据标签
     */
    public static void setLineRender(CategoryPlot plot, boolean isShowDataLabels, boolean isShapesVisible) {
        plot.setNoDataMessage(NO_DATA_MSG);
        plot.setInsets(new RectangleInsets(10, 10, 0, 10), false);
        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
        renderer.setDefaultStroke(new BasicStroke(1.5F));
        if (isShowDataLabels) {
            renderer.setDefaultItemLabelsVisible(true);
            renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator(StandardCategoryItemLabelGenerator.DEFAULT_LABEL_FORMAT_STRING,
                    NumberFormat.getInstance()));
            renderer.setDefaultPositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE1, TextAnchor.BOTTOM_CENTER));// weizhi
        }
        renderer.setDefaultShapesVisible(isShapesVisible);// 数据点绘制形状
        setXAixs(plot);
        setYAixs(plot);
    }

    /**
     * 设置饼状图渲染
     */
    public static void setPieRender(Plot plot) {
        plot.setNoDataMessage(NO_DATA_MSG);
        plot.setInsets(new RectangleInsets(10, 10, 5, 10));
        PiePlot piePlot = (PiePlot) plot;
        piePlot.setInsets(new RectangleInsets(0, 0, 0, 0));
        piePlot.setCircular(true);// 圆形
        // piePlot.setSimpleLabels(true);// 简单标签
        piePlot.setLabelGap(0.01);
        piePlot.setInteriorGap(0.05D);
        piePlot.setLegendItemShape(new Rectangle(10, 10));// 图例形状
        piePlot.setIgnoreNullValues(true);
        piePlot.setLabelBackgroundPaint(null);// 去掉背景色
        piePlot.setLabelShadowPaint(null);// 去掉阴影
        piePlot.setLabelOutlinePaint(null);// 去掉边框
        piePlot.setShadowPaint(null);
        // 0:category 1:value:2 :percentage
        piePlot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}:{2}"));// 显示标签数据
    }

    /**
     * 设置类别图表(CategoryPlot) X坐标轴线条颜色和样式
     *
     * @param
     */
    public static void setXAixs(CategoryPlot plot) {
        Color lineColor = new Color(31, 121, 170);
        plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        plot.getDomainAxis().setAxisLinePaint(lineColor); // X坐标轴颜色
        plot.getDomainAxis().setTickMarkPaint(lineColor); // X坐标轴标记|竖线颜色

    }

    /**
     * 设置类别图表(CategoryPlot) Y坐标轴线条颜色和样式 同时防止数据无法显示
     *
     * @param
     */
    public static void setYAixs(CategoryPlot plot) {
        Color lineColor = new Color(31, 121, 170);
        ValueAxis axis = plot.getRangeAxis();
        axis.setAxisLinePaint(lineColor);// Y坐标轴颜色
        axis.setTickMarkPaint(lineColor);// Y坐标轴标记|竖线颜色
        // false隐藏Y刻度
        axis.setAxisLineVisible(true);
        axis.setTickMarksVisible(true);
        // Y轴网格线条
        plot.setRangeGridlinePaint(new Color(192, 192, 192));
        plot.setRangeGridlineStroke(new BasicStroke(1));

        plot.getRangeAxis().setUpperMargin(0.1);// 设置顶部Y坐标轴间距,防止数据无法显示
        plot.getRangeAxis().setLowerMargin(0.1);// 设置底部Y坐标轴间距

    }

    /**
     * 设置XY图表(XYPlot) X坐标轴线条颜色和样式
     *
     * @param
     */
    public static void setXY_XAixs(XYPlot plot) {
        Color lineColor = new Color(31, 121, 170);
        plot.getDomainAxis().setAxisLinePaint(lineColor);// X坐标轴颜色
        plot.getDomainAxis().setTickMarkPaint(lineColor);// X坐标轴标记|竖线颜色
    }

    /**
     * 设置XY图表(XYPlot) Y坐标轴线条颜色和样式 同时防止数据无法显示
     *
     * @param
     */
    public static void setXY_YAixs(XYPlot plot) {
        Color lineColor = new Color(192, 208, 224);
        ValueAxis axis = plot.getRangeAxis();
        axis.setAxisLinePaint(lineColor);// X坐标轴颜色
        axis.setTickMarkPaint(lineColor);// X坐标轴标记|竖线颜色
        // 隐藏Y刻度
        axis.setAxisLineVisible(false);
        axis.setTickMarksVisible(false);
        // Y轴网格线条
        plot.setRangeGridlinePaint(new Color(192, 192, 192));
        plot.setRangeGridlineStroke(new BasicStroke(1));
        plot.setDomainGridlinesVisible(false);

        plot.getRangeAxis().setUpperMargin(0.12);// 设置顶部Y坐标轴间距,防止数据无法显示
        plot.getRangeAxis().setLowerMargin(0.12);// 设置底部Y坐标轴间距

    }

    public static void main(String[] args) {

        // 绘制饼图
        Map<String, Double> map = new HashMap<String, Double>();
        map.put("天使", (double) 1000);
        map.put("雄兵连", (double) 700);
        map.put("太阳之光", (double) 600);
        map.put("辅助", (double) 400);
        createPiePort("投票结果", map,"/Users/yoyo/Temp/pieChart.jpg");

        // 绘制折线图
        Map<String, Double> map1 = new HashMap<String, Double>();
        map1.put("2020-02-03", (double) 700);
        map1.put("2020-02-04", (double) 1000);
        map1.put("2020-02-05", (double) 600);
        map1.put("2020-02-06", (double) 400);
        map1.put("2020-02-07", (double) 4000);
        map1.put("2020-02-08", (double) 1200);
        map1.put("2020-02-09", (double) 800);
        createLinePort("近7日金额",map1,"日期","金额（元）","/Users/yoyo/Temp/lineChart.jpg");

    }

}

