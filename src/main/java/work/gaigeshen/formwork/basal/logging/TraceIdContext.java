package work.gaigeshen.formwork.basal.logging;

/**
 * 用于维护当前的跟踪标识符
 *
 * @author gaigeshen
 */
public interface TraceIdContext {

    TraceId getTraceId();

    void setTraceId(TraceId traceId);

    void removeTraceId();
}
