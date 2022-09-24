package work.gaigeshen.formwork.security.web.authentication;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import work.gaigeshen.formwork.security.AuthenticationToken;
import work.gaigeshen.formwork.security.AuthenticationTokenMissingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * 抽象的认证过滤器，子类需要关注如何从认证请求对象中解析认证凭证以及认证之后如何处理
 *
 * @author gaigeshen
 */
public abstract class AbstractAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    /**
     * 创建此认证过滤器
     *
     * @param requestMatcher 此过滤器处理什么样的请求，此参数不能为空
     * @param authenticationManager 认证管理器不能为空，用于处理具体的认证过程
     */
    protected AbstractAuthenticationFilter(RequestMatcher requestMatcher, AuthenticationManager authenticationManager) {
        super(requestMatcher, authenticationManager);
        setAuthenticationSuccessHandler(this::onAuthenticationSuccess);
        setAuthenticationFailureHandler(this::onAuthenticationFailure);
        setAuthenticationDetailsSource(this::buildDetails);
    }

    @Override
    public final Authentication attemptAuthentication(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
            throws AuthenticationException {
        AuthenticationToken authenticationToken = resolveAuthenticationToken(httpRequest);
        if (Objects.isNull(authenticationToken)) {
            throw new AuthenticationTokenMissingException("cannot resolve authentication token from " + httpRequest);
        }
        authenticationToken.setDetails(authenticationDetailsSource.buildDetails(httpRequest));
        return getAuthenticationManager().authenticate(authenticationToken);
    }

    public AuthenticationManager getAuthenticationManager() {
        return super.getAuthenticationManager();
    }

    /**
     * 返回的对象将会直接被设置到认证凭证中
     *
     * @param context 当前的认证请求对象
     * @return 默认返回当前的认证请求对象
     */
    public Object buildDetails(HttpServletRequest context) {
        return context;
    }

    /**
     * 认证成功之后的处理，内部方法
     *
     * @param httpRequest 当前的认证请求对象
     * @param httpResponse 用于响应结果
     * @param authentication 已认证的认证凭证
     * @throws IOException 在处理时发生异常
     */
    public final void onAuthenticationSuccess(HttpServletRequest httpRequest, HttpServletResponse httpResponse,
                                              Authentication authentication) throws IOException {
        onSuccess(httpResponse, (AuthenticationToken) authentication);
    }

    /**
     * 认证失败之后的处理，内部方法
     *
     * @param httpRequest 当前的认证请求对象
     * @param httpResponse 用于响应结果
     * @param exception 认证失败的异常
     * @throws IOException 在处理时发生异常
     */
    public final void onAuthenticationFailure(HttpServletRequest httpRequest, HttpServletResponse httpResponse,
                                              AuthenticationException exception) throws IOException {
        onFailure(httpResponse, exception);
    }

    /**
     * 从当前的认证请求对象中解析认证凭证并返回
     *
     * @param httpRequest 当前的认证请求对象
     * @return 解析的认证凭证应该是未认证的，如果发现缺少必要的参数而无法创建认证凭证可以直接返回空对象
     * @throws AuthenticationException 可以抛出的异常
     */
    protected abstract AuthenticationToken resolveAuthenticationToken(HttpServletRequest httpRequest) throws AuthenticationException;

    /**
     * 认证成功之后的处理
     *
     * @param httpResponse 用于响应结果
     * @param token 已认证的认证凭证
     * @throws IOException 在处理时发生异常
     */
    protected abstract void onSuccess(HttpServletResponse httpResponse, AuthenticationToken token) throws IOException;

    /**
     * 认证失败之后的处理
     *
     * @param httpResponse 用于响应结果
     * @param ex 认证失败的异常
     * @throws IOException 在处理时发生异常
     */
    protected abstract void onFailure(HttpServletResponse httpResponse, AuthenticationException ex) throws IOException;
}
