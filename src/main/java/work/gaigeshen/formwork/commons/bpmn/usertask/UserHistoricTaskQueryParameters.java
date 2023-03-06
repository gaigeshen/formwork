package work.gaigeshen.formwork.commons.bpmn.usertask;

import java.util.Set;

/**
 * 用户历史任务查询参数
 *
 * @author gaigeshen
 */
public class UserHistoricTaskQueryParameters {

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
     * 审批候选人组或者审批候选人
     */
    private final Set<String> candidates;

    private UserHistoricTaskQueryParameters(Builder builder) {
        this.taskId = builder.taskId;
        this.processId = builder.processId;
        this.businessKey = builder.businessKey;
        this.candidates = builder.candidates;
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

    public Set<String> getCandidates() {
        return candidates;
    }

    @Override
    public String toString() {
        return "UserHistoricTaskQueryParameters{" +
                "taskId='" + taskId + '\'' +
                ", processId='" + processId + '\'' +
                ", businessKey='" + businessKey + '\'' +
                ", candidates=" + candidates +
                '}';
    }

    public static class Builder {

        private String taskId;

        private String processId;

        private String businessKey;

        private Set<String> candidates;

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

        public Builder candidates(Set<String> candidates) {
            this.candidates = candidates;
            return this;
        }

        public UserHistoricTaskQueryParameters build() {
            return new UserHistoricTaskQueryParameters(this);
        }
    }
}
