package work.gaigeshen.formwork.security;

import java.util.Set;

/**
 * 授权信息
 *
 * @author gaigeshen
 */
public interface Authorization {

    String getUserId();

    String getUsername();

    Set<String> getAuthorities();
}
