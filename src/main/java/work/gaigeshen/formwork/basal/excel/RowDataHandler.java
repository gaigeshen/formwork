package work.gaigeshen.formwork.basal.excel;

import java.util.List;
import java.util.Map;

/**
 * 行数据处理器
 *
 * @author gaigeshen
 */
public interface RowDataHandler<R> {
    /**
     * 批量处理行数据
     *
     * @param manyRowData 许多行数据不为空
     */
    default void handleRowData(List<R> manyRowData) { }

    /**
     * 处理标题行数据
     *
     * @param headerRowData 标题行数据
     */
    default void handleHeaderRowData(Map<Integer, String> headerRowData) { }
}
