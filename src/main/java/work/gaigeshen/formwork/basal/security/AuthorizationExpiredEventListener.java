package work.gaigeshen.formwork.basal.security;

import org.springframework.context.ApplicationListener;
import work.gaigeshen.formwork.basal.security.accesstoken.AccessTokenCreator;

import java.util.Objects;

/**
 * 授权信息已过期事件监听器用于失效对应的访问令牌
 *
 * @author gaigeshen
 */
public class AuthorizationExpiredEventListener implements ApplicationListener<AuthorizationExpiredEvent> {

    private final AccessTokenCreator accessTokenCreator;

    public AuthorizationExpiredEventListener(AccessTokenCreator accessTokenCreator) {
        this.accessTokenCreator = accessTokenCreator;
    }

    @Override
    public void onApplicationEvent(AuthorizationExpiredEvent event) {
        Authorization authorization = event.getAuthorization();
        if (Objects.isNull(authorization)) {
            return;
        }
        accessTokenCreator.invalidate(authorization);
    }
}
