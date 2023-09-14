package work.gaigeshen.formwork.basal.security.accesstoken;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import work.gaigeshen.formwork.basal.security.Authorization;
import work.gaigeshen.formwork.basal.security.DefaultAuthorization;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author gaigeshen
 */
public interface JWTAccessTokenSupport {

    String CLAIM_USER_ID = "userId";

    String CLAIM_USER_NAME = "userName";

    String CLAIM_AUTHORITIES = "authorities";

    String CLAIM_PURPOSE = "purpose";

    /**
     * 解析访问令牌的过期时间
     *
     * @param jwtToken 访问令牌不能为空
     * @return 访问令牌的过期时间
     */
    default LocalDateTime resolveExpiresTime(String jwtToken) {
        if (Objects.isNull(jwtToken)) {
            throw new IllegalArgumentException("token cannot be null");
        }
        DecodedJWT decodedJWT = JWT.decode(jwtToken);
        Instant expiresAtAsInstant = decodedJWT.getExpiresAtAsInstant();
        if (Objects.isNull(expiresAtAsInstant)) {
            return null;
        }
        return LocalDateTime.ofInstant(expiresAtAsInstant, ZoneId.systemDefault());
    }

    /**
     * 解析并验证访问令牌然后返回当时生成该访问令牌的授权信息
     *
     * @param jwtToken 访问令牌不能为空
     * @return 解析并验证成功的情况返回授权信息，否则返回空对象
     */
    default Authorization resolveAndVerifyAuthorization(String jwtToken) {
        if (Objects.isNull(jwtToken)) {
            throw new IllegalArgumentException("token cannot be null");
        }
        DecodedJWT decodedJWT = JWT.decode(jwtToken);
        String userId = decodedJWT.getClaim(CLAIM_USER_ID).asString();
        String userName = decodedJWT.getClaim(CLAIM_USER_NAME).asString();
        String purpose = decodedJWT.getClaim(CLAIM_PURPOSE).asString();
        List<String> authorities = decodedJWT.getClaim(CLAIM_AUTHORITIES).asList(String.class);
        if (Objects.isNull(userId) || Objects.isNull(userName)
                || Objects.isNull(purpose) || Objects.isNull(authorities)) {
            return null;
        }
        DefaultAuthorization authorization = new DefaultAuthorization(userId, userName, new HashSet<>(authorities));
        authorization.setPurpose(purpose);
        if (!validateJWTTokenInternal(jwtToken, authorization)) {
            return null;
        }
        return authorization;
    }

    /**
     * 校验访问令牌和对应的授权信息
     *
     * @param jwtToken 访问令牌
     * @param authorization 授权信息
     * @return 校验结果
     */
    boolean validateJWTTokenInternal(String jwtToken, Authorization authorization);
}
