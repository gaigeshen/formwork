package work.gaigeshen.formwork.commons.bpmn.usertask;

import work.gaigeshen.formwork.commons.bpmn.candidate.Candidate;

import java.util.Map;
import java.util.Objects;

/**
 * 用户任务完成参数
 *
 * @author gaigeshen
 */
public class UserTaskCompleteParameters {

    /**
     * 需要完成的用户任务
     */
    private final UserTask userTask;

    /**
     * 任务参数
     */
    private final Map<String, Object> variables;

    /**
     * 审批候选人用于完成用户任务
     */
    private final Candidate candidate;

    /**
     * 审批结果是否拒绝
     */
    private final boolean rejected;

    private UserTaskCompleteParameters(Builder builder) {
        this.userTask = builder.userTask;
        this.variables = builder.variables;
        this.candidate = builder.candidate;
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

    public Candidate getCandidate() {
        return candidate;
    }

    public boolean isRejected() {
        return rejected;
    }

    @Override
    public String toString() {
        return "UserTaskCompleteParameters{" +
                "userTask=" + userTask +
                ", variables=" + variables +
                ", candidate=" + candidate +
                ", rejected=" + rejected +
                '}';
    }

    public static class Builder {

        private UserTask userTask;

        private Map<String, Object> variables;

        private Candidate candidate;

        private boolean rejected;

        public Builder userTask(UserTask userTask) {
            this.userTask = userTask;
            return this;
        }

        public Builder variables(Map<String, Object> variables) {
            this.variables = variables;
            return this;
        }

        public Builder candidate(Candidate candidate) {
            this.candidate = candidate;
            return this;
        }

        public Builder rejected(boolean rejected) {
            this.rejected = rejected;
            return this;
        }

        public UserTaskCompleteParameters build() {
            if (Objects.isNull(userTask)) {
                throw new IllegalArgumentException("userTask cannot be null");
            }
            if (Objects.isNull(variables)) {
                throw new IllegalArgumentException("variables cannot be null");
            }
            if (Objects.isNull(candidate)) {
                throw new IllegalArgumentException("candidate cannot be null");
            }
            return new UserTaskCompleteParameters(this);
        }
    }
}
