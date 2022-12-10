package work.gaigeshen.formwork.commons.web.trace;

import org.slf4j.MDC;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * 日志跟踪码过滤器
 *
 * @author gaigeshen
 */
public class TraceIdFilter implements Filter {

    public static final String TRACE_ID_KEY = "tid";

    public static final String TRACE_ID_HEADER = "X-Trace-ID";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String traceId = UUID.randomUUID().toString().replace("-", "");

        ((HttpServletResponse) response).setHeader(TRACE_ID_HEADER, traceId);

        MDC.put(TRACE_ID_KEY, traceId);
        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove(TRACE_ID_KEY);
        }
    }
}
