package work.gaigeshen.formwork.basal.logging;

/**
 * 用于维护当前的跟踪标识符，基于线程本地变量实现
 *
 * @author gaigeshen
 */
public class ThreadTraceIdContext implements TraceIdContext {

    private final ThreadLocal<TraceId> traceIdLocal = new InheritableThreadLocal<>();

    @Override
    public TraceId getTraceId() {
        return traceIdLocal.get();
    }

    @Override
    public void setTraceId(TraceId traceId) {
        traceIdLocal.set(traceId);
    }

    @Override
    public void removeTraceId() {
        traceIdLocal.remove();
    }
}
