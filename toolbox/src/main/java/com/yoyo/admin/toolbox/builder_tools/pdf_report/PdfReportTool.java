package com.yoyo.admin.toolbox.builder_tools.pdf_report;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;
import com.itextpdf.text.pdf.draw.LineSeparator;

import java.io.File;
import java.nio.file.Files;

public class PdfReportTool {

    // 定义全局的字体静态变量
    private static Font titlefont;
    private static Font headfont;
    private static Font keyfont;
    private static Font textfont;
    // 最大宽度
    private static int maxWidth = 520;
    // 静态代码块
    static {
        try {
            // 不同字体（这里定义为同一种字体：包含不同字号、不同style）
            BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            titlefont = new Font(bfChinese, 16, Font.BOLD);
            headfont = new Font(bfChinese, 14, Font.BOLD);
            keyfont = new Font(bfChinese, 10, Font.BOLD);
            textfont = new Font(bfChinese, 10, Font.NORMAL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 生成PDF文件
    public void generatePDF(Document document) throws Exception {

        // 段落
        Paragraph paragraph = new Paragraph("美好的一天从早起开始", titlefont);
        paragraph.setAlignment(1); //设置文字居中 0靠左 1居中 2靠右
        paragraph.setIndentationLeft(12); //设置左缩进
        paragraph.setIndentationRight(12); //设置右缩进
        paragraph.setFirstLineIndent(24); //设置首行缩进
        paragraph.setLeading(20f); //行间距
        paragraph.setSpacingBefore(5f); //设置段落上空白
        paragraph.setSpacingAfter(10f); //设置段落下空白

        // 点线
        Paragraph p1 = new Paragraph();
        p1.add(new Chunk(new DottedLineSeparator()));

        // 空行
        Chunk newline = Chunk.NEWLINE;

        // 直线
        Paragraph p2 = new Paragraph();
        p2.add(new Chunk(new LineSeparator()));

        // 超链接
        Anchor anchor = new Anchor("baidu");
        anchor.setReference("https://www.baidu.com");

        // 添加图片
        Image image = Image.getInstance("https://www.baidu.com/img/PCtm_d9c8750bed0b3c7d089fa7d55720d6cf.png");
        image.setAlignment(Image.ALIGN_CENTER);
        image.scalePercent(40); //依照比例缩放

        // 表格
        PdfPTable table = createTable(new float[] {120, 80, 120, 80, 120, 80, 120});
        table.addCell(createCell("", keyfont));
        table.addCell(createCell("早上9 : 00", keyfont, Element.ALIGN_CENTER));
        table.addCell(createCell("中午11 : 00", keyfont, Element.ALIGN_CENTER));
        table.addCell(createCell("中午13 : 00", keyfont, Element.ALIGN_CENTER));
        table.addCell(createCell("下午15 : 00", keyfont, Element.ALIGN_CENTER));
        table.addCell(createCell("下午17 : 00", keyfont, Element.ALIGN_CENTER));
        table.addCell(createCell("晚上19 : 00", keyfont, Element.ALIGN_CENTER));
        String[] weekDays = {"星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"};
        for (int i = 0; i < 7; i++) {
            table.addCell(createCell(weekDays[i], textfont));
            table.addCell(createCell("起床", textfont));
            table.addCell(createCell("吃午饭", textfont));
            table.addCell(createCell("午休", textfont));
            table.addCell(createCell("下午茶", textfont));
            table.addCell(createCell("回家", textfont));
            table.addCell(createCell("吃晚饭", textfont));
        }
        table.addCell(createCell("总计", keyfont));
        table.addCell(createCell("6 件事", textfont, Element.ALIGN_CENTER, 6));

        document.add(paragraph);
        document.add(anchor);
        document.add(p1);
        document.add(newline);
        document.add(table);
        document.add(image);
        document.add(p2);
    }

    /**------------------------创建表格单元格的方法start----------------------------*/
    /**
     * 创建单元格（指定字体）
     * @param value
     * @param font
     * @return
     */
    public PdfPCell createCell(String value, Font font) {
        PdfPCell cell = new PdfPCell();
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPhrase(new Phrase(value, font));
        return cell;
    }
    /**
     * 创建单元格（指定字体、水平对齐）
     * @param value
     * @param font
     * @param align
     * @return
     */
    public PdfPCell createCell(String value, Font font, int align) {
        PdfPCell cell = new PdfPCell();
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(align);
        cell.setPhrase(new Phrase(value, font));
        return cell;
    }
    /**
     * 创建单元格（指定字体、水平对齐、单元格跨x列合并）
     * @param value
     * @param font
     * @param align
     * @param colspan
     * @return
     */
    public PdfPCell createCell(String value, Font font, int align, int colspan) {
        PdfPCell cell = new PdfPCell();
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(align);
        cell.setColspan(colspan);
        cell.setPhrase(new Phrase(value, font));
        return cell;
    }
    /**
     * 创建单元格（指定字体、水平..、边框宽度：0表示无边框、内边距）
     * @param value
     * @param font
     * @param align
     * @param borderWidth
     * @param paddingSize
     * @param flag
     * @return
     */
    public PdfPCell createCell(String value, Font font, int align, float[] borderWidth, float[] paddingSize, boolean flag) {
        PdfPCell cell = new PdfPCell();
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(align);
        cell.setPhrase(new Phrase(value, font));
        cell.setBorderWidthLeft(borderWidth[0]);
        cell.setBorderWidthRight(borderWidth[1]);
        cell.setBorderWidthTop(borderWidth[2]);
        cell.setBorderWidthBottom(borderWidth[3]);
        cell.setPaddingTop(paddingSize[0]);
        cell.setPaddingBottom(paddingSize[1]);
        if (flag) {
            cell.setColspan(2);
        }
        return cell;
    }
    /**------------------------创建表格单元格的方法end----------------------------*/


    /**--------------------------创建表格的方法start------------------- ---------*/
    /**
     * 创建默认列宽，指定列数、水平(居中、右、左)的表格
     * @param colNumber
     * @param align
     * @return
     */
    public PdfPTable createTable(int colNumber, int align) {
        PdfPTable table = new PdfPTable(colNumber);
        try {
            table.setTotalWidth(maxWidth);
            table.setLockedWidth(true);
            table.setHorizontalAlignment(align);
            table.getDefaultCell().setBorder(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return table;
    }
    /**
     * 创建指定列宽、列数的表格
     * @param widths
     * @return
     */
    public PdfPTable createTable(float[] widths) {
        PdfPTable table = new PdfPTable(widths);
        try {
            table.setTotalWidth(maxWidth);
            table.setLockedWidth(true);
            table.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.getDefaultCell().setBorder(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return table;
    }
    /**
     * 创建空白的表格
     * @return
     */
    public PdfPTable createBlankTable() {
        PdfPTable table = new PdfPTable(1);
        table.getDefaultCell().setBorder(0);
        table.addCell(createCell("", keyfont));
        table.setSpacingAfter(20.0f);
        table.setSpacingBefore(20.0f);
        return table;
    }
    /**--------------------------创建表格的方法end------------------- ---------*/


    public static void main(String[] args) throws Exception {
        try {
            // 1.新建document对象
            Document document = new Document(PageSize.A4);// 建立一个Document对象

            // 2.建立一个PdfWriter对象，与document对象关联
            File file = new File("/Users/yoyo/Desktop/PDFDemo.pdf");
            PdfWriter writer = PdfWriter.getInstance(document, Files.newOutputStream(file.toPath()));
            writer.setPageEvent(new Watermark("HELLO ITEXTPDF"));// 水印
            writer.setPageEvent(new HeaderFooter());// 页眉/页脚

            // 3.打开文档
            document.open();
            document.addTitle("Title@PDF-Java");// 标题
            document.addAuthor("Author@yoyo");// 作者
            document.addSubject("Subject@iText-pdf-sample");// 主题
            document.addKeywords("Keywords@iTextpdf");// 关键字
            document.addCreator("Creator@yoyo`s");// 创建者

            // 4.向文档中添加内容
            new PdfReportTool().generatePDF(document);

            // 5.关闭文档
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
