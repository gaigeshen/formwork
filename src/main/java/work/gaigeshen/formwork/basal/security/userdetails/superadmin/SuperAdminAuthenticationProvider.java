package work.gaigeshen.formwork.basal.security.userdetails.superadmin;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import work.gaigeshen.formwork.basal.security.AbstractAuthenticationProvider;
import work.gaigeshen.formwork.basal.security.AuthenticationToken;
import work.gaigeshen.formwork.basal.security.Authorization;
import work.gaigeshen.formwork.basal.security.userdetails.DefaultUserDetails;

import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

/**
 * 超级管理员认证
 *
 * @author gaigeshen
 */
public class SuperAdminAuthenticationProvider extends AbstractAuthenticationProvider {

    private final SuperAdminProperties superAdminProperties;

    private final PasswordEncoder passwordEncoder;

    public SuperAdminAuthenticationProvider(SuperAdminProperties superAdminProperties, PasswordEncoder passwordEncoder) {
        this.superAdminProperties = superAdminProperties;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected Authorization authenticate(AuthenticationToken token) throws AuthenticationException {
        String userName = superAdminProperties.getUserName();
        if (Objects.nonNull(userName) && Objects.equals(token.getPrincipal(), userName)) {
            if (passwordEncoder.matches((CharSequence) token.getCredentials(), superAdminProperties.getPassword())) {
                HashMap<String, Object> properties = new HashMap<>();
                properties.put("admin", true);
                return DefaultUserDetails.create(userName, userName, Collections.emptySet(), properties);
            }
        }
        return null;
    }
}
