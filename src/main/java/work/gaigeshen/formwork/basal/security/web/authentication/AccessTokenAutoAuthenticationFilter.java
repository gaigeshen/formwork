package work.gaigeshen.formwork.basal.security.web.authentication;

import work.gaigeshen.formwork.basal.security.accesstoken.AccessTokenCreator;
import work.gaigeshen.formwork.basal.security.Authorization;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * 访问令牌认证过滤器，此过滤器用于从认证请求对象中解析访问令牌从而得到授权信息
 *
 * @author gaigeshen
 */
public class AccessTokenAutoAuthenticationFilter extends AbstractAutoAuthenticationFilter {

    public static final String ACCESS_TOKEN_HEADER = "X-Auth-Token";

    private final AccessTokenCreator accessTokenCreator;

    public AccessTokenAutoAuthenticationFilter(AccessTokenCreator accessTokenCreator) {
        if (Objects.isNull(accessTokenCreator)) {
            throw new IllegalArgumentException("access token creator cannot be null");
        }
        this.accessTokenCreator = accessTokenCreator;
    }

    @Override
    protected Authorization resolveAuthorization(HttpServletRequest request) {
        String accessToken = request.getHeader(ACCESS_TOKEN_HEADER);
        if (Objects.isNull(accessToken)) {
            return null;
        }
        return accessTokenCreator.validateToken(accessToken);
    }
}
