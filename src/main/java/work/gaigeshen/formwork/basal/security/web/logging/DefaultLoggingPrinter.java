package work.gaigeshen.formwork.basal.security.web.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import work.gaigeshen.formwork.basal.json.JsonCodec;
import work.gaigeshen.formwork.basal.security.SecurityUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 默认的日志打印器
 *
 * @author gaigeshen
 */
public class DefaultLoggingPrinter implements LoggingPrinter {

    private static final Logger log = LoggerFactory.getLogger(DefaultLoggingPrinter.class);

    @Override
    public void printParameters(HttpServletRequest httpRequest, Object[] parameters) {

        log.info("=================================>");

        log.info("User: {}", SecurityUtils.getUserId().orElse("None"));

        log.info("URI: {} {}", httpRequest.getMethod(), httpRequest.getRequestURI());

        log.info("Query: {}", httpRequest.getQueryString());

        log.info("Headers: {}", resolveHeaders(httpRequest));

        log.info("Parameters: {}", JsonCodec.instance().encode(parameters));
    }

    @Override
    public void printResult(HttpServletRequest httpRequest, Object result) {

        log.info("Result: {}", JsonCodec.instance().encode(result));

        log.info("<=================================");
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
