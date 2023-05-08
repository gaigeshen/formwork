package work.gaigeshen.formwork.basal.bpmn.process;

/**
 * 表示业务流程
 *
 * @author gaigeshen
 */
public interface Process {

    /**
     * 返回流程标识符
     *
     * @return 流程标识符
     */
    String getProcessId();

    /**
     * 返回业务标识符
     *
     * @return 业务标识符
     */
    String getBusinessKey();

    /**
     * 返回业务流程发起人
     *
     * @return 业务流程发起人
     */
    String getUserId();
}
