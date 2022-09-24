package work.gaigeshen.formwork.commons.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;

import java.io.OutputStream;
import java.util.List;
import java.util.Objects;

/**
 * 默认的电子表格导出工具，基于第三方库实现
 *
 * @author gaigeshen
 * @see EasyExcel
 */
public class DefaultExcelExporter<R> implements ExcelExporter<R> {

    private final Class<R> rowDataClass;

    public DefaultExcelExporter(Class<R> rowDataClass) {
        if (Objects.isNull(rowDataClass)) {
            throw new IllegalArgumentException("rowDataClass cannot be null");
        }
        this.rowDataClass = rowDataClass;
    }

    @Override
    public void handleExport(String sheetName, List<R> manyRowData, OutputStream outputStream) {
        if (Objects.isNull(sheetName)) {
            throw new IllegalArgumentException("sheetName cannot be null");
        }
        if (Objects.isNull(manyRowData)) {
            throw new IllegalArgumentException("manyRowData cannot be null");
        }
        if (Objects.isNull(outputStream)) {
            throw new IllegalArgumentException("outputStream cannot be null");
        }
        EasyExcel.write(outputStream, rowDataClass)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .autoCloseStream(false)
                .sheet(sheetName).doWrite(manyRowData);
    }
}
