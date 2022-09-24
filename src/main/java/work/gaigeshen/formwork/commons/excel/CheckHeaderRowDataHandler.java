package work.gaigeshen.formwork.commons.excel;

import java.util.*;
import java.util.function.Function;

/**
 * 检测行标题是否合法的行数据处理器
 *
 * @author gaigeshen
 */
public class CheckHeaderRowDataHandler<R> extends AbstractRowDataHandler<R> {

    private final List<String> header;

    private final boolean includeOrder;

    private Function<String, RuntimeException> exception;

    /**
     * 设置异常提供者，在检测到标题不合法的时候使用此异常，会传递期待的正确的行标题
     *
     * @param exception 异常提供者，或者提供的异常可以为空，为空的情况则会使用默认的异常
     */
    public void setException(Function<String, RuntimeException> exception) {
        this.exception = exception;
    }

    /**
     * @param header 期待的行标题不要为空对象
     * @param includeOrder 标题顺序是否考虑
     */
    public CheckHeaderRowDataHandler(List<String> header, boolean includeOrder) {
        if (Objects.isNull(header)) {
            throw new IllegalArgumentException("header cannot be null");
        }
        this.header = header;
        this.includeOrder = includeOrder;
    }

    /**
     * @param header 期待的行标题不要为空对象，不考虑标题顺序
     */
    public CheckHeaderRowDataHandler(List<String> header) {
        this(header, false);
    }

    @Override
    public void handleHeaderRowData(Map<Integer, String> headerRowData) {
        if (Objects.isNull(header) || header.isEmpty()) {
            return;
        }
        if (includeOrder) {
            int index = 0;
            for (String h : header) {
                String actualHeader = headerRowData.get(index++);
                if (!Objects.equals(h, actualHeader)) {
                    if (Objects.nonNull(exception)) {
                        RuntimeException runtimeException = exception.apply(h);
                        if (Objects.nonNull(runtimeException)) {
                            throw runtimeException;
                        }
                    }
                    throw new IllegalStateException("header is wrong: expect: " + h + ", actual: " + actualHeader);
                }
            }
            return;
        }
        Collection<String> actualHeaders = headerRowData.values();
        for (String h : header) {
            if (!actualHeaders.contains(h)) {
                if (Objects.nonNull(exception)) {
                    RuntimeException runtimeException = exception.apply(h);
                    if (Objects.nonNull(runtimeException)) {
                        throw runtimeException;
                    }
                }
                throw new IllegalStateException("missing header: expect: " + h + ", but not found");
            }
        }
    }
}
