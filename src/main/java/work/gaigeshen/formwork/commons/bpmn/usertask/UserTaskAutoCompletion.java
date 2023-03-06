package work.gaigeshen.formwork.commons.bpmn.usertask;

import work.gaigeshen.formwork.commons.bpmn.candidate.Candidate;

/**
 * 用户任务自动完成结果
 *
 * @author gaigeshen
 */
public class UserTaskAutoCompletion {

    private final String processId;

    private final String businessKey;

    private final Candidate candidate;

    private final boolean hasMoreTasks;

    private UserTaskAutoCompletion(Builder builder) {
        this.processId = builder.processId;
        this.businessKey = builder.businessKey;
        this.candidate = builder.candidate;
        this.hasMoreTasks = builder.hasMoreTasks;
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

    public Candidate getCandidate() {
        return candidate;
    }

    public boolean hasMoreUserTasks() {
        return hasMoreTasks;
    }

    public static class Builder {

        private String processId;

        private String businessKey;

        private Candidate candidate;

        private boolean hasMoreTasks;

        public Builder processId(String processId) {
            this.processId = processId;
            return this;
        }

        public Builder businessKey(String businessKey) {
            this.businessKey = businessKey;
            return this;
        }

        public Builder candidate(Candidate candidate) {
            this.candidate = candidate;
            return this;
        }

        public Builder hasMoreTasks(boolean hasMoreTasks) {
            this.hasMoreTasks = hasMoreTasks;
            return this;
        }

        public UserTaskAutoCompletion build() {
            return new UserTaskAutoCompletion(this);
        }
    }
}
