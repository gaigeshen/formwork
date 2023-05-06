package work.gaigeshen.formwork.util.excel;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.function.Consumer;

/**
 * 电子表格导出工具
 *
 * @author gaigeshen
 */
public interface ExcelExporter<R> {

    /**
     * 处理导出
     *
     * @param outputStream 导出输出流不能为空
     * @param manyRowData 可迭代的数据行内容不能为空
     * @param batchConsumer 数据行内容被处理之前会分批次传递给此消费者不能为空
     * @throws IOException 可能抛出异常
     */
    void handleExport(OutputStream outputStream, Iterable<R> manyRowData, Consumer<List<R>> batchConsumer) throws IOException;

    /**
     * 处理导出
     *
     * @param source 用于获取导出输出流不能为空
     * @param manyRowData 可迭代的数据行内容不能为空
     * @param batchConsumer 数据行内容被处理之前会分批次传递给此消费者不能为空
     * @throws IOException 可能抛出异常
     */
    default void handleExport(OutputStreamSource source, Iterable<R> manyRowData, Consumer<List<R>> batchConsumer) throws IOException {
        handleExport(source.getOutputStream(), manyRowData, batchConsumer);
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
