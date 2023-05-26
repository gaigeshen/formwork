package work.gaigeshen.formwork.basal.security.web.logging;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import work.gaigeshen.formwork.basal.identity.IdentityGenerator;
import work.gaigeshen.formwork.basal.security.SecurityUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 此过滤器将会打印请求和响应信息，以及处理器的处理时长
 *
 * @author gaigeshen
 */
@Component
public class LoggingFilter implements Filter, HandlerInterceptor, WebMvcConfigurer {

    private static final Logger log = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        ContentCachingRequestWrapper requestToUse = new ContentCachingRequestWrapper(httpRequest);
        ContentCachingResponseWrapper responseToUse = new ContentCachingResponseWrapper(httpResponse);

        String traceId = createTraceId();
        httpResponse.setHeader("X-Trace-ID", traceId);

        // 不用清理跟踪标识，因为如果后续有异常的情况下日志会缺失跟踪标识
        MDC.put("tid", traceId);

        log.info("---> URI: {} {}", httpRequest.getMethod(), httpRequest.getRequestURI());
        log.info("---> Query: {}", httpRequest.getQueryString());
        log.info("---> Client: {}", httpRequest.getRemoteAddr());

        try {
            chain.doFilter(requestToUse, responseToUse);
        } catch (Exception ex) {
            log.info("---> Content: {}", getRequestContent(requestToUse));
            throw ex;
        }
        log.info("---> Content: {}", getRequestContent(requestToUse));
        log.info("<--- Result: {}", getResponseContent(responseToUse));
        responseToUse.copyBodyToResponse();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            if (!ErrorController.class.isAssignableFrom(handlerMethod.getBeanType())) {
                log.info("---> Principal: {}", SecurityUtils.getPrincipal().orElse(null));
                log.info("---> Handler: {}", handler);
                request.setAttribute("stopWatch", StopWatch.createStarted());
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            if (!ErrorController.class.isAssignableFrom(handlerMethod.getBeanType())) {
                StopWatch stopWatch = (StopWatch) request.getAttribute("stopWatch");
                log.info("---> Duration: {}", stopWatch.formatTime());
                request.removeAttribute("stopWatch");
            }
        }
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this);
    }

    protected String createTraceId() {
        return IdentityGenerator.generateDefault();
    }

    protected String getRequestContent(ContentCachingRequestWrapper request) throws IOException {
        if (StringUtils.containsIgnoreCase(request.getContentType(), "json")) {
            byte[] contentBytes = request.getContentAsByteArray();
            if (contentBytes.length > 0) {
                List<String> contentLines = IOUtils.readLines(new ByteArrayInputStream(contentBytes), StandardCharsets.UTF_8);
                return contentLines.stream().map(StringUtils::trim).collect(Collectors.joining(""));
            }
        }
        return "None";
    }

    protected String getResponseContent(ContentCachingResponseWrapper response) {
        if (StringUtils.containsIgnoreCase(response.getContentType(), "json")) {
            byte[] resultBytes = response.getContentAsByteArray();
            if (resultBytes.length > 0) {
                return new String(resultBytes, StandardCharsets.UTF_8);
            }
        }
        return "None";
    }
}
