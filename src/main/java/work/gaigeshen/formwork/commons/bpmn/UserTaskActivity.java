package work.gaigeshen.formwork.commons.bpmn;

import work.gaigeshen.formwork.commons.bpmn.candidate.Candidate;

import java.util.Date;

/**
 * 用户任务活动
 *
 * @author gaigeshen
 */
public class UserTaskActivity {

    private final String taskId;

    private final Candidate candidate;

    private final Date startTime;

    private final Date endTime;

    private final Status status;

    private UserTaskActivity(Builder builder) {
        this.taskId = builder.taskId;
        this.candidate = builder.candidate;
        this.startTime = builder.startTime;
        this.endTime = builder.endTime;
        this.status = builder.status;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getTaskId() {
        return taskId;
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

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

        public UserTaskActivity build() {
            return new UserTaskActivity(this);
        }
    }

    public enum Status {

        PROCESSING(1) {
            @Override
            public boolean isProcessing() {
                return true;
            }
        },
        APPROVED(2) {
            @Override
            public boolean isApproved() {
                return true;
            }
        },
        REJECTED(3) {
            @Override
            public boolean isRejected() {
                return true;
            }
        };

        private final int code;

        Status(int code) {
            this.code = code;
        }

        public boolean isProcessing() {
            return false;
        }

        public boolean isApproved() {
            return false;
        }

        public boolean isRejected() {
            return false;
        }

        public int getCode() {
            return code;
        }
    }
}
