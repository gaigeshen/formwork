package work.gaigeshen.formwork.basal.security.web.logging.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import work.gaigeshen.formwork.basal.security.web.logging.LoggingPrinter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@Aspect
public class LoggingAspect {

    private final LoggingPrinter printer;

    public LoggingAspect(LoggingPrinter printer) {
        this.printer = printer;
    }

    @Around("@annotation(org.springframework.web.bind.annotation.GetMapping) " +
            "|| @annotation(org.springframework.web.bind.annotation.PostMapping)")
    public Object doArount(ProceedingJoinPoint pjp) throws Throwable {

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        if (Objects.isNull(requestAttributes) || !(requestAttributes instanceof ServletRequestAttributes)) {
            return pjp.proceed();
        }

        HttpServletRequest httpRequest = ((ServletRequestAttributes) requestAttributes).getRequest();

        HttpServletResponse httpResponse = ((ServletRequestAttributes) requestAttributes).getResponse();



        return pjp.proceed();
    }

}
