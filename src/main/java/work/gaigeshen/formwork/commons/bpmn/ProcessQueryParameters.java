package work.gaigeshen.formwork.commons.bpmn;

/**
 * 业务流程查询参数
 *
 * @author gaigeshen
 */
public class ProcessQueryParameters {

    private final String processId;

    private final String businessKey;

    private final String userId;

    private final boolean includeHistorical;

    private ProcessQueryParameters(Builder builder) {
        this.processId = builder.processId;
        this.businessKey = builder.businessKey;
        this.userId = builder.userId;
        this.includeHistorical = builder.includeHistorical;
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

    public String getUserId() {
        return userId;
    }

    public boolean isIncludeHistorical() {
        return includeHistorical;
    }

    @Override
    public String toString() {
        return "ProcessQueryParameters{" +
                "processId='" + processId + '\'' +
                ", businessKey='" + businessKey + '\'' +
                ", userId='" + userId + '\'' +
                ", includeHistorical=" + includeHistorical +
                '}';
    }

    public static class Builder {

        private String processId;

        private String businessKey;

        private String userId;

        private boolean includeHistorical;

        public Builder processId(String processId) {
            this.processId = processId;
            return this;
        }

        public Builder businessKey(String businessKey) {
            this.businessKey = businessKey;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder includeHistorical(boolean includeHistorical) {
            this.includeHistorical = includeHistorical;
            return this;
        }

        public ProcessQueryParameters build() {
            return new ProcessQueryParameters(this);
        }
    }
}
