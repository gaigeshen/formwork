package work.gaigeshen.formwork.commons.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;

import java.io.InputStream;
import java.util.Objects;

/**
 * 默认的电子表格导入工具，基于第三方库实现
 *
 * @author gaigeshen
 * @see EasyExcel
 */
public class DefaultExcelImporter<R> implements ExcelImporter<R> {

    private final RowDataHandler<R> rowDataHandler;

    private final Class<R> rowDataClass;

    public DefaultExcelImporter(RowDataHandler<R> rowDataHandler, Class<R> rowDataClass) {
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
        if (Objects.isNull(inputStream)) {
            throw new IllegalArgumentException("inputStream cannot be null");
        }
        if (sheetNum < 0) {
            throw new IllegalArgumentException("invalid sheet number: " + sheetNum);
        }
        if (batchCount < 0) {
            throw new IllegalArgumentException("invalid batch count: " + batchCount);
        }
        ExcelReader reader = null;
        try {
            reader = EasyExcel.read(inputStream, rowDataClass, createRowDataListener(batchCount))
                    .autoCloseStream(false).build();
            ReadSheet sheet = EasyExcel.readSheet(sheetNum).build();
            reader.read(sheet);
        } finally {
            if (Objects.nonNull(reader)) {
                reader.finish();
            }
        }
    }

    private RowDataReadListener<R> createRowDataListener(int batchCount) {
        return new RowDataReadListener<>(rowDataHandler, batchCount);
    }

}
