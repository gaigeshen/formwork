package work.gaigeshen.formwork.commons.transaction;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 默认的事务任务执行器
 *
 * @author gaigeshen
 * @see PlatformTransactionManager
 */
public class SpringTransactionTaskExecutor implements TransactionTaskExecutor {

    private final PlatformTransactionManager transactionManager;

    private final ThreadPoolExecutor threadPoolExecutor;

    /**
     * 创建默认的事务任务执行器
     *
     * @param transactionManager 事务管理器不能为空
     * @param threadPoolExecutor 执行任务的线程池
     */
    public SpringTransactionTaskExecutor(PlatformTransactionManager transactionManager, ThreadPoolExecutor threadPoolExecutor) {
        if (Objects.isNull(transactionManager) || Objects.isNull(threadPoolExecutor)) {
            throw new IllegalArgumentException("transaction manager and thread pool executor cannot be null");
        }
        this.transactionManager = transactionManager;
        this.threadPoolExecutor = threadPoolExecutor;
    }

    @Override
    public List<TransactionTaskResult> executeTasks(List<TransactionTask> tasks) throws Exception {
        if (Objects.isNull(tasks)) {
            throw new IllegalArgumentException("tasks cannot be null");
        }
        CountDownLatch taskLatch =  new CountDownLatch(tasks.size());
        List<Future<TransactionTaskResult>> futures = new ArrayList<>();
        AtomicBoolean hasError = new AtomicBoolean();
        for (TransactionTask task : tasks) {
            futures.add(threadPoolExecutor.submit(new InternalTask(hasError, taskLatch, task)));
        }
        taskLatch.await();
        List<TransactionTaskResult> results = new ArrayList<>();
        for (Future<TransactionTaskResult> future : futures) {
            results.add(future.get());
        }
        return results;
    }

    /**
     * 任务的二次包装
     *
     * @author gaigeshen
     */
    private class InternalTask implements Callable<TransactionTaskResult> {

        private final AtomicBoolean hasError;

        private final CountDownLatch taskLatch;

        private final TransactionTask task;

        /**
         * 包装事务任务，传入共享的异常标识以及计数器
         *
         * @param hasError 共享的布尔标识，用于标识当前批量处理的任务中是否存在异常
         * @param taskLatch 批量处理任务的计数器，计数器被还原就表示所有的任务都已经执行完毕
         * @param task 事务任务，如果该任务抛出任何异常，则会设置共享的布尔标识为有错误
         */
        private InternalTask(AtomicBoolean hasError, CountDownLatch taskLatch, TransactionTask task) {
            this.hasError = hasError;
            this.taskLatch = taskLatch;
            this.task = task;
        }

        /**
         * 将在单独的线程内开启新的事务并执行任务，执行完任务之后等待其他任务也执行完，最后根据共享的异常标识决定提交事务还是回滚
         *
         * @return 成功执行任务之后返回结果
         * @throws Exception 可能在等待其他任务执行完毕的时候出现异常
         */
        @Override
        public TransactionTaskResult call() throws Exception {
            TransactionStatus status = transactionManager.getTransaction(TransactionDefinition.withDefaults());
            try {
                return TransactionTaskResult.createResult(task.call());
            } catch (Exception e) {
                hasError.set(true);
                return TransactionTaskResult.createError(e);
            } finally {
                taskLatch.countDown();
                taskLatch.await();
                if (hasError.get()) {
                    transactionManager.rollback(status);
                } else {
                    transactionManager.commit(status);
                }
            }
        }
    }
}
