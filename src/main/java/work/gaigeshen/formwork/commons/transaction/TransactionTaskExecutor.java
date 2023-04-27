package work.gaigeshen.formwork.commons.transaction;

import java.util.List;
import java.util.concurrent.Future;

/**
 * 事务任务执行器，可以选择异步执行所有的任务，还是等待所有的任务执行结果
 *
 * @author gaigeshen
 */
public interface TransactionTaskExecutor {

    /**
     * 批量执行任务，会等待所有的任务执行完成
     *
     * @param tasks 事务任务集合，事务任务抛出的异常不会被此方法再次抛出，而是会被包装为事务任务结果中的异常
     * @return 所有任务的执行结果，顺序同传入的任务
     * @throws Exception 在等待所有任务执行完成过程中发生异常
     */
    List<TransactionTaskResult> executeTasks(List<TransactionTask> tasks) throws Exception;

    /**
     * 批量执行任务，此方法立即返回
     *
     * @param tasks 事务任务集合，事务任务抛出的异常不会被此方法再次抛出，而是会被包装为事务任务结果中的异常
     * @return 所有任务的执行结果，顺序同传入的任务
     */
    Future<List<TransactionTaskResult>> executeTasksAsync(List<TransactionTask> tasks);

    /**
     * 批量执行链式任务，会等待所有的任务执行完成
     *
     * @param tasks 链式事务任务集合，事务任务抛出的异常不会被此方法再次抛出，而是会被包装为事务任务结果中的异常
     * @param breakOnError 在任何任务发生异常的时候是否中断剩余任务的执行，如果选择中断的情况，返回的事务任务结果数量要少于任务数量
     * @return 所有任务的执行结果，顺序同传入的任务
     * @throws Exception 在等待所有任务执行完成过程中发生异常
     */
    List<TransactionTaskResult> executeChainTasks(List<TransactionChainTask> tasks, boolean breakOnError) throws Exception;

    /**
     * 批量执行链式任务，此方法立即返回
     *
     * @param tasks 链式事务任务集合，事务任务抛出的异常不会被此方法再次抛出，而是会被包装为事务任务结果中的异常
     * @param breakOnError 在任何任务发生异常的时候是否中断剩余任务的执行，如果选择中断的情况，返回的事务任务结果数量要少于任务数量
     * @return 所有任务的执行结果，顺序同传入的任务
     */
    Future<List<TransactionTaskResult>> executeChainTasksAsync(List<TransactionChainTask> tasks, boolean breakOnError);

    /**
     * 批量执行链式任务，会等待所有的任务执行完成，在任何任务发生异常的时候不继续执行剩余的任务，注意返回的事务任务结果数量可能少于任务数量
     *
     * @param tasks 链式事务任务集合，事务任务抛出的异常不会被此方法再次抛出，而是会被包装为事务任务结果中的异常
     * @return 所有任务的执行结果，顺序同传入的任务
     * @throws Exception 在等待所有任务执行完成过程中发生异常
     */
    default List<TransactionTaskResult> executeChainTasks(List<TransactionChainTask> tasks) throws Exception {
        return executeChainTasks(tasks, true);
    }

    /**
     * 批量执行链式任务，此方法立即返回，在任何任务发生异常的时候不继续执行剩余的任务，注意返回的事务任务结果数量可能少于任务数量
     *
     * @param tasks 链式事务任务集合，事务任务抛出的异常不会被此方法再次抛出，而是会被包装为事务任务结果中的异常
     * @return 所有任务的执行结果，顺序同传入的任务
     */
    default Future<List<TransactionTaskResult>> executeChainTasksAsync(List<TransactionChainTask> tasks) {
        return executeChainTasksAsync(tasks, true);
    }
}
