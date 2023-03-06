package work.gaigeshen.formwork.commons.bpmn.process;

/**
 * @author gaigeshen
 */
public class DefaultProcess implements Process {

    private final String processId;

    private final String businessKey;

    private final String userId;

    private DefaultProcess(Builder builder) {
        this.processId = builder.processId;
        this.businessKey = builder.businessKey;
        this.userId = builder.userId;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String getProcessId() {
        return processId;
    }

    @Override
    public String getBusinessKey() {
        return businessKey;
    }

    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return "DefaultProcess{" +
                "processId='" + processId + '\'' +
                ", businessKey='" + businessKey + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }

    public static class Builder {

        private String processId;

        private String businessKey;

        private String userId;

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

        public DefaultProcess build() {
            return new DefaultProcess(this);
        }
    }
}
