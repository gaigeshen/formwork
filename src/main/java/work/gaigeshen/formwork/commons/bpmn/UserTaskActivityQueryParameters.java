package work.gaigeshen.formwork.commons.bpmn;

/**
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
            return new UserTaskActivityQueryParameters(this);
        }
    }
}
