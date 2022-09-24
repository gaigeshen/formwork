package work.gaigeshen.formwork.security;

import java.util.Set;

/**
 * 默认的授权信息
 *
 * @author gaigeshen
 */
public class DefaultAuthorization extends AbstractAuthorization {

    public DefaultAuthorization(String userId, String username, Set<String> authorities) {
        super(userId, username, authorities);
    }
}
