package work.gaigeshen.formwork.basal.security.web.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 默认的日志处理器仅仅是打印而已
 *
 * @author gaigeshen
 */
public class DefaultHandlerLoggingProcessor implements HandlerLoggingProcessor {

    private static final Logger log = LoggerFactory.getLogger(DefaultHandlerLoggingProcessor.class);

    @Override
    public void processParameters(Object[] parameters, String typeName, String methodName, HttpServletRequest httpRequest) {
        log.info("------> Handler: {}#{}", typeName, methodName);
        log.info("------> URI: {} {}", httpRequest.getMethod(), httpRequest.getRequestURI());
        log.info("------> Query: {}", httpRequest.getQueryString());
        log.info("------> Headers: {}", resolveHeaders(httpRequest));
        log.info("------> Parameters: {}", Arrays.toString(parameters));
    }

    @Override
    public void processError(Throwable throwable, HttpServletRequest httpRequest) {
        log.warn("<------ Error: {}", throwable.getMessage());
    }

    @Override
    public void processResult(Object result, HttpServletRequest httpRequest) {
        log.info("<------ Result: {}", result);
    }

    /**
     * 从请求对象中解析请求头
     *
     * @param httpRequest 请求对象
     * @return 请求头
     */
    private Map<String, List<String>> resolveHeaders(HttpServletRequest httpRequest) {
        Map<String, List<String>> headers = new HashMap<>();
        for (Enumeration<String> names = httpRequest.getHeaderNames(); names.hasMoreElements(); ) {
            String headerName = names.nextElement();
            List<String> headerValues = new ArrayList<>();
            headers.put(headerName, headerValues);
            for (Enumeration<String> values = httpRequest.getHeaders(headerName); values.hasMoreElements(); ) {
                String headerValue = values.nextElement();
                headerValues.add(headerValue);
            }
        }
        return headers;
    }
}
