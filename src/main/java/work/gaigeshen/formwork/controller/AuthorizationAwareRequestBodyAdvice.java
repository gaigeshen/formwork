package work.gaigeshen.formwork.controller;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;
import work.gaigeshen.formwork.security.AuthorizationAware;
import work.gaigeshen.formwork.security.SecurityContextUtils;

import java.lang.reflect.Type;

/**
 * 用于将当前的安全授权信息设置到请求体参数对象中
 *
 * @author gaigeshen
 */
@ControllerAdvice
public class AuthorizationAwareRequestBodyAdvice extends RequestBodyAdviceAdapter {

    @Override
    public boolean supports(MethodParameter methodParameter,
                            Type targetType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage message,
                                MethodParameter parameter,
                                Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        if (body instanceof AuthorizationAware) {
            ((AuthorizationAware) body).setAuthorization(SecurityContextUtils.getAuthorization());
        }
        return body;
    }
}
