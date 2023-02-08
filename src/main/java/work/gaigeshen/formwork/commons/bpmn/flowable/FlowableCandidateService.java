package work.gaigeshen.formwork.commons.bpmn.flowable;

import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.FlowNode;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.TaskService;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.task.api.history.HistoricTaskInstance;
import work.gaigeshen.formwork.commons.bpmn.CandidateService;
import work.gaigeshen.formwork.commons.bpmn.VariableService;
import work.gaigeshen.formwork.commons.bpmn.candidate.Candidate;
import work.gaigeshen.formwork.commons.bpmn.candidate.TypedCandidate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static work.gaigeshen.formwork.commons.bpmn.flowable.FlowableBpmnParser.*;

public class FlowableCandidateService implements CandidateService {

    private final RepositoryService repositoryService;

    private final HistoryService historyService;

    private final TaskService taskService;

    private final VariableService variableService;

    public FlowableCandidateService(RepositoryService repositoryService,
                                    HistoryService historyService, TaskService taskService,
                                    VariableService variableService) {
        this.repositoryService = repositoryService;
        this.historyService = historyService;
        this.taskService = taskService;
        this.variableService = variableService;
    }

    @Override
    public TypedCandidate getTaskCandidate(String taskId) {
        if (Objects.isNull(taskId)) {
            throw new IllegalArgumentException("taskId cannot be null");
        }
        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery()
                .taskId(taskId).singleResult();
        if (Objects.isNull(historicTaskInstance)) {
            throw new IllegalStateException("historic task instance not found: " + taskId);
        }
        return getCandidate(getTaskFlowNode(historicTaskInstance.getProcessDefinitionId(),
                historicTaskInstance.getTaskDefinitionKey()));
    }

    @Override
    public List<TypedCandidate> getTaskCandidates(String taskId, Map<String, Object> variables) {
        if (Objects.isNull(taskId) || Objects.isNull(variables)) {
            throw new IllegalArgumentException("taskId and variables cannot be null");
        }
        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery()
                .taskId(taskId).singleResult();
        if (Objects.isNull(historicTaskInstance)) {
            throw new IllegalStateException("historic task instance not found: " + taskId);
        }
        return getCandidates(getTaskFlowNode(historicTaskInstance.getProcessDefinitionId(),
                historicTaskInstance.getTaskDefinitionKey()), variables);
    }

    @Override
    public List<TypedCandidate> getProcessCalculateCandidates(String processId, Map<String, Object> variables) {
        if (Objects.isNull(processId) || Objects.isNull(variables)) {
            throw new IllegalArgumentException("processId and variables cannot be null");
        }
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(wrapProcessId(processId))
                .singleResult();
        if (Objects.isNull(processDefinition)) {
            throw new IllegalStateException("process definition not found: " + processId);
        }
        FlowNode starterFlowNode = getProcessStarterFlowNode(processDefinition.getId());
        return getCandidates(starterFlowNode, variables);
    }

    @Override
    public TypedCandidate getTaskAppliedCandidate(String taskId) {
        if (Objects.isNull(taskId)) {
            throw new IllegalArgumentException("taskId cannot be null");
        }
        HistoricTaskInstance task = historyService.createHistoricTaskInstanceQuery()
                .taskId(taskId).singleResult();
        if (Objects.isNull(task)) {
            throw new IllegalStateException("historic task instance not found: " + taskId);
        }
        Map<String, Object> taskVariables = task.getTaskLocalVariables();
        Object taskCandidate = taskVariables.get("candidate");
        if (Objects.isNull(taskCandidate)) {
            throw new IllegalStateException("applied candidate not found " + taskId);
        }
        return (TypedCandidate) taskCandidate;
    }

    @Override
    public Map<String, TypedCandidate> getProcessTaskAppliedCandidates(String processId, String businessKey) {
        Map<String, TypedCandidate> taskAppliedCandidates = new HashMap<>();
        variableService.getProcessTaskVariables(processId, businessKey).forEach((taskId, vars) -> {
            TypedCandidate candidate = (TypedCandidate) vars.get("candidate");
            if (Objects.isNull(candidate)) {
                throw new IllegalStateException("missing applied candidate of task: " + taskId);
            }
            taskAppliedCandidates.put(taskId, candidate);
        });
        return taskAppliedCandidates;
    }

    @Override
    public Map<String, TypedCandidate> getProcessTaskAssignees(String processId, String businessKey) {
        Map<String, TypedCandidate> taskAssignees = new HashMap<>();
        variableService.getProcessTaskVariables(processId, businessKey).forEach((taskId, vars) -> {
            TypedCandidate candidate = (TypedCandidate) vars.get("assignee");
            if (Objects.isNull(candidate)) {
                throw new IllegalStateException("missing assignee of task: " + taskId);
            }
            taskAssignees.put(taskId, candidate);
        });
        return taskAssignees;
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
    public void removeTaskCandidate(String taskId, Candidate candidate) {
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
