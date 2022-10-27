package work.gaigeshen.formwork.commons.bpmn;

import java.util.Objects;

/**
 * 用户任务活动查询参数
 *
 * @author gaigeshen
 */
public class UserTaskActivityQueryParameters {

    private final String processId;

    private final String businessKey;

    private UserTaskActivityQueryParameters(Builder builder) {
        this.processId = builder.processId;
        this.businessKey = builder.businessKey;
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

    @Override
    public String toString() {
        return "UserTaskActivityQueryParameters{" +
                "processId='" + processId + '\'' +
                ", businessKey='" + businessKey + '\'' +
                '}';
    }

    public static class Builder {

        private String processId;

        private String businessKey;

        public Builder processId(String processId) {
            this.processId = processId;
            return this;
        }

        public Builder businessKey(String businessKey) {
            this.businessKey = businessKey;
            return this;
        }

        public UserTaskActivityQueryParameters build() {
            if (Objects.isNull(processId)) {
                throw new IllegalArgumentException("processId cannot be null");
            }
            if (Objects.isNull(businessKey)) {
                throw new IllegalArgumentException("businessKey cannot be null");
            }
            return new UserTaskActivityQueryParameters(this);
        }
    }
}
