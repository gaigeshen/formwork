package work.gaigeshen.formwork.basal.logging;

import java.util.Date;

public class LogRecord {

    private final String name;

    private final String parameters;

    private final String result;

    private final String traceId;

    private final Date createTime;

    private LogRecord(Builder builder) {
        this.name = builder.name;
        this.parameters = builder.parameters;
        this.result = builder.result;
        this.traceId = builder.traceId;
        this.createTime = builder.createTime;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getName() {
        return name;
    }

    public String getParameters() {
        return parameters;
    }

    public String getResult() {
        return result;
    }

    public String getTraceId() {
        return traceId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public static class Builder {

        private String name;

        private String parameters;

        private String result;

        private String traceId;

        private Date createTime;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder parameters(String parameters) {
            this.parameters = parameters;
            return this;
        }

        public Builder result(String result) {
            this.result = result;
            return this;
        }

        public Builder traceId(String traceId) {
            this.traceId = traceId;
            return this;
        }

        public Builder createTime(Date createTime) {
            this.createTime = createTime;
            return this;
        }

        public LogRecord build() {
            return new LogRecord(this);
        }
    }
}
