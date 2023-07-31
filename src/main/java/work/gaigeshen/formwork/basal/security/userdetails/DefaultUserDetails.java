package work.gaigeshen.formwork.basal.security.userdetails;

import work.gaigeshen.formwork.basal.security.AbstractAuthorization;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author gaigeshen
 */
public class DefaultUserDetails extends AbstractAuthorization implements UserDetails {

    private final Map<String, Object> properties;

    private final boolean disabled;

    private final boolean locked;

    public DefaultUserDetails(String userId, String userName,
                              Set<String> authorities, Map<String, Object> properties,
                              boolean disabled, boolean locked) {
        super(userId, userName, authorities);
        this.properties = properties;
        this.disabled = disabled;
        this.locked = locked;
    }

    public static DefaultUserDetails create(String userId, String userName,
                                            Set<String> authorities, Map<String, Object> properties) {
        return new DefaultUserDetails(userId, userName, authorities, properties, false, false);
    }

    public static DefaultUserDetails create(String userId, String userName,
                                            Set<String> authorities, Map<String, Object> properties, boolean disabled) {
        return new DefaultUserDetails(userId, userName, authorities, properties, disabled, false);
    }

    @Override
    public Map<String, Object> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    @Override
    public boolean isDisabled() {
        return disabled;
    }

    @Override
    public boolean isLocked() {
        return locked;
    }
}
