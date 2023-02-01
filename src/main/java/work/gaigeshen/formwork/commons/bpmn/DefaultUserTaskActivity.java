package work.gaigeshen.formwork.commons.bpmn;

import java.util.Date;
import java.util.List;

/**
 * @author gaigeshen
 */
public class DefaultUserTaskActivity implements UserTaskActivity {

    private final String taskId;

    private final String assignee;

    private final List<String> groups;

    private final List<String> users;

    private final Date startTime;

    private final Date endTime;

    private final Status status;

    private DefaultUserTaskActivity(Builder builder) {
        this.taskId = builder.taskId;
        this.assignee = builder.assignee;
        this.groups = builder.groups;
        this.users = builder.users;
        this.startTime = builder.startTime;
        this.endTime = builder.endTime;
        this.status = builder.status;
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
    public List<String> getGroups() {
        return groups;
    }

    @Override
    public List<String> getUsers() {
        return users;
    }

    @Override
    public Date getStartTime() {
        return startTime;
    }

    @Override
    public Date getEndTime() {
        return endTime;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    public static class Builder {

        private String taskId;

        private String assignee;

        private List<String> groups;

        private List<String> users;

        private Date startTime;

        private Date endTime;

        private Status status;

        public Builder taskId(String taskId) {
            this.taskId = taskId;
            return this;
        }

        public Builder assignee(String assignee) {
            this.assignee = assignee;
            return this;
        }

        public Builder groups(List<String> groups) {
            this.groups = groups;
            return this;
        }

        public Builder users(List<String> users) {
            this.users = users;
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

        public Builder status(Status status) {
            this.status = status;
            return this;
        }

        public DefaultUserTaskActivity build() {
            return new DefaultUserTaskActivity(this);
        }
    }
}
