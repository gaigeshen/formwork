package work.gaigeshen.formwork.commons.bpmn.flowable;

import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.FlowNode;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import work.gaigeshen.formwork.commons.bpmn.CandidateService;
import work.gaigeshen.formwork.commons.bpmn.candidate.*;

import java.util.*;

import static work.gaigeshen.formwork.commons.bpmn.flowable.FlowableBpmnParser.*;

public class FlowableCandidateService implements CandidateService {

    private final RepositoryService repositoryService;

    private final RuntimeService runtimeService;

    private final TaskService taskService;

    public FlowableCandidateService(RepositoryService repositoryService,
                                    RuntimeService runtimeService, TaskService taskService) {
        this.repositoryService = repositoryService;
        this.runtimeService = runtimeService;
        this.taskService = taskService;
    }

    @Override
    public TypedCandidate getTaskCandidate(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (Objects.isNull(task)) {
            throw new IllegalStateException("could not find task: " + taskId);
        }
        return getCandidate(getTaskFlowNode(task));
    }

    @Override
    public List<TypedCandidate> getTaskCandidates(String taskId, Map<String, Object> variables) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (Objects.isNull(task)) {
            throw new IllegalStateException("could not find task: " + taskId);
        }
        return getCandidates(getTaskFlowNode(task), variables);
    }

    @Override
    public List<TypedCandidate> getProcessCalculateCandidates(String processId, Map<String, Object> variables) {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(wrapProcessId(processId))
                .singleResult();
        if (Objects.isNull(processDefinition)) {
            throw new IllegalStateException("could not find process definition: " + processId);
        }
        FlowNode starterFlowNode = getProcessStarterFlowNode(processDefinition);
        return getCandidates(starterFlowNode, variables);
    }

    @Override
    public CandidateVariables getProcessCandidateVariables(String processId, String businessKey) {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processDefinitionKey(wrapProcessId(processId))
                .processInstanceBusinessKey(businessKey)
                .singleResult();
        if (Objects.isNull(processInstance)) {
            throw new IllegalStateException("could not find process instance: " + processId);
        }
        Map<String, Object> processVariables = processInstance.getProcessVariables();
        Object candidateVariables = processVariables.get("candidateVariables");
        if (Objects.isNull(candidateVariables)) {
            throw new IllegalStateException("could not find candidate variables: " + processId);
        }
        return (CandidateVariables) candidateVariables;
    }

    @Override
    public CandidateVariables getTaskProcessCandidateVariables(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (Objects.isNull(task)) {
            throw new IllegalStateException("could not find task: " + taskId);
        }
        Map<String, Object> processVariables = task.getProcessVariables();
        Object candidateVariables = processVariables.get("candidateVariables");
        if (Objects.isNull(candidateVariables)) {
            throw new IllegalStateException("could not find candidate variables: " + taskId);
        }
        return (CandidateVariables) candidateVariables;
    }

    @Override
    public void updateProcessCandidateVariables(String processId, String businessKey, CandidateVariables variables) {
        Execution processExecution = runtimeService.createExecutionQuery()
                .processDefinitionKey(wrapProcessId(processId))
                .processInstanceBusinessKey(businessKey)
                .singleResult();
        if (Objects.isNull(processExecution)) {
            throw new IllegalStateException("could not find process execution: " + processId);
        }
        runtimeService.setVariable(processExecution.getId(), "candidateVariables", variables);
    }

    @Override
    public void updateTaskProcessCandidateVariables(String taskId, CandidateVariables variables) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (Objects.isNull(task)) {
            throw new IllegalStateException("could not find task: " + taskId);
        }
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(task.getProcessInstanceId())
                .singleResult();
        if (Objects.isNull(processInstance)) {
            throw new IllegalStateException("could not find process instance: " + taskId);
        }
        String processId = unWrapProcessId(processInstance.getProcessDefinitionKey());
        String businessKey = processInstance.getBusinessKey();
        updateProcessCandidateVariables(processId, businessKey, variables);
    }

    @Override
    public void addTaskCandidate(String taskId, Candidate candidate) {
        for (String group : candidate.getGroups()) {
            taskService.addCandidateGroup(taskId, group);
        }
        for (String user : candidate.getUsers()) {
            taskService.addCandidateUser(taskId, user);
        }
    }

    @Override
    public void addTaskCandidates(String taskId, Set<Candidate> candidates) {
        Set<String> groups = new HashSet<>();
        Set<String> users = new HashSet<>();
        for (Candidate candidate : candidates) {
            groups.addAll(candidate.getGroups());
            users.addAll(candidate.getUsers());
        }
        addTaskCandidate(taskId, DefaultCandidate.create(groups, users));
    }

    @Override
    public void removeTaskCandidate(String taskId, Candidate candidate) {
        for (String group : candidate.getGroups()) {
            taskService.deleteCandidateGroup(taskId, group);
        }
        for (String user : candidate.getUsers()) {
            taskService.deleteCandidateUser(taskId, user);
        }
    }

    @Override
    public void removeTaskCandidates(String taskId, Set<Candidate> candidates) {
        Set<String> groups = new HashSet<>();
        Set<String> users = new HashSet<>();
        for (Candidate candidate : candidates) {
            groups.addAll(candidate.getGroups());
            users.addAll(candidate.getUsers());
        }
        removeTaskCandidate(taskId, DefaultCandidate.create(groups, users));
    }

    @Override
    public void updateTaskStarterCandidate(String taskId, CandidateVariables variables) {
        String starter = variables.getStarter();
        if (Objects.isNull(starter)) {
            throw new IllegalStateException("missing starter candidate: " + taskId);
        }
        addTaskCandidate(taskId, DefaultCandidate.createUsers(Collections.singleton(starter)));
    }

    @Override
    public void updateTaskStarterAppointeeCandidate(String taskId, CandidateVariables variables) {
        Candidates starterAppointee = variables.getStarterAppointee();
        if (Objects.isNull(starterAppointee)) {
            throw new IllegalStateException("missing starter appoint candidate: " + taskId);
        }
        Candidate candidate = starterAppointee.poll();
        if (Objects.isNull(candidate)) {
            throw new IllegalStateException("could not poll starter appoint candidate: " + taskId);
        }
        addTaskCandidate(taskId, candidate);
    }

    @Override
    public void updateTaskCandidateUpdatesCandidate(String taskId, CandidateVariables variables) {
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
        if (!candidatesToAdd.isEmpty()) {
            addTaskCandidates(taskId, candidatesToAdd);
        }
        if (!candidatesToRemove.isEmpty()) {
            removeTaskCandidates(taskId, candidatesToRemove);
        }
    }

    @Override
    public void updateTaskCandidate(String taskId) {
        CandidateVariables variables = getTaskProcessCandidateVariables(taskId);
        CandidateType taskCandidateType = getTaskCandidate(taskId).getType();
        if (taskCandidateType.isStarter()) {
            updateTaskStarterCandidate(taskId, variables);
        }
        else if (taskCandidateType.isStarterAppointee()) {
            updateTaskStarterAppointeeCandidate(taskId, variables);
        }
        updateTaskCandidateUpdatesCandidate(taskId, variables);
        updateTaskProcessCandidateVariables(taskId, variables);
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
