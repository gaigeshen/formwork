package work.gaigeshen.formwork.commons.bpmn.usertask;

import java.util.Map;
import java.util.Objects;

/**
 * 用户任务自动完成参数，适用于需要自动审批通过的用户任务
 *
 * @author gaigeshen
 */
public class UserTaskAutoCompleteParameters {

    /**
     * 流程标识符
     */
    private final String processId;

    /**
     * 业务标识符
     */
    private final String businessKey;

    /**
     * 任务参数
     */
    private final Map<String, Object> variables;

    private UserTaskAutoCompleteParameters(Builder builder) {
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

    @Override
    public String toString() {
        return "UserTaskAutoCompleteParameters{" +
                "processId='" + processId + '\'' +
                ", businessKey='" + businessKey + '\'' +
                ", variables=" + variables +
                '}';
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

        public UserTaskAutoCompleteParameters build() {
            if (Objects.isNull(processId)) {
                throw new IllegalArgumentException("processId cannot be null");
            }
            if (Objects.isNull(businessKey)) {
                throw new IllegalArgumentException("businessKey cannot be null");
            }
            if (Objects.isNull(variables)) {
                throw new IllegalArgumentException("variables cannot be null");
            }
            return new UserTaskAutoCompleteParameters(this);
        }
    }
}
