package work.gaigeshen.formwork.commons.bpmn.flowable;

import org.flowable.engine.HistoryService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.variable.api.history.HistoricVariableInstance;
import work.gaigeshen.formwork.commons.bpmn.VariableService;

import java.util.*;

import static work.gaigeshen.formwork.commons.bpmn.flowable.FlowableBpmnParser.wrapProcessId;

public class FlowableVariableService implements VariableService {

    private final HistoryService historyService;

    public FlowableVariableService(HistoryService historyService) {
        this.historyService = historyService;
    }

    @Override
    public Map<String, Object> createProcessVariables(Map<String, Object> variables, boolean rejected) {
        if (Objects.isNull(variables)) {
            return Collections.emptyMap();
        }
        Map<String, Object> wrappedVariables = new HashMap<>();
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            wrappedVariables.put("variable_" + entry.getKey(), entry.getValue());
        }
        wrappedVariables.put("rejected", rejected);
        return wrappedVariables;
    }

    @Override
    public Map<String, Object> createTaskVariables(Map<String, Object> variables, boolean rejected) {
        Map<String, Object> taskVariables = new HashMap<>(variables);
        taskVariables.put("rejected", rejected);
        return taskVariables;
    }

    @Override
    public Map<String, Map<String, Object>> getProcessTaskVariables(String processId, String businessKey) {
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
    public boolean getTaskRejectedVariable(String taskId, Map<String, Object> taskVariables) {
        Object rejectedVariable = taskVariables.get("rejected");
        if (Objects.isNull(rejectedVariable)) {
            throw new IllegalStateException("could not find 'rejected' variable: " + taskId);
        }
        return (boolean) rejectedVariable;
    }
}
