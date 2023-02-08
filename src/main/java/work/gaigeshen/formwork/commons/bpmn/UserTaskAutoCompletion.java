package work.gaigeshen.formwork.commons.bpmn;

import java.util.Set;

/**
 * 用户任务自动完成结果
 *
 * @author gaigeshen
 */
public class UserTaskAutoCompletion {

    private final String processId;

    private final String businessKey;

    private final Set<String> groups;

    private final Set<String> users;

    private final boolean hasMoreUserTasks;

    private UserTaskAutoCompletion(Builder builder) {
        this.processId = builder.processId;
        this.businessKey = builder.businessKey;
        this.groups = builder.groups;
        this.users = builder.users;
        this.hasMoreUserTasks = builder.hasMoreUserTasks;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getProcessId() {
        return processId;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public Set<String> getGroups() {
        return groups;
    }

    public Set<String> getUsers() {
        return users;
    }

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

        public UserTaskAutoCompletion build() {
            return new UserTaskAutoCompletion(this);
        }
    }
}
