package work.gaigeshen.formwork.basal.security.accesstoken;

import org.redisson.api.*;
import work.gaigeshen.formwork.basal.identity.IdentityGenerator;
import work.gaigeshen.formwork.basal.security.Authorization;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 采用外部缓存的访问令牌创建器，同个授权信息可以有多个令牌
 *
 * @author gaigeshen
 */
public class RedissonMultiAccessTokenCreator implements AccessTokenCreator {

    private final RedissonClient redisson;

    private final long expiresSeconds;

    public RedissonMultiAccessTokenCreator(RedissonClient redisson, long expiresSeconds) {
        if (Objects.isNull(redisson)) {
            throw new IllegalArgumentException("redisson client cannot be null");
        }
        this.redisson = redisson;
        this.expiresSeconds = expiresSeconds;
    }

    @Override
    public void invalidate(String token) {
        if (Objects.isNull(token)) {
            throw new IllegalArgumentException("token cannot be null");
        }
        getAuthorizationBucket(token).delete();
    }

    /**
     * 调用此方法无任何效果，因为没有缓存授权信息对应的访问令牌
     *
     * @param authorization 授权信息
     */
    @Override
    public void invalidate(Authorization authorization) { }

    /**
     * 创建访问令牌，此方法已改写接口定义的逻辑，对同个授权信息可以多次创建
     *
     * @param authorization 授权信息
     * @return 新的访问令牌
     */
    @Override
    public String createToken(Authorization authorization) {
        if (Objects.isNull(authorization)) {
            throw new IllegalArgumentException("authorization cannot be null");
        }
        String newToken = IdentityGenerator.generateDefault();
        getAuthorizationBucket(newToken).set(authorization, expiresSeconds, TimeUnit.SECONDS);
        return newToken;
    }

    @Override
    public Authorization validateToken(String token) {
        if (Objects.isNull(token)) {
            throw new IllegalArgumentException("token cannot be null");
        }
        RBucket<Authorization> authorizationBucket = getAuthorizationBucket(token);
        Authorization authorization = authorizationBucket.get();
        if (Objects.isNull(authorization)) {
            return null;
        }
        authorizationBucket.touch();
        return authorization;
    }

    private RBucket<Authorization> getAuthorizationBucket(String token) {
        return redisson.getBucket("token_" + token);
    }
}
