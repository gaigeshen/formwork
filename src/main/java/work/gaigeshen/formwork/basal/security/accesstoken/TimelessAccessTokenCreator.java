package work.gaigeshen.formwork.basal.security.accesstoken;

import work.gaigeshen.formwork.basal.identity.IdentityGenerator;
import work.gaigeshen.formwork.basal.security.Authorization;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 此访问令牌创建器只能用于测试环境因为访问令牌不会自动过期
 *
 * @author gaigeshen
 */
public class TimelessAccessTokenCreator implements AccessTokenCreator {

    private final Map<Authorization, Set<String>> allTokens = new ConcurrentHashMap<>();

    @Override
    public void invalidate(String token) {
        for (Map.Entry<Authorization, Set<String>> entry : allTokens.entrySet()) {
            if (entry.getValue().remove(token)) {
                break;
            }
        }
    }

    /**
     * 此实现有所不同，不会做任何操作
     *
     * @param authorization 授权信息
     */
    @Override
    public void invalidate(Authorization authorization) {
    }

    /**
     * 此实现有所不同，同样的授权信息可以多次调用此方法创建多个访问令牌
     *
     * @param authorization 授权信息
     * @return 新的访问令牌
     */
    @Override
    public String createToken(Authorization authorization) {
        String newToken = IdentityGenerator.generateDefault();
        allTokens.computeIfAbsent(authorization, a -> new CopyOnWriteArraySet<>()).add(newToken);
        return newToken;
    }

    @Override
    public Authorization validateToken(String token) {
        for (Map.Entry<Authorization, Set<String>> entry : allTokens.entrySet()) {
            if (entry.getValue().contains(token)) {
                return entry.getKey();
            }
        }
        return null;
    }
}
