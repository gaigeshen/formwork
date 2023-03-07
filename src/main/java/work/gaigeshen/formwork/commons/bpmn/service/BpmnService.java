package work.gaigeshen.formwork.commons.bpmn.service;

import work.gaigeshen.formwork.commons.bpmn.candidate.TypedCandidate;
import work.gaigeshen.formwork.commons.bpmn.process.Process;
import work.gaigeshen.formwork.commons.bpmn.process.*;
import work.gaigeshen.formwork.commons.bpmn.usertask.*;

import java.util.Collection;
import java.util.List;

/**
 * 业务流程模型服务
 *
 * @author gaigeshen
 */
public interface BpmnService {

    /**
     * 查询业务流程
     *
     * @param parameters 业务流程查询参数
     * @return 业务流程
     */
    Collection<Process> queryProcesses(ProcessQueryParameters parameters);

    /**
     * 查询用户任务
     *
     * @param parameters 用户任务查询参数
     * @return 用户任务集合
     */
    Collection<UserTask> queryTasks(UserTaskQueryParameters parameters);

    /**
     * 查询历史用户任务
     *
     * @param parameters 历史用户任务查询参数
     * @return 历史用户任务集合
     */
    Collection<UserHistoricTask> queryHistoricTasks(UserHistoricTaskQueryParameters parameters);

    /**
     * 查询业务流程的用户任务活动
     *
     * @param parameters 用户任务活动查询参数
     * @return 用户任务活动集合
     */
    List<UserTaskActivity> queryTaskActivities(UserTaskActivityQueryParameters parameters);

    /**
     * 查询业务流程的下个进行中的用户任务活动，如果不存在则返回空对象
     *
     * @param parameters 下个进行中的用户任务活动查询参数
     * @return 下个进行中的用户任务活动
     */
    UserTaskActivity queryNextProcessingTaskActivity(UserTaskActivityQueryParameters parameters);

    /**
     * 用户任务执行完成操作，需要自动完成的用户任务也会执行
     *
     * @param parameters 执行完成操作参数
     * @return 任务自动执行完成结果
     */
    UserTaskAutoCompletion completeTask(UserTaskCompleteParameters parameters);

    /**
     * 用户任务执行自动完成操作，只有需要自动审批通过的任务才会执行
     *
     * @param parameters 执行自动完成操作参数
     * @return 任务自动执行完成结果
     */
    UserTaskAutoCompletion autoCompleteTasks(UserTaskAutoCompleteParameters parameters);

    /**
     * 开启业务流程，需要自动完成的用户任务会自动执行
     *
     * @param parameters 业务流程开启参数
     * @return 任务自动执行完成结果
     */
    UserTaskAutoCompletion startProcess(ProcessStartParameters parameters);

    /**
     * 预测业务流程审批候选人
     *
     * @param parameters 业务流程审批候选人预测参数
     * @return 审批候选人集合
     */
    List<TypedCandidate> prognoseProcessCandidates(ProcessCandidatePrognosisParameters parameters);

    /**
     * 部署业务流程
     *
     * @param parameters 业务流程部署参数
     */
    void deployProcess(ProcessDeployParameters parameters);
}
