package work.gaigeshen.formwork.security.web.logout;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import work.gaigeshen.formwork.security.AuthenticationToken;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 抽象的登出处理器，将会被内置的登出过滤器调用
 *
 * @author gaigeshen
 */
public abstract class AbstractLogoutHandler implements LogoutHandler, LogoutSuccessHandler {

    @Override
    public final void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        logout(request, response, (AuthenticationToken) authentication);
    }

    @Override
    public final void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        onSuccess(request, response, (AuthenticationToken) authentication);
    }

    /**
     * 需要实现的登出处理方法，比如让访问令牌失效
     *
     * @param request 请求对象
     * @param response 响应对象
     * @param token 已认证的认证凭证
     */
    protected abstract void logout(HttpServletRequest request, HttpServletResponse response, AuthenticationToken token);

    /**
     * 登出成功之后的处理，比如响应对应的内容
     *
     * @param request 请求对象
     * @param response 响应对象
     * @param token 已认证的认证凭证
     * @throws IOException 可能在响应内容的时候发生异常
     */
    protected abstract void onSuccess(HttpServletRequest request, HttpServletResponse response, AuthenticationToken token) throws IOException;
}
