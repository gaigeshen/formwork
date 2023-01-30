package work.gaigeshen.formwork.commons.bpmn;

import java.util.Set;

/**
 * 用户历史任务查询参数
 *
 * @author gaigeshen
 */
public class UserHistoricTaskQueryParameters {

    private final String taskId;

    private final String processId;

    private final String businessKey;

    private final Set<String> assignees;

    private UserHistoricTaskQueryParameters(Builder builder) {
        this.taskId = builder.taskId;
        this.processId = builder.processId;
        this.businessKey = builder.businessKey;
        this.assignees = builder.assignees;
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

    public Set<String> getAssignees() {
        return assignees;
    }

    @Override
    public String toString() {
        return "UserHistoricTaskQueryParameters{" +
                "taskId='" + taskId + '\'' +
                ", processId='" + processId + '\'' +
                ", businessKey='" + businessKey + '\'' +
                ", assignees=" + assignees +
                '}';
    }

    public static class Builder {

        private String taskId;

        private String processId;

        private String businessKey;

        private Set<String> assignees;

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

        public Builder assignees(Set<String> assignees) {
            this.assignees = assignees;
            return this;
        }

        public UserHistoricTaskQueryParameters build() {
            return new UserHistoricTaskQueryParameters(this);
        }
    }
}
