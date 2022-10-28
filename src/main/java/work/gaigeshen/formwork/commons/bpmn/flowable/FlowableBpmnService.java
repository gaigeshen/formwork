package work.gaigeshen.formwork.commons.bpmn.flowable;

import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.FlowNode;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.flowable.variable.api.history.HistoricVariableInstance;
import work.gaigeshen.formwork.commons.bpmn.BpmnService;
import work.gaigeshen.formwork.commons.bpmn.Candidate;
import work.gaigeshen.formwork.commons.bpmn.DefaultUserTask;
import work.gaigeshen.formwork.commons.bpmn.DefaultUserTaskActivity;
import work.gaigeshen.formwork.commons.bpmn.ProcessDeployParameters;
import work.gaigeshen.formwork.commons.bpmn.ProcessNode;
import work.gaigeshen.formwork.commons.bpmn.ProcessStartParameters;
import work.gaigeshen.formwork.commons.bpmn.UserTask;
import work.gaigeshen.formwork.commons.bpmn.UserTaskActivity;
import work.gaigeshen.formwork.commons.bpmn.UserTaskActivityQueryParameters;
import work.gaigeshen.formwork.commons.bpmn.UserTaskCompleteParameters;
import work.gaigeshen.formwork.commons.bpmn.UserTaskQueryParameters;

import java.util.*;
import java.util.stream.Collectors;

import static work.gaigeshen.formwork.commons.bpmn.flowable.FlowableBpmnParser.getNextCandidatesAndCurrent;
import static work.gaigeshen.formwork.commons.bpmn.flowable.FlowableBpmnParser.parseProcess;

/**
 *
 * @author gaigeshen
 */
public class FlowableBpmnService implements BpmnService {

    private final RepositoryService repositoryService;

    private final HistoryService historyService;

    private final RuntimeService runtimeService;

    private final TaskService taskService;

    public FlowableBpmnService(RepositoryService repositoryService,
                               HistoryService historyService,
                               RuntimeService runtimeService, TaskService taskService) {
        this.repositoryService = repositoryService;
        this.historyService = historyService;
        this.runtimeService = runtimeService;
        this.taskService = taskService;
    }

    @Override
    public Collection<UserTask> queryTasks(UserTaskQueryParameters parameters) {
        if (Objects.isNull(parameters)) {
            throw new IllegalArgumentException("user task query parameters cannot be null");
        }
        TaskQuery taskQuery = taskService.createTaskQuery()
                .processDefinitionKey(wrapProcessId(parameters.getProcessId()))
                .processInstanceBusinessKey(parameters.getBusinessKey());
        if (Objects.nonNull(parameters.getTaskId())) {
            taskQuery.taskId(parameters.getTaskId());
        }
        Set<String> candidateGroups = parameters.getCandidateGroups();
        if (Objects.nonNull(candidateGroups) && !candidateGroups.isEmpty()) {
            taskQuery.taskCandidateGroupIn(candidateGroups);
        }
        String candidateUser = parameters.getCandidateUser();
        if (Objects.nonNull(candidateUser)) {
            taskQuery.taskCandidateUser(candidateUser);
        }
        List<Task> queryResult = taskQuery.list();
        if (queryResult.isEmpty()) {
            return Collections.emptyList();
        }
        return queryResult.stream().map(task -> {
            String processId = parameters.getProcessId();
            String businessKey = parameters.getBusinessKey();
            if (Objects.isNull(processId) || Objects.isNull(businessKey)) {
                ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                        .processInstanceId(task.getProcessInstanceId())
                        .singleResult();
                if (Objects.isNull(processInstance)) {
                    throw new IllegalStateException("process instance not found of task: " + task);
                }
                processId = unWrapProcessId(processInstance.getProcessDefinitionKey());
                businessKey = processInstance.getBusinessKey();
            }
            DefaultUserTask.Builder builder = DefaultUserTask.builder()
                    .id(task.getId()).name(task.getName()).description(task.getDescription())
                    .processId(processId).businessKey(businessKey).assignee(task.getAssignee())
                    .createTime(task.getCreateTime()).dueDate(task.getDueDate()).claimTime(task.getClaimTime());
            return builder.build();
        }).collect(Collectors.toSet());
    }

    @Override
    public List<UserTaskActivity> queryTaskActivities(UserTaskActivityQueryParameters parameters) {
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                .processDefinitionKey(wrapProcessId(parameters.getProcessId()))
                .processInstanceBusinessKey(parameters.getBusinessKey())
                .singleResult();
        if (Objects.isNull(historicProcessInstance)) {
            return Collections.emptyList();
        }
        List<HistoricActivityInstance> activities = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(historicProcessInstance.getId())
                .activityType("userTask").orderByHistoricActivityInstanceStartTime()
                .asc().list();
        if (activities.isEmpty()) {
            return Collections.emptyList();
        }
        Map<String, List<HistoricVariableInstance>> taskVariables = historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(historicProcessInstance.getId()).variableName("rejected")
                .taskIds(activities.stream().map(HistoricActivityInstance::getTaskId).collect(Collectors.toSet()))
                .list().stream().collect(Collectors.groupingBy(HistoricVariableInstance::getTaskId));
        List<UserTaskActivity> userTaskActivities = new ArrayList<>();
        HistoricActivityInstance lastActivity = null;
        // 已经完成的用户任务有开始时间和结束时间以及签收人
        for (HistoricActivityInstance activity : activities) {
            if (Objects.isNull(activity.getEndTime())) {
                lastActivity = activity;
                break;
            } else {
                List<HistoricVariableInstance> variables = taskVariables.get(activity.getTaskId());
                if (Objects.isNull(variables)) {
                    throw new IllegalStateException("'rejected' variable not found");
                }
                HistoricVariableInstance rejectedVariable = variables.iterator().next();
                UserTaskActivity.Status status;
                if ((boolean) rejectedVariable.getValue()) {
                    status = UserTaskActivity.Status.REJECTED;
                } else {
                    status = UserTaskActivity.Status.APPROVED;
                }
                UserTaskActivity userTaskActivity = DefaultUserTaskActivity.builder()
                        .taskId(activity.getTaskId()).status(status)
                        .assignee(activity.getAssignee())
                        .startTime(activity.getStartTime()).endTime(activity.getEndTime())
                        .build();
                userTaskActivities.add(userTaskActivity);
            }
        }
        // 当前正在进行的用户任务只有候选审批人以及开始时间
        if (Objects.nonNull(lastActivity)) {
            Candidate candidate = getTaskCandidate(lastActivity.getTaskId());
            DefaultUserTaskActivity lastActivityCandidate = DefaultUserTaskActivity.builder()
                    .taskId(lastActivity.getTaskId()).status(UserTaskActivity.Status.PROCESSING)
                    .groups(candidate.getGroups()).users(candidate.getUsers())
                    .startTime(lastActivity.getStartTime())
                    .build();
            userTaskActivities.add(lastActivityCandidate);
        }
        return userTaskActivities;
    }

    @Override
    public boolean completeTask(UserTaskCompleteParameters parameters) {
        if (Objects.isNull(parameters)) {
            throw new IllegalArgumentException("user task complete parameters cannot be null");
        }
        UserTask userTask = parameters.getUserTask();
        String taskId = userTask.getId();
        Map<String, Object> variables = new HashMap<>(parameters.getVariables());
        variables.put("rejected", parameters.isRejected());
        try {
            taskService.setAssignee(taskId, parameters.getAssignee());
            taskService.setVariableLocal(taskId, "rejected", parameters.isRejected());
            taskService.complete(taskId, variables);
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                    .processDefinitionKey(wrapProcessId(userTask.getProcessId()))
                    .processInstanceBusinessKey(userTask.getBusinessKey())
                    .singleResult();
            return Objects.isNull(historicProcessInstance.getEndTime());
        } catch (Exception e) {
            throw new IllegalStateException("could not complete user task: " + parameters, e);
        }
    }

    @Override
    public boolean startProcess(ProcessStartParameters parameters) {
        if (Objects.isNull(parameters)) {
            throw new IllegalArgumentException("process start parameters cannot be null");
        }
        String processId = wrapProcessId(parameters.getProcessId());
        long processCount = runtimeService.createProcessInstanceQuery()
                .processDefinitionKey(processId).processInstanceBusinessKey(parameters.getBusinessKey())
                .count();
        if (processCount > 0) {
            return true;
        }
        HashMap<String, Object> variables = new HashMap<>(parameters.getVariables());
        variables.put("rejected", false);
        try {
            runtimeService.startProcessInstanceByKey(processId, parameters.getBusinessKey(), variables);
        } catch (Exception e) {
            throw new IllegalStateException("could not start process: " + parameters, e);
        }
        return false;
    }

    @Override
    public void deployProcess(ProcessDeployParameters parameters) {
        if (Objects.isNull(parameters)) {
            throw new IllegalArgumentException("process deploy parameters cannot be null");
        }
        String processId = wrapProcessId(parameters.getProcessId());
        String procesName = parameters.getProcesName();
        ProcessNode processNode = parameters.getProcessNode();
        String processResourceName = processId + "(" + procesName + ").bpmn20.xml";
        try {
            BpmnModel bpmnModel = parseProcess(processNode, processId, procesName);
            repositoryService.createDeployment().addBpmnModel(processResourceName, bpmnModel).deploy();
        } catch (Exception e) {
            throw new IllegalStateException("could not deploy process: " + parameters, e);
        }
    }

    /**
     * 获取用户任务的审批人
     *
     * @param taskId 用户任务
     * @return 审批人
     */
    private Candidate getTaskCandidate(String taskId) {
        List<Candidate> candidates = getTaskNextCandidates(taskId);
        if (candidates.isEmpty()) {
            return Candidate.createEmpty();
        }
        return candidates.get(0);
    }

    /**
     * 获取用户任务的审批人以及后续的审批人
     *
     * @param taskId 用户任务编号
     * @return 审批人列表
     */
    private List<Candidate> getTaskNextCandidates(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (Objects.isNull(task)) {
            throw new IllegalStateException("could not find task: " + taskId);
        }
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        if (Objects.isNull(bpmnModel)) {
            throw new IllegalStateException("could not find bpmn model: " + task);
        }
        FlowElement taskFlowElement = bpmnModel.getFlowElement(task.getTaskDefinitionKey());
        if (Objects.isNull(taskFlowElement)) {
            throw new IllegalStateException("could not find task flow element: " + task);
        }
        Map<String, Object> variables = runtimeService.getVariables(task.getExecutionId());

        return getNextCandidatesAndCurrent((FlowNode) taskFlowElement, variables);
    }

    /**
     * 包装流程标识符，由于用户传入的流程标识符可能不合法（例如数字开头）会造成部署流程失败，所以需要进行包装
     *
     * @param processId 原始的流程标识符
     * @return 包装后的流程标识符
     */
    private String wrapProcessId(String processId) {
        return Objects.isNull(processId) ? null : "process_" + processId;
    }

    /**
     * 解包装流程标识符
     *
     * @param wrappedProcessId 包装的流程标识符
     * @return 解包装后的流程标识符
     */
    private String unWrapProcessId(String wrappedProcessId) {
        if (Objects.isNull(wrappedProcessId)) {
            throw new IllegalArgumentException("wrapped process id cannot be null");
        }
        return wrappedProcessId.replace("process_", "");
    }
}
