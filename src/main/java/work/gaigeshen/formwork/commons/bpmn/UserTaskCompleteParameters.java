package work.gaigeshen.formwork.commons.bpmn;

import java.util.Map;

/**
 *
 * @author gaigeshen
 */
public class UserTaskCompleteParameters {

    private final UserTask userTask;

    private final Map<String, Object> variables;

    private final String assignee;

    private final boolean rejected;

    private UserTaskCompleteParameters(Builder builder) {
        this.userTask = builder.userTask;
        this.variables = builder.variables;
        this.assignee = builder.assignee;
        this.rejected = builder.rejected;
    }

    public static Builder builder() {
        return new Builder();
    }

    public UserTask getUserTask() {
        return userTask;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public String getAssignee() {
        return assignee;
    }

    public boolean isRejected() {
        return rejected;
    }

    public static class Builder {

        private UserTask userTask;

        private Map<String, Object> variables;

        private String assignee;

        private boolean rejected;

        public Builder userTask(UserTask userTask) {
            this.userTask = userTask;
            return this;
        }

        public Builder variables(Map<String, Object> variables) {
            this.variables = variables;
            return this;
        }

        public Builder assignee(String assignee) {
            this.assignee = assignee;
            return this;
        }

        public Builder rejected(boolean rejected) {
            this.rejected = rejected;
            return this;
        }

        public UserTaskCompleteParameters build() {
            return new UserTaskCompleteParameters(this);
        }
    }
}
