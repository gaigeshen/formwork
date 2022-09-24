package work.gaigeshen.formwork.security.web;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 访问被拒绝的时候被调用
 *
 * @author gaigeshen
 */
public abstract class AbstractAccessDeniedHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

    @Override
    public final void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        handle(request, response, authException);
    }

    @Override
    public final void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        handle(request, response, (Exception) accessDeniedException);
    }

    /**
     * 此方法访问被拒绝的时候被调用，有可能没有足够的权限或者没有获取有效的已认证凭证
     *
     * @see AccessDeniedException
     * @see InsufficientAuthenticationException
     */
    protected abstract void handle(HttpServletRequest request, HttpServletResponse response, Exception ex) throws IOException;
}
