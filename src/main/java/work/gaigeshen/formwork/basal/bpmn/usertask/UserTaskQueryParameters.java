package work.gaigeshen.formwork.basal.bpmn.usertask;

import java.util.Set;

/**
 * 用户任务查询参数
 *
 * @author gaigeshen
 */
public class UserTaskQueryParameters {

    /**
     * 任务标识符
     */
    private final String taskId;

    /**
     * 流程标识符
     */
    private final String processId;

    /**
     * 业务标识符
     */
    private final String businessKey;

    /**
     * 审批候选人组
     */
    private final Set<String> candidateGroups;

    /**
     * 审批候选人
     */
    private final String candidateUser;

    private UserTaskQueryParameters(Builder builder) {
        this.taskId = builder.taskId;
        this.processId = builder.processId;
        this.businessKey = builder.businessKey;
        this.candidateGroups = builder.candidateGroups;
        this.candidateUser = builder.candidateUser;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getTaskId() {
        return taskId;
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

    @Override
    public String toString() {
        return "UserTaskQueryParameters{" +
                "taskId='" + taskId + '\'' +
                ", processId='" + processId + '\'' +
                ", businessKey='" + businessKey + '\'' +
                ", candidateGroups=" + candidateGroups +
                ", candidateUser='" + candidateUser + '\'' +
                '}';
    }

    public static class Builder {

        private String taskId;

        private String processId;

        private String businessKey;

        private Set<String> candidateGroups;

        private String candidateUser;

        public Builder taskId(String taskId) {
            this.taskId = taskId;
            return this;
        }

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
