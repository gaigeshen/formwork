package work.gaigeshen.formwork.commons.excel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 抽象的行数据处理器，接受所有的行数据
 *
 * @author gaigeshen
 */
public abstract class AbstractRowDataHandler<R> implements RowDataHandler<R> {

    private final List<R> content = new ArrayList<>();

    @Override
    public void handleRowData(List<R> manyRowData) {
        content.addAll(manyRowData);
    }

    /**
     * 处理标题行数据，可以在这里去校验标题行，如果抛出异常则会终止后续的行数据处理
     *
     * @param headerRowData 标题行数据
     */
    @Override
    public void handleHeaderRowData(Map<Integer, String> headerRowData) {

    }

    public final List<R> getContent() {
        return content;
    }
}
