package work.gaigeshen.formwork.security.web.logout;

import org.apache.commons.lang3.StringUtils;
import work.gaigeshen.formwork.security.AuthenticationToken;
import work.gaigeshen.formwork.security.Authorization;
import work.gaigeshen.formwork.security.accesstoken.AccessTokenCreator;
import work.gaigeshen.formwork.security.web.authentication.AccessTokenAutoAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * 抽象的访问令牌登出处理器，将会失效对应的访问令牌
 *
 * @author gaigeshen
 */
public abstract class AbstractAccessTokenLogoutHandler extends AbstractLogoutHandler {

    private final AccessTokenCreator accessTokenCreator;

    protected AbstractAccessTokenLogoutHandler(AccessTokenCreator accessTokenCreator) {
        if (Objects.isNull(accessTokenCreator)) {
            throw new IllegalArgumentException("access token creator cannot be null");
        }
        this.accessTokenCreator = accessTokenCreator;
    }

    @Override
    protected final void logout(HttpServletRequest request, HttpServletResponse response, AuthenticationToken token) {
        if (Objects.nonNull(token)) {
            Authorization authorization = token.getAuthorization();
            if (Objects.nonNull(authorization)) {
                accessTokenCreator.invalidate(authorization);
            }
        }
        String accessToken = request.getHeader(AccessTokenAutoAuthenticationFilter.ACCESS_TOKEN_HEADER);
        if (StringUtils.isNotBlank(accessToken)) {
            accessTokenCreator.invalidate(accessToken);
        }
    }
}
