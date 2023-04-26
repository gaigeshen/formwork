package work.gaigeshen.formwork.commons.transaction;

import java.util.List;

/**
 * 事务任务执行器
 *
 * @author gaigeshen
 */
public interface TransactionTaskExecutor {

    /**
     * 批量执行任务，会等待所有的任务执行完成
     *
     * @param tasks 事务任务集合
     * @return 所有任务的执行结果，顺序同传入的任务
     * @throws Exception 在等待所有任务执行完成过程中发生异常
     */
    List<TransactionTaskResult> executeTasks(List<TransactionTask> tasks) throws Exception;

}