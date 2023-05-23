package work.gaigeshen.formwork.basal.security.web.logging.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.web.bind.annotation.PostMapping;

@Aspect
public class LoggingAspect {

    @Around("@annotation(mapping)")
    public Object doArount(ProceedingJoinPoint pjp, PostMapping mapping) throws Throwable {


        return pjp.proceed();
    }

}
