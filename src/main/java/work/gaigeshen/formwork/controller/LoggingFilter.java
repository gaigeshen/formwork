package work.gaigeshen.formwork.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

/**
 * 此过滤器用于打印请求和响应数据内容
 *
 * @author gaigeshen
 */
@WebFilter("/*")
@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class LoggingFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(LoggingFilter.class);

    public static final String TRACE_ID_KEY = "tid";

    public static final String TRACE_ID_HEADER = "X-Trace-ID";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper((HttpServletRequest) request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper((HttpServletResponse) response);

        String traceId = UUID.randomUUID().toString().replace("-", "");
        responseWrapper.setHeader(TRACE_ID_HEADER, traceId);

        MDC.put(TRACE_ID_KEY, traceId);

        log.info("URI => {} {}", requestWrapper.getMethod(), requestWrapper.getRequestURI());
        log.info("Query => {}", requestWrapper.getQueryString());

        log.info("Headers => {}", new ServletServerHttpRequest(requestWrapper).getHeaders());

        log.info("Body => {}", getBody(requestWrapper));

        try {
            chain.doFilter(requestWrapper, responseWrapper);
            log.info("Response => {}", getResponse(responseWrapper));
            responseWrapper.copyBodyToResponse();
        } finally {
            MDC.remove(TRACE_ID_KEY);
        }
    }

    private String getBody(ContentCachingRequestWrapper httpRequest) {
        String contentType = httpRequest.getContentType();
        if (Objects.nonNull(contentType) && contentType.startsWith("application/json")) {
            return new String(httpRequest.getContentAsByteArray());
        }
        return "null";
    }

    private String getResponse(ContentCachingResponseWrapper httpResponse) {
        String contentType = httpResponse.getContentType();
        if (Objects.nonNull(contentType) && contentType.startsWith("application/json")) {
            return new String(httpResponse.getContentAsByteArray());
        }
        return "null";
    }
}
