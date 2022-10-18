package work.gaigeshen.formwork.commons.bpmn;

import java.util.Collection;
import java.util.List;

/**
 * 业务流程模型服务
 *
 * @author gaigeshen
 */
public interface BpmnService {

    /**
     * 查询用户任务
     *
     * @param parameters 用户任务查询参数
     * @return 用户任务集合
     */
    Collection<UserTask> queryTasks(UserTaskQueryParameters parameters);

    /**
     * 查询用户任务历史
     *
     * @param parameters 用户任务历史查询参数
     * @return 用户任务历史集合
     */
    List<UserTaskActivity> queryTaskActivities(UserTaskActivityQueryParameters parameters);

    /**
     * 用户任务执行完成操作
     *
     * @param parameters 执行完成操作参数
     * @return 是否有后续用户任务
     */
    boolean completeTask(UserTaskCompleteParameters parameters);

    /**
     * 开启业务流程
     *
     * @param parameters 业务流程开启参数
     * @return 返回是否已经存在业务流程，如果已经存在业务流程则直接返回
     */
    boolean startProcess(ProcessStartParameters parameters);

    /**
     * 部署业务流程
     *
     * @param parameters 业务流程部署参数
     */
    void deployProcess(ProcessDeployParameters parameters);
}
