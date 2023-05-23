package work.gaigeshen.formwork.basal.security.web.logging;

import javax.servlet.http.HttpServletRequest;

/**
 * 日志处理器用于请求的目标方法被执行时各项参数和返回结果的处理
 *
 * @author gaigeshen
 */
public interface HandlerLoggingProcessor {

    default void processParameters(Object[] parameters, String typeName, String methodName, HttpServletRequest httpRequest) {}

    default void processError(Throwable throwable, HttpServletRequest httpRequest) {}

    default void processResult(Object result, HttpServletRequest httpRequest) {}
}
