package work.gaigeshen.formwork.basal.logging.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import work.gaigeshen.formwork.basal.identity.IdentityGenerator;
import work.gaigeshen.formwork.basal.json.JsonCodec;
import work.gaigeshen.formwork.basal.logging.*;
import work.gaigeshen.formwork.basal.logging.RecordStore;

import java.util.Date;
import java.util.Objects;

/**
 *
 * @author gaigeshen
 */
@Aspect
public class LoggingAspect {

    private final ThreadLocal<LogRecordStack> logRecordStackLocal = new InheritableThreadLocal<>();

    private final TraceIdContext traceIdContext;

    private final RecordStore recordStore;

    public LoggingAspect(TraceIdContext traceIdContext, RecordStore recordStore) {
        this.traceIdContext = traceIdContext;
        this.recordStore = recordStore;
    }

    @Around("@annotation(logging)")
    public Object doAround(ProceedingJoinPoint joinPoint, Logging logging) throws Throwable {
        Signature signature = joinPoint.getSignature();
        String declaringTypeName = signature.getDeclaringTypeName();
        String methodName = signature.getName();

        LogRecord.Builder builder = LogRecord.builder()
                .createTime(new Date()).traceId(getOrCreateTraceId())
                .name(declaringTypeName + "#" + methodName)
                .input(convertParameters(joinPoint.getArgs()));

        LogRecordStack logRecordStack = getOrCreateLogRecordStack();

        Object proceedResult;
        try {
            proceedResult = joinPoint.proceed();
        } catch (Throwable ex) {
            logRecordStack.pushRecord(builder.output(ex.getMessage()).build());
            if (logging.first()) {
                recordStore.saveRecords(logRecordStack.toList());
                removeLogRecordStack();
                removeTraceId();
            }
            throw ex;
        }
        logRecordStack.pushRecord(builder.output(convertResponse(proceedResult)).build());
        if (logging.first()) {
            recordStore.saveRecords(logRecordStack.toList());
        }
        return proceedResult;
    }

    private void removeLogRecordStack() {
        logRecordStackLocal.remove();
    }

    private void removeTraceId() {
        traceIdContext.removeTraceId();
    }

    private LogRecordStack getOrCreateLogRecordStack() {
        LogRecordStack logRecordStack = logRecordStackLocal.get();
        if (Objects.nonNull(logRecordStack)) {
            return logRecordStack;
        }
        LogRecordStack newLogRecordStack = new LogRecordStack();
        logRecordStackLocal.set(newLogRecordStack);
        return newLogRecordStack;
    }

    private String getOrCreateTraceId() {
        TraceId traceId = traceIdContext.getTraceId();
        if (Objects.nonNull(traceId)) {
            return traceId.getTraceId();
        }
        TraceId newTraceId = new TraceId(IdentityGenerator.generateDefault());
        traceIdContext.setTraceId(newTraceId);
        return newTraceId.getTraceId();
    }

    private String convertParameters(Object[] args) {
        return JsonCodec.instance().encode(args);
    }

    private String convertResponse(Object result) {
        return JsonCodec.instance().encode(result);
    }
}
