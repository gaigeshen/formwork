package work.gaigeshen.formwork.commons.bpmn;

import java.util.Map;

public interface VariableService {

    Map<String, Object> createProcessVariables(Map<String, Object> variables, boolean rejected);

    Map<String, Object> createTaskVariables(Map<String, Object> variables, boolean rejected);

    Map<String, Map<String, Object>> getProcessTaskVariables(String processId, String businessKey);

    boolean getTaskRejectedVariable(String taskId, Map<String, Object> taskVariables);
}
