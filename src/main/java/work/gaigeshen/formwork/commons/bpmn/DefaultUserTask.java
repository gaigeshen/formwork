package work.gaigeshen.formwork.commons.bpmn;

import java.util.Date;
import java.util.Objects;

/**
 *
 * @author gaigeshen
 */
public class DefaultUserTask implements UserTask {

    private final String id;

    private final String name;

    private final String description;

    private final String businessKey;

    private final String assignee;

    private final Date createTime;

    private final Date dueDate;

    private final Date claimTime;

    private DefaultUserTask(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
        this.businessKey = builder.businessKey;
        this.assignee = builder.assignee;
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
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getBusinessKey() {
        return businessKey;
    }

    @Override
    public String getAssignee() {
        return assignee;
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

        private String name;

        private String description;

        private String businessKey;

        private String assignee;

        private Date createTime;

        private Date dueDate;

        private Date claimTime;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
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
