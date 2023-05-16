package work.gaigeshen.formwork.basal.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import work.gaigeshen.formwork.basal.security.userdetails.UserDetails;
import work.gaigeshen.formwork.basal.security.userdetails.UserDetailsChecker;

import java.util.Objects;

/**
 * 子类继承此类来执行具体的认证逻辑
 *
 * @author gaigeshen
 */
public abstract class AbstractAuthenticationProvider implements AuthenticationProvider {

    @Override
    public final Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Authorization authorization = authenticate((AuthenticationToken) authentication);
        if (Objects.isNull(authorization)) {
            throw new AuthorizationNotFoundException("authorization of " + authentication + " not found");
        }
        if (authorization instanceof UserDetails) {
            UserDetailsChecker.check((UserDetails) authentication);
        }
        return AuthenticationToken.authenticated(authorization, authentication.getCredentials());
    }

    @Override
    public final boolean supports(Class<?> authentication) {
        return AuthenticationToken.class == authentication;
    }

    /**
     * 实现具体的认证逻辑
     *
     * @param token 认证凭证是未认证的
     * @return 认证成功之后返回对应的授权信息，如果未找到授权信息则可以直接返回空对象
     * @throws AuthenticationException 认证失败可以抛出异常
     */
    protected abstract Authorization authenticate(AuthenticationToken token) throws AuthenticationException;
}
