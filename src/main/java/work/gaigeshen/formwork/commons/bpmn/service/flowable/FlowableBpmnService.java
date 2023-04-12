package work.gaigeshen.formwork.commons.bpmn.service.flowable;

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
import work.gaigeshen.formwork.commons.bpmn.process.*;
import work.gaigeshen.formwork.commons.bpmn.service.BpmnService;
import work.gaigeshen.formwork.commons.bpmn.usertask.*;
import work.gaigeshen.formwork.commons.bpmn.usertask.UserTaskActivity.Status;
import work.gaigeshen.formwork.commons.bpmn.candidate.*;
import work.gaigeshen.formwork.commons.bpmn.process.Process;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static work.gaigeshen.formwork.commons.bpmn.candidate.CandidateType.STARTER_APPOINTEE;
import static work.gaigeshen.formwork.commons.bpmn.service.flowable.FlowableBpmnParser.*;

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
        TaskQuery taskQuery = taskService.createTaskQuery().orderByTaskCreateTime().asc();
        if (Objects.nonNull(parameters.getProcessId())) {
            taskQuery.processDefinitionKey(wrapProcessId(parameters.getProcessId()));
        }
        if (Objects.nonNull(parameters.getBusinessKey())) {
            taskQuery.processInstanceBusinessKey(parameters.getBusinessKey());
        }
        if (Objects.nonNull(parameters.getTaskId())) {
            taskQuery.taskId(parameters.getTaskId());
        }
        Set<String> candidateGroups = parameters.getCandidateGroups();
        if (Objects.nonNull(candidateGroups) && !candidateGroups.isEmpty()) {
            taskQuery.taskCandidateGroupIn(candidateGroups);
        }
        if (Objects.nonNull(parameters.getCandidateUser())) {
            taskQuery.taskCandidateUser(parameters.getCandidateUser());
        }
        List<Task> queryResult = taskQuery.list();
        Collection<UserTask> userTasks = new ArrayList<>();
        for (Task task : queryResult) {
            String processId = parameters.getProcessId();
            String businessKey = parameters.getBusinessKey();
            if (Objects.isNull(processId) || Objects.isNull(businessKey)) {
                ProcessInstance process = runtimeService.createProcessInstanceQuery()
                        .processInstanceId(task.getProcessInstanceId())
                        .singleResult();
                processId = unWrapProcessId(process.getProcessDefinitionKey());
                businessKey = process.getBusinessKey();
            }
            TypedCandidate appliedCandidate = getTaskAppliedCandidate(task.getId());
            UserTask.Builder builder = UserTask.builder().id(task.getId())
                    .processId(processId).businessKey(businessKey)
                    .candidate(appliedCandidate)
                    .createTime(task.getCreateTime());
            userTasks.add(builder.build());
        }
        return userTasks;
    }

    @Override
    public Collection<UserHistoricTask> queryHistoricTasks(UserHistoricTaskQueryParameters parameters) {
        if (Objects.isNull(parameters)) {
            throw new IllegalArgumentException("user historic task query parameters cannot be null");
        }
        HistoricTaskInstanceQuery historicTaskInstanceQuery = historyService.createHistoricTaskInstanceQuery()
                .orderByHistoricTaskInstanceStartTime().asc();
        if (Objects.nonNull(parameters.getTaskId())) {
            historicTaskInstanceQuery.taskId(parameters.getTaskId());
        }
        if (Objects.nonNull(parameters.getProcessId())) {
            historicTaskInstanceQuery.processDefinitionKey(wrapProcessId(parameters.getProcessId()));
        }
        if (Objects.nonNull(parameters.getBusinessKey())) {
            historicTaskInstanceQuery.processInstanceBusinessKey(parameters.getBusinessKey());
        }
        List<HistoricTaskInstance> queryResult = new ArrayList<>();
        if (Objects.nonNull(parameters.getCandidates()) && !parameters.getCandidates().isEmpty()) {
            for (String candidate : parameters.getCandidates()) {
                historicTaskInstanceQuery.taskAssigneeLike("%" + candidate + "%");
                queryResult.addAll(historicTaskInstanceQuery.list());
            }
        } else {
            queryResult.addAll(historicTaskInstanceQuery.list());
        }
        Collection<UserHistoricTask> userHistoricTasks = new ArrayList<>();
        for (HistoricTaskInstance historicTask : queryResult) {
            String processId = parameters.getProcessId();
            String businessKey = parameters.getBusinessKey();
            if (Objects.isNull(processId) || Objects.isNull(businessKey)) {
                HistoricProcessInstance historicProcess = historyService.createHistoricProcessInstanceQuery()
                        .processInstanceId(historicTask.getProcessInstanceId())
                        .singleResult();
                processId = unWrapProcessId(historicProcess.getProcessDefinitionKey());
                businessKey = historicProcess.getBusinessKey();
            }
            TypedCandidate appliedCandidate = getTaskAppliedCandidate(historicTask.getId());
            UserHistoricTask.Builder builder = UserHistoricTask.builder().id(historicTask.getId())
                    .processId(processId).businessKey(businessKey)
                    .candidate(appliedCandidate)
                    .createTime(historicTask.getCreateTime()).endTime(historicTask.getEndTime());
            userHistoricTasks.add(builder.build());
        }
        return userHistoricTasks;
    }

    @Override
    public List<UserTaskActivity> queryTaskActivities(UserTaskActivityQueryParameters parameters) {
        if (Objects.isNull(parameters)) {
            throw new IllegalArgumentException("user task activity query parameters cannot be null");
        }
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                .processDefinitionKey(wrapProcessId(parameters.getProcessId()))
                .processInstanceBusinessKey(parameters.getBusinessKey())
                .singleResult();
        if (Objects.isNull(historicProcessInstance)) {
            return Collections.emptyList();
        }
        Date startTime = historicProcessInstance.getStartTime();
        String startUserId = historicProcessInstance.getStartUserId();
        List<UserTaskActivity> taskActivities = new ArrayList<>();
        taskActivities.add(
                UserTaskActivity.builder()
                        .candidate(DefaultCandidate.createUser(startUserId))
                        .startTime(startTime).endTime(startTime).status(Status.APPROVED)
                        .build()
        );
        List<HistoricActivityInstance> activities = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(historicProcessInstance.getId()).activityType("userTask")
                .orderByHistoricActivityInstanceStartTime().asc().list();
        if (activities.isEmpty()) {
            return taskActivities;
        }
        Map<String, TypedCandidate> taskApprovedCandidates = getProcessTaskApprovedCandidates(parameters.getProcessId(), parameters.getBusinessKey());
        Map<String, Map<String, Object>> taskVariables = getProcessTaskVariables(parameters.getProcessId(), parameters.getBusinessKey());
        for (HistoricActivityInstance activity : activities) {
            String taskId = activity.getTaskId();
            if (Objects.isNull(activity.getEndTime())) {
                Map<String, TypedCandidate> taskAppliedCandidates = getProcessTaskAppliedCandidates(parameters.getProcessId(), parameters.getBusinessKey());
                UserTaskActivity lastActivityCandidate = UserTaskActivity.builder()
                        .candidate(taskAppliedCandidates.get(taskId))
                        .startTime(activity.getStartTime()).status(Status.PROCESSING)
                        .build();
                taskActivities.add(lastActivityCandidate);
                break;
            } else {
                Map<String, Object> variables = taskVariables.get(activity.getTaskId());
                Object rejected = variables.get("rejected");
                if (Objects.isNull(rejected)) {
                    throw new IllegalStateException("missing rejected variable of task: " + activity.getTaskId());
                }
                UserTaskActivity userTaskActivity = UserTaskActivity.builder()
                        .status(((boolean) rejected) ? Status.REJECTED : Status.APPROVED)
                        .candidate(taskApprovedCandidates.get(taskId))
                        .startTime(activity.getStartTime()).endTime(activity.getEndTime())
                        .comment((String) variables.get("comment"))
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
        String taskId = userTask.getId();
        Map<String, Object> variables = parameters.getVariables();
        Candidate candidate = parameters.getCandidate();
        boolean rejected = parameters.isRejected();

        Map<String, Object> processVariables = createProcessVariables(variables, rejected);
        Map<String, Object> taskVariables = createTaskVariables(variables, rejected, parameters.getComment());

        Map<String, TypedCandidate> appliedCandidates = getProcessTaskAppliedCandidates(userTask.getProcessId(), userTask.getBusinessKey());
        TypedCandidate approvedCandidate = appliedCandidates.get(taskId).replaceCandidate(candidate);
        taskVariables.put("approvedCandidate", approvedCandidate);

        try {
            Set<String> groupsAndUsers = new HashSet<>(approvedCandidate.getGroups());
            groupsAndUsers.addAll(approvedCandidate.getUsers());
            taskService.setAssignee(taskId, String.join(",", groupsAndUsers));
            taskService.setVariablesLocal(taskId, taskVariables);
            taskService.complete(taskId, processVariables);
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
        String wrappedProcessId = wrapProcessId(parameters.getProcessId());
        String businessKey = parameters.getBusinessKey();
        UserTaskAutoCompletion.Builder builder = UserTaskAutoCompletion.builder()
                .processId(parameters.getProcessId()).businessKey(businessKey);
        Task nextTask = taskService.createTaskQuery().processDefinitionKey(wrappedProcessId)
                .processInstanceBusinessKey(businessKey)
                .singleResult();
        if (Objects.isNull(nextTask)) {
            return builder.candidate(DefaultCandidate.create()).hasMoreTasks(false).build();
        }
        Set<String> allGroups = new HashSet<>();
        Set<String> allUsers = new HashSet<>();

        Map<String, Object> processVariables = createProcessVariables(parameters.getVariables(), false);

        Map<String, TypedCandidate> taskAppliedCandidates = getProcessTaskAppliedCandidates(parameters.getProcessId(), businessKey);

        try {
            while (Objects.nonNull(nextTask)) {
                applyProcessTaskCandidates(parameters.getProcessId(), businessKey);

                TypedCandidate appliedCandidate = taskAppliedCandidates.get(nextTask.getId());
                if (!appliedCandidate.getType().isAutoApprover()) {
                    return builder.hasMoreTasks(true).build();
                }
                Set<String> groupsAndUsers = new HashSet<>(appliedCandidate.getGroups());
                groupsAndUsers.addAll(appliedCandidate.getUsers());
                taskService.setAssignee(nextTask.getId(), String.join(",", groupsAndUsers));

                Map<String, Object> taskVariables = createTaskVariables(parameters.getVariables(), false, null);
                taskVariables.put("approvedCandidate", appliedCandidate);

                taskService.setVariablesLocal(nextTask.getId(), taskVariables);
                taskService.complete(nextTask.getId(), processVariables);
                nextTask = taskService.createTaskQuery().processDefinitionKey(wrappedProcessId)
                        .processInstanceBusinessKey(businessKey)
                        .singleResult();
                allGroups.addAll(appliedCandidate.getGroups());
                allUsers.addAll(appliedCandidate.getUsers());
            }
            return builder.candidate(DefaultCandidate.create(allGroups, allUsers)).hasMoreTasks(false).build();
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
        Map<String, Object> variables = parameters.getVariables();
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processDefinitionKey(processId)
                .processInstanceBusinessKey(businessKey)
                .singleResult();
        if (Objects.nonNull(processInstance)) {
            throw new IllegalStateException("could not start process because has been started: " + parameters);
        }
        Map<String, Object> processVariables = createProcessVariables(variables, false);
        processVariables.put("userId", parameters.getUserId());
        processVariables.put("appointees", parameters.getAppointees());
        processVariables.put("updates", parameters.getUpdates());
        try {
            Authentication.setAuthenticatedUserId(parameters.getUserId());
            runtimeService.startProcessInstanceByKey(processId, businessKey, processVariables);
        } catch (Exception e) {
            throw new IllegalStateException("could not start process: " + parameters, e);
        }
        UserTaskAutoCompleteParameters completeParameters = UserTaskAutoCompleteParameters.builder()
                .processId(parameters.getProcessId())
                .businessKey(parameters.getBusinessKey())
                .variables(variables)
                .build();
        return autoCompleteTasks(completeParameters);
    }

    @Override
    public List<TypedCandidate> prognoseProcessCandidates(ProcessCandidatePrognosisParameters parameters) {
        if (Objects.isNull(parameters)) {
            throw new IllegalArgumentException("process candidate prognosis parameters cannot be null");
        }
        Map<String, Object> processVariables = createProcessVariables(parameters.getVariables(), false);
        return getProcessCalculateCandidates(parameters.getProcessId(), processVariables);
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
        Map<String, Object> processVariables = new HashMap<>();
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            processVariables.put("variable_" + entry.getKey(), entry.getValue());
        }
        processVariables.put("rejected", rejected);
        return processVariables;
    }

    private Map<String, Object> createTaskVariables(Map<String, Object> variables, boolean rejected, String comment) {
        Map<String, Object> taskVariables = new HashMap<>();
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            taskVariables.put("task_variable_" + entry.getKey(), entry.getValue());
        }
        taskVariables.put("rejected", rejected);
        if (Objects.nonNull(comment)) {
            taskVariables.put("comment", comment);
        }
        return taskVariables;
    }

    private Map<String, Map<String, Object>> getProcessTaskVariables(String processId, String businessKey) {
        HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery()
                .processDefinitionKey(wrapProcessId(processId))
                .processInstanceBusinessKey(businessKey)
                .singleResult();
        if (Objects.isNull(processInstance)) {
            throw new IllegalStateException("historic process instance not found: " + businessKey);
        }
        List<HistoricVariableInstance> variableInstances = historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(processInstance.getId())
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
        List<HistoricTaskInstance> taskInstances = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processInstance.getId())
                .list();
        for (HistoricTaskInstance taskInstance : taskInstances) {
            if (!processTaskVariables.containsKey(taskInstance.getId())) {
                processTaskVariables.put(taskInstance.getId(), Collections.emptyMap());
            }
        }
        return processTaskVariables;
    }

    private TypedCandidate getTaskCandidate(String taskId) {
        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        if (Objects.isNull(historicTaskInstance)) {
            throw new IllegalStateException("historic task instance not found: " + taskId);
        }
        String processDefinitionId = historicTaskInstance.getProcessDefinitionId();
        String taskDefinitionKey = historicTaskInstance.getTaskDefinitionKey();
        return getCandidate(getTaskFlowNode(processDefinitionId, taskDefinitionKey));
    }

    private List<TypedCandidate> getProcessCalculateCandidates(String processId, Map<String, Object> variables) {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(wrapProcessId(processId))
                .singleResult();
        if (Objects.isNull(processDefinition)) {
            throw new IllegalStateException("process definition not found: " + processId);
        }
        String processDefinitionId = processDefinition.getId();
        return getCandidates(getProcessStarterFlowNode(processDefinitionId), variables);
    }

    private TypedCandidate getTaskAppliedCandidate(String taskId) {
        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery()
                .taskId(taskId).singleResult();
        if (Objects.isNull(historicTaskInstance)) {
            throw new IllegalStateException("historic task instance not found: " + taskId);
        }
        Map<String, Object> taskVariables = historicTaskInstance.getTaskLocalVariables();
        Object appliedCandidate = taskVariables.get("appliedCandidate");
        if (Objects.isNull(appliedCandidate)) {
            throw new IllegalStateException("missing applied candidate of task: " + taskId);
        }
        return (TypedCandidate) appliedCandidate;
    }

    private Map<String, TypedCandidate> getProcessTaskAppliedCandidates(String processId, String businessKey) {
        Map<String, TypedCandidate> taskAppliedCandidates = new HashMap<>();
        getProcessTaskVariables(processId, businessKey).forEach((taskId, vars) -> {
            TypedCandidate appliedCandidate = (TypedCandidate) vars.get("appliedCandidate");
            if (Objects.isNull(appliedCandidate)) {
                throw new IllegalStateException("missing applied candidate of task: " + taskId);
            }
            taskAppliedCandidates.put(taskId, appliedCandidate);
        });
        return taskAppliedCandidates;
    }

    private Map<String, TypedCandidate> getProcessTaskApprovedCandidates(String processId, String businessKey) {
        Map<String, TypedCandidate> taskApprovedCandidates = new HashMap<>();
        getProcessTaskVariables(processId, businessKey).forEach((taskId, vars) -> {
            TypedCandidate candidate = (TypedCandidate) vars.get("approvedCandidate");
            if (Objects.isNull(candidate)) {
                throw new IllegalStateException("missing approved candidate of task: " + taskId);
            }
            taskApprovedCandidates.put(taskId, candidate);
        });
        return taskApprovedCandidates;
    }

    private void applyProcessTaskCandidates(String processId, String businessKey) {
        AtomicInteger appointeeIndex = new AtomicInteger();
        getProcessTaskVariables(processId, businessKey).forEach((taskId, vars) -> {
            TypedCandidate definedCandidate = getTaskCandidate(taskId);
            if (vars.containsKey("appliedCandidate")) {
                if (STARTER_APPOINTEE == definedCandidate.getType()) {
                    appointeeIndex.getAndIncrement();
                }
                return;
            }
            HistoricTaskInstance taskInstance = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
            List<HistoricVariableInstance> variableInstances = historyService.createHistoricVariableInstanceQuery()
                    .processInstanceId(taskInstance.getProcessInstanceId()).list();
            Map<String, HistoricVariableInstance> groupedVariables = variableInstances.stream().collect(
                    Collectors.groupingBy(HistoricVariableInstance::getVariableName, Collectors.collectingAndThen(Collectors.toList(), lst -> lst.get(0))));
            if (!groupedVariables.containsKey("userId")
                    || !groupedVariables.containsKey("appointees")
                    || !groupedVariables.containsKey("updates")) {
                throw new IllegalStateException("missing starter or appointee or updates of task: " + taskId);
            }
            String userId = (String) groupedVariables.get("userId").getValue();
            Candidates appointees = (Candidates) groupedVariables.get("appointees").getValue();
            CandidateUpdates updates = (CandidateUpdates) groupedVariables.get("updates").getValue();
            switch (definedCandidate.getType()) {
                case STARTER:
                    definedCandidate = definedCandidate.replaceCandidate(DefaultCandidate.createUser(userId));
                    addTaskCandidate(taskId, definedCandidate);
                    break;
                case STARTER_APPOINTEE:
                    Candidate appointee = appointees.getCandidate(appointeeIndex.getAndIncrement());
                    if (Objects.isNull(appointee)) {
                        throw new IllegalStateException("could not find next appointee candidate of task: " + taskId);
                    }
                    definedCandidate = definedCandidate.replaceCandidate(appointee);
                    addTaskCandidate(taskId, definedCandidate);
                    break;
            }
            Set<Candidate> candidatesToAdd = new HashSet<>();
            Set<Candidate> candidatesToRemove = new HashSet<>();
            for (Map.Entry<String, Candidate> update : updates.getGroupUpdates().entrySet()) {
                long count = taskService.createTaskQuery().taskId(taskId).taskCandidateGroup(update.getKey()).count();
                if (count > 0) {
                    candidatesToAdd.add(update.getValue());
                    candidatesToRemove.add(DefaultCandidate.createGroups(Collections.singleton(update.getKey())));
                }
            }
            for (Map.Entry<String, Candidate> update : updates.getUserUpdates().entrySet()) {
                long count = taskService.createTaskQuery().taskId(taskId).taskCandidateUser(update.getKey()).count();
                if (count > 0) {
                    candidatesToAdd.add(update.getValue());
                    candidatesToRemove.add(DefaultCandidate.createUsers(Collections.singleton(update.getKey())));
                }
            }
            definedCandidate = definedCandidate.mergeCandidates(candidatesToAdd).clearCandidates(candidatesToRemove);
            taskService.setVariableLocal(taskId, "appliedCandidate", definedCandidate);
            addTaskCandidates(taskId, candidatesToAdd);
            removeTaskCandidates(taskId, candidatesToRemove);
        });
    }

    private void addTaskCandidates(String taskId, Set<Candidate> candidates) {
        for (Candidate candidate : candidates) {
            addTaskCandidate(taskId, candidate);
        }
    }

    private void removeTaskCandidates(String taskId, Set<Candidate> candidates) {
        for (Candidate candidate : candidates) {
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

    private FlowNode getProcessStarterFlowNode(String processDefinitionId) {
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        if (Objects.isNull(bpmnModel)) {
            throw new IllegalStateException("missing bpmn model: " + processDefinitionId);
        }
        FlowElement element = bpmnModel.getMainProcess().getInitialFlowElement();
        if (Objects.isNull(element)) {
            throw new IllegalStateException("missing initial flow element: " + processDefinitionId);
        }
        return (FlowNode) element;
    }

    private FlowNode getTaskFlowNode(String processDefinitionId, String taskDefinitionKey) {
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        if (Objects.isNull(bpmnModel)) {
            throw new IllegalStateException("missing bpmn model: " + processDefinitionId);
        }
        FlowElement element = bpmnModel.getFlowElement(taskDefinitionKey);
        if (Objects.isNull(element)) {
            throw new IllegalStateException("missing task flow element: " + taskDefinitionKey);
        }
        return (FlowNode) element;
    }
}
