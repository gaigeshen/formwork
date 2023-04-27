package work.gaigeshen.formwork.commons.transaction;

import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 默认的事务任务执行器
 *
 * @author gaigeshen
 */
public class SpringTransactionTaskExecutor implements TransactionTaskExecutor {

    private final PlatformTransactionManager transactionManager;

    private final AsyncTaskExecutor asyncTaskExecutor;

    /**
     * 创建默认的事务任务执行器
     *
     * @param transactionManager 事务管理器不能为空
     * @param asyncTaskExecutor 执行任务的线程池
     */
    public SpringTransactionTaskExecutor(PlatformTransactionManager transactionManager, AsyncTaskExecutor asyncTaskExecutor) {
        if (Objects.isNull(transactionManager) || Objects.isNull(asyncTaskExecutor)) {
            throw new IllegalArgumentException("transaction manager and thread pool executor cannot be null");
        }
        this.transactionManager = transactionManager;
        this.asyncTaskExecutor = asyncTaskExecutor;
    }

    @Override
    public List<TransactionTaskResult> executeTasks(List<TransactionTask> tasks) throws Exception {
        if (Objects.isNull(tasks)) {
            throw new IllegalArgumentException("tasks cannot be null");
        }
        return asyncTaskExecutor.submit(new TaskBatchRunner(tasks)).get();
    }

    @Override
    public Future<List<TransactionTaskResult>> executeTasksAsync(List<TransactionTask> tasks) {
        if (Objects.isNull(tasks)) {
            throw new IllegalArgumentException("tasks cannot be null");
        }
        return asyncTaskExecutor.submit(new TaskBatchRunner(tasks));
    }

    @Override
    public List<TransactionTaskResult> executeChainTasks(List<TransactionChainTask> tasks, boolean breakOnError) throws Exception {
        if (Objects.isNull(tasks)) {
            throw new IllegalArgumentException("tasks cannot be null");
        }
        return asyncTaskExecutor.submit(new ChainTaskBatchRunner(tasks, breakOnError)).get();
    }

    @Override
    public Future<List<TransactionTaskResult>> executeChainTasksAsync(List<TransactionChainTask> tasks, boolean breakOnError) {
        if (Objects.isNull(tasks)) {
            throw new IllegalArgumentException("tasks cannot be null");
        }
        return asyncTaskExecutor.submit(new ChainTaskBatchRunner(tasks, breakOnError));
    }

    /**
     * 批量事务任务运行，等待所有的事务任务运行结束
     *
     * @author gaigeshen
     */
    private class TaskBatchRunner implements Callable<List<TransactionTaskResult>> {

        private final List<TransactionTask> tasks;

        /**
         * 创建批量事务任务运行
         *
         * @param tasks 事务任务集合
         */
        private TaskBatchRunner(List<TransactionTask> tasks) {
            this.tasks = tasks;
        }

        @Override
        public List<TransactionTaskResult> call() throws Exception {
            CountDownLatch taskLatch =  new CountDownLatch(tasks.size());
            List<Future<TransactionTaskResult>> futures = new ArrayList<>();
            AtomicBoolean hasError = new AtomicBoolean();
            for (TransactionTask task : tasks) {
                futures.add(asyncTaskExecutor.submit(new TaskRunner(hasError, taskLatch, task)));
            }
            taskLatch.await();
            List<TransactionTaskResult> results = new ArrayList<>();
            for (Future<TransactionTaskResult> future : futures) {
                results.add(future.get());
            }
            return results;
        }
    }

    /**
     * 事务任务运行，每个事务任务运行的时候，如果发生异常将会回滚，且会设置全局的异常标识
     *
     * @author gaigeshen
     */
    private class TaskRunner implements Callable<TransactionTaskResult> {

        private final AtomicBoolean hasError;

        private final CountDownLatch taskLatch;

        private final TransactionTask task;

        /**
         * 事务任务运行，传入共享的异常标识以及计数器
         *
         * @param hasError 共享的布尔标识，用于标识当前批量处理的任务中是否存在异常
         * @param taskLatch 批量处理任务的计数器，计数器被还原就表示所有的任务都已经执行完毕
         * @param task 事务任务，如果该任务抛出任何异常，则会设置共享的布尔标识为有错误
         */
        private TaskRunner(AtomicBoolean hasError, CountDownLatch taskLatch, TransactionTask task) {
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

    /**
     * 批量链式事务任务运行，等待所有的事务任务运行结束
     *
     * @author gaigeshen
     */
    private class ChainTaskBatchRunner implements Callable<List<TransactionTaskResult>> {

        private final List<TransactionChainTask> chainTasks;

        private final boolean breakOnError;

        /**
         * 创建批量链式事务任务运行
         *
         * @param chainTasks 链式事务任务集合，将会按照传入集合的顺序来运行任务
         * @param breakOnError 是否在任何任务发生异常的时候中断后续任务运行
         */
        private ChainTaskBatchRunner(List<TransactionChainTask> chainTasks, boolean breakOnError) {
            this.chainTasks = chainTasks;
            this.breakOnError = breakOnError;
        }

        @Override
        public List<TransactionTaskResult> call() throws Exception {
            AtomicReference<TransactionTaskResult> previousTaskResult = new AtomicReference<>();
            previousTaskResult.set(TransactionTaskResult.createResult(null));
            List<TransactionTaskResult> results = new ArrayList<>();
            for (TransactionChainTask task : chainTasks) {
                TransactionTaskResult taskResult = previousTaskResult.get();
                if (Objects.nonNull(taskResult.getError()) && breakOnError) {
                    return results;
                }
                TransactionTaskResult nextTaskResult = asyncTaskExecutor.submit(new ChainTaskRunner(task, taskResult)).get();
                previousTaskResult.set(nextTaskResult);
                results.add(nextTaskResult);
            }
            return results;
        }
    }

    /**
     * 链式事务任务运行，每个链式事务任务执行完毕之后会提交事务，如果发生异常会回滚且返回异常的事务任务结果
     *
     * @author gaigeshen
     */
    private class ChainTaskRunner implements Callable<TransactionTaskResult> {

        private final TransactionChainTask chainTask;

        private final TransactionTaskResult previousTaskResult;

        /**
         * 链式事务任务运行需要任务对象和上个任务的执行结果
         *
         * @param chainTask 链式事务任务
         * @param previousTaskResult 上个任务的执行结果
         */
        private ChainTaskRunner(TransactionChainTask chainTask, TransactionTaskResult previousTaskResult) {
            this.chainTask = chainTask;
            this.previousTaskResult = previousTaskResult;
        }

        @Override
        public TransactionTaskResult call() {
            TransactionStatus status = transactionManager.getTransaction(TransactionDefinition.withDefaults());
            try {
                Object runTaskResult = chainTask.runTask(previousTaskResult);
                transactionManager.commit(status);
                return TransactionTaskResult.createResult(runTaskResult);
            } catch (Exception e) {
                transactionManager.rollback(status);
                return TransactionTaskResult.createError(e);
            }
        }
    }
}
