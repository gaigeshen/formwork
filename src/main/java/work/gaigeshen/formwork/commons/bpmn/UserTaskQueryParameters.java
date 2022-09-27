package work.gaigeshen.formwork.commons.bpmn;

import java.util.Set;

/**
 *
 * @author gaigeshen
 */
public class UserTaskQueryParameters {

    private final String processId;

    private final String businessKey;

    private final Set<String> candidateGroups;

    private final String candidateUser;

    private UserTaskQueryParameters(Builder builder) {
        this.processId = builder.processId;
        this.businessKey = builder.businessKey;
        this.candidateGroups = builder.candidateGroups;
        this.candidateUser = builder.candidateUser;
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

    public Set<String> getCandidateGroups() {
        return candidateGroups;
    }

    public String getCandidateUser() {
        return candidateUser;
    }

    public static class Builder {

        private String processId;

        private String businessKey;

        private Set<String> candidateGroups;

        private String candidateUser;

        public Builder processId(String processId) {
            this.processId = processId;
            return this;
        }

        public Builder businessKey(String businessKey) {
            this.businessKey = businessKey;
            return this;
        }

        public Builder candidateGroups(Set<String> candidateGroups) {
            this.candidateGroups = candidateGroups;
            return this;
        }

        public Builder candidateUser(String candidateUser) {
            this.candidateUser = candidateUser;
            return this;
        }

        public UserTaskQueryParameters build() {
            return new UserTaskQueryParameters(this);
        }
    }
}
