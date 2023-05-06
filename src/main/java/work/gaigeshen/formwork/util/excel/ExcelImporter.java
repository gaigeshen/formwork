package work.gaigeshen.formwork.util.excel;

import java.io.InputStream;

/**
 * 电子表格导入工具
 *
 * @author gaigeshen
 */
public interface ExcelImporter<R> {

    /**
     * 返回此导入工具所使用的行数据处理器
     *
     * @return 行数据处理器不为空
     */
    RowDataHandler<R> getRowDataHandler();

    /**
     * 处理导入方法
     *
     * @param inputStream 数据输入流不能为空
     * @param sheetNum 电子表格索引从零开始
     * @param batchCount 批处理数据条数不能小于零
     */
    void handleImport(InputStream inputStream, int sheetNum, int batchCount);

    /**
     * 处理导入方法，假定电子表格索引为零
     *
     * @param inputStream 数据输入流不能为空
     * @param batchCount 批处理数据条数不能小于零
     */
    default void handleImport(InputStream inputStream, int batchCount) {
        handleImport(inputStream, 0, batchCount);
    }

}
