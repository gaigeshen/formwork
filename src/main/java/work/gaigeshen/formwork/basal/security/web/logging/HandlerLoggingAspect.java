package work.gaigeshen.formwork.basal.security.web.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * 日志处理切面
 *
 * @author gaigeshen
 */
@Aspect
public class HandlerLoggingAspect {

    private final HandlerLoggingProcessor loggingProcessor;

    public HandlerLoggingAspect(HandlerLoggingProcessor loggingProcessor) {
        if (Objects.isNull(loggingProcessor)) {
            throw new IllegalArgumentException("loggingProcessor cannot be null");
        }
        this.loggingProcessor = loggingProcessor;
    }

    @Around("@annotation(org.springframework.web.bind.annotation.GetMapping) || @annotation(org.springframework.web.bind.annotation.PostMapping)")
    public Object doArount(ProceedingJoinPoint pjp) throws Throwable {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (!(requestAttributes instanceof ServletRequestAttributes)) {
            return pjp.proceed();
        }
        HttpServletRequest httpRequest = ((ServletRequestAttributes) requestAttributes).getRequest();

        Object[] parameters = pjp.getArgs();
        Signature signature = pjp.getSignature();
        String typeName = signature.getDeclaringTypeName();
        String methodName = signature.getName();

        loggingProcessor.handleParameters(parameters, typeName, methodName, httpRequest);

        Object proceedResult;
        try {
            proceedResult = pjp.proceed();
        } catch (Throwable ex) {
            loggingProcessor.handleError(ex, httpRequest);
            throw ex;
        }
        loggingProcessor.handleResult(proceedResult, httpRequest);
        return proceedResult;
    }
}
