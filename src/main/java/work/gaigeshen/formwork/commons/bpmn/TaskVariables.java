package work.gaigeshen.formwork.commons.bpmn;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author gaigeshen
 */
public class TaskVariables {

    private final Map<String, Object> variables;

    private final boolean rejected;

    private TaskVariables(Map<String, Object> variables, boolean rejected) {
        this.variables = variables;
        this.rejected = rejected;
    }

    public static TaskVariables createRejected(Map<String, Object> variables) {
        return new TaskVariables(variables, true);
    }

    public static TaskVariables createNotRejected(Map<String, Object> variables) {
        return new TaskVariables(variables, false);
    }

    public void addVariable(String name, Object value) {
        variables.put(name, value);
    }

    public Map<String, Object> getVariables() {
        Map<String, Object> copiedVariables = new HashMap<>(variables);
        copiedVariables.put("rejected", rejected);
        return Collections.unmodifiableMap(copiedVariables);
    }

    public boolean isRejected() {
        return rejected;
    }
}
