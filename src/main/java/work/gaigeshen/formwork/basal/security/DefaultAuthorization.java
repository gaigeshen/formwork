package work.gaigeshen.formwork.basal.security;

import java.util.Set;

/**
 * 默认的授权信息
 *
 * @author gaigeshen
 */
public class DefaultAuthorization extends AbstractAuthorization {

    public DefaultAuthorization(String userId, String userName, Set<String> authorities) {
        super(userId, userName, authorities);
    }
}
