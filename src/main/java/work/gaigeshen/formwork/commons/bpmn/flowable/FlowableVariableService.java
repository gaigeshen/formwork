package work.gaigeshen.formwork.commons.bpmn.flowable;

import org.flowable.engine.HistoryService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.variable.api.history.HistoricVariableInstance;
import work.gaigeshen.formwork.commons.bpmn.VariableService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static work.gaigeshen.formwork.commons.bpmn.flowable.FlowableBpmnParser.wrapProcessId;

public class FlowableVariableService implements VariableService {

    private final HistoryService historyService;

    private final TaskService taskService;

    public FlowableVariableService(HistoryService historyService, TaskService taskService) {
        this.historyService = historyService;
        this.taskService = taskService;
    }

    @Override
    public Map<String, Object> createProcessVariables(Map<String, Object> variables, boolean rejected) {
        Map<String, Object> processVariables = new HashMap<>();
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            processVariables.put("variable_" + entry.getKey(), entry.getValue());
        }
        processVariables.put("rejected", rejected);
        return processVariables;
    }

    @Override
    public Map<String, Object> createTaskVariables(Map<String, Object> variables, boolean rejected) {
        Map<String, Object> taskVariables = new HashMap<>();
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            taskVariables.put("task_variable_" + entry.getKey(), entry.getValue());
        }
        taskVariables.put("rejected", rejected);
        return taskVariables;
    }

    @Override
    public Map<String, Map<String, Object>> getProcessTaskVariables(String processId, String businessKey) {
        if (Objects.isNull(processId) || Objects.isNull(businessKey)) {
            throw new IllegalArgumentException("processId and businessKey cannot be null");
        }
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

    @Override
    public Map<String, Object> getTaskVariables(String taskId) {
        if (Objects.isNull(taskId)) {
            throw new IllegalArgumentException("taskId cannot be null");
        }
        Map<String, Object> taskVariables = new HashMap<>();
        List<HistoricVariableInstance> variableInstances = historyService.createHistoricVariableInstanceQuery()
                .taskId(taskId).list();
        for (HistoricVariableInstance variableInstance : variableInstances) {
            taskVariables.put(variableInstance.getVariableName(), variableInstance.getValue());
        }
        return taskVariables;
    }

    @Override
    public void setTaskVariables(String taskId, Map<String, Object> variables) {
        if (Objects.isNull(taskId) || Objects.isNull(variables)) {
            throw new IllegalArgumentException("taskId and variables cannot be null");
        }
        taskService.setVariablesLocal(taskId, variables);
    }
}
