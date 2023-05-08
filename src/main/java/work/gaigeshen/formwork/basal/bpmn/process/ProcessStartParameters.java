package work.gaigeshen.formwork.basal.bpmn.process;

import work.gaigeshen.formwork.basal.bpmn.candidate.CandidateUpdates;
import work.gaigeshen.formwork.basal.bpmn.candidate.Candidates;

import java.util.Map;
import java.util.Objects;

/**
 * 业务流程开启参数，用于开始某个业务流程
 *
 * @author gaigeshen
 */
public class ProcessStartParameters {

    /**
     * 流程标识符
     */
    private final String processId;

    /**
     * 业务标识符
     */
    private final String businessKey;

    /**
     * 任务参数
     */
    private final Map<String, Object> variables;

    /**
     * 业务流程发起人
     */
    private final String userId;

    /**
     * 发起人自选审批候选人
     */
    private final Candidates appointees;

    /**
     * 审批候选人修改参数
     */
    private final CandidateUpdates updates;

    private ProcessStartParameters(Builder builder) {
        this.processId = builder.processId;
        this.businessKey = builder.businessKey;
        this.variables = builder.variables;
        this.userId = builder.userId;
        this.appointees = builder.appointees;
        this.updates = builder.updates;
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

    public Map<String, Object> getVariables() {
        return variables;
    }

    public String getUserId() {
        return userId;
    }

    public Candidates getAppointees() {
        return appointees;
    }

    public CandidateUpdates getUpdates() {
        return updates;
    }

    @Override
    public String toString() {
        return "ProcessStartParameters{" +
                "processId='" + processId + '\'' +
                ", businessKey='" + businessKey + '\'' +
                ", variables=" + variables +
                ", userId='" + userId + '\'' +
                ", appointees=" + appointees +
                ", updates=" + updates +
                '}';
    }

    public static class Builder {

        private String processId;

        private String businessKey;

        private Map<String, Object> variables;

        private String userId;

        private Candidates appointees;

        private CandidateUpdates updates;

        public Builder processId(String processId) {
            this.processId = processId;
            return this;
        }

        public Builder businessKey(String businessKey) {
            this.businessKey = businessKey;
            return this;
        }

        public Builder variables(Map<String, Object> variables) {
            this.variables = variables;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder appointees(Candidates appointees) {
            this.appointees = appointees;
            return this;
        }

        public Builder updates(CandidateUpdates updates) {
            this.updates = updates;
            return this;
        }

        public ProcessStartParameters build() {
            if (Objects.isNull(processId)) {
                throw new IllegalArgumentException("processId cannot be null");
            }
            if (Objects.isNull(businessKey)) {
                throw new IllegalArgumentException("businessKey cannot be null");
            }
            if (Objects.isNull(variables)) {
                throw new IllegalArgumentException("variables cannot be null");
            }
            if (Objects.isNull(userId)) {
                throw new IllegalArgumentException("userId cannot be null");
            }
            if (Objects.isNull(appointees)) {
                throw new IllegalArgumentException("appointees cannot be null");
            }
            if (Objects.isNull(updates)) {
                throw new IllegalArgumentException("updates cannot be null");
            }
            return new ProcessStartParameters(this);
        }
    }
}
