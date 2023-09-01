package work.gaigeshen.formwork.basal.security.accesstoken;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import work.gaigeshen.formwork.basal.security.Authorization;
import work.gaigeshen.formwork.basal.security.DefaultAuthorization;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 *
 * @author gaigeshen
 */
public class JWTAccessTokenCreator extends AbstractAccessTokenCreator {

    public static final String CLAIM_USER_ID = "userId";

    public static final String CLAIM_USER_NAME = "userName";

    public static final String CLAIM_AUTHORITIES = "authorities";

    public static final String CLAIM_PURPOSE = "purpose";

    private final long expiresSeconds;

    private final String secret;

    private JWTAccessTokenCreator(long expiresSeconds, long maxTokenCount, String secret) {
        super(expiresSeconds, maxTokenCount);
        this.expiresSeconds = expiresSeconds;
        this.secret = secret;
    }

    public static JWTAccessTokenCreator create(long expiresSeconds, long maxTokenCount, String secret) {
        return new JWTAccessTokenCreator(expiresSeconds, maxTokenCount, secret);
    }

    public static JWTAccessTokenCreator create(String secret) {
        return create(1800, 10000, secret);
    }

    @Override
    protected String createTokenInternal(Authorization authorization) {
        long currentTimeMillis = System.currentTimeMillis();
        JWTCreator.Builder builder = JWT.create().withIssuedAt(new Date(currentTimeMillis))
                .withExpiresAt(new Date(currentTimeMillis + expiresSeconds * 1000))
                .withClaim(CLAIM_USER_ID, authorization.getUserId())
                .withClaim(CLAIM_USER_NAME, authorization.getUserName())
                .withClaim(CLAIM_PURPOSE, authorization.getPurpose());
        if (Objects.isNull(authorization.getAuthorities())) {
            builder.withClaim(CLAIM_AUTHORITIES, Collections.emptyList());
        } else {
            builder.withClaim(CLAIM_AUTHORITIES, new ArrayList<>(authorization.getAuthorities()));
        }
        return builder.sign(Algorithm.HMAC256(secret));
    }

    @Override
    protected boolean validateTokenInternal(String token, Authorization authorization) {
        try {
            JWT.require(Algorithm.HMAC256(secret))
                    .withClaim(CLAIM_USER_ID, authorization.getUserId())
                    .withClaim(CLAIM_USER_NAME, authorization.getUserName())
                    .withClaim(CLAIM_PURPOSE, authorization.getPurpose())
                    .build().verify(token);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 解析访问令牌为授权信息，同时会校验该访问令牌的合法性
     *
     * @param token 访问令牌
     * @return 如果该访问令牌合法且成功解析则返回授权信息
     */
    public Authorization resolveAndVerifyAuthorization(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        String userId = decodedJWT.getClaim(CLAIM_USER_ID).asString();
        String userName = decodedJWT.getClaim(CLAIM_USER_NAME).asString();
        String purpose = decodedJWT.getClaim(CLAIM_PURPOSE).asString();
        List<String> authorities = decodedJWT.getClaim(CLAIM_AUTHORITIES).asList(String.class);
        if (Objects.isNull(userId) || Objects.isNull(userName)
                || Objects.isNull(purpose) || Objects.isNull(authorities)) {
            return null;
        }
        DefaultAuthorization authorization = new DefaultAuthorization(
                userId, userName, new HashSet<>(authorities));
        authorization.setPurpose(purpose);
        if (!validateTokenInternal(token, authorization)) {
            return null;
        }
        return authorization;
    }

    /**
     * 解析访问令牌的过期时间，此方法不校验该访问令牌是否合法
     *
     * @param token 访问令牌
     * @return 过期时间，如果该访问令牌没有设置过期时间则返回空
     */
    public LocalDateTime resolveExpiresTime(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        Instant expiresAtAsInstant = decodedJWT.getExpiresAtAsInstant();
        if (Objects.isNull(expiresAtAsInstant)) {
            return null;
        }
        return LocalDateTime.ofInstant(expiresAtAsInstant, ZoneId.systemDefault());
    }
}
