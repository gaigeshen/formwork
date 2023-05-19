package work.gaigeshen.formwork.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper((HttpServletResponse) response);

        String traceId = UUID.randomUUID().toString().replace("-", "");
        responseWrapper.setHeader(TRACE_ID_HEADER, traceId);

        MDC.put(TRACE_ID_KEY, traceId);

        log.info("URI => {} {}", httpRequest.getMethod(), httpRequest.getRequestURI());
        log.info("Query => {}", httpRequest.getQueryString());

        log.info("Headers => {}", new ServletServerHttpRequest(httpRequest).getHeaders());

        ByteArrayOutputStream bodyBytes = new ByteArrayOutputStream();
        StreamUtils.copy(httpRequest.getInputStream(), bodyBytes);

        log.info("Body => {}", getBody(httpRequest, bodyBytes.toByteArray()));

        try {
            chain.doFilter(new RequestBodyHttpServletRequest(httpRequest, bodyBytes.toByteArray()), responseWrapper);
            log.info("Response => {}", getResponse(responseWrapper));
            responseWrapper.copyBodyToResponse();
        } finally {
            MDC.remove(TRACE_ID_KEY);
        }
    }

    private String getBody(HttpServletRequest httpRequest, byte[] bodyBytes) {
        String contentType = httpRequest.getContentType();
        if (Objects.nonNull(contentType) && contentType.startsWith("application/json")) {
            return new String(bodyBytes);
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

    private static class RequestBodyHttpServletRequest extends HttpServletRequestWrapper {

        private final byte[] bodyBytes;

        public RequestBodyHttpServletRequest(HttpServletRequest request, byte[] bodyBytes) {
            super(request);
            this.bodyBytes = bodyBytes;
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            return new RequestBodyInputStream(getRequest().getInputStream(), bodyBytes);
        }
    }

    private static class RequestBodyInputStream extends ServletInputStream {

        private final InputStream origin;

        private final ByteArrayInputStream delegate;

        public RequestBodyInputStream(InputStream origin, byte[] body) {
            this.origin = origin;
            this.delegate = new ByteArrayInputStream(body);
        }

        @Override
        public boolean isFinished() {
            return false;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
        }

        @Override
        public int read() {
            return delegate.read();
        }

        @Override
        public int read(byte[] b, int off, int len) {
            return delegate.read(b, off, len);
        }

        @Override
        public int read(byte[] b) throws IOException {
            return delegate.read(b);
        }

        @Override
        public long skip(long n) {
            return delegate.skip(n);
        }

        @Override
        public int available() {
            return delegate.available();
        }

        @Override
        public void close() throws IOException {
            origin.close();
        }

        @Override
        public synchronized void mark(int readlimit) {
            delegate.mark(readlimit);
        }

        @Override
        public synchronized void reset() {
            delegate.reset();
        }

        @Override
        public boolean markSupported() {
            return delegate.markSupported();
        }
    }
}
