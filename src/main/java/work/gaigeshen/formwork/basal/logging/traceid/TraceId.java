package work.gaigeshen.formwork.basal.logging.traceid;

import java.util.Objects;

/**
 * 跟踪标识符
 *
 * @author gaigeshen
 */
public class TraceId {

    private final String traceId;

    public TraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getTraceId() {
        return traceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TraceId)) {
            return false;
        }
        TraceId that = (TraceId) o;
        return Objects.equals(traceId, that.traceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(traceId);
    }

    @Override
    public String toString() {
        return traceId;
    }
}
