package work.gaigeshen.formwork.commons.bpmn;

import work.gaigeshen.formwork.commons.bpmn.candidate.CandidateVariables;

import java.util.Map;
import java.util.Objects;

/**
 * 业务流程开启参数，用于开始某个业务流程
 *
 * @author gaigeshen
 */
public class ProcessStartParameters {

    private final String processId;

    private final String businessKey;

    private final Map<String, Object> variables;

    private final String userId;

    private final CandidateVariables candidateVariables;

    private ProcessStartParameters(Builder builder) {
        this.processId = builder.processId;
        this.businessKey = builder.businessKey;
        this.variables = builder.variables;
        this.userId = builder.userId;
        this.candidateVariables = builder.candidateVariables;
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

    public String getUserId() {
        return userId;
    }

    public CandidateVariables getCandidateVariables() {
        return candidateVariables;
    }

    @Override
    public String toString() {
        return "ProcessStartParameters{" +
                "processId='" + processId + '\'' +
                ", businessKey='" + businessKey + '\'' +
                ", variables=" + variables +
                ", userId='" + userId + '\'' +
                ", candidateVariables=" + candidateVariables +
                '}';
    }

    public static class Builder {

        private String processId;

        private String businessKey;

        private Map<String, Object> variables;

        private String userId;

        private CandidateVariables candidateVariables;

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

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder candidateVariables(CandidateVariables candidateVariables) {
            this.candidateVariables = candidateVariables;
            return this;
        }

        public ProcessStartParameters build() {
            if (Objects.isNull(processId)) {
                throw new IllegalArgumentException("processId cannot be null");
            }
            if (Objects.isNull(businessKey)) {
                throw new IllegalArgumentException("businessKey cannot be null");
            }
            if (Objects.isNull(variables)) {
                throw new IllegalArgumentException("variables cannot be null");
            }
            if (Objects.isNull(userId)) {
                throw new IllegalArgumentException("userId cannot be null");
            }
            if (Objects.isNull(candidateVariables)) {
                throw new IllegalArgumentException("candidateVariables cannot be null");
            }
            return new ProcessStartParameters(this);
        }
    }
}
