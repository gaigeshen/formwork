package work.gaigeshen.formwork.basal.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 认证凭证
 *
 * @author gaigeshen
 */
public class AuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;

    private Object credentials;

    protected AuthenticationToken(Object principal, Object credentials) {
        super(null);
        this.principal = principal;
        this.credentials = credentials;
    }

    protected AuthenticationToken(Set<GrantedAuthority> authorities, Object principal, Object credentials) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
    }

    /**
     * 创建认证凭证对象，此凭证对象未认证
     *
     * @param principal 认证主体比如用户名
     * @param credentials 认证凭据比如密码
     * @return 返回新创建的认证凭证对象
     */
    public static AuthenticationToken unauthenticated(Object principal, Object credentials) {
        AuthenticationToken authenticationToken = new AuthenticationToken(principal, credentials);
        authenticationToken.setAuthenticated(false);
        return authenticationToken;
    }

    /**
     * 创建认证凭证对象，此凭证对象已认证
     *
     * @param authorization 已认证的授权信息
     * @param credentials 认证凭据比如密码
     * @return 返回新创建的认证凭证对象
     */
    public static AuthenticationToken authenticated(Authorization authorization, Object credentials) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        for (String authority : authorization.getAuthorities()) {
            if (Objects.isNull(authority)) {
                continue;
            }
            authorities.add(new SimpleGrantedAuthority(authority));
        }
        AuthenticationToken authenticationToken = new AuthenticationToken(authorities, authorization, credentials);
        authenticationToken.setAuthenticated(true);
        return authenticationToken;
    }

    /**
     * 创建认证凭证对象，此凭证对象已认证
     *
     * @param authorization 已认证的授权信息
     * @return 返回新创建的认证凭证对象
     */
    public static AuthenticationToken authenticated(Authorization authorization) {
        return authenticated(authorization, null);
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
        if (authenticated && Objects.isNull(getAuthorization())) {
            return;
        }
        super.setAuthenticated(authenticated);
    }

    @Override
    public void eraseCredentials() {
        credentials = null;
    }

    /**
     * 返回已认证的授权信息，如果此凭证对象未认证则返回空对象
     *
     * @return 已认证的授权信息
     */
    public Authorization getAuthorization() {
        if (principal instanceof Authorization) {
            return (Authorization) principal;
        }
        return null;
    }
}
