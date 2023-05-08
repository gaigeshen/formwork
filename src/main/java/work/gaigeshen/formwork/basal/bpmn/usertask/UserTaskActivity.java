package work.gaigeshen.formwork.basal.bpmn.usertask;

import work.gaigeshen.formwork.basal.bpmn.candidate.Candidate;

import java.util.Date;

/**
 * 用户任务活动
 *
 * @author gaigeshen
 */
public class UserTaskActivity {

    private final Candidate candidate;

    private final Date startTime;

    private final Date endTime;

    private final Status status;

    private final String comment;

    private UserTaskActivity(Builder builder) {
        this.candidate = builder.candidate;
        this.startTime = builder.startTime;
        this.endTime = builder.endTime;
        this.status = builder.status;
        this.comment = builder.comment;
    }

    public static Builder builder() {
        return new Builder();
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

    public String getComment() {
        return comment;
    }

    public static class Builder {

        private Candidate candidate;

        private Date startTime;

        private Date endTime;

        private Status status;

        private String comment;

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

        public Builder comment(String comment) {
            this.comment = comment;
            return this;
        }

        public UserTaskActivity build() {
            return new UserTaskActivity(this);
        }
    }

    /**
     * 用户任务审批结果状态
     *
     * @author gaigeshen
     */
    public enum Status {

        /**
         * 正在审批中
         */
        PROCESSING(1) {
            @Override
            public boolean isProcessing() {
                return true;
            }
        },

        /**
         * 审批结果为通过
         */
        APPROVED(2) {
            @Override
            public boolean isApproved() {
                return true;
            }
        },

        /**
         * 审批结果为拒绝
         */
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
