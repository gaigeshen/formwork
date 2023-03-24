package work.gaigeshen.formwork.security.accesstoken;

import org.redisson.api.BatchOptions;
import org.redisson.api.RBatch;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import work.gaigeshen.formwork.commons.identity.IdentityCreator;
import work.gaigeshen.formwork.security.Authorization;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 采用外部缓存的访问令牌创建器
 *
 * @author gaigeshen
 */
public class RedissonAccessTokenCreator implements AccessTokenCreator {

    private final RedissonClient redisson;

    private final long expiresSeconds;

    public RedissonAccessTokenCreator(RedissonClient redisson, long expiresSeconds) {
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
        Authorization authorization = deleteAuthorizationBucket(getAuthorizationBucket(token));
        if (Objects.isNull(authorization)) {
            return;
        }
        deleteTokenBucket(getTokenBucket(authorization.getUserId()));
    }

    @Override
    public void invalidate(Authorization authorization) {
        if (Objects.isNull(authorization)) {
            throw new IllegalArgumentException("authorization cannot be null");
        }
        String token = deleteTokenBucket(getTokenBucket(authorization.getUserId()));
        if (Objects.isNull(token)) {
            return;
        }
        deleteAuthorizationBucket(getAuthorizationBucket(token));
    }

    @Override
    public String createToken(Authorization authorization) {
        if (Objects.isNull(authorization)) {
            throw new IllegalArgumentException("authorization cannot be null");
        }
        invalidate(authorization);
        String newToken = IdentityCreator.createDefault();
        setTokenAndAuthorizationBucket(newToken, authorization);
        return newToken;
    }

    @Override
    public Authorization validateToken(String token) {
        if (Objects.isNull(token)) {
            throw new IllegalArgumentException("token cannot be null");
        }
        Authorization authorization = getAuthorizationBucket(token).get();
        if (Objects.isNull(authorization)) {
            return null;
        }
        return authorization;
    }

    /**
     * 删除访问令牌缓存
     *
     * @param tokenBucket 访问令牌缓存
     * @return 删除前的访问令牌
     */
    private String deleteTokenBucket(RBucket<String> tokenBucket) {
        return tokenBucket.getAndDelete();
    }

    /**
     * 删除授权信息缓存
     *
     * @param authorizationRBucket 授权信息缓存
     * @return 删除前的授权信息
     */
    private Authorization deleteAuthorizationBucket(RBucket<Authorization> authorizationRBucket) {
        return authorizationRBucket.getAndDelete();
    }

    private RBucket<String> getTokenBucket(String userId) {
        return redisson.getBucket("user_" + userId);
    }

    private RBucket<Authorization> getAuthorizationBucket(String token) {
        return redisson.getBucket("token_" + token);
    }

    private void setTokenAndAuthorizationBucket(String token, Authorization authorization) {
        RBatch batch = redisson.createBatch(BatchOptions.defaults());
        batch.getBucket("user_" + authorization.getUserId()).setAsync(token, expiresSeconds, TimeUnit.SECONDS);
        batch.getBucket("token_" + token).setAsync(authorization, expiresSeconds, TimeUnit.SECONDS);
        batch.execute();
    }
}
