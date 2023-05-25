package work.gaigeshen.formwork.basal.security.web.logging;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.WebUtils;
import work.gaigeshen.formwork.basal.identity.IdentityGenerator;
import work.gaigeshen.formwork.basal.json.JsonCodec;
import work.gaigeshen.formwork.basal.security.SecurityUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * 用于打印请求和响应日志
 *
 * @author gaigeshen
 */
@WebFilter("/*")
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
@ControllerAdvice
public class LoggingAdvice implements Filter, HandlerInterceptor, WebMvcConfigurer, ResponseBodyAdvice<Object> {

    private static final Logger log = LoggerFactory.getLogger(LoggingAdvice.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        StopWatch stopWatch = StopWatch.createStarted();
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        ContentCachingRequestWrapper requestToUse;
        if (httpRequest instanceof ContentCachingRequestWrapper) {
            requestToUse = (ContentCachingRequestWrapper) httpRequest;
        } else {
            requestToUse = new ContentCachingRequestWrapper(httpRequest);
        }
        String traceId = IdentityGenerator.generateDefault();
        httpResponse.setHeader("X-Trace-ID", traceId);
        MDC.put("tid", traceId);
        log.info("------> URI: {} {}", httpRequest.getMethod(), httpRequest.getRequestURI());
        log.info("------> Client: {}", httpRequest.getRemoteAddr());
        log.info("------> Principal: {}", SecurityUtils.getPrincipal());
        log.info("------> Query: {}", httpRequest.getQueryString());
        try {
            chain.doFilter(requestToUse, response);
        } finally {
            stopWatch.stop();
            log.info("<------ Duration: {}", stopWatch.formatTime());
            MDC.remove("tid");
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.info("------> Handler: {}", handler);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if (Objects.nonNull(ex)) {
            log.info("<------ Error: {}", ex.getMessage());
        }
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this);
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType contentType, Class<? extends HttpMessageConverter<?>> converter, ServerHttpRequest request, ServerHttpResponse response) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            HttpServletRequest httpRequest = ((ServletRequestAttributes) requestAttributes).getRequest();
            ContentCachingRequestWrapper requestWrapper = WebUtils.getNativeRequest(httpRequest, ContentCachingRequestWrapper.class);
            if (Objects.nonNull(requestWrapper)) {
                log.info("------> Content: {}", readJsonHttpRequestContent(requestWrapper, request));
            }
        }
        // 只打印特定类型的响应结果
        if (AbstractJackson2HttpMessageConverter.class.isAssignableFrom(converter)) {
            log.info("------> Result: {}", JsonCodec.instance().encode(body));
        }
        return body;
    }

    /**
     * 读取请求体内容，此方法无论什么情况都会返回字符串内容，如果出现异常则会在内容中体现
     *
     * @param cachingHttpRequest 带有缓存的请求对象
     * @param serverHttpRequest 请求对象
     * @return 返回请求体内容
     */
    private String readJsonHttpRequestContent(ContentCachingRequestWrapper cachingHttpRequest, ServerHttpRequest serverHttpRequest) {
        MediaType contentType = serverHttpRequest.getHeaders().getContentType();
        if (!MediaType.APPLICATION_JSON.equalsTypeAndSubtype(contentType)) {
            return "Not readable content [ " + contentType + " ]";
        }
        byte[] contentBytes = cachingHttpRequest.getContentAsByteArray();
        if (contentBytes.length == 0) {
            return "None";
        }
        ByteArrayInputStream bytesStream = new ByteArrayInputStream(contentBytes);
        BufferedReader bytesReader = new BufferedReader(new InputStreamReader(bytesStream, StandardCharsets.UTF_8));
        String contentLine;
        StringBuilder builder = new StringBuilder();
        try {
            while (Objects.nonNull(contentLine = bytesReader.readLine())) {
                builder.append(contentLine.trim());
            }
            return builder.toString();
        } catch (IOException e) {
            return "Cannot read because " + e.getMessage();
        }
    }
}

