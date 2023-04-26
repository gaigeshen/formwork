package work.gaigeshen.formwork.commons.transaction;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * 事务任务执行器
 *
 * @author gaigeshen
 */
public interface TransactionalTaskExecutor {

    /**
     * 批量执行任务
     *
     * @param tasks 批量任务
     * @return 所有任务的执行结果，顺序同传入的任务
     * @throws InterruptedException 在等待所有任务执行完成过程中发生异常
     * @throws ExecutionException 任何任务在执行过程中发生异常
     */
    List<Object> executeTasks(List<Callable<Object>> tasks) throws InterruptedException, ExecutionException;

}
