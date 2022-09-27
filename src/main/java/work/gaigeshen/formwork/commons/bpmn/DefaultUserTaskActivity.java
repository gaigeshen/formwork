package work.gaigeshen.formwork.commons.bpmn;

import java.util.Date;

/**
 * @author gaigeshen
 */
public class DefaultUserTaskActivity implements UserTaskActivity {

    private final String taskId;

    private final String assignee;

    private final Date startTime;

    private final Date endTime;

    private DefaultUserTaskActivity(Builder builder) {
        this.taskId = builder.taskId;
        this.assignee = builder.assignee;
        this.startTime = builder.startTime;
        this.endTime = builder.endTime;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String getTaskId() {
        return taskId;
    }

    @Override
    public String getAssignee() {
        return assignee;
    }

    @Override
    public Date getStartTime() {
        return startTime;
    }

    @Override
    public Date getEndTime() {
        return endTime;
    }

    public static class Builder {

        private String taskId;

        private String assignee;

        private Date startTime;

        private Date endTime;

        public Builder taskId(String taskId) {
            this.taskId = taskId;
            return this;
        }

        public Builder assignee(String assignee) {
            this.assignee = assignee;
            return this;
        }

        public Builder startTime(Date startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder endTime(Date endTime) {
            this.endTime = endTime;
            return this;
        }

        public DefaultUserTaskActivity build() {
            return new DefaultUserTaskActivity(this);
        }
    }
}
