package work.gaigeshen.formwork.basal.security.userdetails;

import work.gaigeshen.formwork.basal.security.Authorization;

import java.util.Map;

/**
 *
 * @author gaigeshen
 */
public interface UserDetails extends Authorization {

    Map<String, Object> getProperties();

    boolean isDisabled();

    boolean isLocked();
}
