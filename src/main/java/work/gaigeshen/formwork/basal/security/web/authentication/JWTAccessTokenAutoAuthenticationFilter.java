package work.gaigeshen.formwork.basal.security.web.authentication;

import work.gaigeshen.formwork.basal.security.Authorization;
import work.gaigeshen.formwork.basal.security.accesstoken.AccessTokenCreator;
import work.gaigeshen.formwork.basal.security.accesstoken.JWTAccessTokenCreator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * 此访问令牌认证过滤器会在特定场景下生成新的访问令牌并返回给响应头
 *
 * @author gaigeshen
 */
public class JWTAccessTokenAutoAuthenticationFilter extends AccessTokenAutoAuthenticationFilter {

    public static final String RENEWAL_ACCESS_TOKEN_HEADER = "X-Auth-Token-Renewal";

    private final int beforeRenewalSeconds;

    public JWTAccessTokenAutoAuthenticationFilter(AccessTokenCreator accessTokenCreator, int beforeRenewalSeconds) {
        super(accessTokenCreator);
        this.beforeRenewalSeconds = beforeRenewalSeconds;
    }

    public JWTAccessTokenAutoAuthenticationFilter(AccessTokenCreator accessTokenCreator) {
        this(accessTokenCreator, 600);
    }

    @Override
    protected Authorization resolveAuthorization(HttpServletRequest request, HttpServletResponse response) {
        Authorization authorization = super.resolveAuthorization(request, response);
        AccessTokenCreator accessTokenCreator = getAccessTokenCreator();
        if (!(accessTokenCreator instanceof JWTAccessTokenCreator)) {
            return authorization;
        }
        JWTAccessTokenCreator jwtAccessTokenCreator = (JWTAccessTokenCreator) accessTokenCreator;
        String currentToken = getAccessToken(request);
        if (Objects.isNull(authorization)) {
            authorization = jwtAccessTokenCreator.resolveAndVerifyAuthorization(currentToken);
        }
        if (Objects.nonNull(authorization)) {
            LocalDateTime expiresTime = jwtAccessTokenCreator.resolveExpiresTime(currentToken);
            if (Objects.isNull(expiresTime)) {
                return authorization;
            }
            if (LocalDateTime.now().until(expiresTime, ChronoUnit.SECONDS) <= beforeRenewalSeconds) {
                String renewalToken = accessTokenCreator.createToken(authorization);
                response.setHeader(RENEWAL_ACCESS_TOKEN_HEADER, renewalToken);
            }
        }
        return authorization;
    }
}
