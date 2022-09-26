package work.gaigeshen.formwork.controller;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import work.gaigeshen.formwork.commons.web.Result;
import work.gaigeshen.formwork.commons.web.Results;

import java.util.Objects;

/**
 * 用于简化控制器方法返回对象类型的申明
 *
 * @author gaigeshen
 */
@RestControllerAdvice(
        basePackageClasses = ErrorController.class
)
public class ResultResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object returnBody, MethodParameter returnType,
                                  MediaType contentType, Class<? extends HttpMessageConverter<?>> converterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        if (Objects.isNull(returnBody)) {
            return Results.create();
        }
        if (!(returnBody instanceof Result)) {
            return Results.create(returnBody);
        }
        return returnBody;
    }
}
