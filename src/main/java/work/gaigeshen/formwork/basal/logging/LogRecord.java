package work.gaigeshen.formwork.basal.logging;

import java.util.Date;

public class LogRecord {

    private final String name;

    private final String input;

    private final String output;

    private final String traceId;

    private final Date createTime;

    private LogRecord(Builder builder) {
        this.name = builder.name;
        this.input = builder.input;
        this.output = builder.output;
        this.traceId = builder.traceId;
        this.createTime = builder.createTime;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getName() {
        return name;
    }

    public String getInput() {
        return input;
    }

    public String getOutput() {
        return output;
    }

    public String getTraceId() {
        return traceId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public static class Builder {

        private String name;

        private String input;

        private String output;

        private String traceId;

        private Date createTime;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder input(String input) {
            this.input = input;
            return this;
        }

        public Builder output(String output) {
            this.output = output;
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
