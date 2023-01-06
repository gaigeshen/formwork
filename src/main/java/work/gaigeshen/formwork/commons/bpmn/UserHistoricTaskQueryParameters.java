package work.gaigeshen.formwork.commons.bpmn;

/**
 * 用户历史任务查询参数
 *
 * @author gaigeshen
 */
public class UserHistoricTaskQueryParameters {

    private final String taskId;

    private final String processId;

    private final String businessKey;

    private final String assignee;

    private UserHistoricTaskQueryParameters(Builder builder) {
        this.taskId = builder.taskId;
        this.processId = builder.processId;
        this.businessKey = builder.businessKey;
        this.assignee = builder.assignee;
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

    public String getAssignee() {
        return assignee;
    }

    @Override
    public String toString() {
        return "UserTaskQueryParameters{" +
                "taskId='" + taskId + '\'' +
                ", processId='" + processId + '\'' +
                ", businessKey='" + businessKey + '\'' +
                ", assignee='" + assignee + '\'' +
                '}';
    }

    public static class Builder {

        private String taskId;

        private String processId;

        private String businessKey;

        private String assignee;

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

        public Builder assignee(String assignee) {
            this.assignee = assignee;
            return this;
        }

        public UserHistoricTaskQueryParameters build() {
            return new UserHistoricTaskQueryParameters(this);
        }
    }
}
