package work.gaigeshen.formwork.commons.bpmn;

import java.util.Map;

/**
 *
 * @author gaigeshen
 */
public class ProcessStartParameters {

    private final String processId;

    private final String businessKey;

    private final Map<String, Object> variables;

    private ProcessStartParameters(Builder builder) {
        this.processId = builder.processId;
        this.businessKey = builder.businessKey;
        this.variables = builder.variables;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getProcessId() {
        return processId;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public static class Builder {

        private String processId;

        private String businessKey;

        private Map<String, Object> variables;

        public Builder processId(String processId) {
            this.processId = processId;
            return this;
        }

        public Builder businessKey(String businessKey) {
            this.businessKey = businessKey;
            return this;
        }

        public Builder variables(Map<String, Object> variables) {
            this.variables = variables;
            return this;
        }

        public ProcessStartParameters build() {
            return new ProcessStartParameters(this);
        }
    }
}
