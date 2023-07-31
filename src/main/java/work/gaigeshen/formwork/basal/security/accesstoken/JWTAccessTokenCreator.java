package work.gaigeshen.formwork.basal.security.accesstoken;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import work.gaigeshen.formwork.basal.security.Authorization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;

/**
 *
 * @author gaigeshen
 */
public class JWTAccessTokenCreator extends AbstractAccessTokenCreator {

    public static final String CLAIM_USER_ID = "userId";

    public static final String CLAIM_USER_NAME = "userName";

    public static final String CLAIM_AUTHORITIES = "authorities";

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
                .withClaim(CLAIM_USER_NAME, authorization.getUserName());
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
                    .build().verify(token);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
