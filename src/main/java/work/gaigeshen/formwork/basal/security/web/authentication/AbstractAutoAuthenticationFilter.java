package work.gaigeshen.formwork.basal.security.web.authentication;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import work.gaigeshen.formwork.basal.security.AuthenticationToken;
import work.gaigeshen.formwork.basal.security.Authorization;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * 此过滤器用于自动认证
 *
 * @author gaigeshen
 */
public abstract class AbstractAutoAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected final void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        Authorization authorization = resolveAuthorization(request, response);
        if (Objects.nonNull(authorization)) {
            SecurityContextHolder.getContext().setAuthentication(AuthenticationToken.authenticated(authorization));
        }
        chain.doFilter(request, response);
    }

    /**
     * 从请求对象中解析授权信息，如果没有解析出授权信息则直接返回空对象
     *
     * @param request 请求对象
     * @param response 响应对象
     * @return 解析出来的授权信息可以为空
     */
    protected abstract Authorization resolveAuthorization(HttpServletRequest request, HttpServletResponse response);
}
