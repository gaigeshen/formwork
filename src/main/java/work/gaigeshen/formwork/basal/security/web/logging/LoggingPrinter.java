package work.gaigeshen.formwork.basal.security.web.logging;

import javax.servlet.http.HttpServletRequest;

/**
 * 日志打印器，用于打印控制器方法被执行的时候的入参和出参
 *
 * @author gaigeshen
 */
public interface LoggingPrinter {

    /**
     * 打印请求参数
     *
     * @param httpRequest 请求对象
     * @param parameters 请求参数
     */
    void printParameters(HttpServletRequest httpRequest, Object[] parameters);

    /**
     * 打印响应结果
     *
     * @param httpRequest 请求对象
     * @param result 响应结果
     */
    void printResult(HttpServletRequest httpRequest, Object result);
}
