package work.gaigeshen.formwork.basal.bpmn.usertask;

import work.gaigeshen.formwork.basal.bpmn.candidate.Candidate;

import java.util.Date;

/**
 * 用户任务
 *
 * @author gaigeshen
 */
public class UserTask {

    /**
     * 任务标识符
     */
    private final String id;

    /**
     * 流程标识符
     */
    private final String processId;

    /**
     * 业务标识符
     */
    private final String businessKey;

    /**
     * 审批候选人
     */
    private final Candidate candidate;

    /**
     * 创建时间
     */
    private final Date createTime;

    private UserTask(Builder builder) {
        this.id = builder.id;
        this.processId = builder.processId;
        this.businessKey = builder.businessKey;
        this.candidate = builder.candidate;
        this.createTime = builder.createTime;
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

    public static class Builder {

        private String id;

        private String processId;

        private String businessKey;

        private Candidate candidate;

        private Date createTime;

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

        public UserTask build() {
            return new UserTask(this);
        }
    }
}
