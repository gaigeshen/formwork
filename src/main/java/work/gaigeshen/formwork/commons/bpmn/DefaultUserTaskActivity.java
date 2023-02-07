package work.gaigeshen.formwork.commons.bpmn;

import work.gaigeshen.formwork.commons.bpmn.candidate.Candidate;

import java.util.Date;

/**
 * @author gaigeshen
 */
public class DefaultUserTaskActivity implements UserTaskActivity {

    private final String taskId;

    private final Candidate candidate;

    private final Date startTime;

    private final Date endTime;

    private final Status status;

    private DefaultUserTaskActivity(Builder builder) {
        this.taskId = builder.taskId;
        this.candidate = builder.candidate;
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
    public Candidate getCandidate() {
        return candidate;
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

        private Candidate candidate;

        private Date startTime;

        private Date endTime;

        private Status status;

        public Builder taskId(String taskId) {
            this.taskId = taskId;
            return this;
        }

        public Builder candidate(Candidate candidate) {
            this.candidate = candidate;
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
