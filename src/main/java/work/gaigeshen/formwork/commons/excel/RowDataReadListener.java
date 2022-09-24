package work.gaigeshen.formwork.commons.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 用于整合第三方库实现读取电子表格数据
 *
 * @author gaigeshen
 */
public class RowDataReadListener<R> extends AnalysisEventListener<R> {

    private final RowDataHandler<R> rowDataHandler;

    private final int batchCount;

    private final List<R> manyRowData = new ArrayList<>();

    public RowDataReadListener(RowDataHandler<R> rowDataHandler, int batchCount) {
        if (Objects.isNull(rowDataHandler)) {
            throw new IllegalArgumentException("rowDataHandler cannot be null");
        }
        if (batchCount < 0) {
            throw new IllegalArgumentException("invalid batch count: " + batchCount);
        }
        this.rowDataHandler = rowDataHandler;
        this.batchCount = batchCount;
    }

    @Override
    public void invoke(R data, AnalysisContext context) {
        // 获取此行的所有数据
        List<Object> allValues = new ArrayList<>();
        try {
            Class<?> currentClass = data.getClass();
            while (Objects.nonNull(currentClass)) {
                for (Field field : currentClass.getDeclaredFields()) {
                    field.setAccessible(true);
                    allValues.add(field.get(data));
                }
                currentClass = currentClass.getSuperclass();
            }
        } catch (IllegalAccessException e) {
            return;
        }
        // 此行的数据都为空则忽略掉
        if (allValues.stream().noneMatch(Objects::nonNull)) {
            return;
        }
        manyRowData.add(data);
        if (manyRowData.size() >= batchCount) {
            rowDataHandler.handleRowData(manyRowData);
            manyRowData.clear();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        if (!manyRowData.isEmpty()) {
            rowDataHandler.handleRowData(manyRowData);
        }
    }

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        rowDataHandler.handleHeaderRowData(headMap);
    }
}
