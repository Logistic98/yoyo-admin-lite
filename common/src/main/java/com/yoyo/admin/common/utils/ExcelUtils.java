package com.yoyo.admin.common.utils;

import com.yoyo.admin.common.annotation.Excel;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTMarker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.*;

public class ExcelUtils {

    private static final char DELIMITER = ',';
    private static final char RECORD_SEPARATOR = '\n';
    private static final Logger logger = LoggerFactory.getLogger(ExcelUtils.class);

    /**
     * 获取文件的第一个sheet
     *
     * @param inputStream 输入流
     * @return sheet
     */
    public static Sheet getFirstSheet(InputStream inputStream) {
        return getSheet(inputStream, 0);
    }

    /**
     * 获取sheet
     *
     * @param inputStream 输入流
     * @param sheetIndex  sheet序号（从0开始）
     * @return sheet
     */
    public static Sheet getSheet(InputStream inputStream, int sheetIndex) {
        Workbook workbook;
        try {
            workbook = WorkbookFactory.create(inputStream);
        } catch (Exception ex) {
            logger.error("load excel file error");
            return null;
        }
        if (workbook.getNumberOfSheets() >= sheetIndex + 1) {
            return workbook.getSheetAt(sheetIndex);
        } else {
            return null;
        }
    }

    /**
     * 获取Excel表头
     *
     * @param sheet sheet
     * @return excel表头
     */
    public static Map<String, Integer> getHeaders(Sheet sheet) {
        Iterator<Row> rowIterator = sheet.rowIterator();
        if (!rowIterator.hasNext()) {
            return Collections.emptyMap();
        }
        Map<String, Integer> headerMap = new HashMap<>();
        Row row = rowIterator.next();
        Iterator<Cell> cellIterator = row.cellIterator();
        int index = 0;
        while (cellIterator.hasNext()) {
            Object value = getCellValue(cellIterator.next());
            headerMap.put(value == null ? "" : value.toString().trim(), index++);
        }
        return headerMap;
    }

    /**
     * 获取除表头外的所有行的列表
     *
     * @param sheet     sheet
     * @param headerMap 表头
     * @return 所有行的列表
     */
    public static List<Map<String, Object>> getRows(Sheet sheet, Map<String, Integer> headerMap) {
        Iterator<Row> rowIterator = sheet.rowIterator();
        List<Map<String, Object>> rows = new ArrayList<>();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if (row.getRowNum() == 0) {
                continue;//过滤掉header所在的行
            }
            boolean allRowIsNull = true;
            Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()) {
                Object cellValue = getCellValue(cellIterator.next());
                if (cellValue != null) {
                    allRowIsNull = false;
                    break;
                }
            }
            if (allRowIsNull) {
                continue;//整行都空就跳过
            }
            Map<String, Object> rowMap = new HashMap<>();
            for (String k : headerMap.keySet()) {
                Integer index = headerMap.get(k);
                Cell cell = row.getCell(index);
                rowMap.put(k.trim(), getCellValue(cell));
            }
            rows.add(rowMap);
        }
        return rows;
    }

    /**
     * 获取单元格数据
     *
     * @param cell 单元格
     * @return 单元格数据
     */
    private static Object getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        CellType cellType = cell.getCellType();
        if (cellType == CellType.BLANK) {
            return null;
        } else if (cellType == CellType.BOOLEAN) {
            return cell.getBooleanCellValue();
        } else if (cellType == CellType.ERROR) {
            return cell.getErrorCellValue();
        } else if (cellType == CellType.FORMULA) {
            try {
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                } else {
                    return cell.getNumericCellValue();
                }
            } catch (IllegalStateException ex) {
                return cell.getRichStringCellValue();
            }
        } else if (cellType == CellType.NUMERIC) {
            if (DateUtil.isCellDateFormatted(cell)) {
                return cell.getDateCellValue();
            } else {
                return cell.getNumericCellValue();
            }
        } else if (cellType == CellType.STRING && cell.getStringCellValue() != null && !cell.getStringCellValue().isEmpty()) {
            return cell.getStringCellValue();
        } else {
            return null;
        }
    }

    /**
     * 设置单元格数据
     *
     * @param cell      单元格
     * @param value     单元格数据
     * @param dateStyle 日期格式（可为空）
     */
    private static void setCellValue(XSSFCell cell, Object value, CellStyle dateStyle) {
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Float) {
            cell.setCellValue((Float) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        } else if (value instanceof Long) {
            cell.setCellValue((Long) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof Date) {
            cell.setCellValue((Date) value);
            if (dateStyle == null) {
                cell.setCellStyle(createDefaultDateCellStyle(cell.getSheet().getWorkbook()));
            } else {
                cell.setCellStyle(dateStyle);
            }
        } else {
            String textValue = value == null ? "" : value.toString();
            if (textValue.length() > 32767) {
                textValue = textValue.substring(0, 32766);
            }
            XSSFRichTextString richTextString = new XSSFRichTextString(textValue);
            cell.setCellValue(richTextString);
        }
    }

    /**
     * 创建默认的时间类型单元格格式
     *
     * @param workbook 工作簿
     * @return 单元格格式
     */
    private static CellStyle createDefaultDateCellStyle(XSSFWorkbook workbook) {
        XSSFCreationHelper creationHelper = workbook.getCreationHelper();
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("yyyy-MM-dd HH:mm:ss"));
        return cellStyle;
    }

    /**
     * 从sheet中获取所有的图片数据
     *
     * @param sheet sheet
     * @return 图片数据
     */
    public static Map<String, PictureData> getSheetPictures(Sheet sheet) {
        if (sheet instanceof XSSFSheet) {
            return getSheetPicturesFromXlsx((XSSFSheet) sheet);
        } else if (sheet instanceof HSSFSheet) {
            return getSheetPicturesFromXls((HSSFSheet) sheet);
        }
        return new HashMap<>();
    }

    /**
     * 从sheet中获取所有的图片数据(.xlsx)
     *
     * @param sheet sheet
     * @return 图片数据
     */
    private static Map<String, PictureData> getSheetPicturesFromXlsx(XSSFSheet sheet) {
        int i = 0;
        Map<String, PictureData> pictures = new HashMap<>();
        List<POIXMLDocumentPart> list = sheet.getRelations();
        for (POIXMLDocumentPart part : list) {
            if (part instanceof XSSFDrawing) {
                XSSFDrawing drawing = (XSSFDrawing) part;
                List<XSSFShape> shapes = drawing.getShapes();
                for (XSSFShape shape : shapes) {
                    XSSFPicture picture = (XSSFPicture) shape;
                    XSSFClientAnchor anchor = picture.getPreferredSize();
                    CTMarker marker = anchor.getFrom();
                    String key = marker.getRow() + "-" + marker.getCol() + "-" + i++;
                    pictures.put(key, picture.getPictureData());
                }
            }
        }
        return pictures;
    }

    /**
     * 从sheet中获取所有的图片数据(.xls)
     *
     * @param sheet sheet
     * @return 图片数据
     */
    private static Map<String, PictureData> getSheetPicturesFromXls(HSSFSheet sheet) {
        int i = 0;
        Map<String, PictureData> pictures = new HashMap<>();
        if (sheet.getDrawingPatriarch() != null) {
            List<HSSFShape> list = sheet.getDrawingPatriarch().getChildren();
            for (HSSFShape shape : list) {
                if (shape instanceof HSSFPicture) {
                    HSSFPicture picture = (HSSFPicture) shape;
                    HSSFClientAnchor anchor = (HSSFClientAnchor) picture.getAnchor();
                    PictureData pictureData = picture.getPictureData();
                    String key = anchor.getRow1() + "-" + anchor.getCol1() + "-" + i++;
                    pictures.put(key, pictureData);
                }
            }
        }
        return pictures;
    }

    /**
     * 导出Excel工作簿
     *
     * @param headers 表头（key是标识，value是显示名称）
     * @param lines   行数据
     * @return Excel数据流
     */
    public static ByteArrayResource exportExcel(Map<String, Object> headers, List<Map<String, Object>> lines) throws IOException {
        return exportExcel(headers, lines, null);
    }

    /**
     * 导出Excel工作簿
     *
     * @param headers   表头（key是标识，value是显示名称）
     * @param lines     行数据
     * @param dateStyle 日期格式
     * @return Excel数据流
     */
    public static ByteArrayResource exportExcel(Map<String, Object> headers, List<Map<String, Object>> lines, CellStyle dateStyle) throws IOException {
        XSSFWorkbook workbook = exportExcelWorkBook(headers, lines, dateStyle);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        return new ByteArrayResource(outputStream.toByteArray());
    }

    /**
     * 导出Excel工作簿
     *
     * @param headers 表头（key是标识，value是显示名称）
     * @param lines   行数据
     * @return Excel工作簿
     */
    private static XSSFWorkbook exportExcelWorkBook(Map<String, Object> headers, List<Map<String, Object>> lines) {
        return exportExcelWorkBook(headers, lines, null);
    }

    /**
     * 导出Excel工作簿
     *
     * @param headers   表头（key是标识，value是显示名称）
     * @param lines     行数据
     * @param dateStyle 日期格式
     * @return Excel工作簿
     */
    private static XSSFWorkbook exportExcelWorkBook(Map<String, Object> headers, List<Map<String, Object>> lines, CellStyle dateStyle) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet();
        if (dateStyle == null) {
            dateStyle = createDefaultDateCellStyle(workbook);
        }
        writeSheetData(sheet, headers, lines, dateStyle);
        return workbook;
    }

    /**
     * 将数据写入到sheet
     *
     * @param sheet     sheet
     * @param headers   表头（key是标识，value是显示名称）
     * @param lines     行数据
     * @param dateStyle 日期格式
     */
    private static void writeSheetData(XSSFSheet sheet, Map<String, Object> headers, List<Map<String, Object>> lines, CellStyle dateStyle) {
        //生成表格标题行
        XSSFRow headerRow = sheet.createRow(0);
        List<String> columnNames = new ArrayList<>();
        XSSFCellStyle style = getHeaderStyle(sheet.getWorkbook());
        int columnNum = 0;
        for (Map.Entry<String, Object> entry : headers.entrySet()) {
            columnNames.add(entry.getKey());
            XSSFCell cell = headerRow.createCell(columnNum);
            cell.setCellStyle(style);
            Object title = entry.getValue();
            setCellValue(cell, title, dateStyle);
            sheet.setColumnWidth(columnNum, entry.getValue().toString().getBytes().length * 600);
            columnNum++;
        }
        //生成表格数据行
        if (lines == null || lines.size() == 0) {
            return;
        }
        for (int i = 0; i <= lines.size() - 1; i++) {
            Map<String, Object> record = lines.get(i);
            XSSFRow row = sheet.createRow(i + 1);
            for (int j = 0; j <= columnNames.size() - 1; j++) {
                XSSFCell cell = row.createCell(j);
                setCellValue(cell, record.get(columnNames.get(j)), dateStyle);
            }
        }
        // 设定自动宽度
        //for (int i = 0; i <= columnNames.size(); i++) {
        //    sheet.autoSizeColumn(i);
        //}
    }

    /**
     * 获取默认的表头样式
     *
     * @param workbook 工作薄
     * @return 表头样式
     */
    private static XSSFCellStyle getHeaderStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        return style;
    }

    /**
     * 导入时验证Excel表头
     *
     * @param excelHeader 表头Map
     * @return 错误消息
     */
    public static Optional<String> validateExcelHeader(Map<String, Integer> excelHeader, Map<String, Object> standardHeader) {
        if (excelHeader.isEmpty()) {
            return Optional.of("Excel不包含表头");
        }
        for (Map.Entry<String, Object> entry : standardHeader.entrySet()) {
            String title = entry.getValue().toString();
            if (excelHeader.get(title) == null) {
                return Optional.of("Excel表头列内容不正确,其不包含:" + title);
            }
        }
        return Optional.empty();
    }

    /**
     * 导入时验证Excel表头
     *
     * @param header 表头Map
     * @return 错误消息
     */
    public static Optional<String> validateExcelHeaderString(Map<String, String> header, Map<String, String> excelHeaders) {
        if (header.isEmpty()) {
            return Optional.of("Excel不包含表头");
        }
        for (Map.Entry<String, String> entry : excelHeaders.entrySet()) {
            String title = entry.getKey();
            if (header.get(title) == null) {
                return Optional.of("Excel表头列内容不正确,其不包含:" + title);
            }
        }
        return Optional.empty();
    }

    /**
     * 获取导出字段
     *
     * @return
     */
    public static Map<String, String> getExportHeaderAlias(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        Map<String, String> map = new LinkedHashMap<>();
        for (Field field : fields) {
            Excel annotation = field.getAnnotation(Excel.class);
            if (annotation == null) {
                continue;
            }
            map.put(field.getName(), annotation.name());
        }
        return map;
    }

    /**
     * 获取导入字段
     *
     * @return
     */
    public static Map<String, String> getImportHeaderAlias(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        Map<String, String> map = new LinkedHashMap<>();
        for (Field field : fields) {
            Excel annotation = field.getAnnotation(Excel.class);
            if (annotation == null) {
                continue;
            }
            map.put(annotation.name(), field.getName());
        }
        return map;
    }

}
