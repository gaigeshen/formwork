package work.gaigeshen.formwork.commons.bpmn.usertask;

import work.gaigeshen.formwork.commons.bpmn.candidate.Candidate;

import java.util.Date;

/**
 * 用户历史任务
 *
 * @author gaigeshen
 */
public class UserHistoricTask {

    private final String id;

    private final String processId;

    private final String businessKey;

    private final Candidate candidate;

    private final Date createTime;

    private final Date endTime;

    private UserHistoricTask(Builder builder) {
        this.id = builder.id;
        this.processId = builder.processId;
        this.businessKey = builder.businessKey;
        this.candidate = builder.candidate;
        this.createTime = builder.createTime;
        this.endTime = builder.endTime;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getId() {
        return id;
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

    public Date getCreateTime() {
        return createTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public static class Builder {

        private String id;

        private String processId;

        private String businessKey;

        private Candidate candidate;

        private Date createTime;

        private Date endTime;

        public Builder id(String id) {
            this.id = id;
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

        public Builder candidate(Candidate candidate) {
            this.candidate = candidate;
            return this;
        }

        public Builder createTime(Date createTime) {
            this.createTime = createTime;
            return this;
        }

        public Builder endTime(Date endTime) {
            this.endTime = endTime;
            return this;
        }

        public UserHistoricTask build() {
            return new UserHistoricTask(this);
        }
    }
}
