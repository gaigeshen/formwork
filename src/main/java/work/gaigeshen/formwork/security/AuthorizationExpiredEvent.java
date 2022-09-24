package work.gaigeshen.formwork.security;

import org.springframework.context.ApplicationEvent;

/**
 * 授权信息已过期事件，在需要主动失效授权信息的时候可以发布此事件
 *
 * @author gaigeshen
 */
public class AuthorizationExpiredEvent extends ApplicationEvent {

    private final Authorization authorization;

    public AuthorizationExpiredEvent(Object source, Authorization authorization) {
        super(source);
        this.authorization = authorization;
    }

    public Authorization getAuthorization() {
        return authorization;
    }
}
