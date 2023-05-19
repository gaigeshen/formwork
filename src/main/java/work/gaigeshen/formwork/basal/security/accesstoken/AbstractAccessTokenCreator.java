package work.gaigeshen.formwork.basal.security.accesstoken;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import work.gaigeshen.formwork.basal.security.Authorization;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 具体的访问令牌创建器继承此类，注意此类是基于内存缓存实现的
 *
 * @author gaigeshen
 */
public abstract class AbstractAccessTokenCreator implements AccessTokenCreator {

    private final Map<Authorization, String> authorizationTokens = new ConcurrentHashMap<>();

    private final Cache<String, Authorization> authorizations;

    private AbstractAccessTokenCreator(CacheBuilder<Object, Object> cacheBuilder) {
        authorizations = cacheBuilder.removalListener((RemovalListener<String, Authorization>) ntf -> {
            Authorization authorization = ntf.getValue();
            authorizationTokens.remove(authorization, ntf.getKey());
        }).build();
    }

    protected AbstractAccessTokenCreator(long expiresSeconds, long maxTokenCount) {
        this(CacheBuilder.newBuilder().expireAfterAccess(Duration.ofSeconds(expiresSeconds)).maximumSize(maxTokenCount));
    }

    @Override
    public final void invalidate(String token) {
        if (Objects.isNull(token)) {
            throw new IllegalArgumentException("token cannot be null");
        }
        authorizations.invalidate(token);
    }

    @Override
    public final void invalidate(Authorization authorization) {
        if (Objects.isNull(authorization)) {
            throw new IllegalArgumentException("authorization cannot be null");
        }
        String token = authorizationTokens.get(authorization);
        if (Objects.nonNull(token)) {
            invalidate(token);
        }
    }

    @Override
    public final String createToken(Authorization authorization) {
        if (Objects.isNull(authorization)) {
            throw new IllegalArgumentException("authorization cannot be null");
        }
        String newToken = createTokenInternal(authorization);
        String previousToken = authorizationTokens.put(authorization, newToken);
        if (Objects.nonNull(previousToken)) {
            invalidate(previousToken);
        }
        this.authorizations.put(newToken, authorization);
        return newToken;
    }

    @Override
    public final Authorization validateToken(String token) {
        if (Objects.isNull(token)) {
            throw new IllegalArgumentException("token cannot be null");
        }
        Authorization authorization = authorizations.getIfPresent(token);
        if (Objects.isNull(authorization)) {
            return null;
        }
        if (validateTokenInternal(token, authorization)) {
            return authorization;
        }
        invalidate(token);
        return null;
    }

    protected abstract String createTokenInternal(Authorization authorization);

    protected abstract boolean validateTokenInternal(String token, Authorization authorization);
}
