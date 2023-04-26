package work.gaigeshen.formwork.commons.transaction;

/**
 * 事务任务结果
 *
 * @author gaigeshen
 */
public class TransactionTaskResult {

    private final Object result;

    private final Exception error;

    private TransactionTaskResult(Object result, Exception error) {
        this.result = result;
        this.error = error;
    }

    public static TransactionTaskResult create(Object result, Exception error) {
        return new TransactionTaskResult(result, error);
    }

    public static TransactionTaskResult createResult(Object result) {
        return create(result, null);
    }

    public static TransactionTaskResult createError(Exception error) {
        return create(null, error);
    }

    public Object getResult() {
        return result;
    }

    public Exception getError() {
        return error;
    }

    @Override
    public String toString() {
        return "TransactionTaskResult{" +
                "result=" + getResult() +
                ", error=" + getError() +
                '}';
    }
}
