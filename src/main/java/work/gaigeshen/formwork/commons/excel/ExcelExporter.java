package work.gaigeshen.formwork.commons.excel;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;

/**
 * 电子表格导出工具
 *
 * @author gaigeshen
 */
public interface ExcelExporter<R> {
    /**
     * 处理导出方法
     *
     * @param sheetName 电子表格名称不能为空
     * @param manyRowData 需要导出的数据不能为空
     * @param outputStream 导出输出流不能为空
     */
    void handleExport(String sheetName, List<R> manyRowData, OutputStream outputStream);

    /**
     * 处理导出方法
     *
     * @param sheetName 电子表格名称不能为空
     * @param manyRowData 需要导出的数据不能为空
     * @param source 导出输出流源不能为空，用于获取输出流的
     * @throws IOException 在获取导出输出流的时候发生异常
     */
    default void handleExport(String sheetName, List<R> manyRowData, OutputStreamSource source) throws IOException {
        if (Objects.isNull(source)) {
            throw new IllegalArgumentException("outputStreamSource cannot be null");
        }
        handleExport(sheetName, manyRowData, source.getOutputStream());
    }

    /**
     * 此接口用于获取导出输出流
     *
     * @author gaigeshen
     */
    interface OutputStreamSource {
        /**
         * 实现此方法用于返回导出输出流
         *
         * @return 导出输出流不能为空
         * @throws IOException 在获取导出输出流的时候发生异常
         */
        OutputStream getOutputStream() throws IOException;
    }

}
