package work.gaigeshen.formwork.security;

import java.io.Serializable;
import java.util.Set;

/**
 * 授权信息
 *
 * @author gaigeshen
 */
public interface Authorization extends Serializable {

    String getUserId();

    String getUsername();

    Set<String> getAuthorities();
}
