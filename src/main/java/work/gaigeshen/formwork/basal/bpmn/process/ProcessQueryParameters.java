package work.gaigeshen.formwork.basal.bpmn.process;

/**
 * 业务流程查询参数
 *
 * @author gaigeshen
 */
public class ProcessQueryParameters {

    /**
     * 流程标识符
     */
    private final String processId;

    /**
     * 业务标识符
     */
    private final String businessKey;

    /**
     * 业务流程发起人
     */
    private final String userId;

    /**
     * 是否包含历史业务流程
     */
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
