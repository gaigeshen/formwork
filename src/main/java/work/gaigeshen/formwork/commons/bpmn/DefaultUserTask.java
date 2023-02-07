package work.gaigeshen.formwork.commons.bpmn;

import work.gaigeshen.formwork.commons.bpmn.candidate.Candidate;

import java.util.Date;
import java.util.Objects;

/**
 *
 * @author gaigeshen
 */
public class DefaultUserTask implements UserTask {

    private final String id;

    private final String processId;

    private final String businessKey;

    private final Candidate candidate;

    private final Date createTime;

    private final Date dueDate;

    private final Date claimTime;

    private DefaultUserTask(Builder builder) {
        this.id = builder.id;
        this.processId = builder.processId;
        this.businessKey = builder.businessKey;
        this.candidate = builder.candidate;
        this.createTime = builder.createTime;
        this.dueDate = builder.dueDate;
        this.claimTime = builder.claimTime;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getProcessId() {
        return processId;
    }

    @Override
    public String getBusinessKey() {
        return businessKey;
    }

    @Override
    public Candidate getCandidate() {
        return candidate;
    }

    @Override
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public Date getDueDate() {
        return dueDate;
    }

    @Override
    public Date getClaimTime() {
        return claimTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DefaultUserTask that = (DefaultUserTask) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static class Builder {

        private String id;

        private String processId;

        private String businessKey;

        private Candidate candidate;

        private Date createTime;

        private Date dueDate;

        private Date claimTime;

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

        public Builder dueDate(Date dueDate) {
            this.dueDate = dueDate;
            return this;
        }

        public Builder claimTime(Date claimTime) {
            this.claimTime = claimTime;
            return this;
        }

        public DefaultUserTask build() {
            return new DefaultUserTask(this);
        }
    }
}
