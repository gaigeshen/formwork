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
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.api.history.HistoricTaskInstanceQuery;
import org.flowable.variable.api.history.HistoricVariableInstance;
import work.gaigeshen.formwork.commons.bpmn.Process;
import work.gaigeshen.formwork.commons.bpmn.*;
import work.gaigeshen.formwork.commons.bpmn.UserTaskActivity.Status;
import work.gaigeshen.formwork.commons.bpmn.candidate.*;

import java.util.*;
import java.util.stream.Collectors;

import static work.gaigeshen.formwork.commons.bpmn.flowable.FlowableBpmnParser.getCandidate;
import static work.gaigeshen.formwork.commons.bpmn.flowable.FlowableBpmnParser.getCandidates;
import static work.gaigeshen.formwork.commons.bpmn.flowable.FlowableBpmnParser.parseProcess;
import static work.gaigeshen.formwork.commons.bpmn.flowable.FlowableBpmnParser.unWrapProcessId;
import static work.gaigeshen.formwork.commons.bpmn.flowable.FlowableBpmnParser.wrapProcessId;

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
        Date startTime = historicProcessInstance.getStartTime();
        List<UserTaskActivity> taskActivities = new ArrayList<>();
        UserTaskActivity startActivity = DefaultUserTaskActivity.builder()
                .status(Status.APPROVED)
                .startTime(startTime).endTime(startTime)
                .assignee(historicProcessInstance.getStartUserId())
                .build();
        taskActivities.add(startActivity);
        List<HistoricActivityInstance> activities = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(historicProcessInstance.getId())
                .activityType("userTask")
                .orderByHistoricActivityInstanceStartTime().asc().list();
        if (activities.isEmpty()) {
            return taskActivities;
        }
        Map<String, Map<String, Object>> taskVariables = getProcessTaskVariables(processId, businessKey);
        for (HistoricActivityInstance activity : activities) {
            String taskId = activity.getTaskId();
            TypedCandidate taskCandidate = getTaskCandidate(taskId);
            CandidateType taskCandidateType = taskCandidate.getType();
            if (Objects.isNull(activity.getEndTime())) {
                UserTaskActivity lastActivityCandidate = DefaultUserTaskActivity.builder()
                        .taskId(taskId).status(Status.PROCESSING)
                        .groups(taskCandidate.getGroups()).users(taskCandidate.getUsers())
                        .startTime(activity.getStartTime())
                        .build();
                taskActivities.add(lastActivityCandidate);
                break;
            } else {
                if (taskCandidateType.isAutoApprover()) {
                    continue;
                }
                Map<String, Object> variables = taskVariables.get(taskId);
                if (Objects.isNull(variables)) {
                    throw new IllegalStateException("user task variables not found: " + taskId);
                }
                Status status;
                if (getTaskRejectedVariable(taskId, variables)) {
                    status = Status.REJECTED;
                } else {
                    status = Status.APPROVED;
                }
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
            if (userTaskActivity.getStatus().isProcessing()) {
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
        String taskId = userTask.getId();
        boolean rejected = parameters.isRejected();
        try {
            taskService.setAssignee(taskId, parameters.getAssignee());
            taskService.setVariablesLocal(taskId, createTaskVariables(variables, rejected));
            taskService.complete(taskId, createProcessVariables(variables, rejected));
        } catch (Exception ex) {
            throw new IllegalStateException("could not complete user task: " + parameters, ex);
        }
        UserTaskAutoCompleteParameters autoCompleteParameters = UserTaskAutoCompleteParameters.builder()
                .processId(userTask.getProcessId())
                .businessKey(userTask.getBusinessKey())
                .variables(variables)
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
        Map<String, Object> variables = parameters.getVariables();
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
        Map<String, Object> processVariables = createProcessVariables(variables, false);
        Map<String, Object> taskVariables = createTaskVariables(variables, false);
        Set<String> allGroups = new HashSet<>();
        Set<String> allUsers = new HashSet<>();
        builder.groups(allGroups).users(allUsers);
        try {
            while (Objects.nonNull(nextTask)) {
                TypedCandidate taskCandidate = getTaskCandidate(nextTask.getId());
                CandidateType taskCandidateType = taskCandidate.getType();
                if (!taskCandidateType.isAutoApprover()) {
                    return builder.hasMoreUserTasks(true).build();
                }
                Set<String> groupsAndUsers = new HashSet<>(taskCandidate.getGroups());
                groupsAndUsers.addAll(taskCandidate.getUsers());
                taskService.setAssignee(nextTask.getId(), String.join(",", groupsAndUsers));
                taskService.setVariablesLocal(nextTask.getId(), taskVariables);
                taskService.complete(nextTask.getId(), processVariables);
                nextTask = taskService.createTaskQuery().processDefinitionKey(processId)
                        .processInstanceBusinessKey(businessKey)
                        .singleResult();
                allGroups.addAll(taskCandidate.getGroups());
                allUsers.addAll(taskCandidate.getUsers());
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
        Map<String, Object> processVariables = createProcessVariables(parameters.getVariables(), false);
        try {
            Authentication.setAuthenticatedUserId(parameters.getUserId());
            runtimeService.startProcessInstanceByKey(processId, businessKey, processVariables);
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

    private Map<String, Object> createProcessVariables(Map<String, Object> variables, boolean rejected) {
        Map<String, Object> wrappedVariables = new HashMap<>();
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            wrappedVariables.put("variable_" + entry.getKey(), entry.getValue());
        }
        wrappedVariables.put("rejected", rejected);
        return wrappedVariables;
    }

    private Map<String, Object> createTaskVariables(Map<String, Object> variables, boolean rejected) {
        Map<String, Object> taskVariables = new HashMap<>(variables);
        taskVariables.put("rejected", rejected);
        return taskVariables;
    }

    private Map<String, Map<String, Object>> getProcessTaskVariables(String processId, String businessKey) {
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                .processDefinitionKey(wrapProcessId(processId))
                .processInstanceBusinessKey(businessKey)
                .singleResult();
        if (Objects.isNull(historicProcessInstance)) {
            throw new IllegalStateException("could not find historic process instance: " + businessKey);
        }
        List<HistoricVariableInstance> variableInstances = historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(historicProcessInstance.getId())
                .list();
        Map<String, Map<String, Object>> processTaskVariables = new HashMap<>();
        for (HistoricVariableInstance variableInstance : variableInstances) {
            String taskId = variableInstance.getTaskId();
            if (Objects.isNull(taskId)) {
                continue;
            }
            String variableName = variableInstance.getVariableName();
            Object variableValue = variableInstance.getValue();
            if (processTaskVariables.containsKey(taskId)) {
                processTaskVariables.get(taskId).put(variableName, variableValue);
            } else {
                Map<String, Object> newVariables = new HashMap<>();
                newVariables.put(variableName, variableValue);
                processTaskVariables.put(taskId, newVariables);
            }
        }
        return processTaskVariables;
    }

    private boolean getTaskRejectedVariable(String taskId, Map<String, Object> taskVariables) {
        Object rejectedVariable = taskVariables.get("rejected");
        if (Objects.isNull(rejectedVariable)) {
            throw new IllegalStateException("could not find 'rejected' variable: " + taskId);
        }
        return (boolean) rejectedVariable;
    }

    private TypedCandidate getTaskCandidate(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (Objects.isNull(task)) {
            throw new IllegalStateException("could not find task: " + taskId);
        }
        return getCandidate(getTaskFlowNode(task));
    }

    private List<TypedCandidate> getTaskCandidates(String taskId, Map<String, Object> variables) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (Objects.isNull(task)) {
            throw new IllegalStateException("could not find task: " + taskId);
        }
        return getCandidates(getTaskFlowNode(task), variables);
    }

    private List<TypedCandidate> getProcessCalculateCandidates(String processId, Map<String, Object> variables) {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(wrapProcessId(processId))
                .singleResult();
        if (Objects.isNull(processDefinition)) {
            throw new IllegalStateException("could not find process definition: " + processId);
        }
        FlowNode starterFlowNode = getProcessStarterFlowNode(processDefinition);
        return getCandidates(starterFlowNode, variables);
    }

    private CandidateVariables getTaskProcessCandidateVariables(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (Objects.isNull(task)) {
            throw new IllegalStateException("could not find task: " + taskId);
        }
        Object candidateVariables = task.getProcessVariables().get("candidateVariables");
        if (Objects.isNull(candidateVariables)) {
            throw new IllegalStateException("could not find candidate variables: " + taskId);
        }
        return (CandidateVariables) candidateVariables;
    }

    private void updateTaskProcessCandidateVariables(String taskId, CandidateVariables variables) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (Objects.isNull(task)) {
            throw new IllegalStateException("could not find task: " + taskId);
        }
        runtimeService.setVariable(task.getExecutionId(), "candidateVariables", variables);
    }

    private void updateTaskCandidate(String taskId, CandidateVariables variables) {
        CandidateType candidateType = getTaskCandidate(taskId).getType();
        if (candidateType.isStarter()) {
            String starter = variables.getStarter();
            addTaskCandidate(taskId, DefaultCandidate.createUsers(Collections.singleton(starter)));
        }
        else if (candidateType.isStarterAppointee()) {
            Candidates starterAppointee = variables.getStarterAppointee();
            Candidate candidate = starterAppointee.poll();
            if (Objects.isNull(candidate)) {
                throw new IllegalStateException("could not poll starter appoint candidate: " + taskId);
            }
            addTaskCandidate(taskId, candidate);
            updateTaskProcessCandidateVariables(taskId, variables);
        }
        CandidateUpdates candidateUpdates = variables.getCandidateUpdates();
        Set<Candidate> candidatesToAdd = new HashSet<>();
        Set<Candidate> candidatesToRemove = new HashSet<>();
        for (Map.Entry<String, Candidate> update : candidateUpdates.getGroupUpdates().entrySet()) {
            long count = taskService.createTaskQuery().taskId(taskId).taskCandidateGroup(update.getKey()).count();
            if (count > 0) {
                candidatesToAdd.add(update.getValue());
                candidatesToRemove.add(DefaultCandidate.createGroups(Collections.singleton(update.getKey())));
            }
        }
        for (Map.Entry<String, Candidate> update : candidateUpdates.getUserUpdates().entrySet()) {
            long count = taskService.createTaskQuery().taskId(taskId).taskCandidateUser(update.getKey()).count();
            if (count > 0) {
                candidatesToAdd.add(update.getValue());
                candidatesToRemove.add(DefaultCandidate.createUsers(Collections.singleton(update.getKey())));
            }
        }
        for (Candidate candidate : candidatesToAdd) {
            addTaskCandidate(taskId, candidate);
        }
        for (Candidate candidate : candidatesToRemove) {
            removeTaskCandidate(taskId, candidate);
        }
    }

    private void addTaskCandidate(String taskId, Candidate candidate) {
        for (String group : candidate.getGroups()) {
            taskService.addCandidateGroup(taskId, group);
        }
        for (String user : candidate.getUsers()) {
            taskService.addCandidateUser(taskId, user);
        }
    }

    private void removeTaskCandidate(String taskId, Candidate candidate) {
        for (String group : candidate.getGroups()) {
            taskService.deleteCandidateGroup(taskId, group);
        }
        for (String user : candidate.getUsers()) {
            taskService.deleteCandidateUser(taskId, user);
        }
    }

    private FlowNode getProcessStarterFlowNode(ProcessDefinition processDefinition) {
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
        if (Objects.isNull(bpmnModel)) {
            throw new IllegalStateException("could not find bpmn model: " + processDefinition);
        }
        FlowElement element = bpmnModel.getMainProcess().getInitialFlowElement();
        if (Objects.isNull(element)) {
            throw new IllegalStateException("could not find starter element: " + processDefinition);
        }
        return (FlowNode) element;
    }

    private FlowNode getTaskFlowNode(Task task) {
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        if (Objects.isNull(bpmnModel)) {
            throw new IllegalStateException("could not find bpmn model: " + task);
        }
        FlowElement element = bpmnModel.getFlowElement(task.getTaskDefinitionKey());
        if (Objects.isNull(element)) {
            throw new IllegalStateException("could not find task element: " + task);
        }
        return (FlowNode) element;
    }
}
