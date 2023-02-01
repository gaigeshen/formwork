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

import java.util.List;
import java.util.Map;
import java.util.Objects;

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
        FlowNode taskFlowNode = getTaskFlowNode(task);
        return getCandidate(taskFlowNode);
    }

    @Override
    public List<TypedCandidate> getTaskCandidates(String taskId, Map<String, Object> variables) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (Objects.isNull(task)) {
            throw new IllegalStateException("could not find task: " + taskId);
        }
        FlowNode taskFlowNode = getTaskFlowNode(task);
        return getCandidates(taskFlowNode, variables);
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
        return (CandidateVariables) processVariables.get("candidateVariables");
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
    public void addTaskCandidate(String taskId, Candidate candidate) {
        for (String group : candidate.getGroups()) {
            taskService.addCandidateGroup(taskId, group);
        }
        for (String user : candidate.getUsers()) {
            taskService.addCandidateUser(taskId, user);
        }
    }

    @Override
    public void updateTaskCandidate(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (Objects.isNull(task)) {
            throw new IllegalStateException("could not find task: " + taskId);
        }
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(task.getProcessInstanceId())
                .singleResult();
        String processId = unWrapProcessId(processInstance.getProcessDefinitionKey());
        String businessKey = processInstance.getBusinessKey();
        CandidateVariables candidates = getProcessCandidateVariables(processId, businessKey);
        CandidateType taskCandidateType = getTaskCandidate(taskId).getType();
        if (taskCandidateType.isStarter()) {
            String starter = candidates.getStarter();
            if (Objects.isNull(starter)) {
                throw new IllegalStateException("missing starter candidate variable: " + taskId);
            }
            taskService.addCandidateUser(task.getId(), starter);
        }
        else if (taskCandidateType.isStarterAppoint()) {
            Candidates starterAppoint = candidates.getStarterAppoint();
            if (Objects.isNull(starterAppoint) || starterAppoint.isEmpty()) {
                throw new IllegalStateException("missing starter appoint candidate variable: " + taskId);
            }
            Candidate candidate = starterAppoint.poll();
            addTaskCandidate(taskId, candidate);
            runtimeService.setVariable(task.getExecutionId(), "starterAppoint", starterAppoint);
        }
        else if (taskCandidateType.isStarterLeaderInclude()) {
            Candidate candidate = candidates.getStarterLeader();
            if (Objects.isNull(candidate)) {
                throw new IllegalStateException("missing starter leader candidate variable: " + taskId);
            }
            addTaskCandidate(taskId, candidate);
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
