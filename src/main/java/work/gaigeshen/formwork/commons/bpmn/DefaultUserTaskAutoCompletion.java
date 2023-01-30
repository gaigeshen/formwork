package work.gaigeshen.formwork.commons.bpmn;

import java.util.Set;

/**
 *
 * @author gaigeshen
 */
public class DefaultUserTaskAutoCompletion implements UserTaskAutoCompletion {

    private final String processId;

    private final String businessKey;

    private final Set<String> groups;

    private final Set<String> users;

    private final boolean hasMoreUserTasks;

    private DefaultUserTaskAutoCompletion(Builder builder) {
        this.processId = builder.processId;
        this.businessKey = builder.businessKey;
        this.groups = builder.groups;
        this.users = builder.users;
        this.hasMoreUserTasks = builder.hasMoreUserTasks;
    }

    public static Builder builder() {
        return new Builder();
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
    public Set<String> getGroups() {
        return groups;
    }

    @Override
    public Set<String> getUserId() {
        return users;
    }

    @Override
    public boolean hasMoreUserTasks() {
        return hasMoreUserTasks;
    }

    public static class Builder {

        private String processId;

        private String businessKey;

        private Set<String> groups;

        private Set<String> users;

        private boolean hasMoreUserTasks;

        public Builder processId(String processId) {
            this.processId = processId;
            return this;
        }

        public Builder businessKey(String businessKey) {
            this.businessKey = businessKey;
            return this;
        }

        public Builder groups(Set<String> groups) {
            this.groups = groups;
            return this;
        }

        public Builder users(Set<String> users) {
            this.users = users;
            return this;
        }

        public Builder hasMoreUserTasks(boolean hasMoreUserTasks) {
            this.hasMoreUserTasks = hasMoreUserTasks;
            return this;
        }

        public DefaultUserTaskAutoCompletion build() {
            return new DefaultUserTaskAutoCompletion(this);
        }
    }
}
