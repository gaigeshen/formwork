package work.gaigeshen.formwork.security;

import org.springframework.security.core.AuthenticationException;

/**
 * 授权信息未找到的异常，比如在尝试获取认证凭证对应的授权信息时候未找到其授权信息
 *
 * @author gaigeshen
 */
public class AuthorizationNotFoundException extends AuthenticationException {

    public AuthorizationNotFoundException(String msg) {
        super(msg);
    }
}
