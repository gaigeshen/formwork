package work.gaigeshen.formwork.basal.security.accesstoken;

import work.gaigeshen.formwork.basal.security.Authorization;

/**
 * 访问令牌创建器，管理所有的访问令牌
 *
 * @author gaigeshen
 */
public interface AccessTokenCreator {

    /**
     * 失效指定的访问令牌
     *
     * @param token 访问令牌
     */
    void invalidate(String token);

    /**
     * 失效授权信息
     *
     * @param authorization 授权信息
     */
    void invalidate(Authorization authorization);

    /**
     * 创建访问令牌
     *
     * @param authorization 授权信息
     * @return 新的访问令牌
     */
    String createToken(Authorization authorization);

    /**
     * 校验访问令牌并返回授权信息
     *
     * @param token 访问令牌
     * @return 校验成功之后才返回否则返回空对象
     */
    Authorization validateToken(String token);
}
