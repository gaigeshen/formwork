package work.gaigeshen.formwork.basal.bpmn.process;

import java.util.Objects;

/**
 * 流程部署参数
 *
 * @author gaigeshen
 */
public class ProcessDeployParameters {

    private final String processId;

    private final String procesName;

    private final ProcessNode processNode;

    private ProcessDeployParameters(Builder builder) {
        this.processId = builder.processId;
        this.procesName = builder.procesName;
        this.processNode = builder.processNode;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getProcessId() {
        return processId;
    }

    public String getProcesName() {
        return procesName;
    }

    public ProcessNode getProcessNode() {
        return processNode;
    }

    @Override
    public String toString() {
        return "ProcessDeployParameters{" +
                "processId='" + processId + '\'' +
                ", procesName='" + procesName + '\'' +
                ", processNode=" + processNode +
                '}';
    }

    public static class Builder {

        private String processId;

        private String procesName;

        private ProcessNode processNode;

        public Builder processId(String processId) {
            this.processId = processId;
            return this;
        }

        public Builder procesName(String procesName) {
            this.procesName = procesName;
            return this;
        }

        public Builder processNode(ProcessNode processNode) {
            this.processNode = processNode;
            return this;
        }

        public ProcessDeployParameters build() {
            if (Objects.isNull(processId)) {
                throw new IllegalArgumentException("processId cannot be null");
            }
            if (Objects.isNull(procesName)) {
                throw new IllegalArgumentException("processName cannot be null");
            }
            if (Objects.isNull(processNode)) {
                throw new IllegalArgumentException("processNode cannot be null");
            }
            return new ProcessDeployParameters(this);
        }
    }
}
