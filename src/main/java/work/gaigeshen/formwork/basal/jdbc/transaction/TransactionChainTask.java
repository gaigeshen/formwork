package work.gaigeshen.formwork.basal.jdbc.transaction;

/**
 * 链式事务任务，当需要在上个事务任务执行完毕并提交且返回结果之后再执行下个任务的情况，可以选择此类型的任务
 *
 * @author gaigeshen
 */
public interface TransactionChainTask {

    /**
     * 任务方法
     *
     * @param previousTaskResult 上个事务任务的执行结果，此参数不会为空对象，该对象内的值可能为空
     * @return 任务的执行结果
     * @throws Exception 执行任务的时候发生异常
     */
    Object runTask(TransactionTaskResult previousTaskResult) throws Exception;

}
