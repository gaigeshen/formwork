package work.gaigeshen.formwork.commons.bpmn.flowable;

import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.FlowNode;
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
import org.flowable.variable.api.history.HistoricVariableInstance;
import work.gaigeshen.formwork.commons.bpmn.Process;
import work.gaigeshen.formwork.commons.bpmn.*;
import work.gaigeshen.formwork.commons.bpmn.candidate.Candidate;
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

    public FlowableBpmnService(RepositoryService repositoryService,
                               HistoryService historyService,
                               RuntimeService runtimeService, TaskService taskService) {
        this.repositoryService = repositoryService;
        this.historyService = historyService;
        this.runtimeService = runtimeService;
        this.taskService = taskService;
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
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                .processDefinitionKey(wrapProcessId(parameters.getProcessId()))
                .processInstanceBusinessKey(parameters.getBusinessKey())
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
        Map<String, Map<String, HistoricVariableInstance>> taskVariables = historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(historicProcessInstance.getId())
                .taskIds(activities.stream().map(HistoricActivityInstance::getTaskId).collect(Collectors.toSet()))
                .list().stream().collect(
                        Collectors.groupingBy(HistoricVariableInstance::getTaskId,
                                Collectors.groupingBy(HistoricVariableInstance::getVariableName,
                                        Collectors.collectingAndThen(Collectors.toList(), vars -> vars.get(0)))));
        for (HistoricActivityInstance activity : activities) {
            if (Objects.isNull(activity.getEndTime())) {
                // 当前正在进行的用户任务只有候选审批人以及开始时间
                // 由于已经按开始时间进行了升序排序所以此用户任务肯定时最后的
                TypedCandidate candidate = getTaskCandidate(activity.getTaskId());
                UserTaskActivity lastActivityCandidate = DefaultUserTaskActivity.builder()
                        .taskId(activity.getTaskId()).status(UserTaskActivity.Status.PROCESSING)
                        .groups(candidate.getGroups()).users(candidate.getUsers())
                        .startTime(activity.getStartTime())
                        .build();
                taskActivities.add(lastActivityCandidate);
                break;
            } else {
                Map<String, HistoricVariableInstance> variables = taskVariables.get(activity.getTaskId());
                if (Objects.isNull(variables)) {
                    throw new IllegalStateException("user task variables not found");
                }
                HistoricVariableInstance autoApproved = variables.get("autoApproved");
                HistoricVariableInstance rejected = variables.get("rejected");
                if (Objects.isNull(autoApproved)) {
                    throw new IllegalStateException("'autoApproved' variable not found");
                }
                if (Objects.isNull(rejected)) {
                    throw new IllegalStateException("'rejected' variable not found");
                }
                if ((boolean) autoApproved.getValue()) {
                    continue;
                }
                UserTaskActivity.Status status;
                if ((boolean) rejected.getValue()) {
                    status = UserTaskActivity.Status.REJECTED;
                } else {
                    status = UserTaskActivity.Status.APPROVED;
                }
                // 已经完成的用户任务有开始时间和结束时间以及签收人
                UserTaskActivity userTaskActivity = DefaultUserTaskActivity.builder()
                        .taskId(activity.getTaskId()).status(status)
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
        String taskId = userTask.getId();
        boolean rejected = parameters.isRejected();
        Map<String, Object> variables = wrapProcessVariables(parameters.getVariables(), rejected);
        Map<String, Object> taskVariables = createTaskVariables(taskId, rejected);
        try {
            taskService.setAssignee(taskId, parameters.getAssignee());
            taskService.setVariablesLocal(taskId, taskVariables);
            taskService.complete(taskId, variables);
        } catch (Exception ex) {
            throw new IllegalStateException("could not complete user task: " + parameters, ex);
        }
        UserTaskAutoCompleteParameters autoCompleteParameters = UserTaskAutoCompleteParameters.builder()
                .processId(userTask.getProcessId())
                .businessKey(userTask.getBusinessKey())
                .variables(parameters.getVariables())
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

        Map<String, Object> variables = wrapProcessVariables(parameters.getVariables(), false);

        Set<String> allGroups = new HashSet<>();
        Set<String> allUsers = new HashSet<>();
        builder.groups(allGroups).users(allUsers);
        try {
            while (Objects.nonNull(nextTask)) {
                TypedCandidate candidate = getTaskCandidate(nextTask.getId());
                if (!candidate.isAutoApprover()) {
                    return builder.hasMoreUserTasks(true).build();
                }
                taskService.setAssignee(nextTask.getId(), parseAutoApprovedAssignee(candidate));
                taskService.setVariablesLocal(nextTask.getId(), createTaskVariables(candidate, false));
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
        Map<String, Object> variables = wrapProcessVariables(parameters.getVariables(), false);
        variables.put("candidates", parameters.getCandidates());
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
        UserTaskAutoCompletion userTaskAutoCompletion = autoCompleteTasks(autoCompleteParameters);
        Task nextTask = taskService.createTaskQuery().processDefinitionKey(processId)
                .processInstanceBusinessKey(businessKey)
                .singleResult();
        if (Objects.nonNull(nextTask)) {
            updateTaskCandidate(nextTask, processInstance);
        }
        return userTaskAutoCompletion;
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

    private void updateTaskCandidate(Task task, ProcessInstance processInstance) {
        TypedCandidate taskCandidate = getTaskCandidate(task.getId());
        if (taskCandidate.isStarter()) {
            taskService.addCandidateUser(task.getId(), processInstance.getStartUserId());
        }
        else if (taskCandidate.isStarterAppoint()) {
            Map<String, Object> processVariables = processInstance.getProcessVariables();
            List<?> candidates = (List<?>) processVariables.get("candidates");
            if (!candidates.isEmpty()) {
                Candidate candidate = (Candidate) candidates.remove(0);
                for (String group : candidate.getGroups()) {
                    taskService.addCandidateGroup(task.getId(), group);
                }
                for (String user : candidate.getUsers()) {
                    taskService.addCandidateUser(task.getId(), user);
                }
                runtimeService.setVariable(task.getExecutionId(), "candidates", candidates);
            }
        }
    }

    private TypedCandidate getTaskCandidate(String taskId) {
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
        return FlowableBpmnParser.getCurrentCandidate((FlowNode) taskFlowElement);
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

    private Map<String, Object> createTaskVariables(String taskId, boolean rejected) {
        return createTaskVariables(getTaskCandidate(taskId), rejected);
    }

    private Map<String, Object> createTaskVariables(TypedCandidate taskCandidate, boolean rejected) {
        Map<String, Object> taskVariables = new HashMap<>();
        taskVariables.put("rejected", rejected);
        taskVariables.put("candidateType", taskCandidate.getType());
        return taskVariables;
    }

    /**
     * 包装流程变量，由于传入的变量标识符可能非法（例如数字开头）会造成流程运行时异常，所以需要对其进行包装后返回
     *
     * @param variables 需要包装的变量
     * @param rejected 变量表示是否审批拒绝
     * @return 被包装的变量
     */
    private Map<String, Object> wrapProcessVariables(Map<String, Object> variables, boolean rejected) {
        if (Objects.isNull(variables)) {
            return new HashMap<>();
        }
        Map<String, Object> wrappedVariables = new HashMap<>();
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            wrappedVariables.put("variable_" + entry.getKey(), entry.getValue());
        }
        wrappedVariables.put("rejected", rejected);
        return wrappedVariables;
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
