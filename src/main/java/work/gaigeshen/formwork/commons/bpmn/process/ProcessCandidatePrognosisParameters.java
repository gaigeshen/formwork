package work.gaigeshen.formwork.commons.bpmn.process;

import java.util.Map;
import java.util.Objects;

/**
 * 业务流程审批候选人预测参数
 *
 * @author gaigeshen
 */
public class ProcessCandidatePrognosisParameters {

    private final String processId;

    private final Map<String, Object> variables;

    private ProcessCandidatePrognosisParameters(Builder builder) {
        this.processId = builder.processId;
        this.variables = builder.variables;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getProcessId() {
        return processId;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    @Override
    public String toString() {
        return "ProcessCandidatePrognosisParameters{" +
                "processId='" + processId + '\'' +
                ", variables=" + variables +
                '}';
    }

    public static class Builder {

        private String processId;

        private Map<String, Object> variables;

        public Builder processId(String processId) {
            this.processId = processId;
            return this;
        }

        public Builder variables(Map<String, Object> variables) {
            this.variables = variables;
            return this;
        }

        public ProcessCandidatePrognosisParameters build() {
            if (Objects.isNull(processId)) {
                throw new IllegalArgumentException("processId cannot be null");
            }
            if (Objects.isNull(variables)) {
                throw new IllegalArgumentException("variables cannot be null");
            }
            return new ProcessCandidatePrognosisParameters(this);
        }
    }
}
