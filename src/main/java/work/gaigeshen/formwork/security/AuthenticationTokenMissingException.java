package work.gaigeshen.formwork.security;

import org.springframework.security.core.AuthenticationException;

/**
 * 缺少认证凭证的异常，比如从认证请求对象中解析认证凭证发现缺少必要的参数而无法创建认证凭证
 *
 * @author gaigeshen
 */
public class AuthenticationTokenMissingException extends AuthenticationException {

    public AuthenticationTokenMissingException(String msg) {
        super(msg);
    }
}
