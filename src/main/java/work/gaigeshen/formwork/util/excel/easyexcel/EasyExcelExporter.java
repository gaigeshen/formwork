package work.gaigeshen.formwork.util.excel.easyexcel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.alibaba.excel.write.handler.RowWriteHandler;
import com.alibaba.excel.write.handler.context.RowWriteHandlerContext;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import work.gaigeshen.formwork.util.excel.ExcelExporter;

import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * 默认的电子表格导出工具，基于第三方库实现
 *
 * @author gaigeshen
 * @see EasyExcel
 */
public class EasyExcelExporter<R> implements ExcelExporter<R> {

    private final List<String> titleRows;

    private final Class<R> headClass;

    private final int columnCount;

    /**
     * 创建电子表格导出工具实例
     *
     * @param titleRows 标题行内容，此标题行不是字段的标题，是整个电子表格的顶部标题
     * @param headClass 字段标题通过从这个类型中的字段注解获取
     * @param columnCount 导出列数量
     */
    public EasyExcelExporter(List<String> titleRows, Class<R> headClass, int columnCount) {
        if (Objects.isNull(titleRows)) {
            throw new IllegalArgumentException("titleRows cannot be null");
        }
        if (Objects.isNull(headClass)) {
            throw new IllegalArgumentException("headClass cannot be null");
        }
        this.titleRows = titleRows;
        this.headClass = headClass;
        this.columnCount = columnCount;
    }

    @Override
    public final void handleExport(OutputStream outputStream, Iterable<R> manyRowData, Consumer<List<R>> batchConsumer) {
        int titleRowCount = titleRows.size();
        ExcelWriterBuilder writerBuilder = EasyExcel.write(outputStream, headClass).autoCloseStream(false);
        try (ExcelWriter excelWriter = writerBuilder.build()) {
            ExcelWriterSheetBuilder writerSheetBuilder = EasyExcel.writerSheet();
            writerSheetBuilder.relativeHeadRowIndex(titleRowCount);
            writerSheetBuilder.registerWriteHandler(new TitleRowWriteHandler());
            writerSheetBuilder.registerWriteHandler(createCellStyleStrategy());
            WriteSheet writeSheet = writerSheetBuilder.sheetName("sheet1").build();
            int rowIndex = titleRowCount;
            List<R> rowDataBatch = new LinkedList<>();
            for (R rowData : manyRowData) {
                rowIndex++;
                if (rowDataBatch.size() > 1000) {
                    batchConsumer.accept(rowDataBatch);
                    excelWriter.write(rowDataBatch, writeSheet);
                    rowDataBatch.clear();
                }
                rowDataBatch.add(rowData);
            }
            if (!rowDataBatch.isEmpty()) {
                batchConsumer.accept(rowDataBatch);
                excelWriter.write(rowDataBatch, writeSheet);
                rowDataBatch.clear();
            }
            WriteSheetHolder sheetHolder = excelWriter.writeContext().writeSheetHolder();
            createFooterCells(sheetHolder.getSheet(), rowIndex + 1, columnCount);
        }
    }

    /**
     * 重写此方法用于创建电子表格底部行内容，默认情况下什么也不做
     *
     * @param sheet 表格对象
     * @param rowIndex 当前的行索引，可以直接使用该索引来创建行内容
     * @param columnCount 字段数量
     */
    protected void createFooterCells(Sheet sheet, int rowIndex, int columnCount) { }

    /**
     * 创建标题行的单元格，默认情况会合并标题行的所有单元格
     *
     * @param workbook 工作簿
     * @param sheet 工作表
     * @param titleRow 标题行
     * @param title 标题内容
     * @param columnCount 标题行所占用的单元格数量
     */
    protected void createTitleCells(Workbook workbook, Sheet sheet, Row titleRow, String title, int columnCount) {
        int rowNum = titleRow.getRowNum();
        Cell cell = titleRow.createCell(0, CellType.STRING);
        cell.setCellValue(title);
        for (int j = 1; j < columnCount; j++) {
            titleRow.createCell(j);
        }
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, columnCount - 1));
        cell.setCellStyle(createTitleCellStyle(workbook, titleRow));
    }

    /**
     * 创建标题单元格样式，如果没有重写创建标题行的单元格方法，会将该样式应用到标题行的合并单元格中
     *
     * @param workbook 工作簿
     * @param titleRow 标题行
     * @return 单元格样式
     */
    protected CellStyle createTitleCellStyle(Workbook workbook, Row titleRow) {
        CellStyle cellStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        if (titleRow.getRowNum() == 0) {
            font.setBold(true);
            font.setFontHeightInPoints((short) 14);
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
        } else {
            cellStyle.setAlignment(HorizontalAlignment.LEFT);
        }
        cellStyle.setFont(font);
        return cellStyle;
    }

    /**
     * 创建数据行单元格样式策略
     *
     * @return 数据行单元格样式策略
     */
    protected final HorizontalCellStyleStrategy createCellStyleStrategy() {
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        setCellStyle(headWriteCellStyle, true);
        setCellStyle(contentWriteCellStyle, false);
        return new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
    }

    /**
     * 设置数据行单元格的样式
     *
     * @param writeCellStyle 用于设置单元格样式
     * @param isHeader 是否是标题行
     */
    protected void setCellStyle(WriteCellStyle writeCellStyle, boolean isHeader) {
        writeCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        writeCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        writeCellStyle.setBorderBottom(BorderStyle.THIN);
        writeCellStyle.setBorderLeft(BorderStyle.THIN);
        writeCellStyle.setBorderTop(BorderStyle.THIN);
        writeCellStyle.setBorderRight(BorderStyle.THIN);
        if (isHeader) {
            WriteFont writeFont = new WriteFont();
            writeFont.setBold(true);
            writeCellStyle.setWriteFont(writeFont);
            writeCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        }
    }

    /**
     *
     * @author gaigeshen
     */
    private class TitleRowWriteHandler implements RowWriteHandler {
        @Override
        public void beforeRowCreate(RowWriteHandlerContext context) {
            if (titleRows.isEmpty() || context.getRowIndex() != titleRows.size()) {
                return;
            }
            Sheet sheet = context.getWriteSheetHolder().getSheet();
            Workbook workbook = sheet.getWorkbook();
            int rowIndex = 0;
            for (String titleRow : titleRows) {
                Row row = sheet.createRow(rowIndex++);
                createTitleCells(workbook, sheet, row, titleRow, columnCount);
            }
        }
    }
}
