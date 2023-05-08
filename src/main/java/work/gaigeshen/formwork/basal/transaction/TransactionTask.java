package work.gaigeshen.formwork.basal.transaction;

import java.util.concurrent.Callable;

/**
 * 事务任务
 *
 * @author gaigeshen
 */
public abstract class TransactionTask implements Callable<Object> {

    @Override
    public final Object call() throws Exception {
        return runTask();
    }

    /**
     * 执行任务的方法并返回事务任务结果
     *
     * @return 事务任务结果
     * @throws Exception 执行任务的时候发生异常
     */
    protected abstract Object runTask() throws Exception;
}
