package work.gaigeshen.formwork.commons.bpmn.flowable;

import org.flowable.bpmn.model.BpmnModel;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.api.history.HistoricTaskInstanceQuery;
import work.gaigeshen.formwork.commons.bpmn.Process;
import work.gaigeshen.formwork.commons.bpmn.*;
import work.gaigeshen.formwork.commons.bpmn.candidate.TypedCandidate;

import java.util.*;
import java.util.stream.Collectors;

import static work.gaigeshen.formwork.commons.bpmn.flowable.FlowableBpmnParser.*;

/**
 *
 * @author gaigeshen
 */
public class FlowableBpmnService implements BpmnService {

    private final RepositoryService repositoryService;

    private final HistoryService historyService;

    private final RuntimeService runtimeService;

    private final TaskService taskService;

    private final CandidateService candidateService;

    private final VariableService variableService;

    public FlowableBpmnService(RepositoryService repositoryService,
                               HistoryService historyService,
                               RuntimeService runtimeService, TaskService taskService,
                               CandidateService candidateService, VariableService variableService) {
        this.repositoryService = repositoryService;
        this.historyService = historyService;
        this.runtimeService = runtimeService;
        this.taskService = taskService;
        this.candidateService = candidateService;
        this.variableService = variableService;
    }

    @Override
    public Collection<Process> queryProcesses(ProcessQueryParameters parameters) {
        if (Objects.isNull(parameters)) {
            throw new IllegalArgumentException("process query parameters cannot be null");
        }
        HistoricProcessInstanceQuery processInstanceQuery = historyService.createHistoricProcessInstanceQuery();
        if (Objects.nonNull(parameters.getProcessId())) {
            processInstanceQuery.processDefinitionKey(wrapProcessId(parameters.getProcessId()));
        }
        if (Objects.nonNull(parameters.getBusinessKey())) {
            processInstanceQuery.processInstanceBusinessKey(parameters.getBusinessKey());
        }
        if (Objects.nonNull(parameters.getUserId())) {
            processInstanceQuery.startedBy(parameters.getUserId());
        }
        if (!parameters.isIncludeHistorical()) {
            processInstanceQuery.unfinished();
        }
        List<HistoricProcessInstance> processInstances = processInstanceQuery
                .orderByProcessInstanceStartTime().asc().list();
        Collection<Process> processes = new ArrayList<>();
        for (HistoricProcessInstance processInstance : processInstances) {
            DefaultProcess process = DefaultProcess.builder()
                    .processId(unWrapProcessId(processInstance.getProcessDefinitionKey()))
                    .businessKey(processInstance.getBusinessKey())
                    .userId(processInstance.getStartUserId())
                    .build();
            processes.add(process);
        }
        return processes;
    }

    @Override
    public Collection<UserTask> queryTasks(UserTaskQueryParameters parameters) {
        if (Objects.isNull(parameters)) {
            throw new IllegalArgumentException("user task query parameters cannot be null");
        }
        TaskQuery taskQuery = taskService.createTaskQuery()
                .processDefinitionKey(wrapProcessId(parameters.getProcessId()))
                .processInstanceBusinessKey(parameters.getBusinessKey())
                .orderByTaskCreateTime().asc();
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
    public Collection<UserTask> queryHistoricTasks(UserHistoricTaskQueryParameters parameters) {
        if (Objects.isNull(parameters)) {
            throw new IllegalArgumentException("user historic task query parameters cannot be null");
        }
        HistoricTaskInstanceQuery historicTaskInstanceQuery = historyService.createHistoricTaskInstanceQuery()
                .processDefinitionKey(wrapProcessId(parameters.getProcessId()))
                .processInstanceBusinessKey(parameters.getBusinessKey())
                .orderByHistoricTaskInstanceStartTime()
                .asc();
        if (Objects.nonNull(parameters.getTaskId())) {
            historicTaskInstanceQuery.taskId(parameters.getTaskId());
        }
        List<HistoricTaskInstance> queryResult = new ArrayList<>();
        if (Objects.nonNull(parameters.getAssignees()) && !parameters.getAssignees().isEmpty()) {
            for (String assignee : parameters.getAssignees()) {
                historicTaskInstanceQuery.taskAssigneeLike("%" + assignee + "%");
                queryResult.addAll(historicTaskInstanceQuery.list());
            }
        } else {
            queryResult.addAll(historicTaskInstanceQuery.list());
        }
        if (queryResult.isEmpty()) {
            return Collections.emptyList();
        }
        return queryResult.stream().map(task -> {
            String processId = parameters.getProcessId();
            String businessKey = parameters.getBusinessKey();
            if (Objects.isNull(processId) || Objects.isNull(businessKey)) {
                HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                        .processInstanceId(task.getProcessInstanceId())
                        .singleResult();
                if (Objects.isNull(historicProcessInstance)) {
                    throw new IllegalStateException("historic process instance not found of task: " + task);
                }
                processId = unWrapProcessId(historicProcessInstance.getProcessDefinitionKey());
                businessKey = historicProcessInstance.getBusinessKey();
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
        if (Objects.isNull(parameters)) {
            throw new IllegalArgumentException("user task activity query parameters cannot be null");
        }
        String processId = wrapProcessId(parameters.getProcessId());
        String businessKey = parameters.getBusinessKey();
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                .processDefinitionKey(processId).processInstanceBusinessKey(businessKey)
                .singleResult();
        if (Objects.isNull(historicProcessInstance)) {
            return Collections.emptyList();
        }
        // 发起人也加入到返回结果
        Date startTime = historicProcessInstance.getStartTime();
        String startUserId = historicProcessInstance.getStartUserId();
        List<UserTaskActivity> taskActivities = new ArrayList<>();
        DefaultUserTaskActivity startActivity = DefaultUserTaskActivity.builder()
                .status(UserTaskActivity.Status.APPROVED).startTime(startTime).endTime(startTime)
                .assignee(Objects.isNull(startUserId) ? null : startUserId)
                .build();
        taskActivities.add(startActivity);
        List<HistoricActivityInstance> activities = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(historicProcessInstance.getId())
                .activityType("userTask").orderByHistoricActivityInstanceStartTime().asc().list();
        if (activities.isEmpty()) {
            return taskActivities;
        }
        Map<String, Map<String, Object>> taskVariables = variableService.getProcessTaskVariables(processId, businessKey);
        for (HistoricActivityInstance activity : activities) {
            String taskId = activity.getTaskId();
            TypedCandidate taskCandidate = candidateService.getTaskCandidate(taskId);
            if (Objects.isNull(activity.getEndTime())) {
                // 当前正在进行的用户任务只有候选审批人以及开始时间
                // 由于已经按开始时间进行了升序排序所以此用户任务肯定时最后的
                UserTaskActivity lastActivityCandidate = DefaultUserTaskActivity.builder()
                        .taskId(taskId).status(UserTaskActivity.Status.PROCESSING)
                        .groups(taskCandidate.getGroups()).users(taskCandidate.getUsers())
                        .startTime(activity.getStartTime())
                        .build();
                taskActivities.add(lastActivityCandidate);
                break;
            } else {
                if (taskCandidate.getType().isAutoApprover()) {
                    continue;
                }
                Map<String, Object> variables = taskVariables.get(taskId);
                if (Objects.isNull(variables)) {
                    throw new IllegalStateException("user task variables not found: " + taskId);
                }
                UserTaskActivity.Status status;
                if (variableService.getTaskRejectedVariable(taskId, variables)) {
                    status = UserTaskActivity.Status.REJECTED;
                } else {
                    status = UserTaskActivity.Status.APPROVED;
                }
                // 已经完成的用户任务有开始时间和结束时间以及签收人
                UserTaskActivity userTaskActivity = DefaultUserTaskActivity.builder()
                        .taskId(taskId).status(status)
                        .assignee(activity.getAssignee())
                        .startTime(activity.getStartTime()).endTime(activity.getEndTime())
                        .build();
                taskActivities.add(userTaskActivity);
            }
        }
        return taskActivities;
    }

    @Override
    public UserTaskActivity queryNextProcessingTaskActivity(UserTaskActivityQueryParameters parameters) {
        List<UserTaskActivity> userTaskActivities = queryTaskActivities(parameters);
        if (userTaskActivities.isEmpty()) {
            return null;
        }
        for (UserTaskActivity userTaskActivity : userTaskActivities) {
            if (UserTaskActivity.Status.PROCESSING == userTaskActivity.getStatus()) {
                return userTaskActivity;
            }
        }
        return null;
    }

    @Override
    public UserTaskAutoCompletion completeTask(UserTaskCompleteParameters parameters) {
        if (Objects.isNull(parameters)) {
            throw new IllegalArgumentException("user task complete parameters cannot be null");
        }
        UserTask userTask = parameters.getUserTask();
        Map<String, Object> variables = parameters.getVariables();
        boolean rejected = parameters.isRejected();
        Map<String, Object> processVariables = variableService.createProcessVariables(variables, rejected);
        Map<String, Object> taskVariables = variableService.createTaskVariables(variables, rejected);
        try {
            taskService.setAssignee(userTask.getId(), parameters.getAssignee());
            taskService.setVariablesLocal(userTask.getId(), taskVariables);
            taskService.complete(userTask.getId(), processVariables);
        } catch (Exception ex) {
            throw new IllegalStateException("could not complete user task: " + parameters, ex);
        }
        UserTaskAutoCompleteParameters autoCompleteParameters = UserTaskAutoCompleteParameters.builder()
                .processId(userTask.getProcessId())
                .businessKey(userTask.getBusinessKey())
                .variables(processVariables)
                .build();
        return autoCompleteTasks(autoCompleteParameters);
    }

    @Override
    public UserTaskAutoCompletion autoCompleteTasks(UserTaskAutoCompleteParameters parameters) {
        if (Objects.isNull(parameters)) {
            throw new IllegalArgumentException("user task auto complete parameters cannot be null");
        }
        String processId = wrapProcessId(parameters.getProcessId());
        String businessKey = parameters.getBusinessKey();
        DefaultUserTaskAutoCompletion.Builder builder = DefaultUserTaskAutoCompletion.builder()
                .processId(parameters.getProcessId())
                .groups(Collections.emptySet()).users(Collections.emptySet())
                .businessKey(businessKey);
        Task nextTask = taskService.createTaskQuery().processDefinitionKey(processId)
                .processInstanceBusinessKey(businessKey)
                .singleResult();
        if (Objects.isNull(nextTask)) {
            return builder.hasMoreUserTasks(false).build();
        }

        Map<String, Object> variables = variableService.createProcessVariables(parameters.getVariables(), false);

        Set<String> allGroups = new HashSet<>();
        Set<String> allUsers = new HashSet<>();
        builder.groups(allGroups).users(allUsers);
        try {
            while (Objects.nonNull(nextTask)) {
                TypedCandidate candidate = candidateService.getTaskCandidate(nextTask.getId());
                if (!candidate.getType().isAutoApprover()) {
                    return builder.hasMoreUserTasks(true).build();
                }
                taskService.setAssignee(nextTask.getId(), parseAutoApprovedAssignee(candidate));
                taskService.setVariablesLocal(nextTask.getId(), variableService.createTaskVariables(parameters.getVariables(), false));
                taskService.complete(nextTask.getId(), variables);
                nextTask = taskService.createTaskQuery().processDefinitionKey(processId)
                        .processInstanceBusinessKey(businessKey)
                        .singleResult();
                allGroups.addAll(candidate.getGroups());
                allUsers.addAll(candidate.getUsers());
            }
            return builder.hasMoreUserTasks(false).build();
        } catch (Exception e) {
            throw new IllegalStateException("could not auto complete user task: " + parameters, e);
        }
    }

    @Override
    public UserTaskAutoCompletion startProcess(ProcessStartParameters parameters) {
        if (Objects.isNull(parameters)) {
            throw new IllegalArgumentException("process start parameters cannot be null");
        }
        String processId = wrapProcessId(parameters.getProcessId());
        String businessKey = parameters.getBusinessKey();
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processDefinitionKey(processId)
                .processInstanceBusinessKey(businessKey)
                .singleResult();
        if (Objects.isNull(processInstance)) {
            throw new IllegalStateException("could not start process because has started: " + parameters);
        }
        Map<String, Object> variables = variableService.createProcessVariables(parameters.getVariables(), false);
        try {
            Authentication.setAuthenticatedUserId(parameters.getUserId());
            runtimeService.startProcessInstanceByKey(processId, businessKey, variables);
        } catch (Exception e) {
            throw new IllegalStateException("could not start process: " + parameters, e);
        }
        UserTaskAutoCompleteParameters autoCompleteParameters = UserTaskAutoCompleteParameters.builder()
                .processId(parameters.getProcessId())
                .businessKey(parameters.getBusinessKey())
                .variables(parameters.getVariables())
                .build();
        return autoCompleteTasks(autoCompleteParameters);
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
     * 转换自动审批通过的审批人对象至签收人
     *
     * @param candidate 审批人对象
     * @return 签收人
     */
    private String parseAutoApprovedAssignee(TypedCandidate candidate) {
        StringBuilder builder = new StringBuilder();
        Set<String> candidateGroups = candidate.getGroups();
        Set<String> candidateUsers = candidate.getUsers();
        if (!candidateGroups.isEmpty()) {
            builder.append("group:").append(String.join(",", candidateGroups));
        }
        if (!candidateUsers.isEmpty()) {
            if (builder.length() > 0) {
                builder.append("|");
            }
            builder.append("user:").append(String.join(",", candidateUsers));
        }
        if (builder.length() == 0) {
            return "group:|user:";
        }
        return builder.toString();
    }
}
