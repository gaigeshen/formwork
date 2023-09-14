package work.gaigeshen.formwork.basal.security.accesstoken;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import org.redisson.api.RedissonClient;
import work.gaigeshen.formwork.basal.security.Authorization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;

/**
 *
 * @author gaigeshen
 */
public class JWTRedissonAccessTokenCreator extends RedissonAccessTokenCreator implements JWTAccessTokenSupport {

    private final String secret;

    private JWTRedissonAccessTokenCreator(RedissonClient redisson, long expiresSeconds, String secret) {
        super(redisson, expiresSeconds);
        this.secret = secret;
    }

    public static JWTRedissonAccessTokenCreator create(RedissonClient redisson, long expiresSeconds, String secret) {
        return new JWTRedissonAccessTokenCreator(redisson, expiresSeconds, secret);
    }

    @Override
    protected String createTokenInternal(Authorization authorization) {
        long currentTimeMillis = System.currentTimeMillis();
        JWTCreator.Builder builder = JWT.create().withIssuedAt(new Date(currentTimeMillis))
                .withExpiresAt(new Date(currentTimeMillis + getExpiresSeconds() * 1000))
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

    @Override
    public boolean validateJWTTokenInternal(String jwtToken, Authorization authorization) {
        return validateTokenInternal(jwtToken, authorization);
    }
}
