package work.gaigeshen.formwork.basal.excel.easyexcel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.read.metadata.ReadSheet;
import work.gaigeshen.formwork.basal.excel.ExcelImporter;
import work.gaigeshen.formwork.basal.excel.RowDataHandler;

import java.io.InputStream;
import java.util.Objects;

/**
 * 默认的电子表格导入工具，基于第三方库实现
 *
 * @author gaigeshen
 * @see EasyExcel
 */
public class EasyExcelImporter<R> implements ExcelImporter<R> {

    private final RowDataHandler<R> rowDataHandler;

    private final Class<R> rowDataClass;

    /**
     * 创建电子表格导入工具实例
     *
     * @param rowDataHandler 数据行内容处理器
     * @param rowDataClass 数据行对象类型
     */
    public EasyExcelImporter(RowDataHandler<R> rowDataHandler, Class<R> rowDataClass) {
        if (Objects.isNull(rowDataHandler)) {
            throw new IllegalArgumentException("rowDataHandler cannot be null");
        }
        if (Objects.isNull(rowDataClass)) {
            throw new IllegalArgumentException("rowDataClass cannot be null");
        }
        this.rowDataHandler = rowDataHandler;
        this.rowDataClass = rowDataClass;
    }

    @Override
    public RowDataHandler<R> getRowDataHandler() {
        return rowDataHandler;
    }

    @Override
    public void handleImport(InputStream inputStream, int sheetNum, int batchCount) {
        RowDataReadListener<R> listener = new RowDataReadListener<>(rowDataHandler, batchCount);
        ExcelReaderBuilder builder = EasyExcel.read(inputStream, rowDataClass, listener).autoCloseStream(false);
        try (ExcelReader reader = builder.build()) {
            ReadSheet sheet = EasyExcel.readSheet(sheetNum).build();
            reader.read(sheet);
        }
    }
}
