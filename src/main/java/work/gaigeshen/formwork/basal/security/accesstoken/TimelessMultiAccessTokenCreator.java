package work.gaigeshen.formwork.basal.security.accesstoken;

import work.gaigeshen.formwork.basal.identity.IdentityGenerator;
import work.gaigeshen.formwork.basal.security.Authorization;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 此访问令牌创建器没有自动失效功能，且相同的授权信息可以创建多个不同的访问令牌，仅用于测试环境
 *
 * @author gaigeshen
 */
public class TimelessMultiAccessTokenCreator implements AccessTokenCreator {

    private final Map<Authorization, Set<String>> authorizationTokens = new ConcurrentHashMap<>();

    private TimelessMultiAccessTokenCreator() { }

    public static TimelessMultiAccessTokenCreator create() {
        return new TimelessMultiAccessTokenCreator();
    }

    @Override
    public void invalidate(String token) {
        for (Map.Entry<Authorization, Set<String>> entry : authorizationTokens.entrySet()) {
            if (entry.getValue().remove(token)) {
                break;
            }
        }
    }

    /**
     * 此实现无法失效授权信息
     *
     * @param authorization 授权信息
     */
    @Override
    public void invalidate(Authorization authorization) { }

    /**
     * 创建访问令牌
     *
     * @param authorization 授权信息，如果此授权信息已有访问令牌则会再次创建新的访问令牌
     * @return 新的访问令牌
     */
    @Override
    public String createToken(Authorization authorization) {
        String token = IdentityGenerator.generateDefault();
        authorizationTokens.computeIfAbsent(authorization, a -> new HashSet<>()).add(token);
        return token;
    }

    @Override
    public Authorization validateToken(String token) {
        for (Map.Entry<Authorization, Set<String>> entry : authorizationTokens.entrySet()) {
            if (entry.getValue().contains(token)) {
                return entry.getKey();
            }
        }
        return null;
    }
}
